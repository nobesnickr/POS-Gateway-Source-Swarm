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

package com.sonrisa.swarm.admin.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.sonrisa.swarm.admin.model.BaseStatusEntity;
import com.sonrisa.swarm.admin.model.query.BaseStatusQueryEntity.StoreStatus;
import com.sonrisa.swarm.admin.service.StatusProcessingService;
import com.sonrisa.swarm.admin.service.util.StatusVoteEntity;
import com.sonrisa.swarm.posintegration.service.InvoiceCountMonitoringService;

/**
 * Base class for normal and Retail Pro status processing services holding
 * some common funtionality. 
 * 
 * @author Barnabas
 */
public abstract class BaseStatusProcessingService {
    
    /**
     * Date format for formatting dates
     */
    protected SimpleDateFormat dateFormat = new SimpleDateFormat(StatusProcessingService.STORE_STATUS_DATE_FORMAT);

    /**
     * Invoice count service used to fill in the <code>invoice_count</code> and <code>last_invoice</code> fields
     */
    @Autowired
    protected InvoiceCountMonitoringService invoiceCountService;
    
    /**
     * Assemble votes
     */
    protected void assembleVotesIntoStatus(BaseStatusEntity status, StatusVoteEntity... votes){
        List<String> reasons = new ArrayList<String>();
        StoreStatus statusValue = StatusVoteEntity.getMostSevereStatus(votes, reasons);
     
        if(statusValue != StoreStatus.OK){
            status.setReason(reasons);
        }
        
        status.setStatus(statusValue);
    }
    
    /**
     * Were there any invoices from to store in the last 48 hour? Warning otherwise.
     */
    protected StatusVoteEntity voteForLastInvoice(Date lastInvoiceDate){
        final Date twoDaysAgo = hu.sonrisa.backend.model.util.DateUtil.addDays(new Date(), -2);
        final Date last = getDefaultIfNull(lastInvoiceDate);
        if(last.before(twoDaysAgo)){
            if(last.getTime() == 0){
                return new StatusVoteEntity(StoreStatus.WARNING, "No invoice for store");
            } else {
                return new StatusVoteEntity(StoreStatus.WARNING, "No invoice since " + dateFormat.format(lastInvoiceDate));
            }
        } else {
            return new StatusVoteEntity(StoreStatus.OK);
        }
    }
    
    /**
     * Returns default date for null
     */
    protected static Date getDefaultIfNull(Date date){
        return date == null ? new Date(0L) : date;
    }

    public void setInvoiceCountService(InvoiceCountMonitoringService invoiceCountService) {
        this.invoiceCountService = invoiceCountService;
    }
}
