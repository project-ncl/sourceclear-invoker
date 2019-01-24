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
package com.redhat.engineering.srcclr.utils;

import picocli.CommandLine;
import picocli.CommandLine.Model.OptionSpec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The ConfigurationFileProvider can look for configuration files in two known locations.
 * It can look in <code>$HOME/.srcclr/invoker.properties</code> and <code>/etc/srcclr/invoker.properties</code>
 * The home directory properties, if it exists, will override any global properties file.
 */
public class ConfigurationFileProvider
                implements CommandLine.IDefaultValueProvider
{
    private static final String HOME_CONFIG_FILE =
                    System.getProperty( "user.home" ) + File.separatorChar + ".srcclr" + File.separatorChar + "invoker.properties";

    private static final String ETC_CONFIG_FILE =
                    File.separatorChar + "etc" + File.separatorChar + "srcclr" + File.separatorChar + "invoker.properties";

    private Properties configuration = new Properties();

    private String targetConfig;

    public ConfigurationFileProvider()
    {
        File invokerHomeProperties = getConfig( HOME_CONFIG_FILE );
        File invokerEtcProperties = getConfig( ETC_CONFIG_FILE );

        if ( invokerHomeProperties.exists() )
        {
            try (FileInputStream props = new FileInputStream( invokerHomeProperties ))
            {
                configuration.load( props );
            }
            catch ( IOException e )
            {
                throw new CommandLine.PicocliException( "Unable to read properties file " + invokerHomeProperties, e );
            }
            targetConfig = invokerHomeProperties.toString();
        }
        else if ( invokerEtcProperties.exists() )
        {
            try (FileInputStream props = new FileInputStream( invokerEtcProperties ))
            {
                configuration.load( props );
            }
            catch ( IOException e )
            {
                throw new CommandLine.PicocliException( "Unable to read properties file " + invokerEtcProperties, e );
            }
            targetConfig = invokerEtcProperties.toString();
        }
    }

    /**
     * Returns the default value for an option or positional parameter or {@code null}.
     * The returned value is converted to the type of the option/positional parameter
     * via the same type converter used when populating this option/positional
     * parameter from a command line argument.
     * @param argSpec the option or positional parameter, never {@code null}
     * @return the default value for the option or positional parameter, or {@code null} if
     *       this provider has no default value for the specified option or positional parameter
     */
    @Override
    public String defaultValue( CommandLine.Model.ArgSpec argSpec )
    {
        if ( argSpec instanceof OptionSpec )
        {
            return configuration.getProperty( ( (OptionSpec) argSpec ).longestName().substring( 2 ) );
        }
        // Allow default value to be used.
        return null;
    }

    // Wrapper, used with tests
    private File getConfig( String target )
    {
        return new File( target );
    }

    @Override
    public String toString()
    {
        return "Read configuration from " + targetConfig + " with contents " + configuration;
    }
}
