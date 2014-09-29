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
package com.sonrisa.swarm.admin.controller;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.sonrisa.swarm.BaseSecurityIntegrationTest;
import com.sonrisa.swarm.legacy.service.InvoiceService;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.service.ApiService;
import com.sonrisa.swarm.posintegration.service.ExtractorMonitoringService;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity.RpStoreState;

/**
 * Base class for testing status controllers
 * 
 * @author Barnabas
 */
public abstract class BaseStatusControllerTest extends BaseSecurityIntegrationTest {

    @Value("${api.name.shopify}")
    protected String shopifyApiName;

    @Value("${api.name.erply}")
    protected String erplyApiName;
    
    /**
     * Invoice monitor
     */
    @Autowired
    private ExtractorMonitoringService monitoringService;
    
    @Autowired
    private ApiService apiService;
    
    @Autowired
    protected InvoiceService invoiceService;
        
    /**
     * Setup a invoices for store
     */
    protected void setupInvoicesForStore(StoreEntity store, Date lastInvoice, int count){
        for(int i = 0; i < count; i++){
            InvoiceEntity invoice = new InvoiceEntity();
            invoice.setCompleted(Boolean.TRUE);
            invoice.setStore(store);
            invoice.setTotal(new BigDecimal("29.55"));
            invoice.setTs(DateUtils.addDays(lastInvoice, -i));
            invoice.setLsInvoiceId(1000L + i);
            invoiceService.saveEntityFromStaging(invoice);
        }
    }
    
    /**
     * Set up an active store with just finished extraction,
     * and a 5 recent invoice
     */
    protected Long setupStore(String apiName, Date lastActivity, Date lastInvoice, Boolean active, String name){
        StoreEntity store = new StoreEntity();
        store.setApiId(apiService.findByName(apiName).getApiId());
        store.setActive(active);
        store.setCreated(lastActivity);
        store.setName(name);
        final Long storeId = storeService.save(store);
        
        setupInvoicesForStore(store, lastInvoice, 5);
        monitoringService.addSuccessfulExecution(store.getId(), lastActivity);
        return storeId;
    }
    

    /**
     * Set up a Retail Pro 8 store with invoices, but no Heartbeat
     * and a 5 recent invoice
     */
    protected Long setupRpStore(String swarmId, Date lastActivity, Date lastInvoice, String name, RpStoreState state){
        RpStoreEntity rpStore = new RpStoreEntity();
        rpStore.setPosSoftware("retailpro8");
        rpStore.setCreated(lastActivity);
        rpStore.setStoreName(name);
        rpStore.setTimeZone("US/Pacific");
        rpStore.setSwarmId(swarmId);
        rpStore.setSbsNumber("123");
        rpStore.setStoreNumber("001");
        rpStore.setState(state);
        
        StoreEntity store = new StoreEntity();
        store.setCreated(lastActivity);
        storeService.save(store);
        rpStore.setStoreId(store.getId());
        
        setupInvoicesForStore(store, lastInvoice, 20);
        
        return rpStoreService.save(rpStore);
    }
    
    /**
     * Set up a Retail Pro 8 store with invoices, but no Heartbeat
     * and a 5 recent invoice
     */
    protected Long setupRpStore(String swarmId, Date lastActivity, Date lastInvoice, String name){
        return setupRpStore(swarmId, lastActivity, lastInvoice, name, RpStoreState.NORMAL);
    }
}
