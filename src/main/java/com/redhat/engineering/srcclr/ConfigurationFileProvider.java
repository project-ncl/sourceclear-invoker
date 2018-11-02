package com.redhat.engineering.srcclr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Model.OptionSpec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationFileProvider
                implements CommandLine.IDefaultValueProvider
{
    private static final String CONFIG_FILE = System.getProperty("user.home") +
                    File.separatorChar + ".srcclr" + File.separatorChar + "invoker.properties";

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private Properties configuration = new Properties( );

    public ConfigurationFileProvider ()
    {
        File invokerProperties = new File( CONFIG_FILE );

        if ( invokerProperties.exists() )
        {
            try (FileInputStream props = new FileInputStream( invokerProperties ))
            {
                configuration.load( props );
            }
            catch ( IOException e )
            {
                throw new CommandLine.PicocliException( "Unable to read properties file " + invokerProperties, e );
            }
            logger.info( "Read configuration from {} with contents {} ", invokerProperties, configuration );
        }
    }

    /** Returns the default value for an option or positional parameter or {@code null}.
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
            return configuration.getProperty( ((OptionSpec )argSpec).longestName().substring( 2 ) );
        }
        // Allow default value to be used.
        return null;
    }
}
