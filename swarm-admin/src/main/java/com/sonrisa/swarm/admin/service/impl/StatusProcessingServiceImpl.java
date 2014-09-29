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

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.admin.model.StatusDetailsEntity;
import com.sonrisa.swarm.admin.model.StatusEntity;
import com.sonrisa.swarm.admin.model.query.BaseStatusQueryEntity.StoreStatus;
import com.sonrisa.swarm.admin.service.StatusProcessingService;
import com.sonrisa.swarm.admin.service.util.StatusVoteEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.service.ApiService;
import com.sonrisa.swarm.posintegration.service.ExtractorMonitoringService;

/**
 * Implementation of the {@link StatusProcessingService}
 * 
 * @author Barnabas
 */
@Service("statusProcessingService")
public class StatusProcessingServiceImpl extends BaseStatusProcessingService implements StatusProcessingService<StoreEntity,StatusEntity> {
    
    /**
     * API service used to translate api_id to api_name and vice versa
     */
    @Autowired
    private ApiService apiService;
    
    /**
     * Extractor monitoring service used to fill in the <code>last_extract</code> field
     */
    @Autowired 
    private ExtractorMonitoringService extractorMonitoringService;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public StatusEntity processStore(StoreEntity store) {
        StatusEntity status = new StatusEntity();
        status.setStoreId(store.getId());
        status.setApi(apiService.findById(store.getApiId()).getApiName());

        if (store.getCreated() != null) {
            status.setCreated(dateFormat.format(store.getCreated()));
        }

        status.setActive(Boolean.TRUE.equals(store.getActive()) ? "true" : "false");
        status.setName(store.getName());
        status.setNotes(store.getNotes());
        
        StatusDetailsEntity details = new StatusDetailsEntity();
        details.setInvoiceCount(invoiceCountService.getInvoiceCount(store.getId()));
       
        final Date lastInvoice = invoiceCountService.getLastInvoiceDate(store.getId());
        if(lastInvoice != null && lastInvoice.getTime() != 0){
            details.setLastInvoice(dateFormat.format(lastInvoice));
        }
        
        final Date lastExtract = extractorMonitoringService.getLastSuccessfulExecution(store.getId());
        if(lastExtract != null){
            details.setLastExtract(dateFormat.format(lastExtract));
        }
        status.setDetails(details);
        
        if(Boolean.TRUE.equals(store.getActive())){
            assembleVotesIntoStatus(
                    status,
                    voteForLastInvoice(lastInvoice),
                    voteForLastExtract(lastExtract)
            );
        } else {
            assembleVotesIntoStatus(
                    status,
                    voteForActive(store.getActive())
            );
        }
        
        return status;
    }
    
    /**
     * Did extraction execute in the last 24 hours for active stores? Error otherwise.
     */
    private StatusVoteEntity voteForLastExtract(Date lastExtract){
        final Date oneDayAgo = hu.sonrisa.backend.model.util.DateUtil.addDays(new Date(), -1);
        final Date last = getDefaultIfNull(lastExtract);
        if(last.before(oneDayAgo)){
            if(last.getTime() == 0){
                return new StatusVoteEntity(StoreStatus.ERROR, "No data flow for store");
            } else {
                return new StatusVoteEntity(StoreStatus.ERROR, "No data flow since " + dateFormat.format(lastExtract));
            }
        } else {
            return new StatusVoteEntity(StoreStatus.OK);
        }
    }
    

    /**
     * Did extraction execute in the last 24 hours for active stores? Error otherwise.
     */
    private StatusVoteEntity voteForActive(Boolean active){
        if(Boolean.TRUE.equals(active)){
            return new StatusVoteEntity(StoreStatus.OK);
        } else {
            return new StatusVoteEntity(StoreStatus.WARNING, "Store is inactive");
        }
    }
    
    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    public void setExtractorMonitoringService(ExtractorMonitoringService extractorMonitoringService) {
        this.extractorMonitoringService = extractorMonitoringService;
    }
}
