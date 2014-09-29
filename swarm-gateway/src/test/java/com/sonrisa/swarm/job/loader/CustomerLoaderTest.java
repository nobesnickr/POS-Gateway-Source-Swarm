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
package com.sonrisa.swarm.job.loader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import hu.sonrisa.backend.dao.filter.SimpleFilter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.sonrisa.swarm.MockRetailProData;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.model.legacy.CustomerEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.CustomerStage;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;

/**
 * Test cases for the {@link CustomerLoader} class.
 *
 * @author joe
 */
public class CustomerLoaderTest extends BaseLoaderTest {

    /**
     * Number of customers the test uses for testing the loader.
     */
    private static final int NUM_OF_TEST_DATA = 202;
  
    /**
     * Name of the customer processing step
     */
    private static final String CUSTOMER_LOADER_JOB_STEP = "stageCustomerProcessing";

    /**
     * Test case:
     * It creates a bunch of staging customers and stores for them. Then executes the loader step
     * and asserts that all of them has been moved to the data warehouse and checks the properties of one of them.
     */
    @Test
    public void testStepWithNewCustomers() {
        final List<CustomerStage> stgCusts = createTestData();
        
        // executes the loader job
        stepExecute(CUSTOMER_LOADER_JOB_STEP);

        // assert
        final List<CustomerEntity> customers = customerService.find(
                new SimpleFilter<CustomerEntity>(CustomerEntity.class), 0, 0);

        assertNotNull(customers);
        assertEquals(NUM_OF_TEST_DATA, customers.size());

        // asserts the first customer
        // we assume the rest behaves the same way
        final CustomerStage stgCust = stgCusts.get(0);
        // finds the RetailPro store for this staging customer
        final RpStoreEntity rpStore = rpStoreService.findBySbsNoAndStoreNoAndSwarmId(stgCust.getLsSbsNo(), stgCust.getLsStoreNo(), stgCust.getSwarmId());
        assertNotNull(rpStore);
        // finds the store by the RetailPro store
        final StoreEntity store = storeService.find(rpStore.getStoreId());
        assertNotNull(store);
        // find the customer in the data warehouse
        final CustomerEntity cust = customerDao.findByStoreAndForeignId(store.getId(), Long.parseLong(stgCust.getLsCustomerId()));
        assertNotNull(cust);

        // asserts whether the two properties of the two customers equal.
        assertCustomerEquals(stgCust, cust);
        
        // asserts that the staging customers have been removed
        assertTrue(customerStagingService.findAllIds().isEmpty());

    }
    
    /**
     * Test case:
     * A staging customer modifies an existing customer.
     * 
     */
    @Test
    public void testStepWithExistingCustomer() {
        final String swarmId = "swarmId";
        final String sbs = "sbs1";
        final String storeNo = "store1";
        final Long lsCustomerId = 12l;
        
        // creates a store 
        StoreEntity strEntity = MockTestData.mockStoreEntity("myStore");
        storeService.save(strEntity);        
        // creates a customer
        final CustomerEntity cust = MockTestData.mockCustomer(lsCustomerId, "customer name", strEntity);
        customerService.save(cust);
        
        assertEquals("One and only one customer has to exist.", 1, customerDao.findAll().size());
        
        // creates a RetailPro store
        RpStoreEntity rpStore = MockRetailProData.mockRpStoreEntity(swarmId, sbs, storeNo);
        rpStore.setStoreId(strEntity.getId());
        rpStoreService.save(rpStore);
                                
        // creates a staging customer 
        final String modifiedName = "name";
        final String modifiedAddress1 = "modified address1";
        final String modifiedPhone = "modified phone";
        final CustomerStage stgCust = MockTestData.mockCustomerStage(swarmId, sbs, storeNo, lsCustomerId.toString(), modifiedName);
        stgCust.setAddress1(modifiedAddress1);
        stgCust.setPhone(modifiedPhone);
        customerStagingService.save(stgCust);
        
        // executes the loader job        
        stepExecute(CUSTOMER_LOADER_JOB_STEP);
        
        // asserts
        // we expect that the job modifies the existing customer
        assertEquals("One and only one customer has to exist.", 1, customerDao.findAll().size());
        final CustomerEntity modifiedCust = customerService.find(cust.getId());
        assertCustomerEquals(stgCust, modifiedCust);        
        
        // asserts that the staging customers have been removed
        assertTrue(customerStagingService.findAllIds().isEmpty());
    }
    
    /**
     * Test case:
     * Moving a staging customers, who has a storeId to the data warehouse.
     * 
     * Expected result:
     * The loader will be able to identify the store for the customer by its storeId
     * and the customer can be moved to the data warehouse.
     * 
     */
    @Test
    public void testStepWithStoreId() {
        assertEquals("No customer should to exist at the beginning.", 0, customerDao.findAll().size());
        
        // creates a store 
        StoreEntity strEntity = MockTestData.mockStoreEntity("myStore");
        storeService.save(strEntity);                 
              
        // creates a staging customer 
        final Long lsCustomerId = 12l;
        final String name = "name";
        final CustomerStage stgCust = MockTestData.mockCustomerStage(null, null, null, lsCustomerId.toString(), name);
        stgCust.setStoreId(strEntity.getId());
        customerStagingService.save(stgCust);
        
        // executes the loader job        
        stepExecute(CUSTOMER_LOADER_JOB_STEP);
        
        // asserts
        // we expect that the job moved the customer to the data warehouse
        final List<CustomerEntity> customers = customerDao.findAll();
        assertEquals("One and only one customer has to exist.", 1, customers.size());
        final CustomerEntity movedCustomer = customerService.find(customers.get(0).getId());
        assertCustomerEquals(stgCust, movedCustomer);        
        
        // asserts that the staging customers have been removed
        assertTrue(customerStagingService.findAllIds().isEmpty());
    }    
    
    @Test
    public void testWithCustomersWithoutStore(){
        assertEquals("No customer should to exist at the beginning.", 0, customerDao.findAll().size());
        
        // creates a store 
        StoreEntity strEntity = MockTestData.mockStoreEntity("myStore");
        storeService.save(strEntity);                 
              
        // creates a valid staging customer 
        final Long lsCustomerId = 12l;
        final String name = "name";
        final CustomerStage stgCust = MockTestData.mockCustomerStage(lsCustomerId.toString(), name, strEntity.getId());
        customerStagingService.save(stgCust);
        
        // creates an invalid staging customer (with invalid storeId)
        final Long lsCustomerId2 = 122l;
        final String name2 = "name2";
        final CustomerStage stgCust2 = MockTestData.mockCustomerStage(lsCustomerId2.toString(), name2, Long.MIN_VALUE);
        final Long invalidCustomerId = customerStagingService.save(stgCust2);        
        
        // executes the loader job        
        stepExecute(CUSTOMER_LOADER_JOB_STEP);
        
        // asserts
        // we expect that the job moved the customer to the data warehouse
        final List<CustomerEntity> customers = customerDao.findAll();
        assertEquals("One and only one customer has to exist.", 1, customers.size());
        final CustomerEntity movedCustomer = customerService.find(customers.get(0).getId());
        assertCustomerEquals(stgCust, movedCustomer);        
        
        // asserts that the customer with the invalid storeId has been skipped and he remained in the staging DB
        final List<Long> remainedInStaging = customerStagingService.findAllIds();
        assertEquals("The skipped customer has been deleted as well!", 1, remainedInStaging.size());        
        assertEquals(invalidCustomerId, remainedInStaging.get(0));
    }
    
    /**
     * Test case there is a customer with invalid customer id
     * 
     * Expected: Entity is deleted from staging
     */
    @Test
    public void testCustomerWithIllegalId(){
        // creates a store 
        StoreEntity strEntity = MockTestData.mockStoreEntity("myStore");
        storeService.save(strEntity);
        final Long storeId = strEntity.getId();
        
        // creates a staging customer 
        final String lsCustomerId = "ABC";
        final CustomerStage stgCust = MockTestData.mockCustomerStage(lsCustomerId, "name", storeId);
        customerStagingService.save(stgCust);    
        
        launchJob();

        assertNoCustomers();
    }

    /**
     * 
     *
     * @return
     */
    private List<CustomerStage> createTestData() {
        List<CustomerStage> result = new ArrayList<CustomerStage>();

        final String swarmId = "swarmId";
        final String sbs = "sbs1";
        final Set<String> storeNums = new HashSet();

        // staging customers
        for (int i = 0; i < NUM_OF_TEST_DATA; i++) {
            final String storeNo = "store" + new Double(Math.floor(i / 100)).intValue();
            storeNums.add(storeNo);
            final CustomerStage cust = MockTestData.mockCustomerStage(swarmId, sbs, storeNo, Integer.toString(i), "name" + i);
            result.add(cust);

            customerStagingService.save(cust);
        }

        // stores
        createMockStores(storeNums, swarmId, sbs);        
        
        return result;
    }
}
