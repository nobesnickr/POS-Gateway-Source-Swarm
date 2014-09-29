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
package com.sonrisa.swarm.admin.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.admin.model.StoreAdminServiceEntity;
import com.sonrisa.swarm.admin.service.StoreAdminService;
import com.sonrisa.swarm.admin.service.exception.InvalidAdminRequestException;
import com.sonrisa.swarm.admin.service.exception.UnknownStoreException;
import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.service.ApiService;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity.ApiType;

/**
 * Implementation of the {@link StoreAdminServiceImpl} class.
 */
@Service
@Transactional
public class StoreAdminServiceImpl implements StoreAdminService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreAdminServiceImpl.class);
    
    /**
     * Store service for accessing and manipulating stores
     */
    @Autowired
    private StoreService storeService;

    @Autowired
    private ApiService apiService;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Long storeId, StoreAdminServiceEntity entity) throws InvalidAdminRequestException {

        StoreEntity store = storeService.find(storeId);
        if(store == null){
            throw new UnknownStoreException(storeId);
        }
        
        // Verify that store is associated with the GW and isn't Retail Pro
        boolean found = false;
        for(ApiEntity api : apiService.findManyByType(ApiType.PULL_API)){
            if(api.getApiId() == store.getApiId()){
                found = true;
                break;
            }
        }
        
        if(!found){
            throw new InvalidAdminRequestException(
                    "Illegal API, this store is associated with " 
                     + apiService.findById(store.getApiId()).getApiName());
        }
        
        LOGGER.info("Updating  store with storeId:{}, setting values: {}", storeId, entity);
        
        store.setName(entity.getName());
        store.setActive(entity.getActive());
        store.setNotes(entity.getNotes());
        storeService.save(store);
    }

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }
}
