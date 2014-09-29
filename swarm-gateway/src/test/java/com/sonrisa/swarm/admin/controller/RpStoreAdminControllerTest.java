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
package com.sonrisa.swarm.admin.controller;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;

import com.sonrisa.swarm.BaseSecurityIntegrationTest;
import com.sonrisa.swarm.admin.model.RpStoreAdminServiceEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;

/**
 * Integration test for the {@link RpStoreAdminController}.
 * @author Barnabas
 *
 */
public class RpStoreAdminControllerTest extends BaseSecurityIntegrationTest {
    
    private StoreEntity store;
    
    private RpStoreEntity rpStore;
    
    /**
     * Sets up a store in the DB
     */
    @Before
    public void setupStore(){
        store = new StoreEntity();
        store.setName("Old name");
        store.setApiId(apiService.findByName("erply").getApiId());
        storeService.save(store);
        
        rpStore = new RpStoreEntity();
        rpStore.setStoreId(store.getId());
        rpStoreService.save(rpStore);
    }
    

    /**
     * Test case:
     *  We have a single store in DB
     *  
     * Expected:
     *  Store name is updated <code>stores</code> and <code>rp_stores</code>,
     *  and timezone is updated in <code>rp_stores</code>. 
     * 
     * @throws Exception 
     *  
     */
    @Test
    public void testUpdatingNameAndTimezone() throws Exception {
        
        RpStoreAdminServiceEntity entity =  new RpStoreAdminServiceEntity();
        entity.setName("ABC");
        entity.setTimezone("US/Eastern");
        
        perfom(put(RpStatusServiceController.URI + "/" + store.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entity))
                .header("Authorization", getAuthorizationHeader(USERNAME,PASSWORD)))
                .andExpect(status().isCreated());
        
        StoreEntity newStore = storeService.find(store.getId());
        assertEquals(entity.getName(), newStore.getName());
        
        RpStoreEntity newRpStore = rpStoreService.find(rpStore.getId());
        assertEquals(entity.getName(), newRpStore.getStoreName());
        assertEquals(entity.getTimezone(), newRpStore.getTimeZone());
    }
}
