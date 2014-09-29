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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Date;

import org.junit.Test;
import org.springframework.http.MediaType;

import com.sonrisa.swarm.retailpro.model.RpStoreEntity;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity.RpStoreState;
import com.sonrisa.swarm.retailpro.rest.controller.RpClientController;

/**
 * Integration test for the {@link RpStatusServiceController}
 * 
 * @author Barnabas
 */
public class RpStatusServiceControllerTest extends BaseStatusControllerTest {
    
    private static final String URI_OF_HEARTBEAT = RpClientController.URI_BASE + RpClientController.URI_OF_HEARTBEAT;
    
    /**
     * Test case:
     *  We have two stores a Shopify and a Retail Pro, but no heartbeat for Retail Pro
     *  
     * Expected:
     *  Status service returned the Retail Pro store with ERROR
     */
    @Test
    public void testStatusIfNoHeartbeat() throws Exception{
        
        final String swarmId = "sonrisa-test";
        
        setupStore(shopifyApiName, new Date(), new Date(), Boolean.TRUE, "Test shopify");
        setupRpStore(swarmId, new Date(), new Date(), "Test store");

        // Expection only the Retail Pro store to be returned
        perfom(get(RpStatusServiceController.URI)
            .header("Authorization", getAuthorizationHeader(USERNAME,PASSWORD)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stores", hasSize(1)))
            .andExpect(jsonPath("$.stores[0].swarm_id", equalTo(swarmId)))
            .andExpect(jsonPath("$.stores[0].status", equalTo("ERROR")));
    }
    
    /**
     * Test case:
     *  We have two stores a Shopify and a Retail Pro, and the Retail Pro store just received a heartbeat
     *  
     * Expected:
     *  Status service returned the Retail Pro store with ERROR
     */
    @Test
    public void testFilteringByStatus() throws Exception{
        
        final String swarmId = "sonrisa-test";
        final String version = "1.6.2.0";
        
        setupStore(shopifyApiName, new Date(), new Date(), Boolean.TRUE, "Test shopify");
        setupRpStore(swarmId, new Date(), new Date(), "Test store");
        
        // Sending heartbeat
        String heartbeatJson = "{ \"Version\": \"" + version + "\" }";
        perfom(put(URI_OF_HEARTBEAT)
                .content(heartbeatJson)
                .contentType(MediaType.APPLICATION_JSON)
                .header("SwarmId", swarmId));

        // Expecting nothing in return, as Retail Pro store should be OK
        perfom(get(RpStatusServiceController.URI)
                .param("status", "WARNING,ERROR")
            .header("Authorization", getAuthorizationHeader(USERNAME,PASSWORD)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stores", hasSize(0)));
        
        // Expecting the single Retail Pro store to be returned
        perfom(get(RpStatusServiceController.URI)
                .param("status", "OK")
                .param("order_by", "swarm_id")
            .header("Authorization", getAuthorizationHeader(USERNAME,PASSWORD)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stores", hasSize(1)))
            .andExpect(jsonPath("$.stores[0].swarm_id", equalTo(swarmId)))
            .andExpect(jsonPath("$.stores[0].details.client_version", equalTo(version)));
    }
    

    /**
     * Test case:
     *  We have two stores a Shopify and a Retail Pro, but no heartbeat for Retail Pro
     *  
     * Expected:
     *  Status service returned the Retail Pro store with ERROR
     */
    @Test
    public void testIllegalOrderBy() throws Exception{
        
        // Expection only the Retail Pro store to be returned
        perfom(get(RpStatusServiceController.URI)
                .param("order_by", "Illeg@l Column$$")
            .header("Authorization", getAuthorizationHeader(USERNAME,PASSWORD)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorMessage").exists());
    }

	/*
     * Test case:
     *  We have two Retail Pro stores, but one of them is ignored.
     *  
     * Expected:
     *  Status service returns both of the if <code>includeAll=true</code> is added
     */
    @Test
    public void testIncludingAllStores() throws Exception{
        
        setupRpStore("sonrisa-test", new Date(), new Date(), "Test store");
        setupRpStore("sonrisa-test-2", new Date(), new Date(), "Test store ignored", RpStoreState.IGNORED);

        // Expection only the Retail Pro store to be returned
        perfom(get(RpStatusServiceController.URI)
            .header("Authorization", getAuthorizationHeader(USERNAME,PASSWORD))
            .param("include_all", "true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stores", hasSize(2)));
    }
        
    /**
     *  Test case:
     *  We have a Retail Pro store in the stores_rp table which
     *  doesn't have a <code>pos_software</code> set, because
     *  it's an old Retail Pro 8 implementation.
     *  
     * Expected:
     *  It's treated as a Retail Pro 8 store
     */
    @Test
    public void testFilteringForEmptyPos() throws Exception{
        
        final String swarmId = "empty-pos";

        // Setup store
        RpStoreEntity rpStore = new RpStoreEntity();
        rpStore.setSwarmId(swarmId);
        rpStore.setPosSoftware("");
        rpStoreService.save(rpStore);
        
        // Expecting the single Retail Pro store to be returned
        perfom(get(RpStatusServiceController.URI)
                .param("api", "retailpro8")
            .header("Authorization", getAuthorizationHeader(USERNAME,PASSWORD)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stores", hasSize(1)))
            .andExpect(jsonPath("$.stores[0].swarm_id", equalTo(swarmId)));
        
    }
}
