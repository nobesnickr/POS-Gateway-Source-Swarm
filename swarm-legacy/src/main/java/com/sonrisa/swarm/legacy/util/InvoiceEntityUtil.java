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

package com.sonrisa.swarm.legacy.util;

import com.sonrisa.swarm.model.legacy.CustomerEntity;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;

/**
 * Helper class for managing {@link InvoiceEntity} 
 * 
 * @author Barnabas Szirmay <szirmayb@sonrisa.hu>
 */
public class InvoiceEntityUtil {

    /**
     * Copies source customers name, email and mainphone to the invoice fields 
     * 
     * @param invoice
     * @param source
     */
    public static void copyCustomerFieldsToInvoice(InvoiceEntity invoice, final CustomerEntity source){
    	if(invoice == null){
    		throw new IllegalArgumentException("invoice is null");
    	}
    	
    	if(source != null){
	    	invoice.setLsCustomerId(source.getLsCustomerId()); // just to be sure
	        invoice.setCustomerName(source.getName());
	        invoice.setCustomerEmail(source.getEmail());
	        invoice.setCustomerPhone(source.getPhone());
    	} else {
    		invoice.setLsCustomerId(0L);
    	}
    }
}
