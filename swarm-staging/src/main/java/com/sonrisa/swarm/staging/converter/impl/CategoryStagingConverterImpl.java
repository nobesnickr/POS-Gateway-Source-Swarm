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

import com.sonrisa.swarm.legacy.dao.CategoryDao;
import com.sonrisa.swarm.model.StageAndLegacyHolder;
import com.sonrisa.swarm.model.legacy.CategoryEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.CategoryStage;
import com.sonrisa.swarm.staging.converter.CategoryStagingConverter;
import com.sonrisa.swarm.staging.service.CategoryStagingService;

/**
 * Implementation of the {@link CategoryStagingConverter} interface
 * @author sonrisa
 *
 */
@Service
public class CategoryStagingConverterImpl  extends BaseStagingConverterImpl<CategoryStage, CategoryEntity> implements CategoryStagingConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryStagingConverterImpl.class);

    /**
     * DAO of categories in the data warehouse (aka legacy DB).
     */
    @Autowired
    private CategoryDao categoryDao;
    
    /**
     * Service to access staging content
     */
    @Autowired
    private CategoryStagingService categoryStgService;
    
    /**
     * Find category in the legacy DB using storeID and stage ID
     * @param storeId Store id 
     * @param foreignId Stage's foreign identifier
     * @return NULL if not found, the entity otherwise
     */
    private CategoryEntity findCategoryUsingForeignId(Long storeId, String foreignId){
        return categoryDao.findByStoreAndForeignId(storeId,Long.parseLong(foreignId));
    }
    
    public StageAndLegacyHolder<CategoryStage,CategoryEntity> convert(CategoryStage stageEntity)  {
        CategoryEntity category = null;

        final StoreEntity store = categoryStgService.findStore(stageEntity);
        if (store == null) {
            LOGGER.debug("Staging category can not be saved because his store does not exists: " + stageEntity);
        } else {
            
            category = findCategoryUsingForeignId(store.getId(), stageEntity.getLsCategoryId());
            if (category == null) {
                category = new CategoryEntity();
            }

            dozerMapper.map(stageEntity, category);
            category.setStore(store);
        }

        return new StageAndLegacyHolder<CategoryStage, CategoryEntity>(category,stageEntity);
    } 
}
