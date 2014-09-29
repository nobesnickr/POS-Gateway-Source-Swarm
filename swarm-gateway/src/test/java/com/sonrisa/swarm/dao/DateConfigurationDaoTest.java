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
package com.sonrisa.swarm.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.retailpro.dao.impl.DateConfigurationDao;
import com.sonrisa.swarm.retailpro.model.DateConfigurationEntity;
import com.sonrisa.swarm.retailpro.service.DateConfigurationService;

/**
 * Test the {@link DateConfigurationDao} class
 * 
 * @author barna 
 */
@Transactional
public class DateConfigurationDaoTest extends BaseIntegrationTest {

    /**
     * Dao being tested
     */
    @Autowired
    private DateConfigurationDao dao;
    
    /**
     * Service for saving entities
     */
    @Autowired
    private DateConfigurationService service;
    
    /** 
     * Date format used for creating and comparing dates
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Test case: A single entity is inserted into the retailpro_configuration
     * table, and then read back.
     * 
     * Asserting: The inserted and read entities should be identical.
     * 
     * @throws ParseException
     */
    @Test
    public void testReadSingleConfigurationEntity() throws ParseException {

        final DateConfigurationEntity entity = mockDateConfigurationEntity("sonrisa123", "2013-10-30 14:00:00", "2013-09-20 12:00:00");

        // Save entity
        dao.save(Arrays.asList(entity));

        // Read all entities
        final List<DateConfigurationEntity> entitiesFromDao = dao.findAll();
        
        // Assert
        assertEquals(1, entitiesFromDao.size());
        assertEquals(entity.getSwarmId(), entitiesFromDao.get(0).getSwarmId());
        assertEquals(entity.getTimeStampVersion(), entitiesFromDao.get(0).getTimeStampVersion());
        
        assertEquals(entity.getLastModifiedInvoiceDate(), entitiesFromDao.get(0).getLastModifiedInvoiceDate());
        assertNotEquals(entity.getLastModifiedInvoiceDate(), entitiesFromDao.get(0).getLastModifiedStoreDate());
        assertNotEquals(entity.getLastModifiedInvoiceDate(), entitiesFromDao.get(0).getLastModifiedVersionDate());
        
        assertEquals(entity.getLastModifiedStoreDate(), entitiesFromDao.get(0).getLastModifiedStoreDate());
        assertNotEquals(entity.getLastModifiedStoreDate(), entitiesFromDao.get(0).getLastModifiedVersionDate());
        
        assertEquals(entity.getLastModifiedVersionDate(), entitiesFromDao.get(0).getLastModifiedVersionDate());
    }
    
    /**
     * Test case: Single swarm ID was many entries
     * 
     * Asserting: The most recent entity is returned 
     */
    @Test
    public void testMostRecentConfigurationIsAccessed(){

        List<DateConfigurationEntity> entities = new ArrayList<DateConfigurationEntity>();
        
        final String swarmId = "sonrisa123";
        
        for(int i = 1; i <= 30; i++){
            final DateConfigurationEntity entity = mockDateConfigurationEntity(swarmId, String.format("2013-10-%d 14:00:00", i), String.format("2013-09-%d 13:00:00", i));
            entities.add(entity);
         }

        // Save entities
        dao.save(entities);

        final DateConfigurationEntity entityFromDao = dao.findMostRecentBySwarmId(swarmId);
        
        // Assert
        assertNotNull(entityFromDao);
        assertEquals("2013-10-30 14:00:00", DATE_FORMAT.format(entityFromDao.getTimeStampVersion()));
        assertEquals("2013-09-30 13:00:00", DATE_FORMAT.format(entityFromDao.getLastModifiedInvoiceDate()));
    }
    
    
    /**
     * 
     * Test case: Two configuration entries, one with '*', and an other
     * with specified swarmId
     * 
     * Expected behaviour: For anything not matching the swarmId the '*'
     * entry is returned, but the matching one its appropriate row is
     * fetched.
     */
    @Test
    public void testConfigurationWildchar(){
        
        List<DateConfigurationEntity> entities = new ArrayList<DateConfigurationEntity>();
        final String swarmId = "sonrisa123";
        
        final String timestampOfSpecifiedSwarmId = "2013-10-30 14:00:00";
        final String timestampOfWildcharSwarmId = "2013-10-27 14:00:00";
        
        entities.add(mockDateConfigurationEntity(swarmId, timestampOfSpecifiedSwarmId, "2013-09-20 12:00:00"));
        
        // Note that the wildchar's timeStamp is older than that of the one with specified swarmId
        entities.add(mockDateConfigurationEntity("*", timestampOfWildcharSwarmId, "2013-10-06 10:00:00"));

        // Save entities
        dao.save(entities);

        final DateConfigurationEntity entityFromDaoWithMatchingSwarmId = dao.findMostRecentBySwarmId(swarmId);
        final DateConfigurationEntity entityFromDaoWithNotMatchingSwarmId = dao.findMostRecentBySwarmId("321asirnos");
        
        assertNotNull(entityFromDaoWithMatchingSwarmId);
        assertNotNull(entityFromDaoWithNotMatchingSwarmId);
        
        assertEquals(timestampOfSpecifiedSwarmId, DATE_FORMAT.format(entityFromDaoWithMatchingSwarmId.getTimeStampVersion()));
        assertEquals(entities.get(0).getLastModifiedInvoiceDate(), entityFromDaoWithMatchingSwarmId.getLastModifiedInvoiceDate());
        
        assertEquals(timestampOfWildcharSwarmId, DATE_FORMAT.format(entityFromDaoWithNotMatchingSwarmId.getTimeStampVersion()));
        assertEquals(entities.get(1).getLastModifiedInvoiceDate(), entityFromDaoWithNotMatchingSwarmId.getLastModifiedInvoiceDate());
    }
    
    
    /**
     * Create a mock configuration entity
     * 
     * @param swarmId
     * @param timeStamp
     * @param invoiceDate
     * @return
     */
    private static DateConfigurationEntity mockDateConfigurationEntity(final String swarmId, final String timeStamp, final String invoiceDate) {
        
        DateConfigurationEntity entity = new DateConfigurationEntity();
        entity.setAuthor("Sonrisa");
        entity.setComment("Test entity");

        try {
            entity.setLastModifiedInvoiceDate(DATE_FORMAT.parse(invoiceDate));
            entity.setLastModifiedStoreDate(DATE_FORMAT.parse("2013-10-20 12:00:00"));
            entity.setLastModifiedVersionDate(DATE_FORMAT.parse("2013-10-20 12:00:01"));
    
            entity.setSwarmId(swarmId);
            entity.setTimeStampVersion(DATE_FORMAT.parse(timeStamp));
        
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        
        return entity;
    }
}
