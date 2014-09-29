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
package com.sonrisa.swarm.job.rics;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonrisa.swarm.controller.RicsStoreController;
import com.sonrisa.swarm.controller.entity.RicsAccountEntity;
import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.rics.MockRicsData;
import com.sonrisa.swarm.rest.controller.BaseControllerTest;
import com.sonrisa.swarm.rics.constants.RicsUri;

/**
 * Integration test for the {@link RicsStoreController}
 * @author Barnabas
 */
public class RicsStoreControllerTest extends BaseControllerTest {

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
     * Setup Wiremock
     */
    @Before
    public void setUp() {
        stubFor(post(urlMatching(RicsUri.INVOICES.uri)).willReturn(
                aResponse().withBody(MockDataUtil.getResourceAsString(MockRicsData.MOCK_INVOICES_ONE_PAGE_RESPONSE))));

        stubFor(post(urlMatching(RicsUri.LOGIN.uri)).willReturn(
                aResponse().withBody(MockDataUtil.getResourceAsString(MockRicsData.MOCK_TOKEN))));
    }
    

    /**
     * Test case: 
     *  Sending request to /rics/account
     *  
     * Expected:
     *  - Returning a JSON response.
     *  - Store is persisted in the DB.
     */
    @Test
    public void testSavingStore() throws Exception {
        mockMvc.perform(put(RicsStoreController.CONTROLLER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(mockAccountEntity()))))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType("application/json;charset=UTF-8"))
                        .andExpect(jsonPath("$[*].store_id", notNullValue()));
        
        assertEquals(1, storeDao.findAll().size());
    }
    
    /**
     * Test case: 
     *  Sending request to /rics/info
     *  
     * Expected:
     *  - Returning a JSON response.
     *  - Store is <strong>not</strong> persisted in the DB.
     */
    @Test
    public void testStoreInfo() throws Exception {
        
        mockMvc.perform(post(RicsStoreController.INFO_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockAccountEntity())))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
        

        assertEquals(0, storeDao.findAll().size());
    }
    
    /**
     * Test case: 
     *  Sending request to /rics/info
     *  
     * Expected:
     *  - Returning a JSON response.
     *  - Return value has the store's name
     *  - Store is not persisted in the DB.
     */
    @Test
    public void testStoreInfoWithBadCredentials() throws Exception {
        stubFor(post(urlMatching(RicsUri.LOGIN.uri)).willReturn(
                aResponse().withBody(MockDataUtil.getResourceAsString(MockRicsData.MOCK_UNSUCCESS))));
        
        mockMvc.perform(post(RicsStoreController.INFO_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockAccountEntity())))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.error", notNullValue()));
        

        assertEquals(0, storeDao.findAll().size());
    }
    
    /**
     * Create mock account entity
     */
    private static final RicsAccountEntity mockAccountEntity(){
        RicsAccountEntity account = new RicsAccountEntity();
        account.setLoginName("sonrisa");
        account.setPassword("password");
        account.setSerialNum("88888888");
        return account;

    }
}
