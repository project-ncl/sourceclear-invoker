package com.redhat.engineering.srcclr.notification;

import com.redhat.engineering.srcclr.SrcClrWrapper;
import com.redhat.engineering.srcclr.json.Vulnerability;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class EmailNotifierTest
{
    private final int port = 25000;
    private Wiser wiser;

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

        Vulnerability v = new Vulnerability();
        v.setCve( "123456789" );
        v.setTitle( "Dummy Vulnerability" );
        n.notify( wrapper, v );

        List<WiserMessage> messages = wiser.getMessages();
        for ( WiserMessage wm : messages )
        {
//            System.out.println ("### Got message " + wm.toString());
            assertTrue ( wm.toString().contains( "TEST PRODUCT" ));
            assertTrue ( wm.toString().contains( "CVE-123456789" ));
        }
    }
}
