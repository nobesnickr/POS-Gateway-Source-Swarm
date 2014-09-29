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
package com.sonrisa.swarm.job;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;

/**
 * Writer which updates stores
 * @author Barnabas
 */
public class StoreJobWriter implements ItemWriter<List<StoreEntity>> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreJobWriter.class);
    
    /**
     * If true, will not update already existing entities
     */
    private boolean noUpdate = true;
        
    @Autowired
    private StoreService storeService;

    /**
     * Writes entities
     */
    @Override
    public void write(List<? extends List<StoreEntity>> items) throws Exception {

        int entitiesUpdated = 0, entitiesInserted = 0;
        
        for(List<? extends StoreEntity> batchResult : items){
            for(StoreEntity store : batchResult){
                boolean newEntity = (store.getId() == null);
                if(!noUpdate || newEntity){
                    
                    // Count number of items for logging
                    if(newEntity){
                        entitiesInserted++;
                    } else {
                        entitiesUpdated++;
                    }
                    
                    storeService.save(store);
                }
            }
        }
        
        if(entitiesInserted > 0 || entitiesUpdated > 0) {
            LOGGER.info("Finished store job, inserted {} new stores and updated {} stores", entitiesInserted, entitiesUpdated);
        }
    }
    
    /**
     * Set value indicating whether already existing stores should be modified
     */
    public void setNoUpdate(boolean noUpdate) {
        this.noUpdate = noUpdate;
    }
}
