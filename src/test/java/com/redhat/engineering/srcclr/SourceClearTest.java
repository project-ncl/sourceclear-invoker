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

import com.redhat.engineering.srcclr.utils.SourceClearResult;
import org.junit.Assert;
import org.junit.Test;

/**
 * This is the interface that Jenkins runs should use. Parameters to the jenkins runs
 * should be passed in as
 *
 * -DargLine='-Dsourceclear="-d --url=bar --ref=xxx ... "
 *
 *
 */
public class SourceClearTest extends SCBase
{
    @Test
    public void runSourceClear() throws Exception
    {
        SourceClearResult result;
        try
        {
            result = exeSC();

            if ( !result.isPass() )
            {
                logger.error( "Found issues when scanning {}", result.getMessage() );
                Assert.fail( result.getMessage() );
            }
        }
        catch ( Exception e )
        {
            logger.error( "Exception running sourceclear", e );
            throw e;
        }
    }
}
