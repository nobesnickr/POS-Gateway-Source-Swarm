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
package com.sonrisa.swarm.job.cleaner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import com.sonrisa.swarm.BaseBatchTest;
import com.sonrisa.swarm.mock.MockDTOUtil;
import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.staging.job.cleaner.StageCleaner;
import com.sonrisa.swarm.staging.service.CustomerStagingService;
import com.sonrisa.swarm.warehouse.stage.StagingDTOService;

/**
 * Test the functionality of the StageCleaner class
 * responsible for cleaning entires from the staging
 * table older than a certain age (e.g. 2 days)
 */
public class StageCleanerTest extends BaseBatchTest {
    /** Test utility to execute the job */
    @Autowired
    @Qualifier("stageCleanerLauncherTest")
    private JobLauncherTestUtils cleanerJobUtil;
    
    /** We will use jdbc template to screw up the staging table's timestamps */
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    
    /** The staging data save service that saves data into the staging tables    */
    @Autowired
    private StagingDTOService stagingDTOService;
    
    @Autowired
    StageCleaner cleanerJob;
    
    /** This is required to one what id's were inserted into the staging_customers table */
    @Autowired
    private CustomerStagingService customerStgService;
    
    /**
     * Saves mock costumers into the staging_customers table
     * @param count The number of customers to save
     */
    public void saveMockCustomer(int count) {
        
        // create a list as the SwarmDataStore interface expects a List<CustomerDTO>
        List<CustomerDTO> list = new ArrayList<CustomerDTO>();
        for(int i = 0; i < count; i++){
            list.add(MockDTOUtil.mockCustomerDTO(456L + i));
        }
        
        // create a SwarmStore
        SwarmStore store = MockDTOUtil.mockStore(1L);
        stagingDTOService.saveToStage(store, list,CustomerDTO.class);
    }
    
    /**
     * Test that if a new item is inserted, cleaner doesn't
     * clean it instantly
     */
    @Test
    public void testNewInsert(){
        saveMockCustomer(1);

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM staging_customers", Integer.class);
        assertEquals("Mock customer wasn't inserted!", 1, (int)count);
        
        // launch the loader job
        launchJob(cleanerJobUtil);
        
        count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM staging_customers", Integer.class);
        assertEquals("Mock customer was deleted!", 1, (int)count);
    }
    
    /**
     * Test will execute the following steps
     * - Insert two new customer
     * - Set one's stage_created_at property back one hour
     * - Run the cleaning job, it shouldn't be deleted yet 
     * - Set it's stage_created_at property way back
     * - Run the cleaning job, it should be deleted now
     * - Verify that it was cleaned
     */
    @Test
    public void testInsertAndThenClean(){
        //inserted two new customers
        saveMockCustomer(2);

        // clean
        launchJob(cleanerJobUtil);
        List<Long> ids = customerStgService.findAllIds();
        assertEquals(2, ids.size());
        
        //set back the timestamp one hour
        final Timestamp oneHourAgo = new Timestamp((new Date()).getTime() - 3600 * 1000);
        jdbcTemplate.update("UPDATE staging_customers SET stage_created_at = ? WHERE id = ?", oneHourAgo, ids.get(0));
        
        // one hour entires should be untouched
        ids = customerStgService.findAllIds();
        assertEquals("An age of 1h shouldn't mean deleteion", 2, ids.size());
        
        //set the timestamp way back
        final Timestamp twoYearsAgo = new Timestamp((new Date()).getTime() - 365L*24*3600*1000);
        jdbcTemplate.update("UPDATE staging_customers SET stage_created_at = ? WHERE id = ?", twoYearsAgo, ids.get(0));
        
        // run cleaner job again
        launchJob(cleanerJobUtil);
        List<Long> newIds = customerStgService.findAllIds();

        // the new staging table only contains the recent row
        assertEquals("An age of two years should mean deletion", 1, newIds.size());
        // the ids.get(0) id was deleted
        assertFalse(newIds.contains(ids.get(0)));
    }
}
