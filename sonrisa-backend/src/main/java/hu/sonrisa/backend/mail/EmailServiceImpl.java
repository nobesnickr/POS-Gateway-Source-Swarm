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

import hu.sonrisa.backend.BackendMessageConstants;
import hu.sonrisa.backend.model.ResourceBasedUzenet;
import hu.sonrisa.backend.model.util.StringUtil;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import javax.mail.internet.MimeMessage;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * Service implementation for sending out emails from the application
 *
 * @author developer
 */
public class EmailServiceImpl implements EmailService {

    /**
     * Ennek a bean-nek a neve a spring context-ben.
     */
    public static final String BEAN_NAME = "emailService";
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Autowired(required = false)
    private VelocityEngine velocityEngine;
    @Autowired(required = false)
    private JavaMailSender mailSender;
    @Autowired(required = false)
    private MessageSource messageSource;
    private String css;
    private Map<String, Email> emails;

    /**
     * Map of emails that this service is able to send
     *
     * @param emails - key is used for finding the appropriate email
     */
    public EmailServiceImpl(Map<String, Email> emails) {
        this.emails = emails;
    }

    @Override
    public void registerTemplate(String name, Email email) {
        emails.put(name, email);
    }

    /**
     * Sends e-mail using Email object using Velocity template for the body and
     * the properties passed in as Velocity variables.
     *
     * @param msg The e-mail message to be sent, except for the body.
     * @param templateName
     * @param variables Variables to use when processing the template.
     */
    @Override
    public void send(final Email msg, Map<String, Object> model, Locale locale) {

        if (msg == null) {
            throw new IllegalArgumentException(BackendMessageConstants.M_00033);
        }

        if (!msg.isEnabled()) {
            return;
        }
        final Map<String, Object> variables = replaceResources(model, locale);
        variables.put("css", css);
        variables.put("resources", messageSource);
        variables.put("locale", locale);
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            //interface declares throws Exception, not nice but it is given
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, StringUtil.UTF8);
                message.setTo(msg.getTo());
                message.setFrom(msg.getFrom());
                message.setSubject(msg.getSubject());
                String body =
                        VelocityEngineUtils.mergeTemplateIntoString(
                        velocityEngine,
                        msg.getTemplateName(), "UTF-8", variables);
                message.setText(body, true);
                mimeMessage.setHeader("Content-Transfer-Encoding", "8bit");
                mimeMessage.setHeader("Content-Type", "text/html; charset=UTF-8; format=flowed");
            }
        };

        mailSender.send(preparator);
        LOGGER.info("Sent e-mail to '{}' subject:'{}'.", msg.getTo(), msg.getSubject());
    }

    /**
     * Email küldése, kivételről.
     *
     * @param info
     * @param ex
     */
    @Override
    public void sendExceptionMail(String info, Exception ex, Locale locale) {
        // stacktrace to string
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String stacktrace = sw.toString();

        // e-mail template params
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("stacktrace", stacktrace);
        params.put("info", new ResourceBasedUzenet(info));

        Email email = emails.get(EXCEPTION_TEMPLATE);
        if (email != null) {
            send(email, params, locale);
        }
    }

    @Override
    public Email findTemplate(String name) {
        Email e = emails.get(name);
        if (e != null) {
            e = e.clone();
        }
        return e;
    }

    @Override
    public Email getDefaultTemplate() {
        Email e = emails.get("default");
        if (e != null) {
            e = e.clone();
        } else {
            e = new Email();
        }
        return e;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    private Map<String, Object> replaceResources(Map<String, Object> model, Locale locale) {
        Map<String, Object> ret = new HashMap<String, Object>();
        for (Map.Entry<String, Object> re : model.entrySet()) {
            if (re.getValue() instanceof ResourceBasedUzenet) {
                ResourceBasedUzenet uzenet = (ResourceBasedUzenet) re.getValue();
                ret.put(re.getKey(), messageSource.getMessage(uzenet.getResourceKey(), uzenet.getParameters(), uzenet.getResourceKey(), locale));
            } else {
                ret.put(re.getKey(), re.getValue());
            }
        }
        return ret;
    }
}
