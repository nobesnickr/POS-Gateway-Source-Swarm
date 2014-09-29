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
package com.sonrisa.swarm.retailpro.loader.store;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.service.ApiService;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity.ApiType;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;

/**
 * RetailPro store processor which creates a store entity for this RetailPro store
 * in the analytics DB and sets the {@link RpStoreEntity#storeId} on the RetailPro store
 * to maintain this relation.
 *
 * @author joe
 */
public class RpStoreProcessor implements ItemProcessor<RpStoreEntity, RpStoreEntity>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpStoreProcessor.class);   
    
    /** Note used by the processor if a new store has been created. */
    private static final String NOTE = "Created by RetailPro store processor job.";
    
    @Autowired
    private ApiService apiService;
    
    @Autowired
    private StoreService storeService;

    /** By default the first element of the {@link RpStoreProcessor#apiIdForPosSoftwareList} **/
    private String defaultApiForPosSoftware;
    
    /** 
     * Allowed pos softwares (e.g. retailpro9)
     */
    private Set<String> allowedPosNames = null;
    
    /**
     * Read API values after dependencies are set
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.allowedPosNames = apiService.findApiNamesByType(ApiType.RETAILPRO_API);
        if(this.allowedPosNames.size() == 0){
            throw new IllegalStateException("Should have at least one API"); 
        }
        this.defaultApiForPosSoftware = allowedPosNames.iterator().next();
    }
    
    @Override
    public RpStoreEntity process(RpStoreEntity item) throws Exception {
        LOGGER.debug("RetailPro store is under processing: " + item);
        
        // creates a store entity for this RetailPro store
        StoreEntity store = new StoreEntity();
        store.setCreated(new Date());
        store.setName(generateStoreName(item));
        store.setActive(Boolean.TRUE);
        store.setNotes(NOTE);
        store.setApiId(getApiId(item));        
                
        final Long storeId = storeService.save(store);
     
        // sets the storeId on the RetailPro store
        item.setStoreId(storeId); 
        LOGGER.debug("The storeId has been set on this RetailPro store: " + item);
        
        
        return item;
    }
        
    /**
     * Returns the API ID of this RetailPro store.
     * 
     * @param item
     * @return 
     */
    private Long getApiId(final RpStoreEntity item){
                
        if(allowedPosNames.contains(item.getPosSoftware())){
            return apiService.findByName(item.getPosSoftware()).getApiId();
        } else {
            return apiService.findByName(defaultApiForPosSoftware).getApiId();
        }
    }
    
    /**
     * Returns the name of this store generated from the identifiers 
     * of the RetailPro store.
     * 
     * @param item
     * @return 
     */
    private String generateStoreName(final RpStoreEntity item){  
        final StringBuilder name = new StringBuilder();      
        
        if(!StringUtils.isEmpty(item.getStoreName())){
            name.append(item.getStoreName());
        } else {   
            
            // If no StoreName received use Swarm ID
            name.append("swarmId:");
            name.append(item.getSwarmId());
        }

        // store number
        name.append(" (StoreNo: ");
        name.append(item.getStoreNumber());  
        
        // SBS number
        name.append(", SBS: ");
        name.append(item.getSbsNumber());
        name.append(")");
        
        return name.toString();
    }
}
