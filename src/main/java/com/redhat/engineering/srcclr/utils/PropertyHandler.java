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

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyHandler
{
    //private static final Logger logger = LoggerFactory.getLogger( PropertyHandler.class );

    private static final String REGEX = "(.*(?:scm|binary))(.*)";

    private static final Pattern PATTERN = Pattern.compile( REGEX );

    /**
     * Used to convert a system property into a String array suitable for passing to main for
     * further processing.
     * Minimal parsing is done on it in order to split it up appropriately - just splits the
     * subcommands and command flags up <b>but</b> allows for space separated arguments to the command
     * flags.
     *
     * @param property the String to process.
     * @return a String array.
     * @throws InternalException if an error occurs
     */
    public static String[] convertProperty (String property) throws InternalException
    {
        Matcher m = PATTERN.matcher( property );

        if ( !m.matches() || m.groupCount() != 2)
        {
            throw new InternalException( "Unable to correct match property string: " + property );
        }
        String []first = m.group( 1 ).trim().split( "\\s+|=" );
        String []last = m.group( 2 ).trim().split( " (?=-)");

        // logger.info( "Retrieved res {} ", Arrays.toString( first ) );
        // logger.info( "Retrieved last {} ", Arrays.toString( last ) );

        String []result = Arrays.copyOf( first, first.length + last.length );
        System.arraycopy(last, 0, result, first.length, last.length);

        return result;
    }
}
