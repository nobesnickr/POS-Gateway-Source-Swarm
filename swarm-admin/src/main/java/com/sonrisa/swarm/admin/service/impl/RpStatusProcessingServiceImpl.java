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
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.admin.model.RpStatusDetailsEntity;
import com.sonrisa.swarm.admin.model.RpStatusEntity;
import com.sonrisa.swarm.admin.model.query.BaseStatusQueryEntity.StoreStatus;
import com.sonrisa.swarm.admin.service.StatusProcessingService;
import com.sonrisa.swarm.admin.service.util.StatusVoteEntity;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.retailpro.dao.impl.RpPluginDao;
import com.sonrisa.swarm.retailpro.model.RpLogEntity;
import com.sonrisa.swarm.retailpro.model.RpPluginEntity;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;
import com.sonrisa.swarm.retailpro.service.RpLogMonitoringService;

/**
 * Processing service converting {@link RpStoreEntity} to {@link RpStatusEntity}
 * 
 * @author Barnabas
 */
@Service("rpStatusProcessingService")
public class RpStatusProcessingServiceImpl extends BaseStatusProcessingService implements StatusProcessingService<RpStoreEntity,RpStatusEntity> {
    
    /**
     * Client for reading heartbeat
     */
    @Autowired
    private RpPluginDao pluginDao;
    
    /**
     * Service monitoring logs
     */
    @Autowired
    private RpLogMonitoringService logService;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RpStatusEntity processStore(RpStoreEntity store) {
        RpStatusEntity status = new RpStatusEntity();
        status.setStoreId(store.getStoreId());
        status.setSwarmId(store.getSwarmId());
        status.setTimezone(store.getTimeZone());
        status.setTimeOffset(store.getTimeOffset());
        status.setApi(store.getPosSoftware());
        
        if (store.getCreated() != null) {
            status.setCreated(dateFormat.format(store.getCreated()));
        }
        
        status.setName(store.getStoreName());
        status.setNotes(store.getNotes());
        
        // Calculate status details
        RpStatusDetailsEntity details = new RpStatusDetailsEntity();
        
        RpPluginEntity plugin = pluginDao.findBySwarmId(status.getSwarmId());
        if(plugin != null){
            details.setClientVersion(plugin.getPluginVersion());
            
            // Although this should happen, when we were experiencing
            // problems with the heartbeat controller, we inserted
            // rows manually in the rp_plugins table
            if(plugin.getHeartbeat() != null) {
                details.setLastHeartbeat(dateFormat.format(plugin.getHeartbeat()));
            }
        }
        
        details.setInvoiceCount(invoiceCountService.getInvoiceCount(store.getStoreId()));
        
        final Date lastInvoice = invoiceCountService.getLastInvoiceDate(store.getStoreId());
        if(lastInvoice != null && lastInvoice.getTime() > 0){
            details.setLastInvoice(dateFormat.format(lastInvoice));
        }
        status.setDetails(details);
        
        assembleVotesIntoStatus(
                status,
                voteForLastInvoice(lastInvoice),
                voteForPlugin(plugin),
                voteForErrorLog(logService.getRecentClientError(store.getSwarmId()), plugin),
                voteForTimezone(store.getTimeZone())
        );
        
        return status;
    }

    /**
     * Was there heartbeat from the plugin in the last 24h?
     */
    protected StatusVoteEntity voteForPlugin(RpPluginEntity plugin){
        final Date yesterday = hu.sonrisa.backend.model.util.DateUtil.addDays(new Date(), -1);
        final Date last = plugin != null ? getDefaultIfNull(plugin.getHeartbeat()) : new Date(0L);
        if(last.before(yesterday)){
            if(last.getTime() == 0){
                return new StatusVoteEntity(StoreStatus.ERROR, "No heartbeat for store ever");
            } else {
                return new StatusVoteEntity(StoreStatus.ERROR, "No heartbeat since " + dateFormat.format(last));
            }
        } else {
            return new StatusVoteEntity(StoreStatus.OK);
        }
    }

    /**
     * Was there any error log in the last 24h?
     */
    protected StatusVoteEntity voteForErrorLog(RpLogEntity lastError, RpPluginEntity plugin){
        final Date yesterday = hu.sonrisa.backend.model.util.DateUtil.addDays(new Date(), -1);
        
        // When was the last heartbeat received
        Date lastHeartbeat = new Date(0L);
        if(plugin != null && plugin.getHeartbeat() != null){
            lastHeartbeat = plugin.getHeartbeat();
        }
        
        if(lastError != null && lastError.getServerTimestamp().after(yesterday)){
            // Severity is only error if there was no successful heartbeat after the error
            StoreStatus severity = lastHeartbeat.after(lastError.getServerTimestamp()) ? StoreStatus.WARNING : StoreStatus.ERROR;
            
            StringBuilder message = new StringBuilder();
            message.append("Error occured on the client side at ")
                   .append(ISO8061DateTimeConverter.dateToMysqlString(lastError.getServerTimestamp()))
                   .append(", client says:")
                   .append(lastError.getDetails());
            
            return new StatusVoteEntity(severity, message.toString());
        } else {
            return new StatusVoteEntity(StoreStatus.OK);
        }
    }

    /**
     * Is timezone set?
     */
    protected StatusVoteEntity voteForTimezone(String timezone){
        if(StringUtils.isEmpty(timezone)){
            return new StatusVoteEntity(StoreStatus.WARNING, "Timezone missing.");
        } else {
            return new StatusVoteEntity(StoreStatus.OK);
        }
    }

    public void setPluginDao(RpPluginDao pluginDao) {
        this.pluginDao = pluginDao;
    }


    public void setLogService(RpLogMonitoringService logService) {
        this.logService = logService;
    }
}
