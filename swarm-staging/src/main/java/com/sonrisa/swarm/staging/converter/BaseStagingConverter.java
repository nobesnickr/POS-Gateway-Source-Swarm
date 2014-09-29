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
package com.sonrisa.swarm.staging.converter;

import com.sonrisa.swarm.legacy.model.EntityValidationResult;
import com.sonrisa.swarm.model.BaseSwarmEntity;
import com.sonrisa.swarm.model.StageAndLegacyHolder;
import com.sonrisa.swarm.model.legacy.BaseLegacyEntity;
import com.sonrisa.swarm.model.legacy.CustomerEntity;
import com.sonrisa.swarm.model.staging.BaseStageEntity;
import com.sonrisa.swarm.model.staging.CustomerStage;
import com.sonrisa.swarm.staging.job.loader.StagingEntityProcessor;

/**
 * Common interface used by {@link StagingEntityProcessor} to convert {@link BaseStageEntity} 
 * objects to {@link BaseSwarmEntity} legacy objects.
 *  
 * @author Barna
 *
 * @param <U> Class of the stage entity, e.g. InvoiceStage
 * @param <T> Class of the legacy entity, e.g. InvoiceEntity
 */
public interface BaseStagingConverter<U extends BaseStageEntity, T extends BaseLegacyEntity> {

    /**
     * Converts U to T
     * @param stageEntity
     * @return
     */
    StageAndLegacyHolder<U, T> convert(U stageEntity);
    

    
    /**
     * This method validates the entities to 
     * meet with the requirements of the legacy database.
     * 
     * E.g. the values of the fields fit into the data types declared in the DB schema.
     * 
     * @return a validation result that contains the result and a message
     */
    EntityValidationResult validateEntity(StageAndLegacyHolder<U,T> holder);   
}
