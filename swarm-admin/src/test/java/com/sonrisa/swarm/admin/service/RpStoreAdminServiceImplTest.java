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

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.sonrisa.swarm.admin.model.RpStoreAdminServiceEntity;
import com.sonrisa.swarm.admin.service.exception.InvalidAdminRequestException;
import com.sonrisa.swarm.admin.service.impl.RpStoreAdminServiceImpl;
import com.sonrisa.swarm.admin.service.impl.StoreAdminServiceImpl;
import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;
import com.sonrisa.swarm.retailpro.service.RpStoreService;
/**
 * Unit tests for the {@link StoreAdminServiceImpl} class.
 */
public class RpStoreAdminServiceImplTest {
    
    private RpStoreAdminServiceImpl target;
    
    private StoreService mockStoreService;

    private RpStoreService mockRpStoreService;
    
    private StoreEntity store;
    
    private RpStoreEntity rpStore;
    
    /**
     * Setup target
     */
    @Before
    public void setup(){
        target = new RpStoreAdminServiceImpl();
        
        // Prepare store
        store = new StoreEntity();
        store.setId(123L);
        store.setName("Old name");
        store.setActive(Boolean.FALSE);
        store.setNotes("Old notes");
        
        rpStore = new RpStoreEntity();
        rpStore.setId(456L);
        rpStore.setStoreId(store.getId());
        
        // Store service returns this store
        mockStoreService = mock(StoreService.class);
        when(mockStoreService.find(store.getId())).thenReturn(store);
        target.setStoreService(mockStoreService);
        
        mockRpStoreService = mock(RpStoreService.class);
        when(mockRpStoreService.findByStoreId(store.getId())).thenReturn(rpStore);
        target.setRpStoreService(mockRpStoreService);
    }
    
    /**
     * Test case:
     *  There is a single store in the <code>stores</code> table,
     *  and it's updated.
     *  
     * Expected:
     *  It is indeed updated, including the row in <code>stores_rp</code>
     *  referencing it.
     *  
     * @throws InvalidAdminRequestException 
     */
    @Test
    public void testUpdatingRpStoreFields() throws InvalidAdminRequestException{
        
        final String newName = "New name";
        final String newTimezone = "US/Central";
        final String newNotes = "New notes";
        
        final RpStoreAdminServiceEntity subject = new RpStoreAdminServiceEntity();
        subject.setTimezone(newTimezone);
        subject.setName(newName);
        subject.setNotes(newNotes);
        
        // Act
        target.update(store.getId(), subject);
        
        // Assert
        ArgumentCaptor<StoreEntity> captor = ArgumentCaptor.forClass(StoreEntity.class);        
        verify(mockStoreService).save(captor.capture());
        
        assertEquals(captor.getValue().getName(), newName);
        assertEquals(captor.getValue().getNotes(), newNotes);
        
        ArgumentCaptor<RpStoreEntity> rpCaptor = ArgumentCaptor.forClass(RpStoreEntity.class);
        verify(mockRpStoreService).save(rpCaptor.capture());
        
        assertEquals(rpCaptor.getValue().getTimeZone(), newTimezone);
    }
    
    /**
     * Test case:
     *  Updating an existing store with invalid timezone
     *  
     * Expected:
     *  It's not allowed.
     * @throws InvalidAdminRequestException 
     */
    @Test(expected = InvalidAdminRequestException.class)
    public void testUpdatingWithInvalidTimezone() throws InvalidAdminRequestException{

        final RpStoreAdminServiceEntity subject = new RpStoreAdminServiceEntity();
        subject.setTimezone("Invalid/Timezone");
        
        // Act
        target.update(store.getId(), subject);
    }
    
    /**
     * Test various timezones
     */
    @Test
    public void testManyTimezones(){
        assertTimezoneIsAccepted("US/Eastern");
        assertTimezoneIsAccepted("US/Central");
        assertTimezoneIsAccepted("US/Mountain");
        assertTimezoneIsAccepted("US/Pacific");
        assertTimezoneIsAccepted("PST");
        assertTimezoneIsAccepted("Europe/London");
        assertTimezoneIsAccepted("America/New_York");
    }
    
    /**
     * Assert that a given timezone is accepted by the update method
     * @param timezone
     */
    public void assertTimezoneIsAccepted(String timezone){
        final RpStoreAdminServiceEntity subject = new RpStoreAdminServiceEntity();
        subject.setTimezone(timezone);
        
        // Act
        try {
            target.update(store.getId(), subject);
        } catch (InvalidAdminRequestException e) {
            throw new AssertionError(e);
        }
    }
}
