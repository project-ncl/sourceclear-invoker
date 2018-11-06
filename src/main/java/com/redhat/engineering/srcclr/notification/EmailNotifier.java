package com.redhat.engineering.srcclr.notification;

import com.redhat.engineering.srcclr.SrcClrWrapper;
import com.redhat.engineering.srcclr.json.sourceclear.Vulnerability;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class EmailNotifier implements Notifier
{
    // Keep as class variable as used by test code.
    private static Integer port = 25;

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Override
    public void notify( SrcClrWrapper parent, String scanInfo, Set<Vulnerability> v )
    {
        StringBuffer sb = new StringBuffer( "Located a possible vulnerability within product " )
                        .append( parent.getProduct() )
                        .append( System.lineSeparator() )
                        .append( scanInfo )
                        .append( System.lineSeparator() )
                        .append( System.lineSeparator() );
        v.forEach( vuln -> sb.append( "Vulnerability is " )
                  .append( vuln.getTitle() )
                  .append( System.lineSeparator() )
                  .append( "CVE-" )
                  .append( vuln.getCve() )
                  .append(" with CVSS " )
                  .append( vuln.getCvssScore() )
                  .append( " and overview " )
                  .append( System.lineSeparator() )
                  .append( vuln.getOverview() )
                  .append( System.lineSeparator() )
                  .append( System.lineSeparator() )
        );
        sb.append( System.lineSeparator() );

        Email email = EmailBuilder.startingBlank()
                                  .from( "sourceclear-scanner" + parent.getEmailAddress().substring( parent.getEmailAddress().indexOf( "@" ) ) )
                                  .to( parent.getEmailAddress() )
                                  .withSubject( "SRCCLR-WARN " + parent.getProduct() )
                                  .withPlainText( sb.toString() )
                                  .buildEmail();

        logger.info ( "Sending email to {} on server {} ", parent.getEmailAddress(), parent.getEmailServer() );

        MailerBuilder.withSMTPServer( parent.getEmailServer(), port ).buildMailer().sendMail( email );
    }
}
