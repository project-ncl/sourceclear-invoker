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

import com.redhat.engineering.srcclr.utils.InternalException;
import com.redhat.engineering.srcclr.utils.PropertyHandler;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Test for PropertyHandler.
 */
public class PropertyHandlerTest
{
    @Test
    public void verifyPropertySplit() throws Exception
    {
        String[] result = PropertyHandler.convertProperty(
                        "-d binary -d --url=file:///home/user/foobar.jar \"--name=H2 Database\" --rev=8.0.18 --no-upload" );

        //System.out.println ("Got " + Arrays.toString(result));

        assertEquals( 7, result.length );
        assertEquals( "-d", result[2] );
        assertEquals( "\"--name=H2 Database\"", result[4] );
    }

    @Test( expected = InternalException.class )
    public void verifyPropertySplitNoCommand() throws Exception
    {
        PropertyHandler.convertProperty(
                        "-d --url=file:///home/user/foobar.jar --name=\"H2 Database\" --rev=8.0.18 --no-upload" );
    }

    @Test( expected = InternalException.class )
    public void verifyPropertySplitUnknownCommand() throws Exception
    {
        PropertyHandler.convertProperty(
                        "-d whatareyou --url=file:///home/user/foobar.jar --name=H2 --rev=8.0.18 --no-upload" );
    }

    @Test
    public void verifyPropertySplitNoFirstDebug() throws Exception
    {
        String[] result = PropertyHandler.convertProperty(
                        "scm -d --url=file:///home/user/foobar.jar --name=\"H2 Database\" --rev=8.0.18 --no-upload" );
        assertEquals( 6, result.length );
        assertEquals( "-d", result[1] );
        assertEquals( "--name=\"H2 Database\"", result[3] );
    }

    @Test
    public void verifyPropertySplitOnlyCommand() throws Exception
    {
        PropertyHandler.convertProperty( "scm" );
    }

    @Test
    public void verifyPropertySplitScm() throws Exception
    {
        String[] result = PropertyHandler.convertProperty( "-d scm -d --url=http://www.dummy.com  --ref=8.0.18" );

        // System.out.println ("Got " + Arrays.toString( result));

        assertEquals( 5, result.length );
        assertEquals( "-d", result[2] );
        assertEquals( "--url=http://www.dummy.com", result[3] );
    }

    @Test
    public void verifyPropertySplitScmNoExtraWhitespace() throws Exception
    {
        String[] result = PropertyHandler.convertProperty( "-d scm -d --url=http://www.dummy.com --ref=8.0.18" );

        //System.out.println ("Got " + Arrays.toString( result));

        assertEquals( 5, result.length );
        assertEquals( "-d", result[2] );
        assertEquals( "--url=http://www.dummy.com", result[3] );
    }

    @Test
    public void verifyPropertySplitWithQuotingScm() throws Exception
    {
        String[] result = PropertyHandler.convertProperty( "-d scm --maven-param=\"foobar -Pbar\" --ref" );

        //System.out.println ("Got " + Arrays.toString( result));

        assertEquals( 4, result.length );
        assertEquals( "-d", result[0] );
        assertEquals( "--maven-param=\"foobar -Pbar\"", result[2] );
        assertEquals( "--ref", result[3] );
    }

    @Test
    public void verifyPropertySplitWithQuoting2Scm() throws Exception
    {
        String[] result = PropertyHandler.convertProperty( "-d scm --maven-param=\"-DskipTests=true   -Dfoo  -Pbar\" --ref" );

        //System.out.println ("Got " + Arrays.toString( result));

        assertEquals( 4, result.length );
        assertEquals( "-d", result[0] );
        assertEquals( "--maven-param=\"-DskipTests=true   -Dfoo  -Pbar\"", result[2] );
        assertEquals( "--ref", result[3] );
    }
}
