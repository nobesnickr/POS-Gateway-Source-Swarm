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
package com.sonrisa.swarm.test.service.store;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import hu.sonrisa.backend.dao.filter.JpaFilter;

import java.util.Arrays;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;
import com.sonrisa.swarm.posintegration.service.ApiService;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity;


/**
 * Base class for testing services registering stores into the <code>stores</code> table.
 */
@RunWith(MockitoJUnitRunner.class)
public class BaseStoreServiceTest<T extends SwarmStore> {
    
    /**
     * Utility to encrypt and decrypt values
     */
    protected AESUtility aesUtility = new AESUtility();
    
    /**
     * Mock store service
     */
    @Mock
    protected StoreService mockStoreService;
    
    /**
     * Mock store service
     */
    @Mock
    protected ApiService mockApiService;
        

    /**
     * Setup mock store service to return API id for API name
     */
    protected void setupApiService(String apiName, Long apiId){
        when(mockApiService.findByName(eq(apiName))).thenReturn(new ApiEntity(apiId, apiName));
    }
    
    /**
     * Setup store service to return an active store
     */
    protected StoreEntity setupSingleActiveExistingStore(Long apiId, String userName, String password, String storeFilter){

        StoreEntity existingEntity = new StoreEntity();
        existingEntity.setId(1L);
        existingEntity.setApiId(apiId);
        existingEntity.setName("Store");
        existingEntity.setUsername(aesUtility.aesEncryptToBytes(userName));
        existingEntity.setPassword(aesUtility.aesEncryptToBytes("old-password"));
        existingEntity.setActive(Boolean.TRUE);
        existingEntity.setStoreFilter(storeFilter);
        
        // Stores table contains a single entry
        when(mockStoreService.find(any(JpaFilter.class), anyLong(), anyLong())).thenReturn(Arrays.asList(existingEntity));
        return existingEntity;
    }
}
