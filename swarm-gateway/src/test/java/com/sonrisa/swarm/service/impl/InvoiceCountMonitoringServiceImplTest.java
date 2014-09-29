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

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.legacy.dao.InvoiceDao;
import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.service.InvoiceCountMonitoringService;
import com.sonrisa.swarm.posintegration.service.impl.InvoiceCountMonitoringServiceImpl;

/**
 * Class testing the {@link InvoiceCountMonitoringServiceImpl}
 * 
 * @author Barnabas
 */
@Transactional
public class InvoiceCountMonitoringServiceImplTest extends BaseIntegrationTest {

    /**
     * Target being tested
     */
    @Autowired
    private InvoiceCountMonitoringService target;
    
    /**
     * Invoice service
     */
    @Autowired
    private InvoiceDao invoiceDao;

    /**
     * Store service
     */
    @Autowired
    private StoreService storeService;
    
    /**
     * Test case: 
     *  There are 3 invoices in the <code>invoices</code> table,
     *  2 for the same store, 1 for an other
     *  
     * Expected:
     *  Their cardinality and the most recent can retrivied using the service.
     * @throws ParseException 
     */
    @Test
    public void testInvoiceCountMonitoringService() throws ParseException{
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        StoreEntity store = new StoreEntity();
        storeService.save(store);
        
        StoreEntity otherStore = new StoreEntity();
        storeService.save(otherStore);
                
        invoiceDao.persist(mockInvoice(1L, store, dateFormat.parse("2014-02-01")));
        invoiceDao.persist(mockInvoice(2L, otherStore, dateFormat.parse("2014-04-01")));
        
        final InvoiceEntity mostRecentInvoice = mockInvoice(3L, store, dateFormat.parse("2014-03-01"));
        invoiceDao.persist(mostRecentInvoice);
        
        // Act
        final long invoiceCount = target.getInvoiceCount(store.getId());
        final Date mostRecent = target.getLastInvoiceDate(store.getId());
        
        // Assert
        assertEquals(2L, invoiceCount);
        assertEquals(mostRecentInvoice.getTs(), mostRecent);
    }
    
    /**
     * Create mock {@link InvoiceEntity}
     */
    private static InvoiceEntity mockInvoice(Long invoiceId, StoreEntity store, Date ts){
        InvoiceEntity retVal = new InvoiceEntity();
        retVal.setId(invoiceId);
        retVal.setLsInvoiceId(invoiceId + 1000);
        retVal.setStore(store);
        retVal.setTotal(new BigDecimal("25.00"));
        retVal.setTs(ts);
        return retVal;
    }
}
