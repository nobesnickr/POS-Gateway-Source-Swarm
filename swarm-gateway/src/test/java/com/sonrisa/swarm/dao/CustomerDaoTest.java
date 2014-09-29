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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.examples.AllTest.Customer;
import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.model.staging.CustomerStage;
import com.sonrisa.swarm.retailpro.service.RpInvoiceService;
import com.sonrisa.swarm.retailpro.util.mapper.EntityHolder;
import com.sonrisa.swarm.staging.dao.CustomerStageDao;

/**
 * Test cases for this class: {@link CustomerDaoImpl}.
 * 
 * @author Béla Szabó
 *
 */
@Transactional
public class CustomerDaoTest extends BaseIntegrationTest{

	@Autowired
	private CustomerStageDao customerStageDao;

    @Autowired
    private RpInvoiceService rpInvoiceService;
    
	/**
	 * Delete all record from the staging_customer table.
	 */
	@Before
	public void deleteCustomerStagingTable(){
		jdbcTemplate.execute("delete from staging_customers");
	}
	
	/**
	 * Test case:
     * Save a {@link Customer} instance into DB.
     * Expected result:
     * Get the saved customer from the DB and we compare the DB result with the generated/saved instance.
	 * @throws Exception 
	 */
    @Test
    public void testSave() throws Exception {
        Map<String, Object> customerData = MockDataUtil.getResourceAsMap(MockTestData.MOCK_CUSTOMER);
        EntityHolder entityHolder = rpInvoiceService.processMap("test-swarm-id", customerData);
        customerStageDao.create(entityHolder.getCustomers());
        checkCustomerExists(new Integer(1));
    }
	
	/**
	 * Test case:
     * Save empty list into DB.
     * Expected result:
     * The method won't throw any exception and the inserted record number is zero.
	 */
	@Test
	public void testSaveWithEmptyList() {
		List<CustomerStage> entities = new ArrayList<CustomerStage>();  
		 
		customerStageDao.create(entities);
		
		assertEquals("0", jdbcTemplate.queryForObject("select count(id) from staging_customers", Integer.class).toString());
	}
	
	/**
	 * Test case:
     * Try to add null parameter.
     * Expected result:
     * The method won't throw any exception and the inserted record number is zero.
	 */
	@Test
	public void testSaveWithNullParameter() {
		customerStageDao.create(null);
		assertEquals("0", jdbcTemplate.queryForObject("select count(id) from staging_customers", Integer.class).toString());
	}
}
