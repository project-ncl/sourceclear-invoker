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
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.redhat.engineering.srcclr.utils.ManifestVersionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * Main entry point.
 */
@CommandLine.Command(name = "SrcClrWrapper",
                description = "Wrap SourceClear and invoke it.",
                mixinStandardHelpOptions = true, // add --help and --version options
                versionProvider = ManifestVersionProvider.class,
                subcommands = { SCM.class, Binary.class }
)public class SrcClrWrapper implements Callable<Void>
{
    @CommandLine.Option( names = { "-d", "--debug" }, description = "Enable debug." )
    boolean debug;

    public static void main( String[] args ) throws Exception
    {
        try
        {
            new CommandLine( new SrcClrWrapper() ).parseWithHandler( new CommandLine.RunLast(), args );
        }
        catch ( CommandLine.ExecutionException e )
        {
            if ( e.getCause() != null && e.getCause() instanceof Exception )
            {
                throw (Exception)e.getCause();
            }
            throw e;
        }
    }

    @Override
    public Void call()
    {
        return null;
    }

    void setDebug()
    {
        ch.qos.logback.classic.Logger rootLogger =
                        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger( Logger.ROOT_LOGGER_NAME );
        rootLogger.setLevel( Level.DEBUG );

        LoggerContext loggerContext = rootLogger.getLoggerContext();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext( loggerContext );
        encoder.setPattern( "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n" );
        encoder.start();

        ConsoleAppender<ILoggingEvent> appender = (ConsoleAppender<ILoggingEvent>) rootLogger.getAppender( "STDOUT" );

        if ( appender != null )
        {
            appender.setContext( loggerContext );
            appender.setEncoder( encoder );
            appender.start();
        }
    }
}
