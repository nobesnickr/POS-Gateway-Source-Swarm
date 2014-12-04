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
package com.sonrisa.swarm.model.staging;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sonrisa.swarm.model.staging.annotation.StageInsertableAttr;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableType;
import com.sonrisa.swarm.model.staging.retailpro.RetailProAttr;
import com.sonrisa.swarm.model.staging.retailpro.converter.FieldConcatenationConverter;

/**
 * This entity represents a record in the staging_customers table.
 *
 * @author joe
 */
@Entity
@Table(name = CustomerStage.TABLE_NAME)
@StageInsertableType(dbTableName = "customers")
public class CustomerStage extends BaseStageEntity {
    	
    private static final long serialVersionUID = -2111237543641944660L;

    public static final String TABLE_NAME = "staging_customers";
            
    private Long id;

    /**
     * Inner ID of the store where this staging entity comes from.
     * 
     * Inner means the ID identifies this store within the Swarm System.
     * This ID could be null if the entity does not know the inner ID of its store.
     * E.g.: Entities from RetailPro stores.
     */
    private Long storeId;          
    
    // ------------------------------------------------------------------------
    // ~ Attributes from RetailPro
    // ------------------------------------------------------------------------      

    /**
     * Each Retail Pro store is identified by three values:
     * <ul>
     *  <li>SwarmId (picked for each swarm-partner)</li>
     *  <li>Store number (e.g. NAS)</li>
     *  <li>Subsidiary (e.g. 001), for Rp9 this is optional but clients sends it anyway</li>
     * </ul>
     * 
     * These values are only used for Retail Pro stores.
     */
    @RetailProAttr
    private String swarmId;
    
    @RetailProAttr(value = "StoreNo", maxLength = 100)
    private String lsStoreNo;

    @RetailProAttr(value = "SbsNo", maxLength = 100)
    private String lsSbsNo;   
        
    @RetailProAttr(value = "CustSid", maxLength = 100)
    private String lsCustomerId;

    @RetailProAttr(converter = FieldConcatenationConverter.class, params = {"FirstName" , "LastName"}, value = " ", maxLength = 100)    
    private String name;

    @RetailProAttr(value = "FirstName", maxLength = 50)
    private String firstname;

    @RetailProAttr(value = "LastName", maxLength = 50)
    private String lastname;

    @RetailProAttr(value = "Email", maxLength = 100)
    private String email;

    @RetailProAttr(value = "Phone", maxLength = 20)
    private String phone;

    @RetailProAttr(value = "Address1", maxLength = 100)
    private String address1;

    @RetailProAttr(value = "Address3", maxLength = 100)
    private String address2;

    @RetailProAttr(value = "Address2", maxLength = 50)
    private String city;
    
    // TODO maybe we could write a converter which detects the state from the ZIP code
    private String state;
    
    private String notes;
    
    private String lastModified;
    
    private String postalCode;

    // ------------------------------------------------------------------------
    // ~ Getters / setters
    // ------------------------------------------------------------------------ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Override
    public Long getId() {
        return id;
    }        

    public void setId(Long id) {
        this.id = id;
    }

    @StageInsertableAttr(dbColumnName="swarm_id")
    @Column(name = "swarm_id")
    @Override
    public String getSwarmId() {
        return swarmId;
    }

    @Override
    public void setSwarmId(String swarmId) {
        this.swarmId = swarmId;
    }

    /**
     * {@inheritDoc }
     * 
     * @return 
     */
    @StageInsertableAttr(dbColumnName="ls_store_no")
    @Column(name = "ls_store_no")
    @Override
    public String getLsStoreNo() {
        return lsStoreNo;
    }

    public void setLsStoreNo(String lsStoreNo) {
        this.lsStoreNo = lsStoreNo;
    }

    /**
     * {@inheritDoc }
     * 
     * @return 
     */
    @StageInsertableAttr(dbColumnName="ls_sbs_no")
    @Column(name = "ls_sbs_no")
    @Override
    public String getLsSbsNo() {
        return lsSbsNo;
    }

    public void setLsSbsNo(String lsSbsNo) {
        this.lsSbsNo = lsSbsNo;
    }

    @StageInsertableAttr(dbColumnName="ls_customer_id")
    @Column(name = "ls_customer_id")
    public String getLsCustomerId() {
        return lsCustomerId;
    }

    public void setLsCustomerId(String lsCustomerId) {
        this.lsCustomerId = lsCustomerId;
    }

    @StageInsertableAttr(dbColumnName="name")
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @StageInsertableAttr(dbColumnName="firstname")
    @Column(name = "firstname")
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @StageInsertableAttr(dbColumnName="lastname")
    @Column(name = "lastname")
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @StageInsertableAttr(dbColumnName="email")
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @StageInsertableAttr(dbColumnName = "phone")
    @Column(name = "phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @StageInsertableAttr(dbColumnName = "address1")
    @Column(name = "address1")
    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    @StageInsertableAttr(dbColumnName = "address2")
    @Column(name = "address2")
    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    @StageInsertableAttr(dbColumnName = "city")
    @Column(name = "city")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @StageInsertableAttr(dbColumnName = "state")
    @Column(name = "state")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @StageInsertableAttr(dbColumnName = "notes")
    @Column(name = "notes")
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Returns the inner ID of the store where this staging entity comes from.
     * 
     * Inner means the ID identifies this store within the Swarm System.
     * This ID could be null if the entity does not know the inner ID of its store.
     * E.g.: Entities from RetailPro stores.
     */
    @Column(name = "store_id")
    @Override
    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }                

    @StageInsertableAttr(dbColumnName = "last_modified")
    @Column(name = "last_modified")    
    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
    
    @StageInsertableAttr(dbColumnName = "postcode")
    @Column(name = "postcode")
	public String getPostCode() {
		return postalCode;
	}

	public void setPostCode(String postCode) {
		this.postalCode = postCode;
	}

    
    // ------------------------------------------------------------------------
    // ~ Object methods
    // ------------------------------------------------------------------------
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lsCustomerId == null) ? 0 : lsCustomerId.hashCode());
        result = prime * result + ((lsSbsNo == null) ? 0 : lsSbsNo.hashCode());
        result = prime * result + ((lsStoreNo == null) ? 0 : lsStoreNo.hashCode());
        result = prime * result + ((storeId == null) ? 0 : storeId.hashCode());
        result = prime * result + ((swarmId == null) ? 0 : swarmId.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "CustomerStage [id=" + id + ", storeId=" + storeId + ", swarmId=" + swarmId + ", lsStoreNo=" + lsStoreNo
                + ", lsSbsNo=" + lsSbsNo + ", lsCustomerId=" + lsCustomerId + ", name=" + name + ", firstname="
                + firstname + ", lastname=" + lastname + ", email=" + email + ", phone=" + phone + ", address1="
                + address1 + ", address2=" + address2 + ", city=" + city + ", state=" + state + ", notes=" + notes
                + ", lastModified=" + lastModified + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CustomerStage other = (CustomerStage) obj;
        if (lsCustomerId == null) {
            if (other.lsCustomerId != null)
                return false;
        } else if (!lsCustomerId.equals(other.lsCustomerId))
            return false;
        if (lsSbsNo == null) {
            if (other.lsSbsNo != null)
                return false;
        } else if (!lsSbsNo.equals(other.lsSbsNo))
            return false;
        if (lsStoreNo == null) {
            if (other.lsStoreNo != null)
                return false;
        } else if (!lsStoreNo.equals(other.lsStoreNo))
            return false;
        if (storeId == null) {
            if (other.storeId != null)
                return false;
        } else if (!storeId.equals(other.storeId))
            return false;
        if (swarmId == null) {
            if (other.swarmId != null)
                return false;
        } else if (!swarmId.equals(other.swarmId))
            return false;
        return true;
    }
}
