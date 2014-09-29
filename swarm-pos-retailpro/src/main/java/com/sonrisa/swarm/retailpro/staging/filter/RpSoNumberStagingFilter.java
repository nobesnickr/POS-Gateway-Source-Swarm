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
 * Staging filter for applying a rule based on the Tender type and the SoNumber
 * field of an invoice. 
 */
@Component
public class RpSoNumberStagingFilter implements InvoiceStagingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpTenderStagingFilter.class);

    /**
     * Applies the following rule:
     * 
     *   If: SoNumber is not missing for an entity with Total > 0
     *   Then: Filter out any invoice with Tender != DEPOSIT
     *   Else: Filter out any invoice with Tender == DEPOSIT
     */
    @Override
    public StagingFilterValue approve(InvoiceStage entity) {
        if(entity == null){
            throw new IllegalArgumentException("Invoice entity shouldn't be null.");
        }
     
        // Only affect invoices with SwarmId and Tender fields
        if(StringUtils.hasLength(entity.getSwarmId()) && StringUtils.hasLength(entity.getTender()) && StringUtils.hasLength(entity.getTotal())){
            
            RpTender tender = RpTender.parseTender(entity.getTender());
            boolean isNegativeTotal = entity.getTotal().startsWith("-");
            
            if(StringUtils.hasLength(entity.getSoNumber()) && !isNegativeTotal){
                if(tender != RpTender.DEPOSIT){
                    LOGGER.debug("Flagging {} as it has no SO number and isn't deposit.", entity);
                    return StagingFilterValue.MOVABLE_WITH_FLAG;
                }
            } else {
                if(tender == RpTender.DEPOSIT){
                    LOGGER.debug("Flagging {} as it has SO number and is deposit.", entity);
                    return StagingFilterValue.MOVABLE_WITH_FLAG;
                }
            }
        }
        return StagingFilterValue.APPROVED;
    }

}
