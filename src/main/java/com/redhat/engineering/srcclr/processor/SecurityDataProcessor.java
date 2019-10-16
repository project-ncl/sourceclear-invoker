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
package com.redhat.engineering.srcclr.processor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.engineering.srcclr.json.securitydata.AffectedRelease;
import com.redhat.engineering.srcclr.json.securitydata.PackageState;
import com.redhat.engineering.srcclr.json.securitydata.SecurityDataJSON;
import com.redhat.engineering.srcclr.utils.InternalException;
import kong.unirest.HttpResponse;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.Unirest;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.stream.Stream;

public class SecurityDataProcessor
{
    private final static String REDHAT_SECURITY_DATA_CVE = "https://access.redhat.com/labs/securitydataapi/cve/CVE-";

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final String cpe;

    private final String base_url;

    private String packageName;


    private String getCpeOfMajorVersion()
    {
        String version = cpe.substring(cpe.lastIndexOf(':') + 1);

        String newversion = version;
        String[] splits = version.split("\\.");

        if ( splits.length >= 2 )
        {
            newversion = splits[0];
        }
        
        String first_part = cpe.substring(0, cpe.lastIndexOf(':') + 1);

        return first_part + newversion;
    }

    public void setPackageName( String packageName )
    {
        this.packageName = packageName;
    }

    public SecurityDataProcessor( String startCPE )
    {
        this ( startCPE, REDHAT_SECURITY_DATA_CVE);
    }

    public SecurityDataProcessor( String startCPE, String startBaseUrl )
    {
        cpe = startCPE;
        base_url = startBaseUrl;

        JacksonObjectMapper mapper = new JacksonObjectMapper(
                        new ObjectMapper().configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false )
                                          .configure( DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true ) );
        Unirest.config().setObjectMapper( mapper ).addShutdownHook( true ).verifySsl( false );
    }

    private SecurityDataJSON lookUpAPI( String cve_id ) throws IOException
    {
        String url = base_url + cve_id +".json";

        logger.debug( "Looking up {}", url );

        HttpResponse<SecurityDataJSON> request = Unirest.get( url ).
                        header("accept", "application/json").
                        asObject( SecurityDataJSON.class );

        if ( request.isSuccess() )
        {
            return request.getBody();
        }
        else
        {
            throw new HttpResponseException( request.getStatus(), request.getStatusText() );
        }
     }

    private PackageState searchPackageState(SecurityDataJSON json)
    {
        PackageState ps_found;

        if (json.getPackageState() == null) 
        {
            return null;
        }

        if ( StringUtils.isEmpty( packageName ) )
        {
            
            ps_found = json.getPackageState()
                .stream()
                .filter( ps -> cpe.equals( ps.getCpe() ) )
                .findAny()
                .orElse( null );
        
            if ( ps_found == null )
            {
                ps_found = json.getPackageState()
                    .stream()
                    .filter( ps -> getCpeOfMajorVersion().equals( ps.getCpe() ) )
                    .findAny()
                    .orElse( null );
            }
        
        }
        else
        {
            ps_found = json.getPackageState()
                                .stream()
                                .filter( ps -> cpe.equals( ps.getCpe() ) )
                                .filter( ps -> packageName.equals( ps.getPackageName() ) )
                                .findAny()
                                .orElse( null );

            if ( ps_found == null )
            {
                ps_found = json.getPackageState()
                    .stream()
                    .filter( ps -> getCpeOfMajorVersion().equals( ps.getCpe() ) )
                    .filter( ps -> packageName.equals( ps.getPackageName() ) )
                    .findAny()
                    .orElse( null );     
            }
        }


        return ps_found;
    }

    private AffectedRelease searchAffectedRelease(SecurityDataJSON json)
    {
        AffectedRelease ar_found;

        if (json.getAffectedRelease() == null) 
        {
            return null;
        }

        if ( StringUtils.isEmpty( packageName ) )
        {
            
            ar_found = json.getAffectedRelease()
                .stream()
                .filter( ar -> cpe.equals( ar.getCpe() ) )
                .findAny()
                .orElse( null );
        
            if ( ar_found == null )
            {
                ar_found = json.getAffectedRelease()
                    .stream()
                    .filter( ar -> getCpeOfMajorVersion().equals( ar.getCpe() ) )
                    .findAny()
                    .orElse( null );
            }
        
        }
        else
        {
            ar_found = json.getAffectedRelease()
                                .stream()
                                .filter( ar -> cpe.equals( ar.getCpe() ) )
                                .filter( ar -> packageName.equals( ar.getPackage() ) )
                                .findAny()
                                .orElse( null );

            if ( ar_found == null )
            {
                ar_found = json.getAffectedRelease()
                    .stream()
                    .filter( ar -> getCpeOfMajorVersion().equals( ar.getCpe() ) )
                    .filter( ar -> packageName.equals( ar.getPackage() ) )
                    .findAny()
                    .orElse( null );     
            }
        }


        return ar_found;
    }

    public ProcessorResult process( String cve_id ) throws InternalException
    {
        boolean is_fail;

        ProcessorResult sdpr = new ProcessorResult();

        try
        {
            SecurityDataJSON json = lookUpAPI( cve_id );

            PackageState ps_found = searchPackageState(json);
            
            
            if ( ps_found != null )
            {
                String fixed_state = ps_found.getFixState();

                if ( Stream.of( "will not fix", "not affected", "fix deferred" )
                           .anyMatch( fixed_state::equalsIgnoreCase ) )
                {
                    is_fail = false;
                }
                else if ( Stream.of( "affected", "new" ).anyMatch( fixed_state::equalsIgnoreCase ) )
                {
                    sdpr.setMessage( "fixed_state is " + fixed_state );
                    // setMessage or logger
                    is_fail = true;
                }
                else
                {
                    sdpr.setMessage( "Unexpected fixed_state: " + fixed_state );
                    // setMessage or logger
                    is_fail = true;
                }
            }
            else
            {
                AffectedRelease ar_found = searchAffectedRelease(json);
                

                if ( ar_found != null )
                {
                    sdpr.setMessage( "AffectedRelease exists" );
                    is_fail = true;
                }
                else
                {
                    sdpr.setMessage( "No cpe exists" );
                    is_fail = true;
                }
            }
        }
        catch ( HttpResponseException e )
        {
            logger.info( "No CVE data in security data API. URL {}", e.getMessage() );
            sdpr.setMessage( "No CVE data in security data API" );
            is_fail = true;
        }
        catch ( IOException e )
        {
            throw new InternalException( "Unable to process Security Data", e );
        }

        // if need to block
        if ( is_fail )
        {
            sdpr.setFail( true );

            /*
             * Currently, notification will be sent for every fails.
             * However in case we need a case that test fails but no notification is necessary, set to 'false'.
             */
            sdpr.setNotify( true );
        }

        return sdpr;

    }
}