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

import com.redhat.engineering.srcclr.json.sourceclear.Record;
import com.redhat.engineering.srcclr.json.sourceclear.SourceClearJSON;
import com.redhat.engineering.srcclr.processor.ProcessorResult;
import com.redhat.engineering.srcclr.utils.SourceClearResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;
import static picocli.CommandLine.ParentCommand;
import static picocli.CommandLine.Unmatched;

@Command(name = "scm", description = "Scan a SCM URL"+ SrcClrWrapper.UNMATCHED, mixinStandardHelpOptions = true )
public class SCM implements Callable<SourceClearResult>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Option( names = { "-d", "--debug" }, description = "Enable debug." )
    private boolean debug;

    @Option(names = { "--url" }, required = true, paramLabel = "URL", description = "the SCM url")
    private String url;

    @Option(names = { "--ref" }, /*arity = "0..1",*/ paramLabel = "REF", description = "the SCM reference (e.g. git sha, tag)")
    private String tag;

    @Option(names = { "--maven-param" }, paramLabel = "MAVEN-PARAM", description = "extra maven parameters")
    private String mavenParam;

    @Unmatched
    private List<String> unmatched;

    @ParentCommand
    private SrcClrWrapper parent; // picocli injects reference to parent command

    @Override
    public SourceClearResult call() throws Exception
    {
        SourceClearResult result = new SourceClearResult();

        if ( debug || parent.isDebug() )
        {
            parent.enableDebug();
        }

        List<String> args = new ArrayList<>();
        Map<String,String> env = new HashMap<>(  );
        parent.excludedEnvironment.forEach( s -> env.put( s, null ) );

        if ( url.equals( "." ))
        {
            url = Paths.get("" ).toAbsolutePath().toString();
            logger.info( "Scanning local file system with {}", url );
            args.add( url );
        }
        else if ( url.startsWith( "file://" ))
        {
            String target = url.replaceFirst( "file://", "" );
            logger.info( "Scanning local file system with {}", target );
            args.add( target );
        }
        else
        {
            args.add( "--url" );
            args.add( url );
        }
        if ( isNotEmpty ( tag ) )
        {
            args.add( "--ref" );
            args.add( tag );
        }
        if ( isNotEmpty( mavenParam ) )
        {
            // In case the parameter multiple and inside quotes, strip them off to add the default params.
            mavenParam = mavenParam.trim();
            if ( mavenParam.startsWith( "\"" ) )
            {
                mavenParam = mavenParam.substring( 1 );
            }
            if ( mavenParam.endsWith( "\"" ) )
            {
                mavenParam = mavenParam.substring( 0, mavenParam.length() - 1 );
            }
            String defaultCmds = " -Dcheckstyle.skip=true -e -DskipTests -DskipITs --fail-fast --nsu -Denforcer.skip=true ";
            env.put( "SRCCLR_CUSTOM_MAVEN_COMMAND", mavenParam + defaultCmds );
        }

        if ( unmatched != null )
        {
            logger.debug( "Unmatched is {} " , unmatched );
            args.addAll( unmatched );
        }

        SourceClearJSON json = new SrcClrInvoker( parent.isTrace(), parent.getJson() ).execSourceClear( SrcClrInvoker.ScanType.SCM, env, args );

        Set<ProcessorResult> matched = parent.getProcessor().process ( parent, json );

        // logger.info( "Found json unmatched {} and matched {}  ", json.getRecords().size(), matched );

        if ( parent.isException() && matched.size() > 0 )
        {
            Record record = json.getRecords().get( 0 );

            parent.notifyListeners( this.toString(), matched.stream().filter( ProcessorResult::getNotify ).collect( Collectors.toSet()) );

            result.setMessage( "Found " + matched.size() + " vulnerabilities : " +
                             ( record.getMetadata().getReport() == null ? "no-report-available" : record.getMetadata().getReport() ) );
        }
        else if ( json.getRecords().size() > 0 )
        {
            logger.info( "Report is {}", json.getRecords().get( 0 ).getMetadata().getReport() );

        }
        return result;
    }

    @Override
    public String toString()
    {
        return "scanning " + url + " version " + tag;
    }
}
