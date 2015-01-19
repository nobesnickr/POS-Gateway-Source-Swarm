package com.sonrisa.swarm.vend.dto;

import java.sql.Timestamp;
import java.text.ParseException;

import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.vend.api.util.TimeConversionUtil;

/**
 * Representation of a Vend customer
 */
public class VendCustomerDTO extends CustomerDTO {

    /**
     * Customer's foreign id
     */
    private long customerId;
    
    /**
     * Customer's id as an UUID string
     */
    private String uuidCustomerId;
    
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
    
    /**
     * Customer's phone number
     */
    private String phoneNumber;

    /**
     * Customer's Address 1
     */
    private String address1;
    
    /**
     * Customer's Address 2
     */
    private String address2;
    
    /**
     * Customer's City
     */
    private String city;
    
    /**
     * Customer's State
     */
    private String state;
    
    /** Customer's postal code */
    private String postalCode;
        
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
        return phoneNumber;
    }

    @Override
    public String getAddress() {
        return address1;
    }

    @Override
    public String getAddress2() {
        return address2;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public String getPostalCode() {
        return postalCode;
    }

    @Override
    public String getState() {
        return state;
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
    public void setUuidCustomerId(String uuidCustomerId) {
        this.uuidCustomerId = uuidCustomerId;
    }
    
    public String getUuidCustomerId() {
        return uuidCustomerId;
    }
    
    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    @ExternalField("name")
    public void setFirstName(String firstName) {
    	//FirstName has the complete name,it is necessary to split the string
    	int separationIndex = firstName.lastIndexOf(" ");
        this.firstName = (separationIndex == -1) ? 
        		firstName : firstName.substring(0,separationIndex);
    }

    @ExternalField("name")
    public void setLastName(String lastName) {
    	int separationIndex = lastName.lastIndexOf(" ");
        this.lastName = (separationIndex == -1) ? 
        		"" : lastName.substring(separationIndex+1);
    }

    @ExternalField("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @ExternalField("updated_at")
    public void setLastModified(String lastModified) throws ParseException {
        this.lastModified = TimeConversionUtil.stringToDate(lastModified);
    }
    
    @ExternalField("phone")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    @ExternalField("physical_address1")
    public void setAddress1(String address1) {
        this.address1 = address1;
    }
    
    @ExternalField("physical_address2")
    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    
    @ExternalField("physical_city")
    public void setCity(String city) {
        this.city = city;
    }
    
    @ExternalField("physical_state")
    public void setState(String state) {
        this.state = state;
    }
    
    @ExternalField("physical_postcode")
    public void setPostalCode(String postCode) {
        this.postalCode = postCode;
    }
}
