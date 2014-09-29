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

import com.sonrisa.swarm.model.BaseSwarmEntity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * This entity represents a store in the legacy (aka analytics DB).
 * 
 * It encapsulates all the essential information about the store.
 *
 * @author joe
 */
@Entity
@Table(name = StoreEntity.TABLE_NAME)
public class StoreEntity extends BaseSwarmEntity {
    
    /** Name of the DB table. */
    public static final String TABLE_NAME = "stores";
    
    /** String representation of {@link #apiId} field's name. */
    public static final String FIELD_API_ID = "apiId";    
    /** String representation of {@link #username} field's name. */
    public static final String FIELD_USERNAME = "username";
    /** String representation of {@link #storeFilter} field's name. */
    public static final String FIELD_STORE_FILTER = "storeFilter";
    
    private Long id;
    
    private String name;
    
    private Date created;
    
    private Boolean active;
    
    private String notes;      
    
    private Long apiId;
    
    private byte[] apiUrl;
    
    private byte[] apiKey;
        
    private byte[] username;
    
    private byte[] password;
    
    private byte[] oauthToken;

    private int accountNumber;
    
    private String storeFilter;
    
    private String timeZone;
    
    
    // ------------------------------------------------------------------------
    // ~ Common methods
    // ------------------------------------------------------------------------
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StoreEntity other = (StoreEntity) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "StoreEntity [id=" + id + ", name=" + name + "]";
    }
            
    // ------------------------------------------------------------------------
    // ~ Getters / setters
    // ------------------------------------------------------------------------
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long storeId) {
        this.id = storeId;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Column(name = "active")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Column(name = "notes")
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Column(name = "api_id")
    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    @Column(name = "api_url")
    public byte[] getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(byte[] apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Column(name = "api_key")
    public byte[] getApiKey() {
        return apiKey;
    }

    public void setApiKey(byte[] apiKey) {
        this.apiKey = apiKey;
    }

    @Column(name = "username")
    public byte[] getUsername() {
        return username;
    }

    public void setUsername(byte[] username) {
        this.username = username;
    }

    @Column(name = "password")
    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    @Column(name="oauth_token")
    public byte[] getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(byte[] oauthToken) {
        this.oauthToken = oauthToken;
    }

    @Column(name="account_id")
    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    @Column(name = "tz")
    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Column(name = "store_filter")
    public String getStoreFilter() {
        return storeFilter;
    }

    public void setStoreFilter(String storeFilter) {
        this.storeFilter = storeFilter;
    }
}
