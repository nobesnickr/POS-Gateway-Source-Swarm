/*
 *   Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.
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
package com.sonrisa.swarm.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.retailpro.rest.model.JsonStore;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;
import com.sonrisa.swarm.retailpro.service.RpStoreService;

/**
 *
 * @author joe
 */
public class RpStoreServiceImplTest extends BaseIntegrationTest {
    
    @Autowired
    private RpStoreService storeService;
    
    /**
     * Test case: A new JSON store object has been created and then saved with the service.
     * 
     * Expected result: A new RetailPro Store will be created in the the DB and
     * the find by id method will find it.
     * 
     */
    @Test
    public void basicSaveAndFindTest() {
        final JsonStore jsonStore = new JsonStore();
        jsonStore.setName("myName");
        jsonStore.setSbsNumber("mySbsNumber");
        jsonStore.setStoreNumber("myStoreNumber");
        final String swarmId = "mySwarmId";
        final String posSoftware = "retailpro8";
        jsonStore.setPosTimezone("Pacific Standard Time");
        jsonStore.setNotes("Sunrise, FL 33323");
        
        storeService.save(swarmId, posSoftware, jsonStore);
        
        final RpStoreEntity store = storeService.findBySbsNoAndStoreNoAndSwarmId(jsonStore.getSbsNumber(), jsonStore.getStoreNumber(), swarmId);
        assertNotNull(store);
        assertEquals(jsonStore.getName(), store.getStoreName());
        assertEquals(jsonStore.getSbsNumber(), store.getSbsNumber());
        assertEquals(jsonStore.getStoreNumber(), store.getStoreNumber());
        assertEquals(swarmId, store.getSwarmId());
        assertEquals(posSoftware, store.getPosSoftware());
        assertEquals(jsonStore.getPosTimezone(), store.getPosTimezone());
        assertEquals(jsonStore.getNotes(), store.getNotes());
    }
    
    @Test
    public void timezoneAndOffsetSaveTest(){
        final RpStoreEntity storeEntity = new RpStoreEntity();
        storeEntity.setTimeOffset(-20);
        storeEntity.setTimeZone("UTC+1");
        storeService.save(storeEntity);
        
        final RpStoreEntity fromDb = storeService.find(storeEntity.getId());
        assertEquals(storeEntity.getTimeOffset(), fromDb.getTimeOffset());
        assertEquals(storeEntity.getTimeZone(), fromDb.getTimeZone());
    }
}