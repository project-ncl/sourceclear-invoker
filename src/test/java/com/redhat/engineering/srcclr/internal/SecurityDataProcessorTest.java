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

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import com.redhat.engineering.srcclr.SrcClrWrapper;
import com.redhat.engineering.srcclr.json.securitydata.SecurityDataJSON;
import com.redhat.engineering.srcclr.processor.ProcessorResult;
import com.redhat.engineering.srcclr.processor.SecurityDataProcessor;

import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.cert.X509Certificate;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SecurityDataProcessorTest
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Rule
    public final SystemOutRule systemRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().httpsPort(8089)); 
    private final String mock_url = "https://localhost:8089/";

    @Rule
    public TestRule watcher = new TestWatcher() {
         protected void starting(Description description) {
            logger.info("Starting test: <<< {} >>>>", description.getMethodName());
         }
    };
    

    @Before
    public void ignoreCert() throws Exception
    {
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    @Test
    // test where packageState and affectRelease don't exist 
    public void fieldNullTest() throws Exception
    {
        givenThat(get(urlEqualTo("/null"))
                .willReturn(aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBodyFile("security_data_processor_test/fields_null.json")));

        SecurityDataProcessor sdp = new SecurityDataProcessor("anycpe", mock_url);
        SecurityDataJSON json = (SecurityDataJSON)executeMethod( sdp, "lookUpAPI", new Object [] { "null" });

        logger.info(json.toString());

        assertTrue(json.getPackageState() == null);
        assertTrue(json.getAffectedRelease() == null);
    }

    @Test(expected = FileNotFoundException.class)
    public void invalidLookUpTest() throws Exception
    {
        String cve_id = "CVE-nonexistent"; // non-existing cve id

        SecurityDataProcessor sdp = new SecurityDataProcessor("anycpe");

        executeMethod( sdp, "lookUpAPI", new Object [] { cve_id });
    }   

    @Test
    public void packageTest() throws Exception
    {
        // test with cve-2017-7536-multi-rhoar.json
        givenThat(get(urlEqualTo("/CVE-mock"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBodyFile("security_data_processor_test/cve-2017-7536-multi-rhoar.json")));

        String cpe = "cpe:/a:redhat:openshift_application_runtimes:1.0";
        String package_affected = "swarm";
        String package_notaffected = "springboot";

        SrcClrWrapper wrapper = new SrcClrWrapper();

        FieldUtils.writeField( wrapper, "product", cpe, true );

        SecurityDataProcessor sdp = new SecurityDataProcessor(wrapper.getProduct(), mock_url);

        // Test 1: swarm
        FieldUtils.writeField( wrapper, "packageName", package_affected, true );
        sdp.setPackageName(wrapper.getPackageName());

        ProcessorResult sdpr = sdp.process("CVE-mock");

        assertEquals("fixed_state is affected", sdpr.getMessage());
        assertEquals(true, sdpr.getFail() );

        // Test 2: springboot 
        FieldUtils.writeField( wrapper, "packageName", package_notaffected, true );
        sdp.setPackageName(wrapper.getPackageName());

        sdpr = sdp.process("CVE-mock");

        assertEquals(false, sdpr.getFail() );
        
    }

    @Test
    public void packageEmptyTest() throws Exception
    {
        // test with cve-2017-7536-multi-rhoar.json
        givenThat(get(urlEqualTo("/CVE-mock"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBodyFile("security_data_processor_test/cve-2017-7536-multi-rhoar.json")));

        SrcClrWrapper wrapper = new SrcClrWrapper();

        FieldUtils.writeField( wrapper, "product", "cpe:/a:redhat:jboss_fuse:7", true );
        
        SecurityDataProcessor sdp = new SecurityDataProcessor(wrapper.getProduct(), mock_url);
        
        // both tests should PASS by finding the cpe's info when package is not set
        // TEST 1: when not calling setPackageName()
        ProcessorResult sdpr = sdp.process("CVE-mock");
        assertEquals("fixed_state is affected", sdpr.getMessage());
        assertEquals(true, sdpr.getFail() );

        // TEST 2: when package is empty
        FieldUtils.writeField( wrapper, "packageName", "", true );
        sdp.setPackageName(wrapper.getPackageName());

        sdpr = sdp.process("CVE-mock");
        assertEquals("fixed_state is affected", sdpr.getMessage());
        assertEquals(true, sdpr.getFail() );

    }
    
    @Test
    public void failByNoCVETest() throws Exception
    {
        // need test this case with real API rather than using mock 

        String cpe="cpe:/a:redhat:openshift_application_runtimes:1.0";
        String cve_id="CVE-nonexistent";
        
        SecurityDataProcessor sdp = new SecurityDataProcessor(cpe);

        ProcessorResult sdpr = sdp.process( cve_id);

        assertTrue( sdpr.getFail() );
        assertEquals("No CVE data in security data API", sdpr.getMessage());
    }


    /**
     * Executes a method on an object instance.  The name and parameters of
     * the method are specified.  The method will be executed and the value
     * of it returned, even if the method would have private or protected access.
     */
    private Object executeMethod( Object instance, String name, Object[] params ) throws Exception
    {
        Class<?> c = instance.getClass();

        // Fetch the Class types of all method parameters
        Class[] types = new Class[params.length];

        for ( int i = 0; i < params.length; i++ )
            types[i] = params[i].getClass();

        Method m = c.getDeclaredMethod( name, types );
        m.setAccessible( true );

        try
        {
            return m.invoke( instance, params );
        }
        catch( InvocationTargetException e )
        {
            throw (Exception)e.getCause();
        }
    }
}
