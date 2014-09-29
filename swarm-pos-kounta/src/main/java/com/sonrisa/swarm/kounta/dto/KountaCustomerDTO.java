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
package com.sonrisa.swarm.kounta.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;

/**
 * Representation of a Kounta customer
 * 
 * @author Barnabas
 */
public class KountaCustomerDTO extends CustomerDTO {

    /**
     * Customer's foreign id
     */
    private long customerId;
    
    /**
     * Customer's first name
     */
    private String firstName;
    
    /**
     * Customer's last name
     */
    private String lastName;
    
    /**
     * Customer's email address
     */
    private String email;
    
    /**
     * Customer's last known invoice
     */
    private Timestamp lastModified;

    @Override
    public long getRemoteId() {
       return customerId;
    }

    @Override
    public Timestamp getLastModified() {
        return lastModified;
    }

    @Override
    public String getName() {
        return firstName + " " + lastName;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPhoneNumber() {
        return null;
    }

    @Override
    public String getAddress() {
        return null;
    }

    @Override
    public String getAddress2() {
        return null;
    }

    @Override
    public String getCity() {
        return null;
    }

    @Override
    public String getPostalCode() {
        return null;
    }

    @Override
    public String getState() {
        return null;
    }

    @Override
    public String getCountry() {
        return null;
    }

    @Override
    public String getNotes() {
        return null;
    }

    @ExternalField(value="id", required=true)
    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    @ExternalField("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @ExternalField("last_name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @ExternalField("email")
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Should be derived from invoice
     */
    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }
}
