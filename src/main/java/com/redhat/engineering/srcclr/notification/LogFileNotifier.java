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
package com.redhat.engineering.srcclr.notification;

import com.redhat.engineering.srcclr.SrcClrWrapper;
import com.redhat.engineering.srcclr.processor.ProcessorResult;
import com.redhat.engineering.srcclr.utils.InternalException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;

public class LogFileNotifier extends DefaultStringNotifier
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final File target;

    public LogFileNotifier( String directory )
    {
        String logFileName = "vulnerabilityLogFile.txt";

        target = new File( directory, logFileName );
    }

    @Override
    public void notify( SrcClrWrapper parent, String scanInfo, Set<ProcessorResult> processorResults )
                    throws InternalException
    {
        String result = toString( parent, scanInfo, processorResults );

        logger.info ("Writing log file to {}", target.toString() );
        try
        {
            FileUtils.writeStringToFile( target, result, Charset.defaultCharset() );
        }
        catch ( IOException e )
        {
            throw new InternalException( "Error executing SourceClear ", e );
        }
    }
}
