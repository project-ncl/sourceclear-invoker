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
import com.redhat.engineering.srcclr.utils.InternalException;
import com.redhat.engineering.srcclr.utils.SourceClearResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

import java.nio.file.NoSuchFileException;
import java.util.UUID;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Test for Jenkins interface.
 */
public class SourceClearInvokerTest extends SCBase
{
    private static final String SC = "sourceclear";
    
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog().muteForSuccessfulTests();

    @Rule
    public final RestoreSystemProperties systemProperties = new RestoreSystemProperties();

    @Rule
    public final ProvideSystemProperty overideHome = new ProvideSystemProperty( "user.home", UUID.randomUUID().toString() );

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test( expected = InternalException.class )
    public void runParseFailure() throws Exception
    {
        System.setProperty( SC,
                            "-d binary --FOOBAR " );
        exeSC();
    }



    @Test( expected = NoSuchFileException.class )
    public void runBinarySC1() throws Exception
    {
        System.setProperty( SC,
                            "-p=product -d -v=8.0.18 binary --url=file:///home/user/foobar.jar --name=H2 Database --no-upload" );
        exeSC();
    }

    @Test
    public void runBinarySC2() throws Exception
    {
        System.setProperty( SC,
                            "--processor=cvss -p=product -d -v=2.1 binary --url=http://central.maven.org/maven2/commons-io/commons-io/2.1/commons-io-2.1.jar --no-upload" );
        SourceClearResult r = exeSC();

        assertFalse( r.isPass() );
    }

    @Test
    public void runBinarySC3() throws Exception
    {
        System.setProperty( SC,
                            "--processor=cvss -p=product -d -v=1.0 binary --url=http://central.maven.org/maven2/commons-io/commons-io/2.6/commons-io-2.6.jar --no-upload" );
        SourceClearResult r = exeSC();

        assertTrue( r.isPass() );
        assertTrue( systemOutRule.getLog().contains( "SRCCLR_SCM_NAME=product-1.0-commons-io-2.6.jar" ) );
    }

    @Test
    public void runBinarySC4() throws Exception
    {
        System.setProperty( SC,
                            "-t 10 -v=2.1 --product=PRODUCT --package=SUBPACKAGE -d binary --url=http://central.maven.org/maven2/commons-io/commons-io/2.1/commons-io-2.1.jar --no-upload" );
        SourceClearResult r = exeSC();

        assertTrue( r.isPass() );
        assertTrue( systemOutRule.getLog().contains( "SRCCLR_SCM_NAME=PRODUCT-2.1-SUBPACKAGE-commons-io-2.1.jar" ) );
    }

    @Test
    public void runThresholdSC() throws Exception
    {
        System.setProperty( SC,
                            "--processor=cvss -p=product -v=0 -t 8 scm --url=https://github.com/srcclr/example-java-maven.git --ref=a4c94e9 --no-upload" );
        SourceClearResult r = exeSC();
        assertTrue( r.isPass() );
    }

    @Test
    public void runScanFailureSC() throws Exception
    {
        System.setProperty( SC,
                            "-p=product --product-version=0 scm --url=https://github.com/srcclr/example-java-maven.git --ref= --no-upload" );

        SourceClearResult r = exeSC();
        assertFalse( r.isPass() );
        assertTrue( systemOutRule.getLog().contains( "CVE ID 2018-15531" ) );
        assertTrue( systemOutRule.getLog().contains( "GIT_URL=null" ) );
    }

    @Test
    public void verifyLocalSCMScan() throws Exception
    {
        System.setProperty( SC,
                            "--debug --processor=cvss -p=product -v=0 -t 8 scm --url=file://" +
                            getClass().getProtectionDomain().getCodeSource().getLocation().getFile() + "../.. --no-upload" );
        SourceClearResult r = exeSC();
        assertTrue( r.isPass() );
    }

    @Test
    public void verifyLocal2SCMScan() throws Exception
    {
        System.setProperty( SC,
                            "--debug --processor=cvss -p=product -v=0 -t 8 scm --url=" +
                                            getClass().getProtectionDomain().getCodeSource().getLocation().getFile() + "../.. --no-upload" );
        SourceClearResult r = exeSC();
        assertTrue( r.isPass() );
    }

    @Test
    public void verifyLocalSCMCurrentDirectoryScan() throws Exception
    {
        System.setProperty( SC,
                            "--debug --processor=cvss -p=product -v=0 -t 8 scm --url=. --no-upload" );
        SourceClearResult r = exeSC();
        assertTrue( r.isPass() );
    }

    @Test
    public void runScanFailureSC_CVE() throws Exception
    {
        System.setProperty( SC,
                            "-v 0 -p cve scm --url=https://github.com/srcclr/example-java-maven.git --ref= --no-upload" );
        SourceClearResult r = exeSC();

        assertTrue( systemOutRule.getLog().contains( "CVE ID 2018-15531" ) );
        assertTrue( systemOutRule.getLog().contains( "2017-2646" ) );
        assertFalse( r.isPass() );
    }

    @Test
    public void verifyTrace() throws Exception
    {
        System.setProperty( SC,
                            "--trace --processor=cvss -p=product -v=0 -t 8 scm --url=https://github.com/srcclr/example-java-maven.git --ref=a4c94e9 --no-upload" );
        SourceClearResult r = exeSC();

        assertTrue( r.isPass() );
        assertTrue( systemOutRule.getLog().contains( "com.sourceclear.agent.services.ScanServiceImpl" ) );
    }

    @Test
    public void runScmGoScan() throws Exception
    {
        System.setProperty( SC,
                            "--trace --processor=cvss -p=product -v=0 -t 8 scm --url=https://github.com/srcclr/example-go-glide --ref=96b1262 --no-upload" );
        SourceClearResult r = exeSC();

        assertTrue( r.isPass() );
        assertTrue( systemOutRule.getLog().contains( "2016-9121" ) );
        assertTrue( systemOutRule.getLog().contains( "GOPATH" ) );
    }

    @Test
    public void runScanFailureExtraMVNParamsSC() throws Exception
    {
        System.setProperty( SC,
                            "--trace -p=product --product-version=0 scm --maven-param=-Pnot-exist --url=https://github.com/srcclr/example-java-maven.git --ref= --no-upload" );

        SourceClearResult r = exeSC();
        assertFalse( r.isPass() );
        assertTrue( systemOutRule.getLog().contains( "[WARNING] The requested profile \"not-exist\" could not be activated because it does not exist." ) );
    }

    @Test
    public void runScanFailureExtraMVN2ParamsSC() throws Exception
    {
        System.setProperty( SC,
                            "--trace -p=product --product-version=0 scm --maven-param=\"-Pnot-exist -Palso-not-exist\" --url=https://github.com/srcclr/example-java-maven.git --ref= --no-upload" );

        SourceClearResult r = exeSC();
        assertFalse( r.isPass() );
        assertTrue( systemOutRule.getLog().contains( "[WARNING] The requested profile \"not-exist\" could not be activated because it does not exist." ) );
    }
}
