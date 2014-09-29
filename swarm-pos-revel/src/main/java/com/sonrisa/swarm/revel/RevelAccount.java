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
package com.sonrisa.swarm.revel;

import org.apache.commons.lang3.StringUtils;

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.extractor.impl.SimpleSwarmAccount;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;


/**
 * Class for a partner's Revel account
 * @note Each RevelAccount equals one store
 * @author sonrisa
 */
public class RevelAccount extends SimpleSwarmAccount {

    /**
     * API key
     */
    private String apiKey = "";
    
    /** 
     * API secret 
     */
    private String apiSecret = "";
    
    /**
     * Initializes an instance of a RevelAccount
     * @param storeId The local id for the store from the Swarm ID
     */
    public RevelAccount (StoreEntity store, AESUtility aesUtility) {
        super(store, aesUtility);     
        
        final byte[] apiKey = store.getApiKey();
        final byte[] apiSecret = store.getPassword();

        if(apiKey != null && apiKey.length != 0){
            this.apiKey = aesUtility.aesDecrypt(apiKey);
        }
        
        if(apiSecret != null && apiSecret.length != 0){
            this.apiSecret = aesUtility.aesDecrypt(apiSecret);
        }
    }
    
    /**
     * Constructor for creating dummy accounts
     */
    public RevelAccount() {
        super();
    }
    
    public void setUsername(String accountId){
        setAccountId(accountId);
    }
    
    public String getUsername(){
        return getAccountId();
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }
    
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /** 
     * @return Value indicating that this account has a valid establishment filter
     */
    public boolean hasStoreFilter(){
        return !StringUtils.isEmpty(getStoreFilter());
    }
}
