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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.sonrisa.swarm.legacy.model.EntityValidationResult;
import com.sonrisa.swarm.legacy.service.BaseLegacyService;
import com.sonrisa.swarm.model.BaseSwarmEntity;
import com.sonrisa.swarm.model.StageAndLegacyHolder;
import com.sonrisa.swarm.model.staging.BaseStageEntity;
import com.sonrisa.swarm.staging.converter.BaseStagingConverter;
import com.sonrisa.swarm.staging.service.BaseStagingService;

/**
 * This item processor is responsible for moving the staging entity records from the
 * staging table to the data warehouse (aka the legacy DB).
 * 
 *
 * @author joe
 */
public class StagingEntityProcessor  implements ItemProcessor<BaseStageEntity, StageAndLegacyHolder>{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StagingEntityProcessor.class);
    
    /** This service would be used to save the staging entity to the legacy DB. */
    private BaseLegacyService legacyService; 
    
    /** This service would be used to delete the staging entity if necessary. */
    private BaseStagingService stagingService;    
    
    /** This service can be used to convert StagingEntities into legacy legacy entities */
    private BaseStagingConverter stagingConverter;

    @Override
    public StageAndLegacyHolder process(BaseStageEntity stagingEntity) throws Exception {
        final StageAndLegacyHolder holder = stagingConverter.convert(stagingEntity);     
                 
        // Is the conversion succeeded?
        // The null legacy entity indicates that the conversion has been failed. 
        if (holder == null || holder.getLegacyEntity() == null){
            
            // if there is no chance for a next successful try, the staging entity can be deleted
            if (holder != null && holder.isStagingEntityAbsolutelyUnprocessable()){                
                LOGGER.warn("This staging entity can not be moved to the legacy DB and will be deleted. Reason: {} Entity: {}", holder.getMessage(), stagingEntity);
                deleteUnprocessableEntity(stagingEntity);            
            } else{
                // there is a chance that next time it will succeed so the staging entity
                // should be skipped but it is not necessary to delete it.
                LOGGER.debug("This item has been skipped because of an unsuccessful conversion to legacy entity. "
                        + "The staging entity remains in the staging DB and the conversion will be retried next time. Entity: {}", stagingEntity);            
            }
            
            // the processing continues with the next item
            return null;    
        }
        
        final EntityValidationResult result = stagingConverter.validateEntity(holder);
        if (!result.isSuccess()){
            // This validation fails if the legacyEntity can not be inserted to the legacy DB,            
            // and there is no chance for a next successful try.
            // The unprocessable staging entity can be deleted immediately.
            // The other items in the current batch will be processed as usual.
            
            LOGGER.warn("This staging entity can not be moved to the legacy DB and will be deleted. "
                    + " Reason: {} Entity: {}", result.getMessage(), stagingEntity);
            deleteUnprocessableEntity(stagingEntity);
            // the processing continues with the next item     
            return null;           
        }        
                
        LOGGER.debug("");
        return holder;
    }
    
    /**
     * This method deletes the given (hopefully) desperately unprocessable
     * staging entity from the staging DB.
     * 
     * @param stagingEntity 
     */
    private void deleteUnprocessableEntity(BaseStageEntity stagingEntity) {
        if (stagingEntity != null){
            stagingService.delete(stagingEntity.getId());
        }
    }
    
    /**
     * Sets the service that would be used to save the staging entity to the legacy DB
     * 
     * @param legacyService 
     */
    public void setLegacyService(BaseLegacyService legacyService) {
        this.legacyService = legacyService;
    }       

    /**
     * Sets the service that is used to delete the unprocessable staging entities
     * from the staging DB.
     * 
     * @param stagingService 
     */
    public void setStagingService(BaseStagingService stagingService) {
        this.stagingService = stagingService;
    }

    /**
     * Sets the service which is used to convert {@link BaseStageEntity} to {@link BaseSwarmEntity}
     * 
     * @param stagingConverter
     */
    public void setStagingConverter(BaseStagingConverter stagingConverter) {
        this.stagingConverter = stagingConverter;
    }
    
}
