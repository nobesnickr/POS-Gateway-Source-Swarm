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
package com.sonrisa.swarm.erply.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;

/**
 * Customer object retrieved from Erply
 */
public class ErplyCustomerDTO extends CustomerDTO {

    /** The ID of the customer */
    private long customerId;
    
    /** The creation date of the customer (or last modification date) */
    private long lastModified = 0L;

    /** Name of the customer */
    private String name = "";

    /** First & last name of the customer, not existing for companies */
    private String firstName = "";
    
    private String lastName = "";

    /** Contact information of the customer */
    private String email = "";
    
    private String phoneNumber = "";
    
    /** The first postal address of the customer */
    private String address = "";
    
    /** Second line of address if exists */
    private String address2 = "";
    
    /** The city of the address */
    private String city = "";
    
    /** The postal code for the customer */
    private String postalCode = "";
    
    /** The state of the customer, only used for US, CA, MX, AU */
    private String state = "";
    
    /** The country of the customer */
    private String country = "";

    /**
     * @return the customerId
     */
    public long getRemoteId() {
        return customerId;
    }

    /**
     * Get the timestamp of the last modification if the entry
     * 
     * This timestamp is calculated using the remote Unix timestamp of
     * Erply by multiplying it with 1000L, as java.sql.Timestamp expects
     * a timestamp with milliseconds
     * 
     * @return the lastModified
     */
    public Timestamp getLastModified() {
        return new Timestamp(this.lastModified * 1000L);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return the address2
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @return the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @return the notes
     */
    public String getNotes() {
        return "";
    }

    /**
     * @param customerId the customerId to set
     */
    @ExternalField(value = "customerID", required = true)
    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    /**
     * @param lastModified the lastModified to set
     */
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * @param name the name to set
     */
    @ExternalField("fullName")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param firstName the firstName to set
     */
    @ExternalField("firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @param lastName the lastName to set
     */

    @ExternalField("lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @param email the email to set
     */
    @ExternalField("email")
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    @ExternalField("phone")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @param address the address to set
     */
    @ExternalField("address")
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @param address2 the address2 to set
     */
    @ExternalField("address2")
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * @param city the city to set
     */
    @ExternalField("city")
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @param postalCode the postalCode to set
     */
    @ExternalField("postalCode")
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @param state the state to set
     */
    @ExternalField("state")
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @param country the country to set
     */
    @ExternalField("country")
    public void setCountry(String country) {
        this.country = country;
    }
}
