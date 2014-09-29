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

import com.sonrisa.swarm.model.legacy.StoreEntity;

/**
 * Monitoring service monitoring the number of invoices and the last invoice for a {@link StoreEntity}
 * or {@link RpStoreEntity}
 * 
 * @author Barnabas
 */
public interface InvoiceCountMonitoringService {

    /**
     * Returns the last invoice's date for a given store
     * or <code>Date(0L)</code> if empty
     */
    Date getLastInvoiceDate(Long storeId);
    
    /**
     * Returns the total number of invoices in a store
     * @param storeId
     * @return
     */
    long getInvoiceCount(Long storeId);
}
