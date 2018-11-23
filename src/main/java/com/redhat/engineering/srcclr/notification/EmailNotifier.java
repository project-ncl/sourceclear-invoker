package com.redhat.engineering.srcclr.notification;

import com.redhat.engineering.srcclr.SrcClrWrapper;
import com.redhat.engineering.srcclr.processor.ProcessorResult;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class EmailNotifier implements Notifier
{
    // Keep as class variable as used by test code.
    private static Integer port = 25;

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Override
    public void notify( SrcClrWrapper parent, String scanInfo, Set<ProcessorResult> processorResults )
    {
        // We know we have at least one result ; use it to extra the link to the full scan results.
        ProcessorResult first = processorResults.stream().findFirst().get();

        StringBuffer sb = new StringBuffer( "Located a possible vulnerability within product " )
                        .append( parent.getProduct() )
                        .append( System.lineSeparator() )
                        .append( scanInfo )
                        .append( System.lineSeparator() )
                        .append( "and full scan result is " )
                        .append( first.getScanReport() )
                        .append( System.lineSeparator() )
                        .append( System.lineSeparator() );
        processorResults.forEach( pRes -> sb.append( "In library " )
                         .append ( pRes.getLibrary().getCoordinate1() )
                         .append( ':' )
                         .append ( pRes.getLibrary().getCoordinate2() )
                         .append( ':' )
                         .append ( pRes.getLibrary().getVersions().get( 0 ).getVersion() )
                         .append( System.lineSeparator() )
                         .append( "CVE-" )
                         .append( pRes.getVulnerability().getCve() )
                         .append( System.lineSeparator() )
                         .append ( "Vulnerability is " )
                         .append( pRes.getVulnerability().getTitle() )
                         .append( System.lineSeparator() )
                         .append( pRes.getVulnerability().getOverview() )
                         .append( System.lineSeparator() )
                         .append( "Original SourceClear version range " )
                         // Every instance of Libraries/Details appears to be a valid size 1 list.
                         // This test is to avoid layered construction within tests.
                         .append( pRes.getVulnerability().getLibraries().size() == 0 ? "" : pRes.getVulnerability().getLibraries().get( 0 ).getDetails().get( 0 ).getVersionRange() )
                         .append( System.lineSeparator() )
                         .append( "Original SourceClear fixed version is " )
                         .append( pRes.getVulnerability().getLibraries().size() == 0 ? "" : pRes.getVulnerability().getLibraries().get( 0 ).getDetails().get( 0 ).getUpdateToVersion() )
                         .append( System.lineSeparator() )
                         .append( System.lineSeparator() )
        );
        sb.append( System.lineSeparator() );

        // If the Jenkins BUILD_USER_EMAAIL exists use that in preference to the default email from address.
        String userEmailAddress = System.getenv( "BUILD_USER_EMAIL" );
        if ( isEmpty ( userEmailAddress ) )
        {
            userEmailAddress = "soureclear-scanner" + parent.getEmailAddress().substring( parent.getEmailAddress().indexOf( "@" ) ) ;
        }

        Email email = EmailBuilder.startingBlank()
                                  .from( userEmailAddress )
                                  .to( parent.getEmailAddress() )
                                  .withSubject( "SRCCLR-WARN " + parent.getProduct() )
                                  .withPlainText( sb.toString() )
                                  .buildEmail();

        logger.info ( "Sending email to {} on server {} ", parent.getEmailAddress(), parent.getEmailServer() );

        MailerBuilder.withSMTPServer( parent.getEmailServer(), port ).buildMailer().sendMail( email );
    }
}
