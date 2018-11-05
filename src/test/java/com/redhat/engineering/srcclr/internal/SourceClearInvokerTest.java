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

import com.redhat.engineering.srcclr.SourceClearTest;
import com.redhat.engineering.srcclr.utils.ScanException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.io.FileNotFoundException;
import java.util.UUID;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Test for Jenkins interface.
 */
public class SourceClearInvokerTest
{
    private static final String SC = "sourceclear";

    private final SourceClearTest wrapper = new SourceClearTest();

    @Rule
    public final SystemOutRule systemRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    @Rule
	public final ProvideSystemProperty overideHome = new ProvideSystemProperty("user.home", UUID.randomUUID().toString() );

    @Test( expected = FileNotFoundException.class )
    public void runBinarySC1() throws Exception
    {
        try
        {
            System.setProperty( SC,
                                "-d binary --url=file:///home/user/foobar.jar --name=H2 Database --rev=8.0.18 --no-upload" );
            wrapper.runSourceClear();
        }
        finally
        {
            System.clearProperty( SC );
        }
    }

    @Test(expected = ScanException.class)
    public void runBinarySC2() throws Exception
    {
        try
        {
            System.setProperty( SC,
                                "-d binary --url=http://central.maven.org/maven2/commons-io/commons-io/2.1/commons-io-2.1.jar --name=CommonsIO --rev=2.1 --no-upload" );
            wrapper.runSourceClear();
        }
        finally
        {
            System.clearProperty( SC );
        }
    }


    @Test
    public void runThresholdSC() throws Exception
    {
        try
        {
            System.setProperty( SC,
                                "-t 8 scm --url=https://github.com/srcclr/example-java-maven.git --ref=a4c94e9 --no-upload" );
            wrapper.runSourceClear();
        }
        finally
        {
            System.clearProperty( SC );
        }
    }

    @Test( expected = ScanException.class )
    public void runScanFailureSC() throws Exception
    {
        try
        {
            System.setProperty( SC,
                                "scm --url=https://github.com/srcclr/example-java-maven.git --ref= --no-upload" );
            wrapper.runSourceClear();
        }
        finally
        {
            System.clearProperty( SC );
        }
        assertTrue( systemRule.getLog().contains( "score 7.5" ) );
    }

    @Test( expected = ScanException.class )
    public void runScanFailureSC_CVE() throws Exception
    {
        try
        {
            System.setProperty( SC,
                                "-p cve scm --url=https://github.com/srcclr/example-java-maven.git --ref= --no-upload" );
            wrapper.runSourceClear();
        }
        finally
        {
            System.clearProperty( SC );
        }
        assertFalse( systemRule.getLog().contains( "score 7.5" ) );
        assertTrue( systemRule.getLog().contains( "2017-2646" ) );
    }
}
