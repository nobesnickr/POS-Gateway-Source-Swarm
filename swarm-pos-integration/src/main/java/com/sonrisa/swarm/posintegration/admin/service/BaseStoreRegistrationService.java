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

package com.sonrisa.swarm.posintegration.admin.service;

import hu.sonrisa.backend.dao.filter.SimpleFilter;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;
import com.sonrisa.swarm.posintegration.service.ApiService;

/**
 * Base class for implementation store services capable of registering 
 * new store entities.
 * 
 * @author Barnabas
 */
public abstract class BaseStoreRegistrationService {
    
    /**
     * API service to convert API name to API id
     */
    @Autowired
    private ApiService apiService;

    /**
     * Used store service
     */
    @Autowired
    private StoreService storeService;
    
    /**
     * Encryption utility used to encrypt sensible data
     */
    @Autowired
    protected AESUtility aesUtility;
    

    /**
     * Creates store instance, if store with same username/establishment already exists
     * then returns with that store instance, but updates its authentication fields with
     * the new values.
     * 
     * @return
     */
    protected StoreEntity findOrCreateStore(final String storeName, final String userName, final String storeFilter, final String apiName){
        
        final Long apiId = apiService.findByName(apiName).getApiId();
            
        StoreEntity store = findStoreByUsernameAndStoreFilter(apiId, userName, storeFilter);
        
        if(store == null){
            store = new StoreEntity();
            store.setStoreFilter(storeFilter);
            store.setActive(Boolean.FALSE);
            store.setCreated(new Date());
            store.setUsername(aesUtility.aesEncryptToBytes(userName));
            store.setApiId(apiId);
            store.setName(storeName);
            
            if(StringUtils.isEmpty(storeName)){
                if(StringUtils.isEmpty(storeFilter)){
                    store.setName(userName);
                } else {
                    store.setName(storeFilter);
                }
            }
        } 
        
        return store;
    }
    

    /**
     * Checks whether a certain userName and store filter is already in the stores table for the API
     */
    protected StoreEntity findStoreByUsernameAndStoreFilter(final Long apiId, final String userName, final String locationName){
        
        SimpleFilter<StoreEntity> filter = SimpleFilter.of(StoreEntity.class)
                .addParameter(StoreEntity.FIELD_API_ID, apiId)
                .addParameter(StoreEntity.FIELD_USERNAME, aesUtility.aesEncryptToBytes(userName))
                .addParameter(StoreEntity.FIELD_STORE_FILTER, locationName);
        
        final List<StoreEntity> storeFromDb = storeService.find(filter, 0, 0);
        
        if (storeFromDb.size() > 1){
            throw new RuntimeException("Maximum one Lightspeed Pro (apiId: "+apiId
                    +") store should exists with this userName(siteName): "+userName + " and locationName: " + locationName);
        }
        
        return storeFromDb.isEmpty() ? null : storeFromDb.get(0);
    }


    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }


    public void setAesUtility(AESUtility aesUtility) {
        this.aesUtility = aesUtility;
    }
}