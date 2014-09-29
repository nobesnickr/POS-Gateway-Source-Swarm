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
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;
import com.sonrisa.swarm.test.service.store.BaseStoreServiceTest;

/**
 * Unit tests for the {@link RicsStoreServiceImpl} class.
 */
public class RicsStoreServiceImplTest extends BaseStoreServiceTest<RicsAccount> {

    /**
     * Mock Lightspeed Pro API
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
    
    final String storeName = "RICS Retail #1";
    
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
        final String password = "password";
        final String serialNum = "123000";
        final String storeCode = "0001";
        
        // Arrange
        when(mockApi.sendRequest(argThat(new ExternalCommandMatcher<RicsAccount>(RicsUri.INVOICES.uri)
                                .andParam("StoreCode",storeCode)
                                .andParam("BatchStartDate",RicsApi.DATE_MIN)
                                .andParam("BatchEndDate",RicsApi.DATE_MAX))))
                                .thenReturn(MockDataUtil.getResourceAsExternalResponse(MockRicsData.MOCK_INVOICES_ONE_PAGE_RESPONSE));
        
        
        // Act
        RicsAccount account = target.getAccount(userName, password, serialNum, storeCode);
        StoreEntity store = target.getStore(account);
        
        // Assert
        assertEquals(RICS_API_ID, store.getApiId());
        assertEquals(serialNum, aesUtility.aesDecrypt(store.getUsername()));
        assertEquals(password, aesUtility.aesDecrypt(store.getPassword()));
        assertEquals(userName, aesUtility.aesDecrypt(store.getApiKey()));
        assertEquals(storeName, store.getName());
        assertEquals(storeCode, store.getStoreFilter());
        assertEquals(Boolean.FALSE, store.getActive());
    }
    
    /**
     * Test case:
     *  Saving RICS store with its credentials
     *  
     * Expected:
     *  All fields are mapped as expected, and existing 
     *  entity is not overridden
     */
    @Test
    public void testExistingMultiLocationStore() throws Exception {
        
        final String userName = "sonrisa";
        final String password = "password";
        final String serialNum = "123000";
        
        // Arrange
        when(mockApi.sendRequest(argThat(new ExternalCommandMatcher<RicsAccount>(RicsUri.INVOICES.uri))))
                                .thenReturn(MockDataUtil.getResourceAsExternalResponse(MockRicsData.MOCK_INVOICES_ONE_PAGE_RESPONSE));
        
        StoreEntity existingEntity = setupSingleActiveExistingStore(RICS_API_ID, serialNum, password, null);
        
        
        // Act
        RicsAccount account = target.getAccount(userName, password, serialNum, null);
        StoreEntity store = target.getStore(account);
        
        // Assert
        assertEquals(RICS_API_ID, store.getApiId());
        assertEquals("Store userName should be serialNum", aesUtility.aesDecrypt(store.getUsername()), serialNum);
        assertEquals("Credentials should be updated", aesUtility.aesDecrypt(store.getApiKey()),userName);
        assertEquals("Name shouldn't be changed", existingEntity.getName(), store.getName());
        assertNull(store.getStoreFilter());
        assertEquals(Boolean.TRUE, store.getActive());
    }
}
