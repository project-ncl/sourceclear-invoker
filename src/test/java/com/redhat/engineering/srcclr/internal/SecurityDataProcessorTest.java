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

import com.redhat.engineering.srcclr.json.securitydata.SecurityDataJSON;
import com.redhat.engineering.srcclr.processor.SecurityDataProcessor;
import com.redhat.engineering.srcclr.processor.SecurityDataProcessorResult;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.junit.contrib.java.lang.system.SystemErrRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;



public class SecurityDataProcessorTest
{
    @Rule 
    public MethodRule watchman = new TestWatchman() {
        public void starting(FrameworkMethod method) {
          logger.info("<<< {} >>> being run...", method.getName());
        }
      };

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    

    private SecurityDataProcessorResult processTestHelper(String cve_id) throws Exception
    {
        String cpe="cpe:/a:redhat:openshift_application_runtimes:1.0";

        SecurityDataProcessor sdp = new SecurityDataProcessor(cpe);

        SecurityDataProcessorResult sdpr = sdp.process(cve_id);
        logger.info("to_notify {}, to_fail {}", sdpr.isToNotify(), sdpr.isToFail());
        if (sdpr.isToFail())
        {
            logger.info("message: {}", sdpr.getMessage());
        }   

        return sdpr;
    }


    @Test
    public void validLookUpTest() throws Exception
    {
            
        String cve_id = "CVE-2016-6346";

        SecurityDataProcessor sdp = new SecurityDataProcessor("anycpe");

        SecurityDataJSON json = sdp.lookUpAPI(cve_id);

        logger.info("ID {}", json.getName().toString());

        logger.info("package_state {}", json.getPackageState().get(0).getCpe());
      
        assertTrue( true );
    }

    @Test
    public void invalidLookUpTest() throws Exception
    {
            
        String cve_id = "CVE-2016-63461";

        SecurityDataProcessor sdp = new SecurityDataProcessor("anycpe");

        try {
            SecurityDataJSON json = sdp.lookUpAPI(cve_id);
            logger.info("json {}", json.toString());
        } catch (FileNotFoundException e)  {
            logger.info("catched exception. {}", e.toString());
        }
        
        assertTrue( true );
    }

    
    @Test
    public void failByNoCPETest() throws Exception
    {
        // this test won't be able to use anymore, once CVE-2016-6346 security data is updated 
        String cve_id = "CVE-2016-6346";

        SecurityDataProcessorResult sdpr = processTestHelper(cve_id);

        sdpr.isToFail();

        assertEquals( true, sdpr.isToFail() );
    }

    @Test
    public void failByAffectedReleaseTest() throws Exception
    {
        // this test won't be able to use anymore, once CVE-2018-10327 security data is updated 
        String cve_id = "CVE-2018-10237";

        SecurityDataProcessorResult sdpr = processTestHelper(cve_id);

        sdpr.isToFail();

        assertEquals( true, sdpr.isToFail() );
    }

    @Test
    public void failByNoCVETest() throws Exception
    {
        String cve_id = "CVE-2018-102371";

        SecurityDataProcessorResult sdpr = processTestHelper(cve_id);

        sdpr.isToFail();

        assertEquals( true, sdpr.isToFail() );
    }

    public void processInputCVETest() throws Exception
    {
        // To test this:
        //  $ mvn -Dtest=SecurityDataProcessorTest#processInputCVETest -Dcve=CVE-2018-10237 

        String cve_id = "CVE-2018-10237"; // default
        
        cve_id=String.valueOf(System.getProperty("cve"));
        SecurityDataProcessorResult sdpr = processTestHelper(cve_id);

        assert(true);
        
    }
}