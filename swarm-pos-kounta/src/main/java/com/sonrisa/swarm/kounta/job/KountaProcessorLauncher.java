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

package com.sonrisa.swarm.kounta.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sonrisa.swarm.job.InvoiceProcessorLauncher;
import com.sonrisa.swarm.kounta.KountaAccount;
import com.sonrisa.swarm.kounta.extractor.processor.KountaInvoiceProcessor;
import com.sonrisa.swarm.kounta.service.KountaStoreFactory;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.extractor.ExternalProcessor;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;

/**
 * Kounta unfinished invoice DTO processor's processing step
 * 
 * @author Barnabas
 */
public class KountaProcessorLauncher extends InvoiceProcessorLauncher<KountaAccount> {

    @Autowired
    private SwarmDataWarehouse destination;
    
    @Autowired
    private KountaInvoiceProcessor processor;

    /**
     * Service converting {@link StoreEntity} to {@link KountaAccount}
     */
    @Autowired
    private KountaStoreFactory storeFactory;
    
    /**
     * Store cache used to avoid authenticating the some KountaAccount
     * multiple times from different threads.
     */
    private Map<StoreEntity, KountaAccount> cache = new HashMap<StoreEntity, KountaAccount>();
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected SwarmDataWarehouse getDataWarehouse() {
        return destination;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ExternalProcessor<KountaAccount, InvoiceEntity> getExternalProcessor() {
        return processor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected KountaAccount createAccount(StoreEntity store) {
        synchronized(cache){
            KountaAccount account = cache.get(store);
            if(account == null){
                account = storeFactory.getAccount(store);
                cache.put(store, account);
            }
            
            return account;
        }
    }
}
