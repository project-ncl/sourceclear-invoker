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
import com.redhat.engineering.srcclr.notification.LogFileNotifier;
import com.redhat.engineering.srcclr.notification.Notifier;
import com.redhat.engineering.srcclr.processor.ProcessorResult;
import com.redhat.engineering.srcclr.utils.InternalException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;

public class LogFileNotifierTest
{
    @Rule
    public final SystemOutRule systemRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder( );


    @Test
    public void testWriteLogFile () throws IllegalAccessException, InternalException, IOException
    {
        SrcClrWrapper wrapper = new SrcClrWrapper();
        Notifier n = new LogFileNotifier();

        FieldUtils.writeField( wrapper, "product", "TEST PRODUCT", true );

        File newTarget = new File(folder.newFolder(), FieldUtils.readDeclaredField( n, "target", true ).toString());

        FieldUtils.writeField( n, "target", newTarget, true );

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

        assertTrue( FileUtils.readFileToString( newTarget, Charset.defaultCharset()).contains( "Located 1 possible vulnerability within product TEST PRODUCT" ) );
        assertTrue( newTarget.toString().contains( "target" ) );
    }
}
