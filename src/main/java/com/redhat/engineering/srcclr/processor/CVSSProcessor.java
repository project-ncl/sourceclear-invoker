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
import com.redhat.engineering.srcclr.json.Library;
import com.redhat.engineering.srcclr.json.Record;
import com.redhat.engineering.srcclr.json.SourceClearJSON;
import com.redhat.engineering.srcclr.json.Vulnerability;
import com.redhat.engineering.srcclr.utils.InternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CVSSProcessor implements ScanResult
{
    private final Logger logger = LoggerFactory.getLogger( CVSSProcessor.class );

    @Override
    public ArrayList<Vulnerability> process( SrcClrWrapper parent, SourceClearJSON json ) throws InternalException
    {
        Record record = json.getRecords().get( 0 );
        List<Library> libs = record.getLibraries();
        ArrayList<Vulnerability> matched = new ArrayList<>( );

        for ( Vulnerability vuln : record.getVulnerabilities() )
        {
            Library library = locateLibrary( libs, vuln );

            if ( vuln.getCvssScore() >= parent.getThreshold() )
            {
                matched.add( vuln );
                logger.info ( "Found vulnerability '{}' with score {} in library {}:{}:{} and report is {}",
                              vuln.getTitle(), vuln.getCvssScore(), library.getCoordinate1(),
                              library.getCoordinate2(), library.getVersions().get( 0 ).getVersion(),
                              record.getMetadata().getReport()
                );
            }
        }
        return matched;
    }

    @Override
    public String toString()
    {
        return "CVSS Processor";
    }
}
