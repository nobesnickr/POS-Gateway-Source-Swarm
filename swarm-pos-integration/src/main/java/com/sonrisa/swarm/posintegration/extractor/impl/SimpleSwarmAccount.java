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
package com.sonrisa.swarm.posintegration.extractor.impl;

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;

/**
 * Simple implementation of the {@link SwarmStore} containing the most commonly used fields.
 * 
 * @author Barnabas
 */
public class SimpleSwarmAccount extends BaseSwarmAccount {

    /**
     * Store filter
     */
    private String storeFilter;
    
    /**
     * Store's timezone
     */
    private String timeZone;
    
    /**
     * Value identifying this account in the remote system
     */
    private String accountId;
    
    /**
     * Default constructor doing nothing
     */
    protected SimpleSwarmAccount() {
        super(null);
    }
    
    /**
     * {@inheritDoc}
     * @param storeId
     */
    public SimpleSwarmAccount(StoreEntity store, AESUtility aesUtility) {
        super(store.getId());
        setStoreName(store.getName());
        setTimeZone(store.getTimeZone());

        if(store.getUsername() == null){
            throw new IllegalArgumentException("No username for store: " + store);
        }
        
        setAccountId(aesUtility.aesDecrypt(store.getUsername()));
        setStoreFilter(store.getStoreFilter());
    }
    
    @Override
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getStoreFilter() {
        return storeFilter;
    }

    public void setStoreFilter(String storeFilter) {
        this.storeFilter = storeFilter;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
