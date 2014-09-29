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
package com.sonrisa.swarm.retailpro.staging.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.model.staging.retailpro.enums.RpReceiptType;
import com.sonrisa.swarm.staging.filter.InvoiceStagingFilter;
import com.sonrisa.swarm.staging.filter.StagingFilterValue;

/**
 * Retail Pro Receipt Type attribute filter, which filters out unwanted invoices based on 
 * Receipt Type and Tender.  
 * 
 */
@Component
public class ReceiptAttributeStagingFilter implements InvoiceStagingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiptAttributeStagingFilter.class);
    
    @Override
    public StagingFilterValue approve(InvoiceStage entity) {
        if(entity == null){
            throw new IllegalArgumentException("Invoice entity shouldn't be null.");
        }
        
        if(StringUtils.hasLength(entity.getReceiptType())){
            try {
                final int receiptType = Integer.parseInt(entity.getReceiptType());
                
                if(!validReceiptType(receiptType)){
                    LOGGER.debug("Adding flag to {} invoice entity due to its receipt type: {}", entity.getLsInvoiceId(), entity.getReceiptType());
                    return StagingFilterValue.MOVABLE_WITH_FLAG;
                }           
            } catch(NumberFormatException e){
                LOGGER.warn("An error occured while processing InvoiceStage entity", e);
            }
        }
        
        return StagingFilterValue.APPROVED;
    }
    
    /**
     * Returns value indicating that receipt type is considered approved
     * 
     * @param receiptType
     * @return
     */
    private static boolean validReceiptType(int receiptType){
        return receiptType == RpReceiptType.SALES.getLsReceiptType() ||
               receiptType == RpReceiptType.RETURN.getLsReceiptType();
    }
}
