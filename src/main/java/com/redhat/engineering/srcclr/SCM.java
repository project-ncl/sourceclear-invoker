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
import com.redhat.engineering.srcclr.json.sourceclear.Vulnerability;
import com.redhat.engineering.srcclr.utils.ScanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;
import static picocli.CommandLine.ParentCommand;
import static picocli.CommandLine.Unmatched;

@Command(name = "scm", description = "Scan a SCM URL", mixinStandardHelpOptions = true )
public class SCM implements Callable<Void>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Option( names = { "-d", "--debug" }, description = "Enable debug." )
    boolean debug;

    @Option(names = { "--url" }, required = true, paramLabel = "URL", description = "the SCM url")
    String url;

    @Option(names = { "--ref" }, /*arity = "0..1",*/ paramLabel = "REF", description = "the SCM reference (e.g. git sha, tag)")
    String tag;

    @Unmatched
    List<String> unmatched;

    @ParentCommand
    SrcClrWrapper parent; // picocli injects reference to parent command

    @Override
    public Void call() throws Exception
    {
        if ( debug || parent.isDebug() )
        {
            parent.enableDebug();
        }

        List<String> args = new ArrayList<>();
        Map<String,String> env = new HashMap<>(  );

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

        SourceClearJSON json = new SrcClrInvoker().execSourceClear( SrcClrInvoker.ScanType.SCM, env, args );

//        logger.info( "Found json unmatched {} ", json.getRecords().size() );

        Record record = json.getRecords().get( 0 );
        ArrayList<Vulnerability> matched = parent.getProcessor().process ( parent, json );

        if ( parent.isException() && matched.size() > 0 )
        {
            matched.forEach( v -> parent.notifyListeners( v ) );

            throw new ScanException( "Found " + matched.size() + " vulnerabilities : " +
                             ( record.getMetadata().getReport() == null ? "no-report-available" : record.getMetadata().getReport() ) );
        }
        return null;
    }
}
