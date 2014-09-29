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

package com.sonrisa.posintegration.service;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.sonrisa.swarm.posintegration.service.ExtractorMonitoringService;
import com.sonrisa.swarm.posintegration.service.impl.ExtractorMonitoringServiceImpl;

/**
 * 
 * @author Barnabas
 *
 */
public class ExtractorMonitoringServiceImplTest {
    
    /**
     * Target being tested
     */
    private ExtractorMonitoringService target;
    
    /**
     * Sets up target
     */
    @Before
    public void setupTarget() {
        target = new ExtractorMonitoringServiceImpl();
    }
    
    /**
     * Test case:
     *  Saving a two dates into the {@link ExtractorMonitoringServiceImpl}
     *  
     * Expected:
     *  When requested with the same store, the last one is returned
     */
    @Test
    public void testSavingDate() throws ParseException{
        
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Date oneDayBefore = dateFormat.parse("2014-04-09");
        final Date date = dateFormat.parse("2014-04-10");
        
        final Long storeId = 1L;
        
        target.addSuccessfulExecution(storeId, oneDayBefore);
        target.addSuccessfulExecution(storeId, date);
        
        // Act
        Date result = target.getLastSuccessfulExecution(storeId);
        assertEquals(date, result);   
    }
    
    /**
     * Test case:
     *  Saving a two dates into the {@link ExtractorMonitoringServiceImpl}
     *  
     * Expected:
     *  When requested with the same store, the last one is returned
     */
    @Test
    public void testReadingUnknownStore () throws ParseException{
        
        final Date date = new Date();
        
        final Long realStoreId = 1L;
        final Long unrealStoreId = 2L;
        
        target.addSuccessfulExecution(realStoreId, date);
        
        // Act
        Date result = target.getLastSuccessfulExecution(unrealStoreId);
        
        // Assert 
        assertNull("Date for unknown store should be null", result);   
    }
}
