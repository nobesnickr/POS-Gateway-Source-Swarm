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
package com.sonrisa.swarm.job;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalExtractor;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.service.ExtractorMonitoringService;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;

/**
 * This tasklet is responsible for launching the given {@link ExternalExtractor}.
 * 
 * An external extractor is class that can extract data from
 * foreign location into the given {@link SwarmDataWarehouse} implementation.
 *
 * @author joe
 */
public abstract class ExtractorLauncher implements ItemProcessor<StoreEntity, StoreEntity>{             
            
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractorLauncher.class);
    
    /**
     * Monitoring service which stores the timestamp for each store's last
     * successful extraction
     */
    @Autowired
    private ExtractorMonitoringService monitoringService;

    @Override
    public StoreEntity process(StoreEntity store) throws Exception {

        final SwarmStore account = createAccount(store);

        final String info = extractionInfo(store);

        LOGGER.debug("Extraction begins! " + info);
        try {
            getExtractor().fetchData(account, getDataStore());
            monitoringService.addSuccessfulExecution(store.getId(), new Date());
        } catch (ExternalExtractorException ex) {
            handleExtractorException(ex, info);
        }
        LOGGER.debug("Extraction has been finished. " + info);

        return store;
    }
    
    // ------------------------------------------------------------------------
    // ~ Abstract methods
    // ------------------------------------------------------------------------
    
    /**
     * Returns the extractor that need to be used for the extraction.
     * 
     * @return 
     */
    public abstract ExternalExtractor getExtractor();

    /**
     * Returns the data warehouse to write the received information into.
     * 
     * @return 
     */
    public abstract SwarmDataWarehouse getDataStore();        
    
    /**
     * Creates a {@link SwarmStore} object aka account from a store entity.
     * 
     * An account represents a partner's of Swarm, 
     * an outer source of information that need to be fetched.
     * 
     * @param store
     * @return 
     */
    protected abstract SwarmStore createAccount(StoreEntity store);
    
    /**
     * This method is called when an {@link ExternalExtractorException} is occurred during 
     * the extraction.
     * 
     * The default implementation is only logs the exception. More sophisticated
     * handling would be reasonable in the subclasses.
     * 
     * @param ex
     * @param info 
     */
    protected void handleExtractorException(ExternalExtractorException ex, String info){
        LOGGER.error("An exception occured during the extraction. " + info, ex);
    }
    
    // ------------------------------------------------------------------------
    // ~ Private methods
    // ------------------------------------------------------------------------        
    
    /**
     * Concatenates the basic information about this extraction.
     * 
     * @return 
     */
    private String extractionInfo(StoreEntity store){
        StringBuilder info = new StringBuilder();
        info.append(" Store: ")
                .append(store)
                .append(" extractor type: ")
                .append(getExtractor().getClass().getSimpleName());
        return info.toString();
    }
}
