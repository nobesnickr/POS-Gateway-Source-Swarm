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

import org.springframework.batch.item.ItemProcessor;

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.api.service.SwarmStoreFactory;
import com.sonrisa.swarm.posintegration.api.service.SwarmStoreScannerService;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;

/**
 * Base class for implementing store processors
 * 
 * @author Barnabas
 */
public class StoreJobProcessor<T extends SwarmStore> implements ItemProcessor<StoreEntity, List<StoreEntity>>{
    
    /**
     * Store factory converting {@link StoreEntity} to {@link SwarmStore}
     */
    private SwarmStoreFactory<T> storeFactory;
    
    /**
     * Scanner for new locations
     */
    private SwarmStoreScannerService<T> scanner;

    /**
     * Create new processor bean
     * 
     * @param storeFactory Store factory converting {@link StoreEntity} to {@link SwarmStore}
     * @param scanner Scanner for new locations
     * @param clazz Account's class
     */
    public StoreJobProcessor(SwarmStoreFactory<T> storeFactory, SwarmStoreScannerService<T> scanner, Class<T> clazz) {
        this.storeFactory = storeFactory;
        this.scanner = scanner;
    }

    /**
     * Processor which takes a SwarmAccount, and scans for relating locations
     */
    @Override
    public List<StoreEntity> process(StoreEntity item) throws Exception {

        // Get entity for credentials
        T account = storeFactory.getAccount(item);
                
        // Scan for all locations
        return scanner.scanForLocations(account);
    }
}
