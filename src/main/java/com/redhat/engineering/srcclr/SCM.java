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

import com.redhat.engineering.srcclr.json.Library;
import com.redhat.engineering.srcclr.json.Record;
import com.redhat.engineering.srcclr.json.SourceClearJSON;
import com.redhat.engineering.srcclr.json.Vulnerability;
import com.redhat.engineering.srcclr.utils.InternalException;
import com.redhat.engineering.srcclr.utils.ScanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;
import static picocli.CommandLine.ParentCommand;
import static picocli.CommandLine.Unmatched;

@Command(name = "scm", description = "Scan a SCM URL" )
public class SCM implements Callable<Void>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final Pattern pattern = Pattern.compile( "(/records/0/libraries/)([0-9]+)(.*)");

    @Option( names = { "-e", "--exception" }, description = "Throw exception on vulnerabilities found." )
    boolean exception = true;

    @Option( names = { "-t", "--threshold" }, description = "Threshold on which exception is thrown.", converter=ThresholdConverter.class)
    int threshold = 0;

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
        if ( debug || parent.debug )
        {
            parent.setDebug();
        }

        List<String> args = new ArrayList<>();

        if ( url != null )
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

        SourceClearJSON json = new SrcClrInvoker().execSourceClear( SrcClrInvoker.ScanType.SCM, args );

//        logger.info( "Found json unmatched {} ", json.getRecords().size() );

        Record record = json.getRecords().get( 0 );
        List<Library> libs = record.getLibraries();
        ArrayList<Vulnerability> matched = new ArrayList<>( );

        for ( Vulnerability vuln : record.getVulnerabilities() )
        {
            // Every vulnerability item has a libraries with size 0 and a _links.
            Matcher matcher = pattern.matcher( vuln.getLibraries().get( 0 ).getLinks().getRef());
            Library library = null;
            while ( matcher.find() )
            {
                library = libs.get( Integer.parseInt( matcher.group( 2 ) ) );
            }
            if ( library == null )
            {
                throw new InternalException( "Unable to locate library for vulnerability in output" + vuln );
            }
            if ( vuln.getCvssScore() >= threshold )
            {
                matched.add( vuln );
                logger.info ( "Found vulnerability '{}' with score {} in library {}:{}:{} and report is {}",
                              vuln.getTitle(), vuln.getCvssScore(), library.getCoordinate1(),
                              library.getCoordinate2(), library.getVersions().get( 0 ).getVersion(),
                              record.getMetadata().getReport()
                );
            }
        }
        if ( exception && matched.size() > 0 )
        {
            throw new ScanException( "Found " + matched.size() + " vulnerabilities : " + record.getMetadata().getReport() );
        }
        return null;
    }
}
