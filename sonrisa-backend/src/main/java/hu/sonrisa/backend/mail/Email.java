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

/**
 * Email class declares the fields which are necessary for sending out an arbitrary email from the application
 * Should be used through EmailServiceImpl.
 * @author János Cserép <cserepj@sonrisa.hu>
 */
public class Email implements Cloneable {

    private String to;
    private String from;
    private String templateName;
    private String subject;
    private boolean enabled = true;

    public Email() {
    }
    /**
     * Constructor for creating an object
     * @param to / to whom it should be sent
     * @param from / from field of the email
     * @param templateName / template to be used
     * @param subject / subject field of the email.
     */
    public Email(String to, String from, String templateName, String subject) {
        this.to = to;
        this.from = from;
        this.templateName = templateName;
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getTo() {
        return to;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public boolean isEnabled() {
        return enabled;
    }
    /**
     * Enables this object
     * @param enabled
     * @return / the enabled email object (this)
     */
    public Email setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    protected Email clone(){
        return new Email(to, from, templateName, subject).setEnabled(enabled);
    }
}
