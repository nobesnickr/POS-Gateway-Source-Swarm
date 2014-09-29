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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sonrisa.swarm.model.legacy.StoreEntity;

/**
 * This Spring Batch writer is the last step of the store extraction process.
 * 
 * It is called after several stores have been fetched. 
 * The exact number of the stores depends on the batch size of the extraction. 
 * Normally the batch size is 1 because we want to run every store extraction
 * in a separate transaction and we would like to commit after every store.
 * 
 * Usually this writer has to do nothing, but it can be used to monitor,
 * or to log the fact of the extraction.  
 *
 * @author joe
 */
public class ExtractorLauncherWriter extends BaseMonitoringWriter<StoreEntity> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractorLauncherWriter.class);
    
    public static final String NUM_OF_STORES_EXTRACTED = NUM_OF_ITEMS_EXTRACTED;

    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
