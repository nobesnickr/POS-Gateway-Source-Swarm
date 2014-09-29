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
package com.sonrisa.swarm.kounta.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.kounta.MockKountaData;
import com.sonrisa.swarm.rest.controller.BaseControllerTest;

/**
 * Tests for the {@link KountaOAuthController}
 */
public class KountaOAuthControllerTest extends BaseControllerTest {

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
     * Setup Wiremock for mocking the rest services
     */
    public static void setupRegistrationAndTokenPages(){
        
        final String refreshLocation = "/token.json";

        // Refreshing the token
        stubFor(post(urlMatching(refreshLocation))
                .withRequestBody(containing("refresh_token="))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockKountaData.MOCK_KOUNTA_ACCESS_TOKEN))));
        
        // Exchanging temporary token
        stubFor(post(urlMatching(refreshLocation))
                .withRequestBody(containing("code="))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockKountaData.MOCK_KOUNTA_REFRESH_TOKEN))));
        
        // Fetching company info
        stubFor(get(urlMatching("/companies/me.json"))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockKountaData.MOCK_KOUNTA_COMPANY))));
        
        // Fetching company's site info
        stubFor(get(urlMatching("/companies/1151/sites.json"))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockKountaData.MOCK_KOUNTA_SITES))));
        
    }
    
    /**
     * Setup test context
     */
    @Before
    public void setupContext(){
        setupRegistrationAndTokenPages();
    }
    
    /**
     * Test case:
     *  User lands on the landing page with token
     *  
     * Expected:
     *  User's store is created
     */
    @Test
    public void testBestCaseScenario() throws Exception{
        
        // Act
        mockMvc.perform(get(KountaOAuthController.LANDING_PAGE )
                        .param("code", "risason"));
        
        // Assert
        assertEquals(2, storeDao.findAll().size());   
    }
}
