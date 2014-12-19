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

import java.util.Locale;
import java.util.Map;

/**
 *
 * @author developer
 */
public interface EmailService {

    String EXCEPTION_TEMPLATE = "__EXCEPTION__";

    /**
     * #2129
     *
     * @param email
     * @param model
     */
    void send(Email email, Map<String, Object> model, Locale locale);

    /**
     * Email küldése kivételről.
     *
     * @param info
     * @param ex
     */
    void sendExceptionMail(String info, Exception ex, Locale locale);

    /**
     * Registers an e-mail template that can be used later
     *
     * @param name
     * @param email
     */
    void registerTemplate(String name, Email email);

    Email findTemplate(String name);

    /**
     * Return the default template used by the system If the "to" field is not
     * empty, then it means all e-mails should be forwarded to that address
     *
     * @return
     */
    Email getDefaultTemplate();
}