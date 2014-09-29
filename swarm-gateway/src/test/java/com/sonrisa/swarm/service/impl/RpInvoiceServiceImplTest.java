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
package com.sonrisa.swarm.service.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.retailpro.service.RpInvoiceService;
import com.sonrisa.swarm.retailpro.service.impl.RpInvoiceServiceImpl;
import com.sonrisa.swarm.retailpro.util.mapper.EntityHolder;

/**
 * Integration test for the {@link RpInvoiceServiceImpl} class
 */
public class RpInvoiceServiceImplTest extends BaseIntegrationTest {
    
    /**
     * Target being tested
     */
    @Autowired
    private RpInvoiceService target;

    
    /**
     * Test case: Invokes the write method directly with a mock invoice object.
     * Expected result: The writer inserts the invoice record into the DB and
     * inserts a customer record into the DB as well.
     */
    @Test
    public void testWriteInvoice() throws Exception {
        
        Map<String,Object> jsonMap = MockDataUtil.getResourceAsMap(MockTestData.MOCK_INVOICE);
        
        // Act
        EntityHolder holder = target.processMap("sonrisa-test", jsonMap);
        target.writeToStage(holder);

        // Assert
        checkInvoiceExists(new Integer(1));
    }
    


    /**
     * Test case: Invokes the write method directly with a mock customer object.
     * Expected result: The writer inserts the customer record into the DB.
     */
    @Test
    public void testWriteCustomer() throws Exception {

        Map<String,Object> jsonMap = MockDataUtil.getResourceAsMap(MockTestData.MOCK_CUSTOMER);
        EntityHolder holder = target.processMap("sonrisa-test", jsonMap);
        target.writeToStage(holder);

        checkCustomerExists(new Integer(1));
    }

    /**
     * Test case: Write empty in list into DB. Expected result: The method won't
     * throw any exception and the inserted record number is zero.
     */
    @Test
    public void testWriteWithEmptyList() throws Exception {
        
        EntityHolder holder = new EntityHolder();
        target.writeToStage(holder);

        assertEquals(new Integer(0), jdbcTemplate.queryForObject("select count(id) from staging_customers", Integer.class));
        assertEquals(new Integer(0), jdbcTemplate.queryForObject("select count(*) from staging_invoices", Integer.class));
    }

    /**
     * Test case:
     * Invokes the process method directly with a mock invoice object.
     * Expected result:
     * The processor returns with an {@link Invoice} object.
     * 
     * @throws IOException
     * @throws Exception 
     */
    @Test
    public void processInvoiceSimpleTest() throws IOException, Exception {
        Map<String,Object> jsonMap = MockDataUtil.getResourceAsMap(MockTestData.MOCK_INVOICE);
        
        // Act
        EntityHolder holder = target.processMap("sonrisa-test", jsonMap);

        // Assert
        InvoiceStage entity = holder.getInvoices().get(0);
        assertEquals("123456", entity.getLsCustomerId());
    } 
}
