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

package com.sonrisa.swarm.revel.controller;


import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.revel.MockRevelData;
import com.sonrisa.swarm.rest.controller.BaseControllerTest;
import com.sonrisa.swarm.revel.service.RevelStoreService;

/**
 * Integration test for the {@link RevelStoreController}
 * 
 * @author Barnabas
 */
public class RevelStoreControllerTest extends BaseControllerTest {

    /**
     * HTTP mocking utility
     */
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(2145);
    
    /**
     * Store DAO for asserting created stores
     */
    @Autowired
    private StoreDao storeDao;
    
    /**
     * Setup mocked Revel web service
     */
    @Before
    public void setupWiremock(){
        // Matching any query to establishment returns the mock JSON
        stubFor(get(urlMatching("/" + RevelStoreService.ESTABLISHMENT_URI + ".*?"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockRevelData.MOCK_ESTABLISHMENTS)))));
        
    }
    
    /**
     * Test case: 
     *  Executing POST request to the Revel controller,
     *  with establishment division
     *  
     * Expected:
     *  Establishment are parsed, and two stores are created
     */
    @Test
    public void testBestCaseScenario() throws Exception {
        
        // Act
        mockMvc.perform(post(RevelStoreController.CONTROLLER_PATH)
                .param("username", "sonrisa-user")
                .param("apikey", "key")
                .param("apisecret", "secret")
                .param("division", "true"));
        
        // Assert
        assertEquals(2, storeDao.findAll().size());        
    }
    
    /**
     * Test case: 
     *  Executing POST request to the Revel controller 
     *  with invalid API key, so Revel says:
     *  <code>401 - Unauthorized</code>
     *  
     * Expected:
     *  
     *  Page contains user friendly error message
     */
    @Test
    public void testInvalidApiKey() throws Exception {
        
        stubFor(get(urlMatching("/" + RevelStoreService.ESTABLISHMENT_URI + ".*?"))
                .willReturn(aResponse().withStatus(401)));
        
        // Act
        mockMvc.perform(post(RevelStoreController.CONTROLLER_PATH)
                .param("username", "sonrisa-user")
                .param("apikey", "invalid-key")
                .param("apisecret", "secret")
                .param("division", "true"))
                .andExpect(model().attribute("errorMsg", "Wrong API key or API secret, Revel says: Unauthorized"));
        
        // Assert
        assertEquals(0, storeDao.findAll().size());        
    }
}
