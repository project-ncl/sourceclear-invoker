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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

public class ManifestVersionProvider
                implements CommandLine.IVersionProvider
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

   /**
     * Returns version information for a command.
     * @return version information (each string in the array is displayed on a separate line)
    */
    @Override
    public String[] getVersion()
    {
        return new String[]{"SourceClearWrapper " + getImplVersion() + " ( SHA : " + getScmRevision() + ")"};
    }

    private String getImplVersion() {
        Package p = ManifestVersionProvider.class.getPackage();

        return ((p == null || p.getImplementationVersion() == null) ? "unknown" : p.getImplementationVersion());
    }

    /**
     * Retrieves the SHA this was built with.
     *
     * @return the GIT sha of this codebase.
     */
    private String getScmRevision()
    {
        String scmRevision = "unknown";
        try
        {
            Enumeration<URL> resources =
                            ManifestVersionProvider.class.getClassLoader().getResources( "META-INF/MANIFEST.MF" );

            while ( resources.hasMoreElements() )
            {
                URL jarUrl = resources.nextElement();

                if ( jarUrl.getFile().contains( "srcclr" ) )
                {
                    Manifest manifest = new Manifest( jarUrl.openStream() );
                    String manifestValue = manifest.getMainAttributes().getValue( "Scm-Revision" );

                    if ( manifestValue != null && !manifestValue.isEmpty() )
                    {
                        scmRevision = manifestValue;
                    }

                    break;
                }
            }
        }
        catch ( IOException e )
        {
            logger.warn( "Error getting SCM revision: {}", e.getMessage() );
        }
        return scmRevision;
    }
}
