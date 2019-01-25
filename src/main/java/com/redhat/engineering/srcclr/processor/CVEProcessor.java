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
package com.redhat.engineering.srcclr.processor;

import com.redhat.engineering.srcclr.SrcClrWrapper;
import com.redhat.engineering.srcclr.json.sourceclear.Library;
import com.redhat.engineering.srcclr.json.sourceclear.Record;
import com.redhat.engineering.srcclr.json.sourceclear.SourceClearJSON;
import com.redhat.engineering.srcclr.json.sourceclear.Vulnerability;
import com.redhat.engineering.srcclr.utils.InternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class CVEProcessor
                implements ScanResult
{
    private final Logger logger = LoggerFactory.getLogger( CVEProcessor.class );

    @Override
    public Set<ProcessorResult> process( SrcClrWrapper parent, SourceClearJSON json ) throws InternalException
    {
        Record record = json.getRecords().get( 0 );
        List<Library> libs = record.getLibraries();
        Set<ProcessorResult> matched = new HashSet<>( );
        SecurityDataProcessor securityDataProcessor = new SecurityDataProcessor( parent.getProduct() );
        if ( isNotEmpty(parent.getPackageName()) )
        {
            securityDataProcessor.setPackageName(parent.getPackageName());
        }

        for ( Vulnerability vuln : record.getVulnerabilities() )
        {
            Library library = locateLibrary( libs, vuln );

            if ( isNotEmpty ( vuln.getCve() ) )
            {
                ProcessorResult processorResult = securityDataProcessor.process( vuln.getCve() );
                processorResult.setVulnerability (vuln);
                processorResult.setLibrary (library);
                processorResult.setScanReport( record.getMetadata() );
                matched.add( processorResult );

                logger.info ( "Found vulnerability '{}' with CVE ID {} in library {}:{}:{}",
                              vuln.getTitle(), vuln.getCve(), library.getCoordinate1(),
                              library.getCoordinate2(), library.getVersions().get( 0 ).getVersion()
                );
            }
            else
            {
                logger.warn( "Potential vulnerability without a CVE with CVSS score of {} found as {} in library {}:{}:{}",
                             vuln.getCvssScore(), vuln.getTitle(), library.getCoordinate1(),
                             library.getCoordinate2(), library.getVersions().get( 0 ).getVersion() );
            }
        }
        if ( matched.size() > 0 )
        {
            matched.stream().findAny().ifPresent( p -> logger.info ("Report is {}\n", p.getScanReport() ) );
        }
        return matched;
    }

    @Override
    public String toString()
    {
        return "CVE Processor";
    }
}
