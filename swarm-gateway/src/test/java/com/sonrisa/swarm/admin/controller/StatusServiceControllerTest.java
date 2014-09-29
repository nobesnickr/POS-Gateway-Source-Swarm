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

/**
 * Integration test for the {@link StatusServiceController}
 * 
 * @author Barnabas
 */
public class StatusServiceControllerTest extends BaseStatusControllerTest {
    
    /**
     * Test case:
     *  We have two stores an Erply and Shopify, everything is running smoothly.
     *  
     * Expected:
     *  Both are returned by the status service, with status OK. 
     * 
     * @throws Exception 
     *  
     */
    @Test
    public void testBestCaseScenario() throws Exception{
        
        setupStore(shopifyApiName, new Date(), new Date(), Boolean.TRUE, "Test shopify");
        setupStore(erplyApiName, new Date(), new Date(), Boolean.TRUE, "Test erply");

        perfom(get(StatusServiceController.URI)
            .header("Authorization", getAuthorizationHeader(USERNAME,PASSWORD)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stores", hasSize(2)))
            .andExpect(jsonPath("$.stores.[*].status", containsInAnyOrder("OK", "OK")));
    }
    
    /**
     * Test case:
     *  We have many Erply and Shopify stores, everything is running smoothly.
     *  
     * Expected:
     *  Ordering by name, skipping and paging works
     * 
     */
    @Test
    public void testOrderingAndPaging() throws Exception {
        
        // Create 20 stores, Shopify before Erply in the alphabet
        for(int i = 0; i < 10; i++){
            setupStore(shopifyApiName, new Date(), new Date(), Boolean.TRUE, "AAAA Test shopify " + i);
            setupStore(erplyApiName, new Date(), new Date(), Boolean.TRUE, "ZZZZ Test erply" + i);
        }
        
        // Skipping first 8 shopify stores, so expecting 2-2 Shopify and Erply
        final Integer take = 4;
        final Integer skip = 8;

        perfom(get(StatusServiceController.URI)
                .param("take", take.toString())
                .param("skip", skip.toString())
                .param("order_dir", "asc")
                .param("order_by", "name")
            .header("Authorization", getAuthorizationHeader(USERNAME,PASSWORD)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stores", hasSize(take)))
            .andExpect(jsonPath("$.stores[0].api", equalTo(shopifyApiName)))
            .andExpect(jsonPath("$.stores[(@.length-1)].api", equalTo(erplyApiName)));
    }
    

    /**
     * Test case:
     *  We have many Erply and Shopify stores, everything is running smoothly.
     *  
     * Expected:
     *  Ordering by name, skipping and paging works
     * 
     */
    @Test
    public void testFilteringForApi() throws Exception {

        setupStore(shopifyApiName, new Date(), new Date(), Boolean.TRUE, "Test shopify");
        setupStore(erplyApiName, new Date(), new Date(), Boolean.TRUE, "Test erply");
        
        perfom(get(StatusServiceController.URI)
                .param("api", "revel,shopify")
            .header("Authorization", getAuthorizationHeader(USERNAME,PASSWORD)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stores", hasSize(1)))
            .andExpect(jsonPath("$.stores[0].api", equalTo(shopifyApiName)));
    }
    

    /**
     * Test case:
     *  Sending request which is invalid
     *  
     * Expected:
     *  Response is human readable JSON
     * 
     */
    @Test
    public void testInvalidRequest() throws Exception {

        perfom(get(StatusServiceController.URI)
                .param("status", "COOOL")
            .header("Authorization", getAuthorizationHeader(USERNAME,PASSWORD)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorMessage").exists());
    }
}
