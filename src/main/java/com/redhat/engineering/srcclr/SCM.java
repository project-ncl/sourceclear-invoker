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
import com.redhat.engineering.srcclr.utils.ScanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;
import static picocli.CommandLine.ParentCommand;
import static picocli.CommandLine.Unmatched;

@Command(name = "scm", description = "Scan a SCM URL"+ SrcClrWrapper.UNMATCHED, mixinStandardHelpOptions = true )
public class SCM implements Callable<Void>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Option( names = { "-d", "--debug" }, description = "Enable debug." )
    private boolean debug;

    @Option(names = { "--url" }, required = true, paramLabel = "URL", description = "the SCM url")
    private String url;

    @Option(names = { "--ref" }, /*arity = "0..1",*/ paramLabel = "REF", description = "the SCM reference (e.g. git sha, tag)")
    private String tag;

    @Unmatched
    private List<String> unmatched;

    @ParentCommand
    private SrcClrWrapper parent; // picocli injects reference to parent command

    @Override
    public Void call() throws Exception
    {
        if ( debug || parent.isDebug() )
        {
            parent.enableDebug();
        }

        List<String> args = new ArrayList<>();
        Map<String,String> env = new HashMap<>(  );
        parent.excludedEnvironment.forEach( s -> env.put( s, null ) );

        if ( isNotEmpty( url ) )
        {
            args.add( "--url" );
            args.add( url );
        }
        if ( isNotEmpty ( tag ) )
        {
            args.add( "--ref" );
            args.add( tag );
        }

        if ( unmatched != null )
        {
            logger.debug( "Unmatched is {} " , unmatched );
            args.addAll( unmatched );
        }

        SourceClearJSON json = new SrcClrInvoker(parent.isTrace()).execSourceClear( SrcClrInvoker.ScanType.SCM, env, args );

//        logger.info( "Found json unmatched {} ", json.getRecords().size() );

        Set<ProcessorResult> matched = parent.getProcessor().process ( parent, json );

        if ( parent.isException() && matched.size() > 0 )
        {
            Record record = json.getRecords().get( 0 );

            parent.notifyListeners( this.toString(), matched.stream().filter( ProcessorResult::getNotify ).collect( Collectors.toSet()) );

            throw new ScanException( "Found " + matched.size() + " vulnerabilities : " +
                             ( record.getMetadata().getReport() == null ? "no-report-available" : record.getMetadata().getReport() ) );
        }
        else if ( json.getRecords().size() > 0 )
        {
            logger.info( "Report is {}", json.getRecords().get( 0 ).getMetadata().getReport() );

        }
        return null;
    }

    @Override
    public String toString()
    {
        return "scanning " + url + " version " + tag;
    }
}
