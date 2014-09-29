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
package com.sonrisa.swarm.revel.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sonrisa.swarm.job.ExtractorLauncher;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.extractor.ExternalExtractor;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;
import com.sonrisa.swarm.revel.RevelAccount;

/**
 * This tasklet is responsible for launching the given {@link ShopifyExtractor}.
 *
 */
public class RevelExtractorLauncher extends ExtractorLauncher {

    /** External extractor to launch.  */
    @Autowired
    @Qualifier("RevelExtractor")
    private ExternalExtractor extractor;
    
    /**
     * Service decrypting strings from the MySQL
     * database
     */
    @Autowired
    private AESUtility aesUtility;
    
    /** Warehouse to write the received data into. */
    @Autowired
    private SwarmDataWarehouse dataStore;

    @Override
    protected SwarmStore createAccount(final StoreEntity store) {
        RevelAccount account = new RevelAccount(store, aesUtility);
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
