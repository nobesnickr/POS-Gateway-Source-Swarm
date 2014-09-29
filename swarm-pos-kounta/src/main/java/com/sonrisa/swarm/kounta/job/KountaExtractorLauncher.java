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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sonrisa.swarm.job.ExtractorLauncher;
import com.sonrisa.swarm.kounta.KountaAccount;
import com.sonrisa.swarm.kounta.extractor.KountaExtractor;
import com.sonrisa.swarm.kounta.service.KountaStoreFactory;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.extractor.ExternalExtractor;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;

/**
 * This tasklet is responsible for launching the given {@link KountaExtractor}.
 *
 */
public class KountaExtractorLauncher extends ExtractorLauncher {

    /** External extractor to launch.  */
    @Autowired
    @Qualifier("kountaExtractor")
    private ExternalExtractor extractor;
    
    /**
     * Service converting {@link StoreEntity} to {@link KountaAccount}
     */
    @Autowired
    private KountaStoreFactory storeFactory;
        
    /** Datastore to write the received data into. */
    @Autowired
    private SwarmDataWarehouse dataStore;
    
    @Override
    protected SwarmStore createAccount(final StoreEntity store) {
        KountaAccount account = storeFactory.getAccount(store);
        account.setSite(store.getStoreFilter());
        return account;
    }

    // -----------------------------------------------------------------------
    // ~ Setters / getters
    // -----------------------------------------------------------------------       

    @Override
    public ExternalExtractor getExtractor() {
        return extractor;
    }

    public void setDataStore(SwarmDataWarehouse dataStore) {
        this.dataStore = dataStore;
    }

    public void setExtractor(ExternalExtractor extractor) {
        this.extractor = extractor;
    }            

    @Override
    public SwarmDataWarehouse getDataStore() {
        return dataStore;
    }
}
