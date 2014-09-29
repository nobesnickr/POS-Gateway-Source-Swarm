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

package com.sonrisa.swarm.lspro.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.lspro.controller.entity.LsProAccountEntity;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.lspro.MockLsProData;
import com.sonrisa.swarm.rest.controller.BaseControllerTest;

/**
 * Integration test for {@link LsProStoreController}
 * 
 * @author Barnabas
 */
public class LsProStoreControllerTest extends BaseControllerTest {
    
    /**
     * HTTP mocking utility
     */
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(5555);
    
    /**
     * Service for accessing the <code>stores</code> table
     */
    @Autowired
    public StoreDao storeDao;
    
    /**
     * Setup HTTP mocking
     */
    @Before
    public void setupHttpMocking(){
        // Matching any query to invoices for any LocationFilter
        stubFor(get(urlMatching("/Invoices\\?.*?\\$filter=\\(LocationName\\).*?"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockLsProData.MOCK_LSPRO_EMPTY_INVOICES)))));
        
        // Matching any query to invoices with empty filter
        stubFor(get(urlMatching("/Invoices\\?.*?\\$filter=\\&.*?"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockLsProData.MOCK_LSPRO_SAMPLE)))));
        //MOCK_LSPRO_SAMPLE
    }

    /**
     * Test case: 
     *  Sending request to /lspro/account
     *  
     * Expected:
     *  - Returning a JSON response.
     *  - Return value has the store's name and store's id.
     *  - Store is persisted in the DB.
     */
    @Test
    public void testSavingStore() throws Exception {
        
        LsProAccountEntity account = new LsProAccountEntity();
        account.setUserName("sonrisa");
        account.setToken("88888888-8888-8888-8888-888888888888");
        
        // Act
        mockMvc.perform(put(LsProStoreController.CONTROLLER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(account)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType("application/json;charset=UTF-8"))
                        .andExpect(jsonPath("$.LS-Store1", notNullValue()));
        
        assertEquals(1, storeDao.findAll().size());
    }
    
    /**
     * Test case:
     *  Sending request with a userName which isn't valid in the Lightspeed Pro service.
     *  
     * Expected:
     *  JSON error response.
     */
    @Test
    public void testSavingWithInvalidUsername() throws Exception {

        LsProAccountEntity account = new LsProAccountEntity();
        account.setUserName("sonrisa");
        account.setToken("88888888-8888-8888-8888-888888888888");
        
        // Matching any query to invoices 
        stubFor(get(urlMatching("/Invoices\\?.*"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockLsProData.MOCK_LSPRO_UNAUTHORIZED)))));
        
        // Act
        mockMvc.perform(put(LsProStoreController.CONTROLLER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(account)))
                        .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.inner_error", containsString("User was not found or doesn't have access")));
        
        
        assertEquals(0, storeDao.findAll().size()); 
    }
}
