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
import com.redhat.engineering.srcclr.json.SourceClearJSON;
import com.redhat.engineering.srcclr.json.Vulnerability;
import com.redhat.engineering.srcclr.utils.InternalException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface ScanResult
{
    Pattern pattern = Pattern.compile( "(/records/0/libraries/)([0-9]+)(.*)");

    ArrayList<Vulnerability> process( SrcClrWrapper parent, SourceClearJSON json ) throws InternalException;

    default Library locateLibrary ( List<Library> libs, Vulnerability vuln ) throws InternalException
    {
        // Every vulnerability item has a libraries with size 0 and a _links.
        Matcher matcher = pattern.matcher( vuln.getLibraries().get( 0 ).getLinks().getRef() );
        Library library = null;
        while ( matcher.find() )
        {
            library = libs.get( Integer.parseInt( matcher.group( 2 ) ) );
        }
        if ( library == null )
        {
            throw new InternalException( "Unable to locate library for vulnerability in output" + vuln );
        }
        return library;
    }
}
