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
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.retailpro.dao.impl.DateConfigurationDao;
import com.sonrisa.swarm.retailpro.model.DateConfigurationEntity;
import com.sonrisa.swarm.retailpro.service.DateConfigurationService;


/**
 * Testing the {@link DateConfigurationServiceImpl} class
 * 
 * @author barna
 */
@Transactional
public class DateConfigurationServiceImplTest extends BaseIntegrationTest {

    /**
     * DAO used to insert entities
     */
    @Autowired
    private DateConfigurationDao dao;
    
    /**
     * Object being tested
     */
    @Autowired
    private DateConfigurationService service;
    
    /**
     * Test case: There is an entity in the retailpro_configuration table 
     * 
     * Expected: This entity can be read using the service
     */
    @Test
    public void testBestCaseScenarioForDateConfiguration(){
        
        final String swarmId = "sonrisa123";
        
        DateConfigurationEntity entity = new DateConfigurationEntity();
        entity.setSwarmId(swarmId);
        entity.setTimeStampVersion(new Date());
        entity.setLastModifiedInvoiceDate(new Date());
        
        // Insert
        dao.save(Arrays.asList(entity));
        
        // Assert
        final DateConfigurationEntity entityFromService = service.findMostRecentBySwarmId(swarmId);
        
        assertNotNull(entityFromService);
        assertEquals(entity.getLastModifiedInvoiceDate(), entityFromService.getLastModifiedInvoiceDate());
        assertEquals(entity.getTimeStampVersion(), entityFromService.getTimeStampVersion());
    }
    
    /**
     * Test case: There is an entity in the retailpro_configuration table, but 
     * it doesn't match the swarmId of the request
     * 
     * Expected: The service returns an entity with version set to 0 and 
     * and all timestamps nulled
     */
    @Test
    public void testMissingConfigurationEntity(){
        final String swarmId = "sonrisa123";
        
        DateConfigurationEntity entity = new DateConfigurationEntity();
        entity.setSwarmId(swarmId);
        entity.setTimeStampVersion(new Date());
        entity.setLastModifiedInvoiceDate(new Date());
        
        // Insert
        dao.save(Arrays.asList(entity));
        
        // Assert
        final DateConfigurationEntity entityFromService = service.findMostRecentBySwarmId("abc456");
        
        assertNotNull(entityFromService);
        assertEquals(0L, entityFromService.getTimeStampVersion().getTime());
        assertNull(entityFromService.getLastModifiedInvoiceDate());
        assertNull(entityFromService.getLastModifiedStoreDate());
        assertNull(entityFromService.getLastModifiedVersionDate());
    }
}
