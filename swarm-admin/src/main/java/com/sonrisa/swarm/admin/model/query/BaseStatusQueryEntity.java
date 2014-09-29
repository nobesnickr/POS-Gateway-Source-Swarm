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

package com.sonrisa.swarm.admin.model.query;

import java.util.HashSet;
import java.util.Set;

import com.sonrisa.swarm.admin.model.RpStatusEntity;
import com.sonrisa.swarm.admin.service.BaseStatusService;
import com.sonrisa.swarm.model.legacy.StoreEntity;

/**
 * Base class for filter entity which enables filtering, ordering, etc
 * for the {@link StoreStatus} or {@link RpStatusEntity}
 * entities returned by the {@link BaseStatusService}
 * 
 * @author Barnabas
 */
public abstract class BaseStatusQueryEntity {

    /**
     * Database key for the {@link StoreEntity}
     */
    private Long storeId = null;
    
    /**
     * Result will be the query's skip,skip+1,...skip+take-1 items
     */
    private int skip = 0;
    
    /**
     * Result will be the query's skip,skip+1,...skip+take-1 items
     */
    private int take = 200;
    
    /**
     * Values can be store_id, name, created (default)
     */
    private String orderBy = "created";
    
    /**
     * Order direction
     */
    private OrderDirection orderDir = OrderDirection.DESCENDING;
    
    /**
     * Filter for stores with matching api
     */
    private Set<String> api = new HashSet<String>();
    
    /**
     * Filter for store status (OK, WARNING, ERROR)
     */
    private Set<StoreStatus> status = new HashSet<StoreStatus>(); 
    
    /**
     * Possible values of the <i>orderBy</i> JSON field
     */
    public enum OrderDirection {
        ASCENDING("asc"),
        DESCENDING("desc");
        
        private final String value;
        
        private OrderDirection(String value){
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    /**
     * Possible values of the <i>status</i> JSON field
     */
    public enum StoreStatus {
        OK("OK"),
        WARNING("WARNING"),
        ERROR("ERROR");
        
        private final String value;
        
        private StoreStatus(String value){
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
    
    /**
     * Database key for the {@link StoreEntity}
     */
    public Long getStoreId() {
        return storeId;
    }

    /**
     * Database key for the {@link StoreEntity}
     */
    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    /**
     * Result will be the query's skip,skip+1,...skip+take-1 items
     */
    public int getSkip() {
        return skip;
    }

    /**
     * Result will be the query's skip,skip+1,...skip+take-1 items
     */
    public void setSkip(int skip) {
        this.skip = skip;
    }

    /**
     * Result will be the query's skip,skip+1,...skip+take-1 items
     */
    public int getTake() {
        return take;
    }

    /**
     * Result will be the query's skip,skip+1,...skip+take-1 items
     */
    public void setTake(int take) {
        this.take = take;
    }
    
    /**
     * Values can be store_id, name, created (default)
     */
    public String getOrderBy() {
        return orderBy;
    }
    
    /**
     * Values can be store_id, name, created (default)
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
    
    /**
     * Order direction
     */
    public OrderDirection getOrderDir() {
        return orderDir;
    }
    
    /**
     * Order direction
     */
    public void setOrderDir(OrderDirection orderDir) {
        this.orderDir = orderDir;
    }
    
    /**
     * Filter for stores with matching api
     */
    public Set<String> getApi() {
        return api;
    }

    /**
     * Filter for stores with matching api
     */
    public void setApi(Set<String> api) {
        this.api = api;
    }

    /**
     * Filter for store status (OK, WARNING, ERROR)
     */
    public Set<StoreStatus> getStatus() {
        return status;
    }

    /**
     * Filter for store status (OK, WARNING, ERROR)
     */
    public void setStatus(Set<StoreStatus> status) {
        this.status = status;
    }
}
