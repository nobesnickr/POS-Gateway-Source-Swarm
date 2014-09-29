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
package com.sonrisa.swarm.staging.converter;

import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.InvoiceStage;

/**
* Utility class transforming the timestamps into the correct timezone
* for invoices
*
* @author joe
*/
public interface TimeZoneService {
    
    /**
     * Changes the timezone within the timestamp of the invoice is interpreted.
     * 
     * E.g. if the timestamp is: 2013-11-12 13:33:44 CET
     * but the store is located in CET+3 then the result will be: 2013-11-12 13:33:44 CET+3
     * 
     * Firts it tries to use the TZ field of the Store, if there's no TZ information on the store,
     * then it tries to find a RetailPro store for this invoice to use its TZ and offset values.
     * 
     * @param store
     * @param invoice
     * @param stgInvoice 
     */
    public void correctInvoiceTs(StoreEntity store, InvoiceEntity invoice, InvoiceStage stgInvoice);
    
}
