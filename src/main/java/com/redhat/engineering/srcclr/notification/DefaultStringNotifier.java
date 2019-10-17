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
package com.redhat.engineering.srcclr.notification;

import com.redhat.engineering.srcclr.SrcClrWrapper;
import com.redhat.engineering.srcclr.processor.ProcessorResult;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class DefaultStringNotifier implements Notifier
{
    String toString( SrcClrWrapper parent, String scanInfo, Set<ProcessorResult> processorResults )
    {
        // We know we have at least one result ; use it to extra the link to the full scan results.
        @SuppressWarnings( "OptionalGetWithoutIsPresent" )
        ProcessorResult first = processorResults.stream().findFirst().get();

        StringBuffer sb = new StringBuffer( "Located " )
                        .append( processorResults.size() )
                        .append( " possible vulnerabilit" )
                        .append( processorResults.size() > 1 ? "ies" : "y" )
                        .append( " within product " )
                        .append( parent.getProduct() )
                        .append( System.lineSeparator() )
                        .append( scanInfo )
                        .append( System.lineSeparator() )
                        .append( "and full scan result is " )
                        .append( first.getScanReport() )
                        .append( System.lineSeparator() )
                        .append( System.lineSeparator() );
        processorResults.forEach( pRes -> sb.append( "In library " )
                                            .append ( pRes.getLibrary().getCoordinate1() )
                                            .append( ':' )
                                            .append ( pRes.getLibrary().getCoordinate2() )
                                            .append( ':' )
                                            .append ( pRes.getLibrary().getVersions().get( 0 ).getVersion() )
                                            .append( System.lineSeparator() )
                                            .append( "CVE-" )
                                            .append( pRes.getVulnerability().getCve() )
                                            .append( System.lineSeparator() )
                                            .append ( "Vulnerability is " )
                                            .append( pRes.getVulnerability().getTitle() )
                                            .append( System.lineSeparator() )
                                            .append( "Original SourceClear warning: " )
                                            .append( pRes.getVulnerability().getOverview() )
                                            .append( System.lineSeparator() )
                                            .append( pRes.getMessage() )
                                            .append( System.lineSeparator() )
                                            .append( "Original SourceClear version range " )
                                            // Every instance of Libraries/Details appears to be a valid size 1 list.
                                            // This test is to avoid layered construction within tests.
                                            .append( pRes.getVulnerability().getLibraries().size() == 0 ? "" : pRes.getVulnerability().getLibraries().get( 0 ).getDetails().get( 0 ).getVersionRange() )
                                            .append( System.lineSeparator() )
                                            .append( "Original SourceClear fixed version is " )
                                            .append( pRes.getVulnerability().getLibraries().size() == 0 ? "" : pRes.getVulnerability().getLibraries().get( 0 ).getDetails().get( 0 ).getUpdateToVersion() )
                                            .append( System.lineSeparator() )
                                            .append( System.lineSeparator() )
        );
        sb.append( System.lineSeparator() );

        sb.append( processorResults.stream().map ( r -> r.getVulnerability().toString() ).collect( Collectors.joining( System.lineSeparator())));

        return sb.toString();
    }

    @Override
    public String toString()
    {
        return this.getClass().getName();
    }
}
