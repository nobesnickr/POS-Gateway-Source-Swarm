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
package com.sonrisa.swarm.model.legacy;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 * Customer entity. It represents a record from the "customers" table in the legacy DB.
 *
 * @author joe
 */
@Entity
@Table(name = CustomerEntity.TABLE_NAME)
public class CustomerEntity extends BaseLegacyEntity {
    
    /** Name of the DB table. */
    public static final String TABLE_NAME = "customers";

    private static final long serialVersionUID = 7371457892288623443L;
    
    private Long id;
    
    /** 
     * Foreign customer id.
     * It identifies the customer within a store.
     */
    private String name;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String notes;
    private Date lastModified;

    // ------------------------------------------------------------------------
    // ~ Common methods
    // ------------------------------------------------------------------------
    @Override
	public String toString() {
		return "CustomerEntity [id=" + id + ", name=" + name + ", firstname="
				+ firstname + ", lastname=" + lastname + ", email=" + email
				+ ", phone=" + phone + ", address1=" + address1 + ", address2="
				+ address2 + ", city=" + city + ", state=" + state
				+ ", lastModified=" + lastModified + "]";
	}
            
    // ------------------------------------------------------------------------
    // ~ Getters / setters
    // ------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    @Override
    public Long getId() {
       return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
        
    @Column(name = "ls_customer_id")
    public Long getLsCustomerId() {
        return getLegacySystemId();
    }

    public void setLsCustomerId(Long lsCustomerId) {
        setLegacySystemId(lsCustomerId);
    }
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    @Override
    public StoreEntity getStore() {
        return super.getStore();
    }
    
    
    @Column(name = "name")
    @Size(max=100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "firstname")
    @Size(max=50)
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Column(name = "lastname")
    @Size(max=50)
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Column(name = "email")
    @Size(max=100)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "phone")
    @Size(max=20)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "address1")
    @Size(max=100)
    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    @Column(name = "address2")
    @Size(max=100)
    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    @Column(name = "city")
    @Size(max=50)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "state")
    @Size(max=2)
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Column(name = "notes")
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Column(name = "last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }        
}
