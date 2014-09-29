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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.retailpro.service.RpInvoiceService;
import com.sonrisa.swarm.retailpro.util.mapper.EntityHolder;
import com.sonrisa.swarm.staging.dao.InvoiceStageDao;

/**
 * Test cases for this class: {@link InvoiceDaoImpl}.
 * 
 * @author Béla Szabó
 *
 */
@Transactional
public class InvoiceDaoTest extends BaseIntegrationTest{

	@Autowired
	private InvoiceStageDao invoiceDao;

    @Autowired
    private RpInvoiceService rpInvoiceService;
	/**
	 * Delete all record from the staging_customer table.
	 */
	@Before
	public void deleteCustomerStagingTable(){
		jdbcTemplate.execute("delete from staging_invoices");
	}
	
	/**
	 * Test case:
     * Save an {@link Invoice} instance into DB.
     * Expected result:
     * Get the saved invoice from the DB and we compare the DB result with the generated/saved instance.
	 * 
	 * Test method for {@link com.sonrisa.swarm.dao.impl.InvoiceDaoImpl#writeToStage(java.util.List)}.
	 * @throws Exception 
	 */
	@Test
	public void testSave() throws Exception {				
	InputStream invoiceStream = MockDataUtil.getResourceAsStream(MockTestData.MOCK_INVOICE);
        Map<String,Object> jsonMap = MockDataUtil.getResourceAsMap(MockTestData.MOCK_INVOICE);
        EntityHolder holder = rpInvoiceService.processMap("sonrisa-test", jsonMap);
     
        invoiceDao.create(holder.getInvoices());
        checkInvoiceExists(new Integer(1));
	}
	
	/**
	 * Test case:
     * Save empty list into DB.
     * Expected result:
     * The method won't throw any exception and the inserted record number is zero.
	 * 
	 * Test method for {@link com.sonrisa.swarm.dao.impl.InvoiceDaoImpl#writeToStage(java.util.List)}.
	 */
	@Test
	public void testSaveWithEmptyList() {
		List<InvoiceStage> entities = new ArrayList<InvoiceStage>();

		invoiceDao.create(entities);
		
		assertEquals("0", jdbcTemplate.queryForObject("select count(id) from staging_invoices", Integer.class).toString());
	}
	
	/**
	 * Test case:
     * Try to add null parameter.
     * Expected result:
     * The method won't throw any exception and the inserted record number is zero.
	 * 
	 * Test method for {@link com.sonrisa.swarm.dao.impl.InvoiceDaoImpl#writeToStage(java.util.List)}.
	 */
	@Test
	public void testSaveWithNullParameter() {
		invoiceDao.create(null);
		
		assertEquals("0", jdbcTemplate.queryForObject("select count(id) from staging_invoices", Integer.class).toString());
	}
}
