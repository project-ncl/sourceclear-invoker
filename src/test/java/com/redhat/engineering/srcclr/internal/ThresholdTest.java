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

import com.redhat.engineering.srcclr.SCBase;
import com.redhat.engineering.srcclr.utils.SourceClearResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ThresholdTest extends SCBase
{
    private static final String SC = "sourceclear";

    @Rule
    public final RestoreSystemProperties systemProperties = new RestoreSystemProperties();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    @Rule
	public final SystemErrRule systemRule = new SystemErrRule().enableLog().muteForSuccessfulTests();

    @Rule
    public final ProvideSystemProperty overideHome = new ProvideSystemProperty( "user.home", UUID.randomUUID().toString() );

    @Test
    public void invalidThreshold1Test() throws Exception
    {
        System.setProperty( SC, "-t -1  scm" );

        SourceClearResult r = exeSC();

        assertFalse( r.isPass() );
        assertTrue( systemRule.getLog().contains( "Invalid CVSS Score parameter" ) );
    }

    @Test
    public void invalidThreshold2Test() throws Exception
    {
        System.setProperty( SC, "-t -11  scm" );

        SourceClearResult r = exeSC();

        assertFalse( r.isPass() );
        assertTrue( systemRule.getLog().contains( "Invalid CVSS Score parameter" ) );
    }
}