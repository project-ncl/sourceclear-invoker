package com.redhat.engineering.srcclr.notification;

import com.redhat.engineering.srcclr.SrcClrWrapper;
import com.redhat.engineering.srcclr.json.Vulnerability;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailNotifier implements Notifier
{
    // Keep as class variable as used by test code.
    private static Integer port = 25;

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Override
    public void notify( SrcClrWrapper parent, Vulnerability v )
    {

        String sb = "Located a possible vulnerability within product "
                        + parent.getProduct()
                        + System.lineSeparator()
                        + "Vulnerability is "
                        + v.getTitle()
                        + System.lineSeparator()
                        + "CVE-"
                        + v.getCve()
                        + " with CVSS "
                        + v.getCvssScore()
                        + " and overview "
                        + System.lineSeparator()
                        + v.getOverview()
                        + System.lineSeparator();

        Email email = EmailBuilder.startingBlank()
                                  .from( "sourceclear-scanner" + parent.getEmailAddress().substring( parent.getEmailAddress().indexOf( "@" ) ) )
                                  .to( parent.getEmailAddress() )
                                  .withSubject( "SourceClear Vulnerability Warning" )
                                  .withPlainText( sb )
                                  .buildEmail();

        logger.info ( "Sending email to {} on server {} ", parent.getEmailAddress(), parent.getEmailServer() );

        MailerBuilder.withSMTPServer( parent.getEmailServer(), port ).buildMailer().sendMail( email );
    }
}
