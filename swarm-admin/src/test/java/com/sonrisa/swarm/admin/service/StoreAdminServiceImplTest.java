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
package com.sonrisa.swarm.admin.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.sonrisa.swarm.admin.model.StoreAdminServiceEntity;
import com.sonrisa.swarm.admin.service.exception.InvalidAdminRequestException;
import com.sonrisa.swarm.admin.service.exception.UnknownStoreException;
import com.sonrisa.swarm.admin.service.impl.StoreAdminServiceImpl;
import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.service.ApiService;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity.ApiType;
/**
 * Unit tests for the {@link StoreAdminServiceImpl} class.
 */
public class StoreAdminServiceImplTest {
    
    private StoreAdminServiceImpl target;
    
    private StoreService mockStoreService;
    
    private ApiService mockApiService;
    
    private StoreEntity store;
    
    /**
     * Setup target
     */
    @Before
    public void setup(){
        target = new StoreAdminServiceImpl();
        
        // Prepare store
        store = new StoreEntity();
        store.setId(123L);
        store.setName("Old name");
        store.setActive(Boolean.FALSE);
        store.setNotes("Old notes");
        store.setApiId(1L);
        
        // Store service returns this store
        mockStoreService = mock(StoreService.class);
        when(mockStoreService.find(store.getId())).thenReturn(store);
        target.setStoreService(mockStoreService);
        
        mockApiService = mock(ApiService.class);
        
        when(mockApiService.findManyByType(ApiType.PULL_API))
            .thenReturn(new HashSet<ApiEntity>(Arrays.asList(new ApiEntity(store.getApiId(), "pos-provider"))));
        
        target.setApiService(mockApiService);
    }
    
    /**
     * Test case:
     *  There is a single store in the <code>stores</code> table,
     *  and it's updated.
     *  
     * Expected:
     *  It is indeed updated.
     */
    @Test
    public void testUpdatingStoreFields() throws InvalidAdminRequestException{
        
        final String newName = "New name";
        final String newNotes = "New notes";
        
        final StoreAdminServiceEntity subject = new StoreAdminServiceEntity();
        subject.setActive(Boolean.TRUE);
        subject.setName(newName);
        subject.setNotes(newNotes);
        
        // Act
        target.update(store.getId(), subject);
        
        // Assert
        ArgumentCaptor<StoreEntity> captor = ArgumentCaptor.forClass(StoreEntity.class);        
        verify(mockStoreService).save(captor.capture());
        
        assertEquals(captor.getValue().getName(), newName);
        assertEquals(captor.getValue().getNotes(), newNotes);
        assertEquals(captor.getValue().getActive(), subject.getActive());
    }
    
    /**
     * Test case:
     *  There is a single store in the <code>stores</code> table,
     *  and we're attempting to update an other
     *  
     * Expected:
     *  Exception is thrown
     */
    @Test(expected = UnknownStoreException.class)
    public void testUpdatingNotExistingStore() throws InvalidAdminRequestException {
        target.update(store.getId() + 1, new StoreAdminServiceEntity());
    }
}
