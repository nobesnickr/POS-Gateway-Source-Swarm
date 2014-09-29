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
package com.sonrisa.swarm.staging.service;

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.StageBatchInsertable;
import com.sonrisa.swarm.model.staging.BaseStageEntity;
import hu.sonrisa.backend.entity.SonrisaJPAEntity;
import hu.sonrisa.backend.service.GenericService;
import java.util.Collection;
import java.util.List;

/**
 * Common interface of staging services.
 *
 * @author joe
 */
public interface BaseStagingService<T extends SonrisaJPAEntity<Long>> extends GenericService<Long, T> {
    
    /**
     * Retrieves the store from the legacy DB for this staging entity.
     * 
     * @param stgEntity
     * @return null if the store can not be identified or does not exist
     */
     public StoreEntity findStore(BaseStageEntity stgEntity);
     
     /**
     * Retrieves all IDs from the staging entity table.
     * 
     * @return 
     */
    List<Long> findAllIds();
    
    /**
     * Retrieves stagin entities from the staging tables by their ids.
     * 
     * @param ids ids of the entities to retrieve, its max size is {@link #MAX_LENGTH_OF_IN_CLAUSE}
     * @return 
     * 
     * @throws IllegalArgumentException if the size of the ids list is more than {@link #MAX_LENGTH_OF_IN_CLAUSE}
     */
    List<T> findByIds(List<Long> ids);
    
    /**
     * Creates or updates a entity in the staging DB.
     * 
     * @param stage
     * @return 
     */
    Long save(T stage);
    
    /**
     * Deletes the given entities from the staging DB.
     * 
     * @param entitys 
     * @return the number of the removed entitys     
     */
    int delete(Collection<Long> entityIds);
    
    /**
     * Deletes an entity from the the staging DB.
     * 
     * @param id
     * @return 
     */
    boolean delete(Long id);
    

    /**     
     * Insert objects into the stage tables based on their annotations
     *
     * @param entities 
     */
    void create(List<? extends StageBatchInsertable> entities);
    
    /**     
     * Insert objects into the stage tables based on their annotations
     *
     * @param entities 
     */
    void create(List<? extends StageBatchInsertable> entities, Long localId);
    
}
