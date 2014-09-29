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

package com.sonrisa.swarm.lspro.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.sonrisa.swarm.lspro.LsProAPIReader;
import com.sonrisa.swarm.lspro.LsProAccount;
import com.sonrisa.swarm.lspro.service.impl.LsProStoreServiceImpl;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.lspro.MockLsProData;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.service.exception.StoreScanningException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;
import com.sonrisa.swarm.test.service.store.BaseStoreServiceTest;

/**
 * Class testing the {@link LsProStoreServiceImpl}.
 */
public class LsProStoreServiceImplTest extends BaseStoreServiceTest<LsProAccount>{
    /**
     * Mock Lightspeed Pro API
     */
    @Mock
    protected ExternalAPI<LsProAccount> mockApi;
    
    /**
     * Target being tested
     */
    private LsProStoreServiceImpl target;
    
    /**
     * Name of the Lightspeed Pro API
     */
    private static final String LSPRO_API_NAME = "lightspeedpro";
    
    /**
     * Api id of Lightspeed Pro
     */
    private static final Long LSPRO_API_ID = 8L;
    
    /**
     * Name of the location one the first request
     */
    private static final String LOCATION_NAME = "LS-Store1";
    
    /**
     * Name of the location one the second request
     */
    private static final String SECOND_LOCATION_NAME = "LS-Store2";
        
    /**
     * Sets up target and its dependencies
     */
    @Before
    public void setUp() throws Exception {
        // Setup StoreService
        setupApiService(LSPRO_API_NAME, LSPRO_API_ID);
        
        target = new LsProStoreServiceImpl();
        target.setAesUtility(aesUtility);
        target.setStoreService(mockStoreService);
        target.setApiService(mockApiService);
        target.setLsProApiName(LSPRO_API_NAME);
        
        // Setup API
        when(mockApi.sendRequest(argThat(new ExternalCommandMatcher<LsProAccount>("Invoices")
                                .andParam("$filter","(LocationName)ne('" + LOCATION_NAME + "')"))))
        .thenReturn(MockDataUtil.getResourceAsExternalResponse(MockLsProData.MOCK_LSPRO_EMPTY_INVOICES));
        
        when(mockApi.sendRequest(argThat(new ExternalCommandMatcher<LsProAccount>("Invoices").andParam("$filter", ""))))
        .thenReturn(MockDataUtil.getResourceAsExternalResponse(MockLsProData.MOCK_LSPRO_SAMPLE));
        
        target.setApiReader(new LsProAPIReader(mockApi));
    }

    /**
     * Test case:
     *  Saving LightspeedPro stores with userName and password
     *  
     * Expected:
     *  Few invoices are fetched and their <code>LocationName</code> field 
     *  is saved into the <code>store_filter</code> and <code>name</code> fields
     *  
     *  The <code>user_name</code> and <code>password</code> columns are filled in with
     *  the encrypted values.
     */
    @Test
    public void testCommonStoreFields() throws StoreScanningException {
        
        final String userName = "sonrisa";
        final String password = "password";
        
        // Act
        LsProAccount dummyAccount = target.getStore(userName, password);
        List<StoreEntity> result = target.scanForLocations(dummyAccount);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(LSPRO_API_ID, result.get(0).getApiId());
        assertEquals(userName, aesUtility.aesDecrypt(result.get(0).getUsername()));
        assertEquals(password, aesUtility.aesDecrypt(result.get(0).getPassword()));
        assertEquals(LOCATION_NAME, result.get(0).getName());
        assertEquals(LOCATION_NAME, result.get(0).getStoreFilter());
        assertNull(result.get(0).getApiKey());
        assertNull(result.get(0).getApiUrl());

        assertEquals(Boolean.FALSE, result.get(0).getActive());
    }
    
    /**
     * Test case:
     *  Saving LightspeedPro stores with userName and password,
     *  but REST service has no invoices for this store (yet).
     *  
     * Expected:
     *  A single {@link StoreEntity} is returned with userName and 
     *  password encrypted, and the store's name set to the userName. 
     */
    @Test
    public void testEmptyStoreIsAddedWithNoLocation() throws Exception {

        // Setup API
        when(mockApi.sendRequest(argThat(new ExternalCommandMatcher<LsProAccount>("Invoices"))))
        .thenReturn(MockDataUtil.getResourceAsExternalResponse(MockLsProData.MOCK_LSPRO_EMPTY_INVOICES));
        target.setApiReader(new LsProAPIReader(mockApi));

        final String userName = "sonrisa";
        final String password = "password";
        
        // Act
        LsProAccount dummyAccount = target.getStore(userName, password);
        List<StoreEntity> result = target.scanForLocations(dummyAccount);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(LSPRO_API_ID, result.get(0).getApiId());
        assertEquals(userName, aesUtility.aesDecrypt(result.get(0).getUsername()));
        assertEquals(password, aesUtility.aesDecrypt(result.get(0).getPassword()));
        assertEquals(userName, result.get(0).getName());
        assertNull(result.get(0).getStoreFilter());
        assertNull(result.get(0).getApiKey());
        assertNull(result.get(0).getApiUrl());
        assertEquals(Boolean.FALSE, result.get(0).getActive());
    }

    /**
     * Test case:
     *  Saving LightspeedPro stores with userName and password,
     *  but this userName and location value is already in the DB
     *  
     * Expected:
     *  It's password fields is updated. Other fields, like active is 
     *  not modified.
     */
    @Test
    public void testUpdatingExistingStore() throws Exception {

        final String userName = "sonrisa";
        final String password = "password";
        
        StoreEntity existingEntity = setupSingleActiveExistingStore(LSPRO_API_ID, userName, password, LOCATION_NAME);
        
        // Act
        LsProAccount dummyAccount = target.getStore(userName, password);
        List<StoreEntity> result = target.scanForLocations(dummyAccount);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(existingEntity.getId(), result.get(0).getId());
        assertEquals(LOCATION_NAME, result.get(0).getStoreFilter());
        assertEquals(Boolean.TRUE, result.get(0).getActive());
    }
    
    /**
     * Test case:
     *  Saving LightspeedPro stores with userName and password
     *  
     * Expected:
     *  Invoices are fetched, eliminating the last LocationName value
     *  until empty result set is returned.
     * @throws ExternalExtractorException 
     */
    @Test
    public void testMultipleLocation() throws Exception {
        
        final String userName = "sonrisa";
        final String password = "password";
        
        final String filterForBoth = String.format("(LocationName)ne('%s')and(LocationName)ne('%s')", SECOND_LOCATION_NAME, LOCATION_NAME);
        when(mockApi.sendRequest(argThat(new ExternalCommandMatcher<LsProAccount>("Invoices").andParam("$filter", filterForBoth))))
            .thenReturn(MockDataUtil.getResourceAsExternalResponse(MockLsProData.MOCK_LSPRO_EMPTY_INVOICES));
        
        final String filterForFirst= String.format("(LocationName)ne('%s')", LOCATION_NAME);
        when(mockApi.sendRequest(argThat(new ExternalCommandMatcher<LsProAccount>("Invoices").andParam("$filter", filterForFirst))))
            .thenReturn(MockDataUtil.getResourceAsExternalResponse(MockLsProData.MOCK_LSPRO_DIFFERENT_LOCATION));
        
        when(mockApi.sendRequest(argThat(new ExternalCommandMatcher<LsProAccount>("Invoices").andParam("$filter", ""))))
        .thenReturn(MockDataUtil.getResourceAsExternalResponse(MockLsProData.MOCK_LSPRO_SAMPLE));
        
        target.setApiReader(new LsProAPIReader(mockApi));
        
        // Act
        LsProAccount dummyAccount = target.getStore(userName, password);
        List<StoreEntity> result = target.scanForLocations(dummyAccount);
        
        // Assert
        assertEquals(2, result.size());
        
        Set<String> locationNames = new HashSet<String>();
        for(StoreEntity store : result){
            locationNames.add(store.getStoreFilter());
        }
        
        assertEquals(2, locationNames.size());
        assertTrue("First location missing", locationNames.contains(LOCATION_NAME));
        assertTrue("Last location missing", locationNames.contains(SECOND_LOCATION_NAME));
    }
}
