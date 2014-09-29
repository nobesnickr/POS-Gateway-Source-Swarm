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
package com.sonrisa.swarm.revel.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;

/**
 * POS specific implementation of {@link DWTransferable} interface for the {@link CustomerDTO} class.
 */
public class RevelCustomerDTO extends CustomerDTO {

    /** The ID of the customer */
    private long customerId;
    
    /** The creation date of the customer (or last modification date) */
    private Timestamp lastModified = new Timestamp(0L);

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
    @Override
    public long getRemoteId() {
        return customerId;
    }

    @Override
    public Timestamp getLastModified() {
        return this.lastModified;
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
        return phoneNumber;
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
        return "";
    }

    /**
     * @param customerId the customerId to set
     */
    @ExternalField(value = "id", required = true)
    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    /**
     * @param lastModified the lastModified to set
     */
    @ExternalField("updated_date")
    public void setLastModified(String lastModified) {
        this.lastModified = new Timestamp(ISO8061DateTimeConverter.stringToDate(lastModified).getTime());
    }

    /**
     * @param firstName the firstName to set
     */
    @ExternalField("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @param lastName the lastName to set
     */

    @ExternalField("last_name")
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
    @ExternalField("phone_number")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @param addresses The object containing the addresses
     */
    @ExternalField("addresses")
    public void setAddressDetails(Iterable<ExternalDTO> addresses) {
        for(ExternalDTO dto : addresses){
            this.address = dto.getText("street_1");
            this.address2 = dto.getText("street_2");
            this.city = dto.getText("city");
            this.country = dto.getText("country");
            this.state = dto.getText("state");
            this.postalCode = dto.getText("zipcode");
            break;
        }
    }
}
