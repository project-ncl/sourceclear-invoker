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
package com.redhat.engineering.srcclr;

import ch.qos.logback.classic.Level;
import com.redhat.engineering.srcclr.converters.ProcessorConvertor;
import com.redhat.engineering.srcclr.converters.ThresholdConverter;
import com.redhat.engineering.srcclr.notification.EmailNotifier;
import com.redhat.engineering.srcclr.notification.LogFileNotifier;
import com.redhat.engineering.srcclr.notification.Notifier;
import com.redhat.engineering.srcclr.processor.JSONResult;
import com.redhat.engineering.srcclr.processor.ProcessorResult;
import com.redhat.engineering.srcclr.utils.ConfigurationFileProvider;
import com.redhat.engineering.srcclr.utils.InternalException;
import com.redhat.engineering.srcclr.utils.ManifestVersionProvider;
import com.redhat.engineering.srcclr.utils.SourceClearResult;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Main entry point.
 */
@Command(name = "SrcClrWrapper",
                description = "Wrap SourceClear and invoke it.",
                mixinStandardHelpOptions = true, // add --help and --version options
                versionProvider = ManifestVersionProvider.class,
                subcommands = { SCM.class, Binary.class },
                defaultValueProvider = ConfigurationFileProvider.class)
@Getter
public class SrcClrWrapper implements Callable<Void>
{
    static final String UNMATCHED = " (unmatched args are passed directly to SourceClear)";

    private static CommandLine cl;

    private static final Logger logger = LoggerFactory.getLogger( SrcClrWrapper.class );

    @Option( names = { "--trace" }, description = "Enable trace. Will DISABLE JSON OUTPUT" )
    private boolean trace;

    @Option( names = { "-d", "--debug" }, description = "Enable debug." )
    private boolean debug;

    @Option( names = { "-e", "--exception" }, description = "Throw exception on vulnerabilities found." )
    boolean exception = true;

    @Option( names = { "-t", "--threshold" }, converter=ThresholdConverter.class,
                    description = "Threshold on which exception is thrown. Only used with CVSS Processor")
    private int threshold = 0;

    @Option( names = { "--processor" }, defaultValue = "cve", converter = ProcessorConvertor.class,
                    description = "Processor (cve|cvss) to use to analyse SourceClear results. Default is cve")
    private JSONResult processor;

    @Option( names = { "-p", "--product" }, required = true, description = "Product Name (in same format as CPE Product Name)")
    private String product;

    @Option(names = { "-v", "--product-version" }, required = true, description = "Version of the product")
    private String version;

    @Option( names = "--package", defaultValue="", description = "Package name. It's optional but required for RHOAR, e.g. (vertx|swarm|springboot).")
    private String packageName;

    @Option ( names = "--email-server", description = "SMTP Server to use to send notification email" )
    private String emailServer;

    @Option( names = "--json", defaultValue="target", description = "Directory path to output processed JSON as a file. Set to empty to disable." )
    private String json;

    @Option( names = "--log", defaultValue="target", description = "Directory path to output log file containing results. Set to empty to disable." )
    private String log;

    @Option ( names = "--email-address",
              description = "Comma separated list of email address to notify. Domain portion will be used as FROM address",
              split = ",")
    private List<String> emailAddresses = new ArrayList<>(  );

    private String cpe;

    private Set<Notifier> notifier = new HashSet<>();

    /**
     * List of environment variables to exclude from the SourceClear process to prevent interference.
     */
    protected final List<String>excludedEnvironment = Arrays.asList( "GIT_URL", "GIT_BRANCH" );


    public static void main( String[] args ) throws Exception
    {
        System.exit( invokeWrapper( args ).isPass() ? 0 : 1 );
    }

    static SourceClearResult invokeWrapper( String[] args ) throws Exception
    {
        final ExceptionHandler<List<Object>> handler = new ExceptionHandler<>();
        SourceClearResult result;

        try
        {
            cl = new CommandLine( new SrcClrWrapper() );

            cl.parseWithHandlers( new CommandLine.RunAll(), handler, args );

            if ( handler.isParseException() )
            {
                result = new SourceClearResult().setMessage( "Command line parse exception" );
            }
            else
            {
                ParseResult parseResult = cl.getParseResult();
                if ( parseResult.subcommand() != null && !parseResult.subcommand().isVersionHelpRequested()
                                && !parseResult.subcommand().isUsageHelpRequested() )
                {
                    CommandLine sub = parseResult.subcommand().commandSpec().commandLine();
                    result = sub.getExecutionResult();
                }
                else
                {
                    result = new SourceClearResult();
                }
            }
        }
        catch ( CommandLine.ExecutionException e )
        {
            if ( e.getCause() != null && e.getCause() instanceof Exception )
            {
                if ( e.getCause() instanceof InternalException && e.getCause().getCause() instanceof Exception)
                {
                    logger.debug( "Caught an internal exception", e );

                    result = new SourceClearResult().setMessage( e.getCause().getCause().getMessage() );
                }
                else
                {
                    throw (Exception) e.getCause();
                }
            }
            else
            {
                throw e;
            }
        }
        return result;
    }

    @Override
    public Void call()
    {
        // If we had further use of CPE than just simply assembling a string (e.g. comparison, parsing)
        // then we could have used https://github.com/stevespringett/CPE-Parser
        cpe = "cpe:/a:redhat:" + product + ':' + version;

        if ( emailAddresses.size() > 0 && isNotBlank ( emailServer )
                        && ! ( emailAddresses.size() == 1 && isBlank(emailAddresses.get( 0 ))) )
        {
            notifier.add( new EmailNotifier() );
        }
        if ( isNotBlank( log ) )
        {
            notifier.add( new LogFileNotifier(log) );
        }

        return null;
    }

    void enableDebug()
    {
        ch.qos.logback.classic.Logger rootLogger =
            (ch.qos.logback.classic.Logger) LoggerFactory.getLogger( Logger.ROOT_LOGGER_NAME );

        rootLogger.setLevel( Level.DEBUG );

        logger.debug ( "{}", cl.getCommandSpec().versionProvider() );
        logger.debug ( "{}", cl.getCommandSpec().defaultValueProvider() );
    }


    void notifyListeners( String scanInfo, Set<ProcessorResult> v ) throws InternalException
    {
        if ( v.size() > 0 )
        {
            for ( Notifier n : notifier )
            {
                n.notify( this, scanInfo, v );
            }
        }
    }


    @Getter
    private static class ExceptionHandler<R> extends CommandLine.DefaultExceptionHandler<R>
    {
        private boolean parseException;

        /**
         * Prints the message of the specified exception, followed by the usage message for the command or subcommand
         * whose input was invalid, to the stream returned by {@link #err()}.
         * @param ex the ParameterException describing the problem that occurred while parsing the command line arguments,
         *           and the CommandLine representing the command or subcommand whose input was invalid
         * @param args the command line arguments that could not be parsed
         * @return the empty list
         * @since 3.0 */
        public R handleParseException( CommandLine.ParameterException ex, String[] args)
        {
            parseException = true;
            super.handleParseException( ex, args );
            return super.returnResultOrExit( null );
        }
    }
}
