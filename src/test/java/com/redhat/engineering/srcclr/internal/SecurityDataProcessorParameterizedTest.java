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
import com.redhat.engineering.srcclr.processor.ProcessorResult;
import com.redhat.engineering.srcclr.processor.SecurityDataProcessor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
public class SecurityDataProcessorParameterizedTest
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Rule
    public final SystemOutRule systemRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().httpsPort(8089));

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
        HostnameVerifier allHostsValid = ( hostname, session ) -> true;

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                 { "cpe:/a:redhat:jboss_data_grid:7", false, "" }, // fixed_state: will not fix
                 { "cpe:/a:redhat:openshift_application_runtimes:1.0", false, "" },  // fixed_state: not affected
                 { "arbitrary_cpe", true, "No cpe exists" },
                 {"cpe:/a:redhat:jboss_operations_network:3", true, "fixed_state is Affected"},
                 {"cpe:/a:redhat:jboss_data_virtualization:6", true, "fixed_state is New"}
           });
    }

    private String cpeInput;
    private Boolean bExpected;
    private String msgExpected;

    public SecurityDataProcessorParameterizedTest(String cpe, Boolean to_fail, String msg ) {
        this.cpeInput = cpe;
        this.bExpected = to_fail;
        this.msgExpected = msg;
    }

    @Test
    public void test() throws Exception {
        ProcessorResult sdpr = paramTestHelper();
        assertEquals(bExpected, sdpr.getFail());

        if ( sdpr.getFail() )
        {
            assertEquals(msgExpected, sdpr.getMessage());
        }
    }

    private ProcessorResult paramTestHelper() throws Exception
    {
        givenThat(get(urlEqualTo("/CVE-mock"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBodyFile("security_data_processor_test/cve-2017-7536-multi-rhoar.json")));

        SecurityDataProcessor sdp = new SecurityDataProcessor( cpeInput, "https://localhost:8089/" );

        ProcessorResult sdpr = sdp.process( "CVE-mock");
        logger.info("to_notify {}, to_fail {}", sdpr.getNotify(), sdpr.getFail());
        if (sdpr.getFail())
        {
            logger.info("message: {}", sdpr.getMessage());
        }

        return sdpr;
    }
}
