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
package com.redhat.engineering.srcclr.internal;

import com.redhat.engineering.srcclr.SrcClrInvoker;
import org.jboss.byteman.contrib.bmunit.BMRule;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


@RunWith(BMUnitRunner.class)
public class NotFoundInvokerTest
{
    static
    {
//        System.setProperty( "org.jboss.byteman.verbose", "true" );
//        System.setProperty( "org.jboss.byteman.debug", "true" );
    }

    // Used in ByteMan rule.
    public static final Path DUMMY = Paths.get ("/tmp/srcclr");

    @Test (expected = IOException.class)
    @BMRule(name = "pretend-no-srcclr",
                    targetClass = "SrcClrInvoker",
                    targetMethod = "getDefaultSrcClrLocation",
                    targetLocation = "AT ENTRY",
                    action = " return com.redhat.engineering.srcclr.internal.NotFoundInvokerTest.DUMMY; "
    )
    public void verifynosrcclTest() throws IOException
    {
        SrcClrInvoker srccr = new SrcClrInvoker(false);
        srccr.locateSourceClearJar();
    }
}