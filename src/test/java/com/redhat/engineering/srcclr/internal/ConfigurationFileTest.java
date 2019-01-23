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

import com.redhat.engineering.srcclr.utils.ConfigurationFileProvider;
import org.jboss.byteman.contrib.bmunit.BMRule;
import org.jboss.byteman.contrib.bmunit.BMRules;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import picocli.CommandLine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

@RunWith(BMUnitRunner.class)
//@BMUnitConfig( debug = true, verbose = true)
public class ConfigurationFileTest
{
    private static final TemporaryFolder folder = new TemporaryFolder( );

    private static File userConfig;

    private static File systemConfig;

    @Rule
    public final ProvideSystemProperty overideHome = new ProvideSystemProperty( "user.home", UUID.randomUUID().toString() );

    @Before
    public void before() throws IOException
    {
        folder.create();
        systemConfig = folder.newFile();
        userConfig = folder.newFile();
    }

    @After
    public void after()
    {
        folder.delete();
    }

    @Test
    @BMRules( rules = {
                    @BMRule( name = "home-config-srcclr", targetClass = "com.redhat.engineering.srcclr.utils.ConfigurationFileProvider",
                                 targetMethod = "getConfig", targetLocation = "AT ENTRY", condition = "$1.contains(\".srcclr\")",
                                 action = "return com.redhat.engineering.srcclr.internal.ConfigurationFileTest.userConfig" ),
                    @BMRule( name = "global-config-srcclr", targetClass = "com.redhat.engineering.srcclr.utils.ConfigurationFileProvider",
                             targetMethod = "getConfig", targetLocation = "AT ENTRY", condition = "$1.contains(\"etc\")",
                             action = "return com.redhat.engineering.srcclr.internal.ConfigurationFileTest.systemConfig" )
    } )
    public void verifyUserConfig() throws IOException
    {
        Properties p = new Properties();
        p.setProperty("config", "utest");
        p.store( new FileOutputStream( userConfig ), null );

        ConfigurationFileProvider provider = new ConfigurationFileProvider();
        String x = provider.defaultValue( CommandLine.Model.OptionSpec.builder( new String[] { "-Dconfig" } ).build() );
        assert (x.equalsIgnoreCase( "utest" ));
    }



    @Test
    @BMRules( rules = {
                    @BMRule( name = "user-config-srcclr", targetClass = "com.redhat.engineering.srcclr.utils.ConfigurationFileProvider",
                                 targetMethod = "getConfig", targetLocation = "AT ENTRY", condition = "$1.contains(\".srcclr\")",
                                 action = "return com.redhat.engineering.srcclr.internal.ConfigurationFileTest.userConfig" ),
                    @BMRule( name = "global-config-srcclr", targetClass = "com.redhat.engineering.srcclr.utils.ConfigurationFileProvider",
                             targetMethod = "getConfig", targetLocation = "AT ENTRY", condition = "$1.contains(\"etc\")",
                             action = "return com.redhat.engineering.srcclr.internal.ConfigurationFileTest.systemConfig" )
    } )
    public void verifySystemConfig() throws IOException
    {
        userConfig.delete();
        Properties p = new Properties();
        p.setProperty("config", "stest");
        p.store( new FileOutputStream( systemConfig ), null );

        ConfigurationFileProvider provider = new ConfigurationFileProvider();
        String x = provider.defaultValue( CommandLine.Model.OptionSpec.builder( new String[] { "-Dconfig" } ).build() );
        assert (x.equalsIgnoreCase( "stest" ));
    }

    @Test
    @BMRules( rules = {
                    @BMRule( name = "user-config-srcclr", targetClass = "com.redhat.engineering.srcclr.utils.ConfigurationFileProvider",
                                 targetMethod = "getConfig", targetLocation = "AT ENTRY", condition = "$1.contains(\".srcclr\")",
                                 action = "return com.redhat.engineering.srcclr.internal.ConfigurationFileTest.userConfig" ),
                    @BMRule( name = "global-config-srcclr", targetClass = "com.redhat.engineering.srcclr.utils.ConfigurationFileProvider",
                             targetMethod = "getConfig", targetLocation = "AT ENTRY", condition = "$1.contains(\"etc\")",
                             action = "return com.redhat.engineering.srcclr.internal.ConfigurationFileTest.systemConfig" )
    } )
    public void verifyBothConfig() throws IOException
    {
        Properties p = new Properties();
        p.setProperty("config", "utest");
        p.store( new FileOutputStream( userConfig ), null );

        p = new Properties();
        p.setProperty("config", "stest");
        p.store( new FileOutputStream( systemConfig ), null );

        ConfigurationFileProvider provider = new ConfigurationFileProvider();
        String x = provider.defaultValue( CommandLine.Model.OptionSpec.builder( new String[] { "-Dconfig" } ).build() );
        assert (x.equalsIgnoreCase( "utest" ));
    }
}