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

package com.sonrisa.swarm.admin.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sonrisa.swarm.admin.model.query.BaseStatusQueryEntity.StoreStatus;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;

/**
 * Entity containing the status (invoice_count, active, error, etc.) for 
 * a store {@link StoreEntity} or a {@link RpStoreEntity}
 * 
 * @author Barnabas
 */
@JsonInclude(Include.NON_NULL)  
public abstract class BaseStatusEntity {
    /**
     * Database key for the {@link StoreEntity}
     */
    @JsonProperty("store_id")
    private Long storeId;
    
    /**
     * {@link StoreEntity#getName()}
     */
    private String name;
    
    /**
     * Name for the {@link StoreEntity#getApiId()}
     */
    private String api;
    
    /**
     * Creation date of the {@link StoreEntity#getCreatedAt()}
     */
    private String created;
    
    /**
     * {@link StoreEntity#getNotes()
     */
    private String notes;
    
    /**
     * Value indicating the store's status, can be <code>OK</code>, <code>WARNING</code> or <code>ERROR</code>
     */
    private StoreStatus status;
    
    /**
     * Value indicating why the store's status is WARNING or ERROR
     */
    private List<String> reason;
    

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
    
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public StoreStatus getStatus() {
        return status;
    }

    public void setStatus(StoreStatus status) {
        this.status = status;
    }
    
    public List<String> getReason() {
        return reason;
    }

    public void setReason(List<String> reason) {
        this.reason = reason;
    }
}
