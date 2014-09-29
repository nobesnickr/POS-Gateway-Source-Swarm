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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.sonrisa.swarm.BaseSecurityIntegrationTest;
import com.sonrisa.swarm.admin.model.StoreAdminServiceEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.service.impl.ApiServiceImpl;

/**
 * Integration test for the {@link StoreAdminController}.
 * @author Barnabas
 *
 */
public class StoreAdminControllerTest extends BaseSecurityIntegrationTest {
    
    private StoreEntity store;
    
    /**
     * We need to inject the interface implementation to be able
     * to refresh its cache
     */
    @Autowired
    private ApiServiceImpl apiService;
    
    /**
     * Sets up a store in the DB
     */
    @Before
    public void setupStore(){
        store = new StoreEntity();
        store.setName("Old name");
        store.setApiId(apiService.findByName("erply").getApiId());
        storeService.save(store);
    }
    

    /**
     * Test case:
     *  We have a single store in DB
     *  
     * Expected:
     *  Both are returned by the status service, with status OK. 
     * 
     * @throws Exception 
     *  
     */
    @Test
    public void testUpdatingName() throws Exception {
        
        StoreAdminServiceEntity entity =  new StoreAdminServiceEntity();
        entity.setName("ABC");
        entity.setActive(Boolean.TRUE);
        
        perfom(put(StatusServiceController.URI + "/" + store.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entity))
                .header("Authorization", getAuthorizationHeader(USERNAME,PASSWORD)))
                .andExpect(status().isCreated());
        
        StoreEntity newStore = storeService.find(store.getId());
        assertEquals(entity.getName(), newStore.getName());
    }
    

    /**
     * Test case:
     *  We have a single store in DB
     *  
     * Expected:
     *  Both are returned by the status service, with status OK. 
     * 
     * @throws Exception 
     *  
     */
    @Test
    public void testUpdatingNotGatewayStore() throws Exception {
        
        final Long apiId = 1000L;
        
        // Insert API
        jdbcTemplate.execute("INSERT INTO apis (api_id, name) VALUES (" + apiId + ",'Not-GW');");
        apiService.refreshCache();
        
        StoreEntity otherStore = new StoreEntity();
        otherStore.setApiId(apiId); // Not associated with the GW
        storeService.save(otherStore);
        
        StoreAdminServiceEntity entity =  new StoreAdminServiceEntity();
        entity.setName("ABC");
        entity.setActive(Boolean.TRUE);
        
        perfom(put(StatusServiceController.URI + "/" + otherStore.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entity))
                .header("Authorization", getAuthorizationHeader(USERNAME,PASSWORD)))
                .andExpect(status().isBadRequest());
        
        StoreEntity newStore = storeService.find(otherStore.getId());
        assertNotEquals(entity.getName(), newStore.getName());
    }
}
