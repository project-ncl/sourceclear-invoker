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
import com.redhat.engineering.srcclr.processor.SecurityDataResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class SecurityDataProcessorTest
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );
    
    @Rule
    public TestRule watcher = new TestWatcher() {
         protected void starting(Description description) {
            logger.info("Starting test: <<< {} >>>>", description.getMethodName());
         }
    };
    

    private SecurityDataResult processTestHelper( String cve_id) throws Exception
    {
        String cpe="cpe:/a:redhat:openshift_application_runtimes:1.0";

        SecurityDataProcessor sdp = new SecurityDataProcessor(cpe);

        SecurityDataResult sdpr = sdp.process( cve_id);
        logger.info("to_notify {}, to_fail {}", sdpr.getNotify(), sdpr.getFail());
        if (sdpr.getFail())
        {
            logger.info("message: {}", sdpr.getMessage());
        }   

        return sdpr;
    }

    private SecurityDataResult processMultitestHelper( String cve_id) throws Exception
    {
        String cpe="cpe:/a:redhat:openshift_application_runtimes:1.0";

        SecurityDataProcessor sdp = new SecurityDataProcessor(cpe);

        SecurityDataResult sdpr = sdp.process( cve_id);
        logger.info("to_notify {}, to_fail {}", sdpr.getNotify(), sdpr.getFail());
        if (sdpr.getFail())
        {
            logger.info("message: {}", sdpr.getMessage());
        }   

        return sdpr;
    }


    @Test
    public void validLookUpTest() throws Exception
    {
            
        String cve_id = "2016-6346";

        SecurityDataProcessor sdp = new SecurityDataProcessor("anycpe");

        SecurityDataJSON json = (SecurityDataJSON)executeMethod( sdp, "lookUpAPI", new Object [] { cve_id });

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
            SecurityDataJSON json = (SecurityDataJSON)executeMethod( sdp, "lookUpAPI", new Object [] { cve_id });
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

        SecurityDataResult sdpr = processTestHelper( cve_id);

        sdpr.getFail();

        assertEquals( true, sdpr.getFail() );
    }

    @Test
    public void failByAffectedReleaseTest() throws Exception
    {
        // this test won't be able to use anymore, once CVE-2018-10327 security data is updated 
        String cve_id = "CVE-2018-10237";

        SecurityDataResult sdpr = processTestHelper( cve_id);

        sdpr.getFail();

        assertEquals( true, sdpr.getFail() );
    }

    @Test
    public void failByNoCVETest() throws Exception
    {
        String cve_id = "CVE-2018-102371";

        SecurityDataResult sdpr = processTestHelper( cve_id);

        sdpr.getFail();

        assertEquals( true, sdpr.getFail() );
    }



    @Test
    public void processInputCVETest() throws Exception
    {
        // To test this:
        //  $ mvn -Dtest=SecurityDataProcessorTest#processInputCVETest -Dcve=CVE-2018-10237 

        String cve_id = "CVE-2018-10237"; // default
        
        cve_id=String.valueOf(System.getProperty("cve"));
        SecurityDataResult sdpr = processTestHelper( cve_id);

        SecurityDataJSON json = (SecurityDataJSON) sdpr.getJson();

        if (json != null)
        {
           logger.info("package_state = {}", json.getPackageState()==null ? "null" : json.getPackageState().toString());
           logger.info("affected_release = {}", json.getAffectedRelease()==null ? "null" : json.getAffectedRelease().toString());
        }

        assertEquals( false, sdpr.getFail() );
        
    }

    @Test 
    public void failByNewFixedStateTest() throws Exception
    {
        String cve_id = "2018-11784";

        SecurityDataResult sdpr = processTestHelper( cve_id);

        assertTrue(sdpr.getFail());
        assertEquals("fixed_state is New", sdpr.getMessage());
    }


    @Test
    public void failByWillNotFixTest() throws Exception
    {
        String cve_id = "2018-17456";

        String cpe="cpe:/o:redhat:enterprise_linux:6";

        SecurityDataProcessor sdp = new SecurityDataProcessor(cpe);

        SecurityDataResult sdpr = sdp.process( cve_id);

        logger.info("to_notify {}, to_fail {}", sdpr.getNotify(), sdpr.getFail());
        if (sdpr.getFail())
        {
            logger.info("message: {}", sdpr.getMessage());
        }   

        // Will not fix - no blocking
        assertFalse(sdpr.getFail());
    }

    @Test
    public void failByNotAffectedTest() throws Exception
    {
        String cve_id = "2018-17456";

        String cpe="cpe:/a:redhat:mapc:4";

        SecurityDataProcessor sdp = new SecurityDataProcessor(cpe);

        SecurityDataResult sdpr = sdp.process( cve_id);

        logger.info("to_notify {}, to_fail {}", sdpr.getNotify(), sdpr.getFail());
        if (sdpr.getFail())
        {
            logger.info("message: {}", sdpr.getMessage());
        }   

        // not affected - no blocking
        assertFalse(sdpr.getFail());
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