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

import picocli.CommandLine;

public class ThresholdConverter
                implements CommandLine.ITypeConverter<Integer>
{
    /**
     * Converts the specified command line argument value to some domain object.
     * @param value the command line argument String value
     * @return the resulting domain object
     */
    @Override
    public Integer convert( String value )
    {
        Integer result = Integer.parseInt( value );

        // CVSS Score is https://www.first.org/cvss/v2/guide
        if ( result < 0 || result > 10 )
        {
            throw new CommandLine.TypeConversionException( "Invalid CVSS Score parameter " + result );
        }
        return result;
    }
}
