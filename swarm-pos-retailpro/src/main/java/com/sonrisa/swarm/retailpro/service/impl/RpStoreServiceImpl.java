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
package com.sonrisa.swarm.retailpro.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.retailpro.dao.RpStoreDao;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;
import com.sonrisa.swarm.retailpro.rest.model.JsonStore;
import com.sonrisa.swarm.retailpro.service.RpStoreService;
import com.sonrisa.swarm.retailpro.util.mapper.EntityMapper;

/**
 * This service contains the RetailPro specific store operations.
 *
 * @author joe
 */
@Service
@Transactional
public class RpStoreServiceImpl implements RpStoreService{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RpStoreServiceImpl.class);
    
    /** DAO to provide the CRUD operations for the RetailPro store objects. */
    @Autowired
    private RpStoreDao storeDao;
    
    /** Utility to convert the JSON objects to business entities. */
    @Autowired
    private EntityMapper mapper;

    /**
     * {@inheritDoc }
     * 
     * @param store
     * @return 
     */
    @Override
    public Long save(RpStoreEntity store) {
        if (store.getId() != null){
            LOGGER.debug("This RetailPro store is already exist so it will be updated: " + store);
            storeDao.merge(store);
        }else{
            LOGGER.debug("This RetailPro store does not exist yet so it will be created: " + store);
            storeDao.persist(store);
            storeDao.flush();
        }
                
        return store.getId();
    }
        
    /**
     * {@inheritDoc }
     * 
     * @param jsonStore
     * @return 
     */
    @Override
    public void save(String swarmId, String posSoftware, JsonStore... jsonStores) {
        
        // Avoid null
        if(StringUtils.isEmpty(posSoftware)){
            posSoftware = "";
        }
        
        if (jsonStores != null) {
            for (JsonStore jsonStore : jsonStores) {
                LOGGER.debug("Saving RetailPro store object: " + jsonStore + " swarmId: " + swarmId + " posSoftware:" + posSoftware);

                RpStoreEntity rpStore = storeDao.findBySbsNoAndStoreNoAndSwarmId(jsonStore.getSbsNumber(), jsonStore.getStoreNumber(), swarmId);

                if (rpStore == null) {
                    LOGGER.debug("RetailPro store does not exist yet, a new one will be created: " + jsonStore + " swarmId: " + swarmId + " posSoftware:" + posSoftware);

                    rpStore = mapper.copyToRpStore(jsonStore);
                    rpStore.setSwarmId(swarmId);
                    rpStore.setPosSoftware(posSoftware);
                    storeDao.persist(rpStore);
                    storeDao.flush();
                } else {
                    rpStore = mapper.copyToRpStore(rpStore, jsonStore);
                }

                LOGGER.debug("RetailPro store has been saved, ID: " + rpStore.getId() + " swarmId: " + swarmId + " posSoftware:" + posSoftware);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void save(String swarmId, JsonStore... jsonStores) {
        save(swarmId, null, jsonStores);
    }

    /**
     * {@inheritDoc }
     * 
     * @param id
     * @return 
     */
    @Override
    public RpStoreEntity find(Long id) {
        return storeDao.findById(id);
    }

    /**
     * {@inheritDoc }
     * 
     * @param sbsNo
     * @param storeNo
     * @param swarmId
     * @return 
     */
    @Override
    public RpStoreEntity findBySbsNoAndStoreNoAndSwarmId(String sbsNo, String storeNo, String swarmId) {
        return storeDao.findBySbsNoAndStoreNoAndSwarmId(sbsNo, storeNo, swarmId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RpStoreEntity findByStoreId(Long storeId) {
        return storeDao.findByStoreId(storeId);
    }
    
}
