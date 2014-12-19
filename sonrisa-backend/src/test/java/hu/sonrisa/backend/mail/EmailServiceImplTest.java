/*
 *   Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
 *
 *  This software is the confidential and proprietary information of
 *  Sonrisa Informatikai Kft. ("Confidential Information").
 *  You shall not disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Sonrisa.
 *
 *  SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 *  THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *  TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *  PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 *  ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 *  DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package hu.sonrisa.backend.mail;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import hu.sonrisa.backend.BackendTestBase;
import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 *
 * @author developer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class EmailServiceImplTest extends BackendTestBase {

    private static final String EMAIL_TEMPLATE = "testEmailBody.vm";
    private static final String FROM = "Test@Sonrisa.hu";
    private static final String SUBJECT = "TestEmail";
    private static final String TO = "golyo@sonrisa.hu";
    @Autowired
    private EmailService emailService;

    /**
     *
     */
    @Test
    public void testMessage() {
        try {
            SimpleSmtpServer server = SimpleSmtpServer.start(3025);
            assertNotNull("VelocityEmailSender is null.", emailService);
            Map<String, Object> props = new HashMap<String, Object>();
            props.put("firstName", "Joe");
            props.put("lastName", "Smith");
            props.put("timestamp", new Date().toString());
            Email mail = new Email(TO, FROM, EMAIL_TEMPLATE, SUBJECT);
            emailService.send(mail, props, null);
            server.stop();
            assertTrue(server.getReceivedEmailSize() == 1);
            Iterator emailIter = server.getReceivedEmail();
            SmtpMessage email = (SmtpMessage) emailIter.next();
            assertTrue(email.getHeaderValue("Subject").equals(SUBJECT));
            assertTrue(email.getHeaderValue("From").equals(FROM));
            assertTrue(email.getHeaderValue("To").equals(TO));
            assertTrue(email.getBody().contains(props.get("firstName").toString()));
            assertTrue(email.getBody().contains(props.get("lastName").toString()));
        } catch (Exception ex) {
            Logger.getLogger(EmailServiceImplTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.getMessage());
        }
    }

    /**
     *
     */
    @Test
    public void testExceptionEmail() {
        SimpleSmtpServer smtpServer = SimpleSmtpServer.start(3025);

        final String exMsg = "Ez egy teszt kivetel";
        emailService.sendExceptionMail(exMsg, new RuntimeException("runtime exception történt valahol"), null);

        smtpServer.stop();

        assertEquals(1, smtpServer.getReceivedEmailSize());
    }
}
