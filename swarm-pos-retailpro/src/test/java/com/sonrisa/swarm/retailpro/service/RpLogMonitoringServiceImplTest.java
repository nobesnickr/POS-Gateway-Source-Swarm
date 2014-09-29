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
package com.sonrisa.swarm.retailpro.service;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.retailpro.model.RpLogEntity;
import com.sonrisa.swarm.retailpro.service.impl.LogMonitoringServiceImpl;

/**
 * Test class for the {@link LogMonitoringServiceImpl}
 * @author Barnabas
 *
 */
public class RpLogMonitoringServiceImplTest {
    
    /**
     * Target being tested
     */
    private RpLogMonitoringService target;
    
    /**
     * Sets up target
     */
    @Before
    public void setupTarget(){
        target = new LogMonitoringServiceImpl();
    }

    /**
     * Test case:
     *  Uploading log entries to the {@link RpLogMonitoringService}
     *  
     * Expected: 
     *  The last ERROR is returned by the {@link RpLogMonitoringService#getRecentClientError(String)}
     * 
     */
    @Test
    public void testUploadingLog(){
        final String[] messages = MockDataUtil.getResourceAsString(MockTestData.TEST_RP_LOG_WITH_ERROR).split("\\n");
        final String swarmId = "test-swm-id";
        uploadLogsToTarget(messages, swarmId);
        
        // Act
        RpLogEntity result = target.getRecentClientError(swarmId);
        
        // Assert
        // Last line was: 2014-04-23 10:15:49.6518|ERROR|SwarmV8ExporterProgram.Main => SwarmV8ExporterProgram.RunExporter|Unexpected error: ...
        assertNotNull(result);
        assertEquals("ERROR", result.getLevel());
        assertEquals("2014-04-23 10:15:49.6518",result.getLocalTimestamp());
        assertTrue(result.getStackTrace().endsWith("SwarmV8ExporterProgram.RunExporter"));
        assertTrue(result.getDetails().startsWith("Unexpected error"));
    }
    
    /**
     * Test case:
     *  Uploading log entries to the {@link RpLogMonitoringService}
     *  
     * Expected: 
     *  The last returned by {@link RpLogMonitoringService#getRecentClientError(String)}
     *  is null.
     * 
     */
    @Test
    public void testClientErrorWithInvalidSwarmId(){
        final String[] messages = MockDataUtil.getResourceAsString(MockTestData.TEST_RP_LOG_WITH_ERROR).split("\\n");
              
        uploadLogsToTarget(messages, "tst-swm-id");
        
        // Act
        RpLogEntity result = target.getRecentClientError("something-else");
        
        // Assert
        assertNull(result);
    }
    
    /**
     * Test case:
     *  Uploading log entries to the {@link RpLogMonitoringService}, with only 
     *  a single error saying the the Settings.xml is not found on the machine
     *  
     * Expected: 
     *  This error is ignored and the recent client error request returns 0L 
     *  as timestamp.
     */
    @Test
    public void testIgnoringSettingsFileNotFound(){
        final String[] messages = MockDataUtil.getResourceAsString(MockTestData.TEST_RP_LOG_WITH_SETTINGS_FILE_NOT_FOUND).split("\\n");
        
        uploadLogsToTarget(messages, "tst-swm-id");
        
        // Act
        RpLogEntity result = target.getRecentClientError("something-else");
        
        // Assert
        assertNull(result);
    }
    
    /**
     * Upload all log entries to the target
     */
    private void uploadLogsToTarget(String[] lines, String swarmId){
        // As if each line was uploaded 1 sec appart
        long timestamp = (new Date()).getTime();
        for(String line : lines){
            target.registerLog(swarmId, line, new Date(timestamp));
            timestamp += 1000;
        }
    }
}
