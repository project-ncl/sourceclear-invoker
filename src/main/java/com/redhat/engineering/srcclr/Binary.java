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
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.goots.exploder.Exploder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;
import static picocli.CommandLine.ParentCommand;
import static picocli.CommandLine.Unmatched;

@Command(name = "binary", description = "Scan a remote binary", mixinStandardHelpOptions = true )
public class Binary implements Callable<Void>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Option( names = { "-d", "--debug" }, description = "Enable debug." )
    boolean debug;

    @Option(names = { "--url" }, required = true, paramLabel = "URL", description = "the remote file url")
    String url;

    @Option(names = { "--name" }, required = true, paramLabel = "NAME", description = "Name of binary")
    String name;

    @Option(names = { "--rev" }, required = true, paramLabel = "REV", description = "Version of the binary")
    String rev;

    @Unmatched
    List<String> unmatched;

    @ParentCommand
    SrcClrWrapper parent; // picocli injects reference to parent command

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Void call() throws Exception
    {
        if ( debug || parent.isDebug() )
        {
            parent.enableDebug();
        }

        Path urlDownloadLocation = Files.createTempDirectory( "sourceclear-remote-cache-" );
        Path temporaryLocation = Files.createTempDirectory( "sourceclear-unpack-" );
        try
        {
            URL processedUrl = new URL( url );
            File target = new File( urlDownloadLocation.toFile(), FilenameUtils.getName( processedUrl.getPath() ) );

            if ( name.contains( " " ) )
            {
                logger.warn ("Replace whitespace with '-' in {}", name);
                name = name.replace( ' ', '-' );
            }
            logger.debug( "Created temporary as {} and downloading {} to {} using temporary directory of {}",
                          urlDownloadLocation, processedUrl, target, temporaryLocation);

            // TODO : Implement concurrent axel/aria2c downloader
            FileUtils.copyURLToFile( processedUrl, target );

            // Don't attempt to unpack if we have a single jar file.
            if ( ! target.getName().endsWith( ArchiveStreamFactory.JAR ) )
            {
                // Unpack the target ready for a scan.
                final Exploder exploder = new Exploder().excludeSuffix( ArchiveStreamFactory.JAR ).useTargetDirectory( temporaryLocation.toFile() );
                exploder.unpack( target );
            }
            else
            {
                FileUtils.copyFileToDirectory( target, temporaryLocation.toFile() );
            }
            Map<String,String> env = new HashMap<>(  );
            env.put( "SRCCLR_SCM_NAME", name );

            List<String> args = new ArrayList<>();
            args.add ( "--scm-rev" );
            args.add ( rev );
            args.add ( "--scm-ref" );
            args.add ( "tag" );
            args.add ( "--scm-uri" );
            args.add ( "data://" + name );
            //args.add ( "--scm-ref-type" );
            //args.add ( "tag" );

            if ( unmatched != null )
            {
                logger.debug( "Unmatched is {} ", unmatched );
                args.addAll( unmatched );
            }
            // Add target folder
            args.add( temporaryLocation.toFile().getAbsolutePath() );

            SourceClearJSON json = new SrcClrInvoker().execSourceClear( SrcClrInvoker.ScanType.BINARY, env, args );
            Set<ProcessorResult> matched = parent.getProcessor().process( parent, json );
            Record record = json.getRecords().get( 0 );
            if ( parent.isException() && matched.size() > 0 )
            {
                parent.notifyListeners( this.toString(),
                                        matched.stream().filter( ProcessorResult::getNotify ).collect( Collectors.toSet()) );

                throw new ScanException( "Found " + matched.size() + " vulnerabilities : " +
                             ( record.getMetadata().getReport() == null ? "no-report-available" : record.getMetadata().getReport() ) );
            }
            return null;
        }
        finally
        {
            FileUtils.deleteDirectory( urlDownloadLocation.toFile() );
            FileUtils.deleteDirectory( temporaryLocation.toFile() );
        }
    }

    @Override
    public String toString()
    {
        return "scanning " + name + " version " + rev + " from " + url;
    }
}
