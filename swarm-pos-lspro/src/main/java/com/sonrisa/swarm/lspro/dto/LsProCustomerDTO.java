/*
 *   Copyright (c) 2014 Sonrisa Informatikai Kft. All Rights Reserved.
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

package com.sonrisa.swarm.lspro.dto;

import java.sql.Timestamp;

import org.springframework.util.StringUtils;

import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;

/**
 * DTO for Lightspeed Pro Customer entities
 */
public class LsProCustomerDTO extends CustomerDTO {

    /** Customer's ID */
    private Long id;
    
    /** Date/Time customer was modified in POS system - local time */
    private Long dateModified; 
    
    /** Customer's email address */
    private String emailAddress;
    
    /** Customer first name */
    private String firstName;
    
    /** Customer last name */
    private String lastName;
    
    /** FirstName + LastName */
    private String name;
    
    /** Billing address from Customer card */
    private String billingCity;
    
    /** Billing address from Customer card */
    private String billingState;
    
    /** Billing address from Customer card */
    private String billingStreet;
    
    /** Billing address from Customer card */
    private String billingZip;
    
    /** Billing address from Customer card */
    private String billingCountry;
    
    @Override
    public long getRemoteId() {
        return this.id;
    }

    @Override
    public Timestamp getLastModified() {
        return new Timestamp(this.dateModified);
    }

    @Override
    public String getName() {
        if(StringUtils.hasLength(this.name)){
            return name;
        } else if(StringUtils.hasLength(this.firstName)){
            return this.firstName + this.lastName;
        } else {
            return null;
        }
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public String getEmail() {
        return this.emailAddress;
    }

    /**
     * Customer's phone number is unknown for Lightspeed Pro customers
     */
    @Override
    public String getPhoneNumber() {
        return null;
    }

    @Override
    public String getAddress() {
        return this.billingStreet;
    }

    /**
     * Lightspeed Pro doesn't divide the customer address into two lines
     */
    @Override
    public String getAddress2() {
        return null;
    }

    @Override
    public String getCity() {
        return this.billingCity;
    }

    @Override
    public String getPostalCode() {
        return this.billingZip;
    }

    @Override
    public String getState() {
        return this.billingState;
    }

    @Override
    public String getCountry() {
        return this.billingCountry;
    }

    /**
     * No field for notes in Lightspeed Pro
     */
    @Override
    public String getNotes() {
        return null;
    }
    
    @ExternalField(value = "Id", required = true)
    public void setId(Long id) {
        this.id = id;
    }

    @ExternalField(value = "DateModified")
    public void setDateModified(String dateModified) {
        this.dateModified = ISO8061DateTimeConverter.stringToDate(dateModified).getTime();
    }

    @ExternalField(value = "Email")
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @ExternalField(value = "FirstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @ExternalField(value = "LastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @ExternalField(value = "Name")
    public void setName(String name) {
        this.name = name;
    }

    @ExternalField(value = "BillingCity")
    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    @ExternalField(value = "BillingState")
    public void setBillingState(String billingState) {
        this.billingState = billingState;
    }

    @ExternalField(value = "BillingStreet")
    public void setBillingStreet(String billingStreet) {
        this.billingStreet = billingStreet;
    }

    @ExternalField(value = "BillingZip")
    public void setBillingZip(String billingZip) {
        this.billingZip = billingZip;
    }

    @ExternalField(value = "BillingCountry")
    public void setBillingCountry(String billingCountry) {
        this.billingCountry = billingCountry;
    } 
}
