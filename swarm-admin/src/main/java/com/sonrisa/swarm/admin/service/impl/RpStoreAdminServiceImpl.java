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

import java.util.Arrays;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.admin.model.RpStoreAdminServiceEntity;
import com.sonrisa.swarm.admin.service.RpStoreAdminService;
import com.sonrisa.swarm.admin.service.exception.InvalidAdminRequestException;
import com.sonrisa.swarm.admin.service.exception.UnknownStoreException;
import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;
import com.sonrisa.swarm.retailpro.service.RpStoreService;

/**
 * Implementation for the {@link RpStoreAdminService} service.
 */
@Service
@Transactional
public class RpStoreAdminServiceImpl implements RpStoreAdminService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpStoreAdminServiceImpl.class);

    /**
     * Store service for accessing and manipulating stores
     */
    @Autowired
    private StoreService storeService;
    
    /**
     * Retail Pro Store service for accessing and manipulating stores
     */
    @Autowired
    private RpStoreService rpStoreService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Long storeId, RpStoreAdminServiceEntity entity) throws InvalidAdminRequestException {

        StoreEntity store = storeService.find(storeId);
        if(store == null){
            throw new UnknownStoreException(storeId);
        }
        
        RpStoreEntity rpStore = rpStoreService.findByStoreId(storeId);
        if(rpStore == null){
            throw new InvalidAdminRequestException("Retail Pro store not foudn for " + storeId);
        }
        
        if(!isValidTimezone(entity.getTimezone())){
            throw new InvalidAdminRequestException("Invalid timezone: " + entity.getTimezone());
        }
        
        LOGGER.info("Updating Retail Pro store with storeId:{}, setting values: {}", storeId, entity);
        
        // Update existing entities        
        store.setName(entity.getName());
        store.setNotes(entity.getNotes());
        storeService.save(store);
        
        rpStore.setTimeZone(entity.getTimezone());
        rpStore.setStoreName(entity.getName());
        rpStoreService.save(rpStore);
    }
    
    /**
     * Tests whether timezone is valid
     */
    private boolean isValidTimezone(String timezone){
        return Arrays.asList(TimeZone.getAvailableIDs()).contains(timezone);
    }

    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }

    public void setRpStoreService(RpStoreService rpStoreService) {
        this.rpStoreService = rpStoreService;
    }
}
