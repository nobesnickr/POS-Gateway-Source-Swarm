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
package com.sonrisa.swarm.rest.controller;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.retailpro.model.RpLogEntity;
import com.sonrisa.swarm.retailpro.rest.controller.RpLogUplodingController;
import com.sonrisa.swarm.retailpro.service.RpLogMonitoringService;
import com.sonrisa.swarm.retailpro.service.impl.RpLogUploadingServiceImpl;

/**
 * Test class {@link RpLogUplodingController}
 * 
 * @author barna
 */
public class RpLogUploadingControllerTest extends BaseControllerTest {
    
    /**
     * Directory where the gateway writes log files
     */
    private static final String LOG_DIRECTORY =  System.getProperty("java.io.tmpdir") +  "/";
    
    /**
     * We need this file to access the directory
     */
    @Autowired
    private RpLogUploadingServiceImpl serviceImpl;

    /**
     * Monitoring service
     */
    @Autowired
    private RpLogMonitoringService monitoringService;
    
    /**
     * Test that the request to service doesn't fail
     */
    @Test
    public void testServiceResponds(){
        final String swarmId = "test_rp_log_uploading_service_responds";
        final ResultActions result = perfom(put(RpLogUplodingController.LOG_UPLOAD_URI).content("SOMETHING BAD WE NEED TO KNOW ABOUT").header("SwarmId", swarmId));
        assertCreatedStatus(result);      
    }
    
    /**
     * Test case: Log is to be saved using the service
     * 
     * Expected behavior: Log is saved in the log/client/ folder as client-[swarmId].log
     * @throws IOException 
     */
    @Test
    public void testLogMessageIsSaved() throws IOException{
        
        final Date testStartDate = new Date();
        final String localTs = "2014-04-23 10:15:49.6518";
        final String level = "ERROR";
        final String message = localTs + "|" + level + "|A -> B|Details: details";
        
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        final String swarmId = "testLogMessageIsSaved_" + simpleDateFormat.format(new Date());
        final String posSoftware = "example-pos";
        
        serviceImpl.setUploadedLogDirectory(LOG_DIRECTORY);
        
        // Act
        perfom(put(
                RpLogUplodingController.LOG_UPLOAD_URI).content(message)
                .header("Pos-Software", posSoftware)
                .header("SwarmId", swarmId));
        
        perfom(put(
                RpLogUplodingController.LOG_UPLOAD_URI).content("second message")
                .header("Pos-Software", posSoftware)
                .header("SwarmId", swarmId));
        
        // Assert content saved to file
        assertTrue(FileUtils.readFileToString(new File(LOG_DIRECTORY + "" + swarmId + "-" + posSoftware + "-client.log")).indexOf(message) >= 0);
        
        // Verify that monitoring service received the uploaded logs
        RpLogEntity recentLog = monitoringService.getRecentClientError(swarmId);
        assertNotNull(recentLog);
        assertEquals(localTs, recentLog.getLocalTimestamp());
        assertEquals(level, recentLog.getLevel());
        assertTrue(testStartDate.before(recentLog.getServerTimestamp()));
    }
    
    /**
     * Test case: Not one, but many clients are uploading their logs
     * 
     * Expected behavior: Each client's uploaded log is saved separately
     */
    @Test
    public void testMultipleLogUploadingClients(){
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        serviceImpl.setUploadedLogDirectory(LOG_DIRECTORY);
        
        final String baseMessage = "!@#$" + simpleDateFormat.format(new Date()) + "@@" + Math.random() + "%^&*( -----<<>>----- \n";
        final String baseSwarmId = "testLogMessageIsSaved_" + simpleDateFormat.format(new Date()) + "_";
        final String posSoftware = "example-pos";
        
        for(int i = 0; i < 10; i++){
            final String message = baseMessage + i;
            final String swarmId = baseSwarmId + i;
            
            perfom(put(
                    RpLogUplodingController.LOG_UPLOAD_URI).content(message)
                    .header("Pos-Software", posSoftware)
                    .header("SwarmId", swarmId));
        }
        
        for(int i = 0; i < 10; i++){
            assertLog(baseMessage + i, baseSwarmId + i, posSoftware);
        }
    }
    
    /**
     * Asserts that a certain clients uploaded logs match the expected message
     * @param expected
     * @param actual
     * @param swarmId
     * @throws IOException 
     */
    private void assertLog(final String expected, final String swarmId, final String posSoftware){
        try {
            final String contentOfFile = FileUtils.readFileToString(new File(serviceImpl.getUploadedLogDirectory() + swarmId + "-" + posSoftware + "-client.log"));
            final int position = contentOfFile.indexOf(expected);
            assertEquals("YYYY-MM-DD HH:mm:ss,sss - ".length(), position);
        } catch (IOException e){
            throw new AssertionError(e);
        }
    }
}
