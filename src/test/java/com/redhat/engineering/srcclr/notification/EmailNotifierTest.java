package com.redhat.engineering.srcclr.notification;

import com.redhat.engineering.srcclr.SrcClrWrapper;
import com.redhat.engineering.srcclr.json.sourceclear.Library;
import com.redhat.engineering.srcclr.json.sourceclear.Version;
import com.redhat.engineering.srcclr.json.sourceclear.Vulnerability;
import com.redhat.engineering.srcclr.processor.ProcessorResult;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;

public class EmailNotifierTest
{
    private final int port = 25000;
    private Wiser wiser;

    @Rule
    public final SystemOutRule systemRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    @Before
    public void before()
    {
        wiser = Wiser.port(port);
        wiser.start();
    }

    @Test
    public void testSendMail () throws IllegalAccessException
    {
        SrcClrWrapper wrapper = new SrcClrWrapper();

        FieldUtils.writeField( wrapper, "product", "TEST PRODUCT", true );
        FieldUtils.writeField( wrapper, "emailAddress", "test@test.com", true );
        FieldUtils.writeField( wrapper, "emailServer", "localhost", true );

        Notifier n = new EmailNotifier();
        FieldUtils.writeStaticField( EmailNotifier.class, "port", port, true );

        ProcessorResult pr = new ProcessorResult();
        Library l = new Library();
        List<Version> versions = new ArrayList<>(  );
        Version version = new Version();
        versions.add( version );
        l.setVersions( versions );
        Vulnerability v = new Vulnerability();
        Set<ProcessorResult> processorResults = new HashSet<>(  );
        v.setCve( "123456789" );
        v.setTitle( "Dummy Vulnerability" );
        pr.setVulnerability( v );
        pr.setLibrary( l );
        processorResults.add( pr );
        n.notify( wrapper, "", processorResults );

        List<WiserMessage> messages = wiser.getMessages();
        for ( WiserMessage wm : messages )
        {
//            System.out.println ("### Got message " + wm.toString());
            assertTrue ( wm.toString().contains( "TEST PRODUCT" ));
            assertTrue ( wm.toString().contains( "CVE-123456789" ));
        }
    }
}
