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
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Responsible for locating and invoking the SourceClear Jar.
 */
public class SrcClrInvoker
{
    private final static String DEFAULT_LOCATION = "/usr/local/bin/srcclr";

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    public enum ScanType
    {
        SCM, BINARY, OTHER
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
        command.add( "java" );
        command.add( "-jar" );
        command.add( locateSourceClearJar().toString() );

        // If we add --debug before the scan type command then that enables vast
        // amount of debugging (but breaks the json output).
        // command.add( "--debug" );
        if ( type == ScanType.SCM || type == ScanType.BINARY)
        {
            command.add( "scan" );
            command.add( "--json" );
        }
        if ( type == ScanType.BINARY )
        {
            command.add( "--unmatched" );
            command.add( "--recursive" );
            command.add( "--scan-collectors");
            command.add( "jar");
        }
        command.addAll( args );

        Path temporaryLocation = Files.createTempDirectory( "sourceclear-invoker-" );

        SourceClearJSON json = null;
        try
        {
            logger.info( "Invoking {} ....", command );
            String output = new ProcessExecutor().command( command ).
                            environment( env ).
                            destroyOnExit().
                            directory( temporaryLocation.toFile() ).
                            exitValue( 0 ).
                            redirectError ( Slf4jStream.of(logger).asDebug() ).
                            redirectOutput( Slf4jStream.of(logger).asDebug() ).
                            readOutput( true ).
                            execute().
                            outputUTF8();
//            logger.debug( "Read output {} ", output );

            if ( output.contains( "Encountered errors while collecting component information" ) )
            {
                logger.warn( "Unknown errors encountered collecting component information." );
                output = StringUtils.remove( output, "Encountered errors while collecting component information." );
            }
            if ( output.contains( "SourceClear found no library dependencies." ) )
            {
                throw new InternalException( "Error executing SourceClear - found no library dependencies");
            }
            else if ( type != ScanType.OTHER )
            {
                ObjectMapper mapper = new ObjectMapper().configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
                json = mapper.readValue( output, SourceClearJSON.class );

//                logger.debug( "Read json {} ", json );
            }
            else
            {
                logger.info( output );
            }
        }
        catch ( InvalidExitValueException e )
        {
            logger.error( e.getResult().outputUTF8() );
            logger.error( "Invalid exit {} ", e );
            throw new InternalException( "Error executing SourceClear ", e );
        }
        catch ( InterruptedException | TimeoutException e )
        {
            logger.error( "Failed to execute SourceClear: ", e );
            throw new InternalException( "Error executing SourceClear ", e );
        }
        return json;
    }

    private Path getDefaultSrcClrLocation()
    {
        // TODO: Query RPM DB? Assumption is currently its always /usr/local/bin/srcclr.
        return Paths.get( DEFAULT_LOCATION );
    }
}
