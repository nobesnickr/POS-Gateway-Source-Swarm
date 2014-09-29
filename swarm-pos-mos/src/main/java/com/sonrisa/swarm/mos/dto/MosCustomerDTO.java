/*
 *   Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.
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
package com.sonrisa.swarm.mos.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;

public class MosCustomerDTO extends CustomerDTO {

    /** The ID of the customer */
    private long customerId;
    
    /** The creation date of the customer (or last modification date) */
    private Timestamp lastModified;

    /** First & last name of the customer, not existing for companies */
    private String firstName = "";
    
    private String lastName = "";

    /** Contact information of the customer */
    private String email = "";
    
    /** The first postal address of the customer */
    private String address = "";
    
    /** Second line of address if exists */
    private String address2 = "";
    
    /** The city of the address */
    private String city = "";
    
    /** The postal code for the customer */
    private String postalCode = "";
    
    /** The state of the customer */
    private String state = "";
    
    /** The country of the customer */
    private String country = "";
    
    /** Notes about the customer */
    private String note = "";

    /**
     * @return the customerId
     */
    @Override
    public long getRemoteId() {
        return customerId;
    }

    /**
     * @return the lastModified
     */
    @Override
    public Timestamp getLastModified() {
        return lastModified;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return firstName + " " + lastName;
    }

    /**
     * @return the firstName
     */
    @Override
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return the lastName
     */
    @Override
    public String getLastName() {
        return lastName;
    }

    /**
     * @return the email
     */
    @Override
    public String getEmail() {
        return email;
    }

    /**
     * @return the phoneNumber
     */
    @Override
    public String getPhoneNumber() {
        return "";
    }

    /**
     * @return the address
     */
    @Override
    public String getAddress() {
        return address;
    }

    /**
     * @return the address2
     */
    @Override
    public String getAddress2() {
        return address2;
    }

    /**
     * @return the city
     */
    @Override
    public String getCity() {
        return city;
    }

    /**
     * @return the postalCode
     */
    @Override
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @return the state
     */
    @Override
    public String getState() {
        return state;
    }

    /**
     * @return the country
     */
    @Override
    public String getCountry() {
        return country;
    }

    /**
     * @return the notes
     */
    @Override
    public String getNotes() {
        return this.note;
    }

    /**
     * @param customerId the primary key in the remote system
     */
    @ExternalField(value = "customerID", required = true)
    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    /**
     * @param lastModifiedString Date/time the customer was last modified
     */
    @ExternalField("timeStamp")
    public void setLastModified(String lastModifiedString) {
        this.lastModified = new Timestamp(ISO8061DateTimeConverter.stringToDate(lastModifiedString).getTime());
    }

    /**
     * @param firstName Customer's first name.
     */
    @ExternalField("firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @param lastName Customer's last name.
     */

    @ExternalField("lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @param contact Contact field containing the email, address, etc.
     */
    @ExternalField("Contact")
    public void setContact(ExternalDTO contact) {
        this.email = contact.getText("email");
        this.address = contact.getText("address1");
        this.address2 = contact.getText("address2");
        this.city = contact.getText("city");
        this.state = contact.getText("state");
        this.postalCode = contact.getText("zip");
        this.country = contact.getText("country");
    }
    
    /**
     * @param notes Notes field containing the note
     */
    @ExternalField("Note")
    public void setNote(ExternalDTO notes){
        this.note = notes.getText("note");
    }
}
