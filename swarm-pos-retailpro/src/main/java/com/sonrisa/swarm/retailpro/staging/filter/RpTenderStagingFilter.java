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
import com.sonrisa.swarm.model.staging.retailpro.enums.RpTender;
import com.sonrisa.swarm.staging.filter.InvoiceStagingFilter;
import com.sonrisa.swarm.staging.filter.StagingFilterValue;

/**
 * Filter based on the tender type to filter out foreign currency entries and 
 * gift card purchases. 
 */
@Component
public class RpTenderStagingFilter implements InvoiceStagingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpTenderStagingFilter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public StagingFilterValue approve(InvoiceStage entity) {
        if(entity == null){
            throw new IllegalArgumentException("Invoice entity shouldn't be null.");
        }
        
        if(StringUtils.hasLength(entity.getTender())){
            switch(RpTender.parseTender(entity.getTender())){
            
                case GIFT_CERTIFICATE:
                case GIFT_CARD:
                case FOREIGN_CHECK:
                case CENTRAL_GIFT_CARD:
                case CENTRAL_GIFT_CERTIFICATE:       
                case STORE_CREDIT:
                    LOGGER.debug("Adding flag to {} invoice entity due to its tender type: {}", entity.getLsInvoiceId(), entity.getTender());
                    return StagingFilterValue.MOVABLE_WITH_FLAG;
                    
                case UNKNOWN_TENDER:
                    LOGGER.warn("Adding flag to {} because it has an unkown tender type: {}", entity.getLsInvoiceId(), entity.getTender());
                    return StagingFilterValue.MOVABLE_WITH_FLAG;
                
                default:
                    // All other tenders are OK
                    return StagingFilterValue.APPROVED;
            
            
            }
        }
        
        return StagingFilterValue.APPROVED;
    }
}
