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

import com.redhat.engineering.srcclr.SourceClearTest;
import com.redhat.engineering.srcclr.utils.InternalException;
import com.redhat.engineering.srcclr.utils.ScanException;
import org.junit.Test;

/**
 * Test for Jenkins interface.
 */
public class SourceClearInvokerTest
{
    private static final String SC = "sourceclear";

    private final SourceClearTest wrapper = new SourceClearTest();

    // TODO : This will need to be changed once binary is implemented.
    @Test( expected = InternalException.class )
    public void runBinarySC() throws Exception
    {
        try
        {
            System.setProperty( SC,
                                "binary --url=https://github.com/srcclr/example-java-maven.git --ref= --no-upload" );
            wrapper.runSourceClear();
        }
        finally
        {
            System.clearProperty( SC );
        }
    }

    @Test
    public void runThresholdSC() throws Exception
    {
        try
        {
            System.setProperty( SC,
                                "scm --url=https://github.com/srcclr/example-java-maven.git -t 8 --no-upload" );
            wrapper.runSourceClear();
        }
        finally
        {
            System.clearProperty( SC );
        }
    }

    @Test( expected = ScanException.class )
    public void runScanFailureSC() throws Exception
    {
        try
        {
            System.setProperty( SC,
                                "scm --url=https://github.com/srcclr/example-java-maven.git --ref= --no-upload" );
            wrapper.runSourceClear();
        }
        finally
        {
            System.clearProperty( SC );
        }
    }

}
