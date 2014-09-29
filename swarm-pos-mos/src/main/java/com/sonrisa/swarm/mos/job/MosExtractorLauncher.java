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
package com.sonrisa.swarm.mos.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sonrisa.swarm.job.ExtractorLauncher;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.mos.MosAccount;
import com.sonrisa.swarm.mos.MosExtractor;
import com.sonrisa.swarm.posintegration.extractor.ExternalExtractor;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;

/**
 * This tasklet is responsible for launching the given {@link MosExtractor}.
 *
 */
public class MosExtractorLauncher extends ExtractorLauncher {

    /** External extractor to launch.  */
    @Autowired
    @Qualifier("MosExtractor")
    private ExternalExtractor extractor;
    
    /**
     * Service decrypting strings from the MySQL
     * database
     */
    @Autowired
    private AESUtility aesUtility;
    
    /** Datastore to write te received data into. */
    @Autowired
    private SwarmDataWarehouse dataStore;

    @Override
    protected SwarmStore createAccount(final StoreEntity store) {
        MosAccount account = new MosAccount(store.getId());
        
        final byte[] apiUrl = store.getApiUrl();     
        final byte[] encryptedApiKey = store.getApiKey();
        final byte[] encryptedOauthToken = store.getOauthToken();
        final String storeName = store.getName();
        final String accountId = Integer.toString(store.getAccountNumber());
        final String shopId = store.getStoreFilter();
        
        if(encryptedOauthToken != null && encryptedOauthToken.length != 0){
            account.setEncryptedOauthToken(encryptedOauthToken, aesUtility);
        }
        if(encryptedApiKey != null && encryptedApiKey.length != 0){
            account.setEncryptedApiKey(encryptedApiKey, aesUtility);
        }
        if(apiUrl != null && apiUrl.length != 0){
            account.setUrlBase(apiUrl);
        }
        
        account.setShopId(shopId);
        account.setStoreName(storeName);
        account.setAccountId(accountId);
                
        return account;                
    }

    // -----------------------------------------------------------------------
    // ~ Setters / getters
    // -----------------------------------------------------------------------       

    @Override
    public ExternalExtractor getExtractor() {
        return extractor;
    }

    public void setDataStore(SwarmDataWarehouse dataStore) {
        this.dataStore = dataStore;
    }

    public void setExtractor(ExternalExtractor extractor) {
        this.extractor = extractor;
    }            

    @Override
    public SwarmDataWarehouse getDataStore() {
        return dataStore;
    }
}
