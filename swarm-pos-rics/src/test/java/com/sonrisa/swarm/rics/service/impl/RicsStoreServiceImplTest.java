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
package com.sonrisa.swarm.rics.service.impl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.rics.MockRicsData;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.rics.RicsAccount;
import com.sonrisa.swarm.rics.RicsApiReader;
import com.sonrisa.swarm.rics.api.RicsApi;
import com.sonrisa.swarm.rics.constants.RicsUri;
import com.sonrisa.swarm.rics.service.exception.RicsStoreServiceException;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;
import com.sonrisa.swarm.test.service.store.BaseStoreServiceTest;

/**
 * Unit tests for the {@link RicsStoreServiceImpl} class.
 */
public class RicsStoreServiceImplTest extends BaseStoreServiceTest<RicsAccount> {

    /**
     * Mock RICS API
     */
    @Mock
    protected ExternalAPI<RicsAccount> mockApi;
    
    /**
     * Target being tested
     */
    private RicsStoreServiceImpl target;
    
    /**
     * Name of the RICS API
     */
    private static final String RICS_API_NAME = "rics";
    
    /**
     * API id of RICS
     */
    private static final Long RICS_API_ID = 80L;
    
    /**
     * Name of the store in the mock JSON files
     */
    private static final String STORE_NAME = "Legends Owensboro";
    
    /**
     * Store code in the mock JSON
     */
    private static final String STORE_CODE = "0001";
    
    /**
     * Sets up target and its dependencies
     */
    @Before
    public void setUp() throws Exception {
        // Setup StoreService
        setupApiService(RICS_API_NAME, RICS_API_ID);
        
        target = new RicsStoreServiceImpl();
        target.setAesUtility(aesUtility);
        target.setStoreService(mockStoreService);
        target.setRicsApiName(RICS_API_NAME);
        target.setApiService(mockApiService);
        
        target.setRicsApiReader(new RicsApiReader(mockApi));
        
        when(mockApi.sendRequest(argThat(new ExternalCommandMatcher<RicsAccount>(RicsUri.INVOICES.uri)
                .andParam("StoreCode",STORE_CODE)
                .andParam("BatchStartDate",RicsApi.DATE_MIN)
                .andParam("BatchEndDate",RicsApi.DATE_MAX))))
                .thenReturn(MockDataUtil.getResourceAsExternalResponse(MockRicsData.MOCK_INVOICES_ONE_PAGE_RESPONSE));
     
    }
    
    /**
     * Test case:
     *  Saving RICS store with its credentials
     *  
     * Expected:
     *  All fields are mapped as expected
     */
    @Test
    public void testNewSingleLocationStore() throws Exception {
        
        final String userName = "sonrisa";
        final String token = "token";
        final String timeZone = "US/Eastern";
         
        // Act
        RicsAccount account = target.getAccount(userName, token, STORE_CODE, timeZone);
        StoreEntity store = target.getStore(account);
        
        // Assert
        assertEquals(RICS_API_ID, store.getApiId());
        assertEquals(userName, aesUtility.aesDecrypt(store.getUsername()));
        assertEquals(token, aesUtility.aesDecrypt(store.getApiKey()));
        assertEquals(STORE_NAME, store.getName());
        assertEquals(STORE_CODE, store.getStoreFilter());
        assertEquals(Boolean.FALSE, store.getActive());
        assertEquals(timeZone, store.getTimeZone());
    }
    
    /**
     * Test case:
     *  Saving RICS store with invalid timezone
     *  
     * Expected:
     *  Exception is thrown
     */
    @Test(expected = RicsStoreServiceException.class)
    public void testSavingRicsStoreWithInvalidTimezone() throws Exception {
        
        final String userName = "sonrisa";
        final String token = "token";
        final String timeZone = "US/Invalid";
        
        // Arrange
        when(mockApi.sendRequest(argThat(new ExternalCommandMatcher<RicsAccount>(RicsUri.INVOICES.uri))))
                                .thenReturn(MockDataUtil.getResourceAsExternalResponse(MockRicsData.MOCK_INVOICES_ONE_PAGE_RESPONSE));
        
        // Act
        target.getAccount(userName, token, STORE_CODE, timeZone);
    }
}
