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

import com.redhat.engineering.srcclr.utils.PropertyHandler;
import com.redhat.engineering.srcclr.utils.SourceClearResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Base class to provide a common interface for main test for jenkins and for test system.
 */
public class SCBase
{
    final Logger logger = LoggerFactory.getLogger( getClass() );

    /**
     * Internal method to convert the properties and invoke the wrapper.
     *
     * @return SourceClearResult object
     * @throws Exception if an error occurs.
     */
    protected SourceClearResult exeSC () throws Exception
    {
        String[] arguments = PropertyHandler.convertProperty ( System.getProperty( "sourceclear" ) );

        logger.info( "Retrieved argument {}", String.join( " ", arguments ) );

        return SrcClrWrapper.invokeWrapper( arguments );
    }
}
