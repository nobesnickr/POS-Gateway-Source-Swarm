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
package com.sonrisa.swarm.retailpro.staging.filter;

import org.junit.Before;
import org.junit.Test;

import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.model.staging.retailpro.enums.RpTender;
import com.sonrisa.swarm.staging.filter.InvoiceStagingFilter;
import com.sonrisa.swarm.staging.filter.StagingFilterValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
/**
 * Test class for the {@link RpSoNumberStagingFilter} staging filter
 */
public class RpSoNumberStagingFilterTest {

    /**
     * Target of the tests
     */
    private InvoiceStagingFilter target;
    
    /**
     * Setups up target as an {@link RpSoNumberStagingFilter}
     */
    @Before
    public void setupTarget(){
        target = new RpSoNumberStagingFilter();
    }
    
    /**
     * Test case:
     *  An InvoiceStage has not Retail Pro receipt attributes
     *  
     * Expected:
     *  Its is approved, the {@link RpSoNumberStagingFilter} should only affect
     *  invoices from RetailPro 
     */
    @Test
    public void testApproveIfNotRetailPro(){
        InvoiceStage mockInvoice = new InvoiceStage();
        mockInvoice.setLsInvoiceId("9785643879");
        mockInvoice.setStoreId(123L);
        mockInvoice.setTotal("2333.46");
        
        // Act
        StagingFilterValue result = target.approve(mockInvoice);
        
        // Assert
        assertEquals(StagingFilterValue.APPROVED, result);
    }
    
    /**
     * Test case:
     *   SoNumber is missing for an entity with Total > 0
     *   
     * Expected:
     *   Filter out any invoice with Tender == DEPOSIT
     */
    @Test
    public void testMissingSoNumber(){
        InvoiceStage depositInvoice = getMockInvoice();
        depositInvoice.setTender(Integer.toString(RpTender.DEPOSIT.getLsTenderCode()));
        
        InvoiceStage cashInvoice = getMockInvoice();
        cashInvoice.setTender(Integer.toString(RpTender.CASH.getLsTenderCode()));
        
        // Assert
        assertNotEquals(StagingFilterValue.APPROVED, target.approve(depositInvoice));
        assertEquals(StagingFilterValue.APPROVED, target.approve(cashInvoice));
    }   
    
    /**
     * Test case:
     *   SoNumber is not missing for an entity with any Total
     *   
     * Expected:
     *    Filter out any invoice with Tender != DEPOSIT if total is not negative
     */
    @Test
    public void testNotSoNumber(){
        InvoiceStage depositInvoice = getMockInvoice();
        depositInvoice.setSoNumber("8585222");
        depositInvoice.setTender(Integer.toString(RpTender.DEPOSIT.getLsTenderCode()));
        
        InvoiceStage negativeDepositInvoice = getMockInvoice();
        negativeDepositInvoice.setSoNumber("8585222");
        negativeDepositInvoice.setTotal("-2222.44");
        negativeDepositInvoice.setTender(Integer.toString(RpTender.DEPOSIT.getLsTenderCode()));
        
        InvoiceStage cashInvoice = getMockInvoice();
        cashInvoice.setSoNumber("8585244");
        cashInvoice.setTender(Integer.toString(RpTender.CASH.getLsTenderCode()));
        
        // Assert
        assertNotEquals(StagingFilterValue.APPROVED, target.approve(cashInvoice));
        assertNotEquals(StagingFilterValue.APPROVED, target.approve(negativeDepositInvoice));
        assertEquals(StagingFilterValue.APPROVED, target.approve(depositInvoice));
    } 
    
    /**
     * Generates a mock invoice entity with the common Retail Pro fields
     * @return
     */
    private static InvoiceStage getMockInvoice(){
        InvoiceStage invoice = new InvoiceStage();
        invoice.setSwarmId("abc123");
        invoice.setInvoiceNo("54632");
        invoice.setLsSbsNo("1");
        invoice.setLsStoreNo("444");
        invoice.setTotal("666.55");
        invoice.setLsCustomerId("8585");
        return invoice;
    }
}
