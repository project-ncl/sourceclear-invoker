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

import com.redhat.engineering.srcclr.SrcClrWrapper;
import com.redhat.engineering.srcclr.utils.InternalException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;
import org.junit.contrib.java.lang.system.SystemErrRule;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class ThresholdTest
{
    @Rule
	public final SystemErrRule systemRule = new SystemErrRule().enableLog().muteForSuccessfulTests();

    @Rule
    public final ProvideSystemProperty overideHome = new ProvideSystemProperty( "user.home", UUID.randomUUID().toString() );

    @Test(expected = InternalException.class )
    public void invalidThreshold1Test() throws Exception
    {
        SrcClrWrapper.main( new String [] { "-t", "-1", "scm" } );

        assertTrue( systemRule.getLog().contains( "Invalid CVSS Score parameter" ) );
    }

    @Test(expected = InternalException.class)
    public void invalidThreshold2Test() throws Exception
    {
        SrcClrWrapper.main( new String [] { "-t", "11", "scm" } );

        assertTrue( systemRule.getLog().contains( "Invalid CVSS Score parameter" ) );
    }
}