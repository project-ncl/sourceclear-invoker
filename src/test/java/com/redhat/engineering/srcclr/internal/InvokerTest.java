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
package com.redhat.engineering.srcclr.internal;

import com.redhat.engineering.srcclr.SrcClrInvoker;
import com.redhat.engineering.srcclr.utils.InternalException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InvokerTest
{
    private final SrcClrInvoker srccr = new SrcClrInvoker( false );

    @Rule
    public final SystemOutRule systemRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    @Test
    public void verifylocatingsrcclTest() throws IOException
    {
        Path result = srccr.locateSourceClearJar();
        assertTrue( result.toFile().exists() );
    }

    @Test
    public void verifylocatingsrccljvmTest() throws IOException
    {
        String result = srccr.locateSourceClearJRE();
        assertTrue( new File( result ).exists() );
    }

    @Test
    public void execSCTest() throws IOException, InternalException
    {
        srccr.execSourceClear( SrcClrInvoker.ScanType.OTHER, new HashMap<>(  ), Collections.singletonList( "--version" ) );
    }

    @Test
    public void testStringSplit()
    {
        Pattern p = Pattern.compile( "(/records/0/libraries/)([0-9]+)(.*)" );
        Matcher matcher = p.matcher( "/records/0/libraries/6/versions/0" );
        String result = null;
        while ( matcher.find() )
        {
            result = matcher.group( 2 );
        }
        assertEquals( "6", result );

        String sourceclear = "--url=https://github.com/release-engineering/koji-build-finder.git --ref=v1.0.";
        String[] arguments = sourceclear.trim().split( "\\s+|=" );
        assertEquals( 4, arguments.length );
    }


    @Test
    public void testStringSplit2() throws InternalException
    {
        String output = "AN ERROR! \r\n {\r\n  \"metadata\" : "
                        + "{\r\n    \"requestDate\" : \"2019-03-14T10:41:26.309+0000\"\r\n  },\r\n  \"records\" : [ "
                        + "{\r\n    \"metadata\" : {\r\n      \"recordType\" : \"SCAN\",\r\n      \"report\" : ";

        SrcClrInvoker si = new SrcClrInvoker( false );

        String result = si.stripInvalidOutput( false, output );

        assertTrue ( systemRule.getLog().contains( "ERROR" ) );
        assertFalse( result.contains( "ERROR" ) );
    }
}