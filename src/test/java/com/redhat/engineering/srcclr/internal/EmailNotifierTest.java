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
import com.redhat.engineering.srcclr.json.sourceclear.Library;
import com.redhat.engineering.srcclr.json.sourceclear.Version;
import com.redhat.engineering.srcclr.json.sourceclear.Vulnerability;
import com.redhat.engineering.srcclr.notification.EmailNotifier;
import com.redhat.engineering.srcclr.notification.Notifier;
import com.redhat.engineering.srcclr.processor.ProcessorResult;
import com.redhat.engineering.srcclr.utils.InternalException;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class EmailNotifierTest
{
    private final int port = 25000;
    private Wiser wiser;

    @Rule
    public final SystemOutRule systemRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    @Before
    public void before()
    {
        wiser = Wiser.port(port);
        wiser.start();
    }

    @After
    public void after()
    {
        wiser.stop();
    }

    @Test
    public void testSendMail () throws IllegalAccessException, InternalException
    {
        SrcClrWrapper wrapper = new SrcClrWrapper();

        List<String> emailAddresses = new ArrayList<>( );
        emailAddresses.add( "test@test.com" );

        FieldUtils.writeField( wrapper, "product", "TEST PRODUCT", true );
        FieldUtils.writeField( wrapper, "emailAddresses", emailAddresses, true );
        FieldUtils.writeField( wrapper, "emailServer", "localhost", true );

        Notifier n = new EmailNotifier();
        FieldUtils.writeStaticField( EmailNotifier.class, "port", port, true );

        ProcessorResult pr = new ProcessorResult();
        Library l = new Library();
        List<Version> versions = new ArrayList<>(  );
        Version version = new Version();
        versions.add( version );
        l.setVersions( versions );
        Vulnerability v = new Vulnerability();
        Set<ProcessorResult> processorResults = new HashSet<>(  );
        v.setCve( "123456789" );
        v.setTitle( "Dummy Vulnerability" );
        pr.setMessage("No CPE exist");
        pr.setVulnerability( v );
        pr.setLibrary( l );
       
        processorResults.add( pr );
        n.notify( wrapper, "", processorResults );

        List<WiserMessage> messages = wiser.getMessages();

        assertEquals( 1, messages.size() );
        for ( WiserMessage wm : messages )
        {
//            System.out.println ("### Got message " + wm.toString());
            assertTrue ( wm.toString().contains( "TEST PRODUCT" ));
            assertTrue ( wm.toString().contains( "CVE-123456789" ));
        }
    }

    @Test
    public void testSendMultipleMail () throws IllegalAccessException, InternalException
    {
        SrcClrWrapper wrapper = new SrcClrWrapper();

        List<String> emailAddresses = new ArrayList<>( );
        emailAddresses.add( "test@test.com" );
        emailAddresses.add( "anotherperson@test.com" );

        FieldUtils.writeField( wrapper, "product", "TEST PRODUCT", true );
        FieldUtils.writeField( wrapper, "emailAddresses", emailAddresses, true );
        FieldUtils.writeField( wrapper, "emailServer", "localhost", true );

        Notifier n = new EmailNotifier();
        FieldUtils.writeStaticField( EmailNotifier.class, "port", port, true );

        ProcessorResult pr = new ProcessorResult();
        Library l = new Library();
        List<Version> versions = new ArrayList<>(  );
        Version version = new Version();
        versions.add( version );
        l.setVersions( versions );
        Vulnerability v = new Vulnerability();
        Set<ProcessorResult> processorResults = new HashSet<>(  );
        v.setCve( "123456789" );
        v.setTitle( "Dummy Vulnerability" );
        pr.setMessage("No CPE exist");
        pr.setVulnerability( v );
        pr.setLibrary( l );

        processorResults.add( pr );
        n.notify( wrapper, "", processorResults );

        List<WiserMessage> messages = wiser.getMessages();

        assertEquals( 2, messages.size() );
        for ( int i = 0 ; i < messages.size() ; i++ )
        {
            WiserMessage wm = messages.get( i );

            // System.out.println ("### Got message " + wm.toString());

            if ( i == 0 )
            {
                assertTrue( wm.toString().contains( "test@test.com" ) );
            }
            else
            {
                assertTrue( wm.toString().contains( "anotherperson@test.com" ) );
            }
            assertTrue ( wm.toString().contains( "TEST PRODUCT" ));
            assertTrue ( wm.toString().contains( "CVE-123456789" ));
        }
    }
}
