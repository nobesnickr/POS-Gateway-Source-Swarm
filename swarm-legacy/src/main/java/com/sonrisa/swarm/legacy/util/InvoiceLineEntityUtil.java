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

import java.math.BigDecimal;

import com.sonrisa.swarm.model.legacy.InvoiceLineEntity;
import com.sonrisa.swarm.model.legacy.ProductEntity;

/**
 * Helper class for managing {@link InvoiceLineEntity} 
 * 
 * @author Barnabas Szirmay <szirmayb@sonrisa.hu>
 */
public class InvoiceLineEntityUtil {

    public static void copyProductToInvoiceLine(InvoiceLineEntity invoiceLine, final ProductEntity source){
        
        if(invoiceLine == null){
            throw new IllegalArgumentException("invoiceLine is null");
        }
        
        if(source == null){
            // Do nothing, no source means nothing to copy
            return;
        }

        // if the line item doesn't contain price the price of the product will be used
        if (invoiceLine.getPrice() == null && source.getPrice() != null){
            invoiceLine.setPrice(source.getPrice());
        }
        
        invoiceLine.setDescription(source.getDescription());
        
        // Don't override if already filled out, this
        // value might be derived from the category or manufacturer table
        if(invoiceLine.getClazz() == null){
            invoiceLine.setClazz(source.getCategory());
        }
        
        // Family is manufacturer, but naming comes from Lightspeed
        if(invoiceLine.getFamily() == null){
            invoiceLine.setFamily(source.getManufacturer());
        }
    }
    
}
