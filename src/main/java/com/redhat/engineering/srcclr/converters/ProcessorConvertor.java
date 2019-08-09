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
package com.redhat.engineering.srcclr.converters;

import com.redhat.engineering.srcclr.processor.CVEProcessor;
import com.redhat.engineering.srcclr.processor.CVSSProcessor;
import com.redhat.engineering.srcclr.processor.JSONResult;
import picocli.CommandLine;

public class ProcessorConvertor
                implements CommandLine.ITypeConverter<JSONResult>
{
    /**
     * Converts the specified command line argument value to some domain object.
     * @param value the command line argument String value
     * @return the resulting domain object
     */
    @Override
    public JSONResult convert( String value )
    {
        switch ( value.trim().toLowerCase() )
        {
            case "cvss":
                return new CVSSProcessor();
            case "cve":
                return new CVEProcessor();
            default:
                throw new CommandLine.TypeConversionException( "Invalid processor " + value + " ( valid values : 'cvss|cve')");
        }
    }
}
