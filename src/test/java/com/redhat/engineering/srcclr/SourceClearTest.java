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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * This is the interface that Jenkins runs should use. Parameters to the jenkins runs
 * should be passed in as
 *
 * -DargLine='-Dsourceclear="-d --url=bar --ref=xxx ... "
 *
 *
 */
public class SourceClearTest
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Test
    public void runSourceClear() throws Exception
    {
        System.getProperties().forEach( ( k, v ) -> {
            if ( k.toString().contains( "sourceclear" ) )
            {
                logger.debug( "Properties {}  and {} ", k, v );
            }
        } );

        // Convert the passed in arguments into something useful...
        String[] arguments = System.getProperty( "sourceclear" ).trim().split( "\\s+|=" );
        logger.info( "Retrieved argument {} ", Arrays.toString( arguments ) );

        // TODO: Configure for threshold of exception failure?
        SrcClrWrapper.main( arguments );
    }
}
