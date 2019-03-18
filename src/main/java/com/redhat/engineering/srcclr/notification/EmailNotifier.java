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
package com.redhat.engineering.srcclr.notification;

import com.redhat.engineering.srcclr.SrcClrWrapper;
import com.redhat.engineering.srcclr.processor.ProcessorResult;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class EmailNotifier extends DefaultStringNotifier
{
    static
    {
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap( "text/html;; x-java-content-handler=com.sun.mail.handlers.text_html" );
        mc.addMailcap( "text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml" );
        mc.addMailcap( "text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain" );
        mc.addMailcap( "multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed" );
        mc.addMailcap( "message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822" );
    }

    // Keep as class variable as used by test code.
    private static Integer port = 25;

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Override
    public void notify( SrcClrWrapper parent, String scanInfo, Set<ProcessorResult> processorResults )
    {
        String result = toString( parent, scanInfo, processorResults );
        final String userEmailAddress;

        // If the Jenkins BUILD_USER_EMAIL exists use that in preference to the default email from address.
        String jenkins = System.getenv( "BUILD_USER_EMAIL" );
        if ( isEmpty( jenkins ) )
        {
            String firstMail = parent.getEmailAddresses().get( 0 );
            userEmailAddress = "sourceclear-scanner" + firstMail.substring( firstMail.indexOf( "@" ) );
        }
        else
        {
            userEmailAddress = jenkins;
        }

        parent.getEmailAddresses().forEach( e -> {
            Email email = EmailBuilder.startingBlank()
                                      .from( userEmailAddress )
                                      .to( e )
                                      .withSubject( "SRCCLR-WARN " + parent.getProduct() )
                                      .withPlainText( result )
                                      .buildEmail();

            logger.info( "Sending email to {} on server {} ", e, parent.getEmailServer() );

            MailerBuilder.withSMTPServer( parent.getEmailServer(), port ).buildMailer().sendMail( email );
        } );
    }
}
