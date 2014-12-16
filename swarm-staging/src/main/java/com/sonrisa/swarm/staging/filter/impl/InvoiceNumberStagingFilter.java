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
package com.sonrisa.swarm.staging.filter.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.staging.filter.InvoiceStagingFilter;
import com.sonrisa.swarm.staging.filter.StagingFilterValue;

/**
 * Filter for {@link InvoiceStage} entities, which filters out entities with "0" invoice number.
 *
 */
@Component
public class InvoiceNumberStagingFilter implements InvoiceStagingFilter {


    /**
     * Should staging invoices with {@link #NULL_INVOICE_NUMBER} be skipped during the conversion
     * to legacy invoice entity. If this is true, this kind of invoices will be left
     * in the staging DB.     
     */
    @Value("${retailpro.skip.invoices.with.null.invoice_number}")
    private boolean skipInvoicesWithNullNumber;
    
    @Override
    public StagingFilterValue approve(InvoiceStage entity) {
        if(entity == null){
            throw new IllegalArgumentException("Entity is null");
        }
        
        // This should only affect Retail Pro
        if(StringUtils.isEmpty(entity.getSwarmId())){
            return StagingFilterValue.APPROVED;
        }
        
        if(StringUtils.hasLength(entity.getInvoiceNo())){
           if(!skipInvoicesWithNullNumber || !entity.getInvoiceNo().equals("0")){
               return StagingFilterValue.APPROVED;
           }
        }
        
        return StagingFilterValue.MOVABLE_WITH_FLAG;
    }

    /**
     * @return the skipInvoicesWithNullNumber
     */
    public boolean isSkipInvoicesWithNullNumber() {
        return skipInvoicesWithNullNumber;
    }

    /**
     * @param skipInvoicesWithNullNumber the skipInvoicesWithNullNumber to set
     */
    public void setSkipInvoicesWithNullNumber(boolean skipInvoicesWithNullNumber) {
        this.skipInvoicesWithNullNumber = skipInvoicesWithNullNumber;
    }
}
