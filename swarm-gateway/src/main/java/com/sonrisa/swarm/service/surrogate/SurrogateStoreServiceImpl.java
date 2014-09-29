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
package com.sonrisa.swarm.service.surrogate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.legacy.service.surrogate.SurrogateStoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.BaseStageEntity;
import com.sonrisa.swarm.retailpro.dao.RpStoreDao;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;

/**
 * Implementation of the {@link SurrogateStoreService} interface using the stores_rp MySQL table;
 * @author sonrisa
 *
 */
@Service
public class SurrogateStoreServiceImpl implements SurrogateStoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SurrogateStoreServiceImpl.class);
    
    /**
     * Dao to access to stores
     */
    @Autowired
    private StoreDao storeDao;
    
    /**
     * Dao to access the information of the Retail Pro installations
     */
    @Autowired
    private RpStoreDao rpStoreDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public StoreEntity findStoreForStagingEntity(BaseStageEntity stgEntity) {
        
        if(stgEntity == null){
            LOGGER.error("Can't find StoreEntity for null.");
            return null;
        }
        
        // Because there is no storeId, it tries with the RetailPro store identifiers
        final RpStoreEntity rpStore = rpStoreDao.findBySbsNoAndStoreNoAndSwarmId(
                stgEntity.getLsSbsNo(),
                stgEntity.getLsStoreNo(),
                stgEntity.getSwarmId());
    
        // RpStoreEntity not found?
        if (rpStore == null) {
            LOGGER.debug("RpStoreEntity not found for {}", stgEntity);
            return null;
        }

        return storeDao.findById(rpStore.getStoreId());
    }
}
