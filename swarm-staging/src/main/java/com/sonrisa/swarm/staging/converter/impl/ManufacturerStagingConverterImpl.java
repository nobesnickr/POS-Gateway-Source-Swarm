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
package com.sonrisa.swarm.staging.converter.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.legacy.dao.ManufacturerDao;
import com.sonrisa.swarm.model.StageAndLegacyHolder;
import com.sonrisa.swarm.model.legacy.ManufacturerEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.ManufacturerStage;
import com.sonrisa.swarm.staging.converter.ManufacturerStagingConverter;
import com.sonrisa.swarm.staging.service.ManufacturerStagingService;

/**
 * Implementation of the {@link ManufacturerStagingConverter} interface
 */
@Service
public class ManufacturerStagingConverterImpl extends BaseStagingConverterImpl<ManufacturerStage, ManufacturerEntity> implements ManufacturerStagingConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManufacturerStagingConverterImpl.class);

    /**
     * DAO of manufacturers in the data warehouse (aka legacy DB).
     */
    @Autowired
    private ManufacturerDao dao;
    
    /**
     * Service to access staging content
     */
    @Autowired
    private ManufacturerStagingService manufacturerStgService;
    
    @Override
    public StageAndLegacyHolder<ManufacturerStage, ManufacturerEntity> convert(ManufacturerStage stageEntity) {
        ManufacturerEntity manufacturer = null;

        final StoreEntity store = manufacturerStgService.findStore(stageEntity);
        if (store == null) {
            LOGGER.debug("Staging manufacturer can not be saved because his store does not exists: " + stageEntity);
        } else {
            final Long foreignCategoryId = Long.parseLong(stageEntity.getManufacturerId());
            manufacturer = dao.findByStoreAndForeignId(store.getId(), foreignCategoryId);
            
            if (manufacturer == null) { 
                manufacturer = new ManufacturerEntity();
            }

            dozerMapper.map(stageEntity, manufacturer);
            manufacturer.setStore(store);
        }

        return new StageAndLegacyHolder<ManufacturerStage, ManufacturerEntity>(manufacturer, stageEntity);
    }
}
