/*
 * Copyright (C) 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.engineering.srcclr;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.engineering.srcclr.json.sourceclear.SourceClearJSON;
import com.redhat.engineering.srcclr.utils.InternalException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Responsible for locating and invoking the SourceClear Jar.
 */
public class SrcClrInvoker
{
    private static final String DEFAULT_LOCATION = "/usr/local/bin/srcclr";

    private static final Pattern REGEXP = Pattern.compile("(?s)(^[^{]*)?\\{(.+)");

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final boolean trace;

    private final String jsonDir;

    public SrcClrInvoker( boolean trace, String jsonDir )
    {
        this.trace = trace;
        this.jsonDir = jsonDir;
    }

    public enum ScanType
    {
        SCM, BINARY, OTHER
    }

    public String locateSourceClearJRE() throws IOException
    {
        return getDefaultSrcClrLocation().toRealPath().getParent().getParent().toString() +
                        File.separatorChar + "jre" + File.separatorChar + "bin" + File.separatorChar + "java";
    }

    public Path locateSourceClearJar() throws IOException
    {
        List<Path> result =
                        Files.list( getDefaultSrcClrLocation().toRealPath().getParent().getParent() )
                             .filter( p -> p.getFileName().toString().matches( "srcclr-[0-9]+.*\\.jar" ) )
                             .collect( Collectors.toList() );

        if ( result.size() != 1 )
        {
            logger.error( "SourceClear jar count incorrect {} ", result );
            throw new IOException( "Incorrect SourceClear jar count: " + result.size() );
        }
        else if ( !result.get( 0 ).toFile().exists() )
        {
            throw new IOException( "SourceClear does not exist : " + result.get( 0 ));
        }
        return result.get( 0 );
    }

    public SourceClearJSON execSourceClear( ScanType type, Map<String, String> env, List<String> args ) throws IOException, InternalException
    {
        List<String> command = new ArrayList<>();
        command.add( locateSourceClearJRE() );
        command.add( "-jar" );
        command.add( locateSourceClearJar().toString() );

        // If we add --debug before the scan type command then that enables vast
        // amount of debugging (but breaks the json output).
        if ( trace )
        {
            command.add( "--debug" );
        }
        if ( type == ScanType.SCM || type == ScanType.BINARY)
        {
            command.add( "scan" );
            command.add( "--json" );
        }
        if ( type == ScanType.BINARY )
        {
            command.add( "--unmatched" );
            command.add( "--recursive" );
        }

        command.addAll( args );

        Path temporaryLocation = Files.createTempDirectory( "sourceclear-invoker-" );
        Path goPathTemporaryLocation = Files.createTempDirectory( "sourceclear-gopath-" );
        env.put( "GOPATH", goPathTemporaryLocation.toFile().getAbsolutePath() );
        // Don't need to set this as the default value is true.
        // env.put( "SRCCLR_FORCE_GO_INSTALL", "true");

        SourceClearJSON processedJson;
        try
        {
            logger.info( "Invoking in environment {} command {} ....", env, command );
            String output = new ProcessExecutor().command( command ).
                            environment( env ).
                            destroyOnExit().
                            directory( temporaryLocation.toFile() ).
                            exitValue( 0 ).
                            redirectError( Slf4jStream.of( logger ).asError() ).
                            redirectOutput( Slf4jStream.of(logger).asDebug() ).
                            readOutput( true ).
                            execute().
                            outputUTF8();

            if ( output.contains( "SourceClear found no library dependencies." ) )
            {
                throw new InternalException( "Error executing SourceClear - found no library dependencies");
            }
            else if ( type != ScanType.OTHER )
            {
                output = stripInvalidOutput( trace, output );

                ObjectMapper mapper = new ObjectMapper().configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
                processedJson = mapper.readValue( output, SourceClearJSON.class );

                if ( isNotBlank( jsonDir ) )
                {
                    File jsonTarget = new File ( jsonDir, "sourceclear.json");
                    jsonTarget.getParentFile().mkdirs();
                    FileUtils.writeStringToFile( jsonTarget, output, Charset.defaultCharset() );
                }
//                logger.debug( "Read json {} ", json );
            }
            else
            {
                processedJson = new SourceClearJSON();
            }
        }
        catch ( InvalidExitValueException e )
        {
            logger.error( e.getResult().outputUTF8() );
            logger.error( "Invalid exit: " + e.getMessage() );
            logger.debug( "SourceClear process finished with an error ", e );
            throw new InternalException( "Error executing SourceClear ", e );
        }
        catch ( InterruptedException | TimeoutException e )
        {
            logger.error( "Failed to execute SourceClear: ", e );
            throw new InternalException( "Error executing SourceClear ", e );
        }
        finally
        {
            FileUtils.deleteDirectory( temporaryLocation.toFile() );
            FileUtils.deleteDirectory( goPathTemporaryLocation.toFile() );
        }
        return processedJson;
    }

    private Path getDefaultSrcClrLocation()
    {
        return Paths.get( DEFAULT_LOCATION );
    }


    public String stripInvalidOutput( boolean trace, String output ) throws InternalException
    {
        if ( trace )
        {
            // The regex will strip the trace output for json processing.
            output = output.replaceFirst( "(?m)[\\s\\S]+^ {2}(\"metadata\")", "{ $1");
        }
        Matcher m = REGEXP.matcher( output );

        if ( m.matches() && m.groupCount() == 2 )
        {
            if ( m.group( 1 ).length() > 0 )
            {
                logger.warn( "Found problems when invoking: {}", m.group( 1 ) );
            }
            return '{' + m.group( 2 );
        }
        else
        {
            throw new InternalException( "Invalid split - gave group count of " + m.groupCount() + " for " + output );
        }
    }
}
