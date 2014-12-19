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
package com.sonrisa.swarm.staging.job.loader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import com.sonrisa.swarm.common.job.logger.TimingLogger;
import com.sonrisa.swarm.legacy.service.BaseLegacyService;
import com.sonrisa.swarm.model.StageAndLegacyHolder;
import com.sonrisa.swarm.model.legacy.BaseLegacyEntity;
import com.sonrisa.swarm.staging.job.exception.NonDistinctBatchItemsException;
import com.sonrisa.swarm.staging.service.BaseStagingService;

/**
 * This spring batch writer deletes the moved staging entities from the database.
 *
 * @author joe
 */
public class StagingEntityWriter implements ItemWriter<StageAndLegacyHolder>{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StagingEntityWriter.class);
    
    private BaseStagingService stagingService;
    
    private BaseLegacyService legacyService;

    @Override
    public void write(List<? extends StageAndLegacyHolder> items) throws Exception {
        final long startTime = System.currentTimeMillis();
        LOGGER.debug("Number of items to write: " + items.size());
           
        Set<Long> stagingEntitesToDelete = new HashSet<Long>();
        int numOfDeleted;
              
        // We keep track of all inserted entities within the same batch
        // and force legacy id on duplicated entities to
        // ensure that they are updated and not inserted again
        Set<BaseLegacyEntity> insertedEntities = new HashSet<BaseLegacyEntity>();
        
        for (StageAndLegacyHolder holder : items){
        	BaseLegacyEntity legacyEntity = holder.getLegacyEntity();
        	
        	if(insertedEntities.contains(legacyEntity)){
        		if(legacyEntity.getId() == null){
        			throw new NonDistinctBatchItemsException(legacyEntity.getStore(), legacyEntity.getLegacySystemId());
        		} else {
        			LOGGER.debug("Entity already has legacy id and will be updated: {}", legacyEntity);
        		}
        	}
        	LOGGER.info("Saving entity: "+legacyEntity);
            legacyService.saveEntityFromStaging(legacyEntity);
            
            // Save entity
            insertedEntities.add(legacyEntity);
            
            stagingEntitesToDelete.add(holder.getStagingEntityId());
        }
                        
        // we need to flush the inserted entities  
        // because we would like to be notified if the
        // transaction can not be committed            
        legacyService.flush();
        
        // removing of the staging entities from the staging db
        numOfDeleted = stagingService.delete(stagingEntitesToDelete);
        
        if(items.size() > 0){
            final String className = items.get(0).getStagingEntity().getClass().getSimpleName();
            LOGGER.debug("Processed and deleted {} {} entities from the staging DB in", numOfDeleted, className);
            
            final long durationMillis = System.currentTimeMillis() - startTime;
            TimingLogger.debug("Processed and deleted {} {} entities from the staging DB", durationMillis, stagingEntitesToDelete.size(), className);
        }
    }
    
    /**
     * Setter of staging service bean.
     * This bean will be used to delete the moved staging entities.
     * 
     * @param stagingService 
     */
    public void setStagingService(BaseStagingService stagingService) {
        this.stagingService = stagingService;
    }

    public void setLegacyService(BaseLegacyService legacyService) {
        this.legacyService = legacyService;
    }
}
