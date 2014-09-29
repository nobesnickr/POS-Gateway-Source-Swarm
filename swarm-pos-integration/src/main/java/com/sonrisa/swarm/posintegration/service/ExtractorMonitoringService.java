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

package com.sonrisa.swarm.posintegration.service;

import java.util.Date;

/**
 * Service to monitor the state of extractors 
 * like {@link ShopifyExtractor} 
 * 
 * @author Barnabas
 */
public interface ExtractorMonitoringService {
    /**
     * Gets the last time a successful extraction ran
     * for the store.
     * 
     * Returns <i>null</i> if there is no known successful execution
     *  
     * @param store StoreEntity which was extracted
     * @return
     */
    Date getLastSuccessfulExecution(Long storeId);
    
    /**
     * Add successful execution for a store.
     * 
     * @param store StoreEntity which was extracted
     * @param date Date of the execution
     */
    void addSuccessfulExecution(Long storeId, Date date);
    
}
