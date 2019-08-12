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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class PropertyHandler
{
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
        if ( isEmpty( property ) )
        {
            throw new InternalException( "Invalid property string to match: " + property );
        }
        Matcher m = PATTERN.matcher( property );

        if ( !m.matches() || m.groupCount() != 2)
        {
            throw new InternalException( "Unable to correct match property string: " + property );
        }
        ArrayList<String> result = new ArrayList<>( Arrays.asList( m.group( 1 ).trim().split( "\\s+|=" ) ) );

        result.addAll( splitQuoted( m.group( 2 ).trim() ) );

        return result.toArray( new String[0] );
    }

    private static List<String> splitQuoted (String match)
    {
        List<String> result = new ArrayList<>(  );
        boolean inQuote = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i<match.length(); i++)
        {
            if ( match.charAt( i ) == '"' )
            {
                inQuote = !inQuote;
            }
            if ( match.charAt( i ) != ' ' ||
                            // Allow space separation in quotes
                            (match.charAt( i ) == ' ' && inQuote) )
            {
                sb.append( match.charAt( i ) );
            }
            // Space separated block (if we don't have multiple spaces)
            else if ( match.charAt( i ) == ' ' && match.charAt( i-1 ) != ' ' && !inQuote)
            {
                result.add( sb.toString() );
                sb.delete( 0, sb.length() );
                inQuote = false;
            }
        }
        result.add( sb.toString() );
        return result;
    }
}
