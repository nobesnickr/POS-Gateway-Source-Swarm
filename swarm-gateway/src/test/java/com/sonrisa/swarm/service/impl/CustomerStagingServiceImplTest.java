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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.staging.dao.CustomerStageDao;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.model.staging.CustomerStage;
import com.sonrisa.swarm.staging.service.CustomerStagingService;

/**
 *
 * @author joe
 */
public class CustomerStagingServiceImplTest extends BaseIntegrationTest {
    
    @Autowired
    private CustomerStagingService customerStagingService;
    @Autowired
    private CustomerStageDao customerStageDao;

    @Test
    public void testFindAllIds_and_findByIds() {
        createTestData();
        
        final List<Long> ids = customerStagingService.findAllIds();
        assertNotNull(ids);
        assertFalse(ids.isEmpty());
        
        for (Long id : ids) {
            final CustomerStage cust = customerStagingService.find(id);
            assertNotNull(cust);
        }
        
        final List<CustomerStage> list = customerStagingService.findByIds(ids);
        assertNotNull(list);
        assertEquals(ids.size(), list.size());
        
    }
    
    /**
     * Test case for this method: {@link CustomerStagingService#delete(java.util.List) }.
     * It creates a few staging customers. First it deletes one of them, then the others.
     */
    @Test
    public void deleteTest(){
        createTestData();
                
        final List<Long> list = customerStageDao.findAllIds();
        final int numOfCustomers = list.size();
        assertFalse(list.isEmpty());
        
        // removes one of them
        List<Long> toDelete = new ArrayList<Long>();
        toDelete.add(list.remove(0));        
        customerStagingService.delete(toDelete);
        
        // assert
        assertNull(customerStagingService.find(toDelete.get(0)));
        assertEquals(numOfCustomers-1, customerStageDao.findAll().size());
        
        // removes the rest        
        customerStagingService.delete(list);
        assertTrue(customerStageDao.findAll().isEmpty());        
    }
    
    private void createTestData(){
        List<CustomerStage> list = new ArrayList<CustomerStage>();
                
        list.add(MockTestData.mockCustomerStage("swarmId", "sbs1", "store1", "1", "name1"));
        list.add(MockTestData.mockCustomerStage("swarmId", "sbs1", "store1", "2", "name2"));
        list.add(MockTestData.mockCustomerStage("swarmId", "sbs1", "store2", "1", "name1"));
        list.add(MockTestData.mockCustomerStage("swarmId", "sbs1", "store2", "2", "name3"));                      
        
        for (CustomerStage cust : list){
            customerStagingService.save(cust);
        }
        
        assertEquals(list.size(), customerStageDao.findAll().size());                
    }
}