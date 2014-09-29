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
package com.sonrisa.swarm.revel.service.impl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.revel.MockRevelData;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.api.service.exception.StoreScanningException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;
import com.sonrisa.swarm.posintegration.service.impl.ApiServiceImpl;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity.ApiType;
import com.sonrisa.swarm.revel.RevelAPI;
import com.sonrisa.swarm.revel.RevelAPIReader;
import com.sonrisa.swarm.revel.RevelAccount;
import com.sonrisa.swarm.revel.service.RevelStoreService;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;

/**
 * Test class for the {@link RevelStoreServiceImpl} class.
 */
public class RevelStoreServiceImplTest {

    private static final String REVEL_API_NAME = "revel";
    
    final static Long REVEL_API_ID = 7l;

    final static String USERNAME = "example-store";
    final static String API_KEY = "123456789abcdef";
    final static String API_SECRET = "API-SECRET";

    private AESUtility aesUtility = new AESUtility();
    
    private ApiServiceImpl mockApiService;
    
    private StoreService mockStoreService;
    
    private RevelAPI mockRevelAPI;
    
    /** Instance being tested */
    private RevelStoreService target; 
    
    @Before
    public void setupTest() throws ExternalExtractorException {
        mockStoreService = mock(StoreService.class);
        
        mockApiService = new ApiServiceImpl();
        mockApiService.fillCache(Arrays.asList(new ApiEntity(REVEL_API_ID, REVEL_API_NAME, ApiType.PULL_API)));
        
        this.mockRevelAPI = mock(RevelAPI.class);
        
        when(mockRevelAPI.sendRequest(argThat(new ExternalCommandMatcher<RevelAccount>("enterprise/Establishment"))))
                .thenReturn(MockDataUtil.getResourceAsExternalResponse(MockRevelData.MOCK_ESTABLISHMENTS));
        
        RevelStoreServiceImpl serviceImpl = new RevelStoreServiceImpl();
        serviceImpl.setRevelApiName(REVEL_API_NAME);
        serviceImpl.setStoreService(mockStoreService);
        serviceImpl.setAesUtility(aesUtility);
        serviceImpl.setApiReader(new RevelAPIReader(mockRevelAPI));
        serviceImpl.setApiService(mockApiService);
        this.target = serviceImpl;
    }
    
    /**
     * Tests the {@link RevelStoreServiceImpl#createStore} function
     * 
     *  Test case: Inserting new Revel Store
     *  Expected: {@link StoreService#save(StoreEntity)} is called     * 
     * @throws UnsupportedEncodingException 
     */
    @Test
    public void testCreateRevelStore() throws UnsupportedEncodingException{
        
        // Act
        RevelAccount account = target.getAccount(USERNAME, API_KEY, API_SECRET);
        final StoreEntity result = target.getRootStoreEntity(account);
        
        // Assert
        assertEquals(USERNAME,aesUtility.aesDecrypt(result.getUsername()));
        assertEquals(API_KEY,aesUtility.aesDecrypt((result.getApiKey())));
        assertEquals(API_SECRET,aesUtility.aesDecrypt((result.getPassword())));
        assertEquals(Boolean.FALSE,result.getActive());
        assertEquals(REVEL_API_ID,result.getApiId());
        assertEquals("",result.getStoreFilter());
    }
    

    /**
     * Tests the {@link RevelStoreServiceImpl#createStore} function
     *  Test case: Inserting new Revel Store with no establishment
     *  Expected: {@link StoreService#save(StoreEntity)} is called 
     * @throws RevelEstablishmentParseException 
     * @throws ExternalExtractorException 
     */
    @Test
    public void testCreateManyRevelStores() throws StoreScanningException {
        
        // Act
        RevelAccount account = target.getAccount(USERNAME, API_KEY, API_SECRET);
        List<StoreEntity> stores = target.scanForLocations(account);
        
        // Assert
        assertEquals(2, stores.size());
        
        // Sort stores by store filter
        Collections.sort(stores, new Comparator<StoreEntity>(){
            @Override
            public int compare(StoreEntity o1, StoreEntity o2) {
                return o1.getStoreFilter().compareTo(o2.getStoreFilter());
            }
        });
        
        assertEquals("1", stores.get(0).getStoreFilter());
        assertEquals("US/Pacific",stores.get(0).getTimeZone());
        assertEquals("2", stores.get(1).getStoreFilter());
        assertEquals("US/Pacific",stores.get(1).getTimeZone());
    }
}
