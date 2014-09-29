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
package com.sonrisa.swarm.erply.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonrisa.swarm.erply.controller.dto.ErplyAccountDTO;
import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.erply.MockErplyData;
import com.sonrisa.swarm.rest.controller.BaseControllerTest;

/**
 * Integration test for the {@link ErplyStoreController}
 * 
 * @author Barnabas
 */
public class ErplyStoreControllerTest extends BaseControllerTest {

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
        
        stubFor(post(urlEqualTo("/api/"))
                .withRequestBody(WireMock.containing("request=verifyUser"))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockErplyData.MOCK_ERPLY_VERIFY_USER))));
        
        stubFor(post(urlEqualTo("/api/"))
                .withRequestBody(containing("getConfParameters"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockErplyData.MOCK_ERPLY_CONF_PARAMETERS)))));
        
        stubFor(post(urlEqualTo("/api/"))
                .withRequestBody(containing("getCompanyInfo"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockErplyData.MOCK_ERPLY_COMPANY_INFO)))));
    }
    
    /**
     * Test case: 
     *  Sending request to /erply/account
     *  
     * Expected:
     *  - Returning a JSON response.
     *  - Return value has the store's name and store's id.
     *  - Store is persisted in the DB.
     */
    @Test
    public void testSavingStore() throws Exception {
        
        ErplyAccountDTO account = new ErplyAccountDTO();
        account.setUsername("sonrisa");
        account.setPassword("secr3t");
        account.setClientCode("123567");
        
        // Act
        mockMvc.perform(put(ErplyStoreController.CONTROLLER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(account)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType("application/json;charset=UTF-8"))
                        .andExpect(jsonPath("$['store_name']", is("Sonrisa")));
        
        assertEquals(1, storeDao.findAll().size());
    }
    
    /**
     * Test case: 
     *  Sending request to /erply/account
     *  
     * Expected:
     *  When service returns 404 the error response has a JSON format 
     */
    @Test
    public void testErrorResponseIsJson() throws Exception {

        stubFor(post(urlEqualTo("/api/"))
                .withRequestBody(containing("getConfParameters"))
                .willReturn(aResponse().withStatus(HttpStatus.SC_NOT_FOUND)));
        
        ErplyAccountDTO account = new ErplyAccountDTO();
        account.setUsername("sonrisa");
        account.setPassword("secr3t");
        account.setClientCode("123567");
        
        // Act
        mockMvc.perform(put(ErplyStoreController.CONTROLLER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(account)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType("application/json;charset=UTF-8"))
                        .andExpect(jsonPath("$['error_type']", is("api_error")))
                        .andExpect(jsonPath("$['error_message']", notNullValue()));
        
        assertEquals(0, storeDao.findAll().size());
    }
}
