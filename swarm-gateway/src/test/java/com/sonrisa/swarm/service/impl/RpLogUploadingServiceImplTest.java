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

import com.sonrisa.swarm.retailpro.service.impl.RpLogUploadingServiceImpl;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sonrisa.swarm.BaseIntegrationTest;

/**
 * Test class testing the {@link RpLogUploadingServiceImpl} class
 *  
 * @author barna
 */
public class RpLogUploadingServiceImplTest extends BaseIntegrationTest {

    /**
     * Class being tested
     */
    @Autowired
    private RpLogUploadingServiceImpl service;
    
    /**
     * Source's name for the unit test
     */
    private String sourceName;

    /**
     * Directory where the gateway writes log files
     */
    private static final String LOG_DIRECTORY =  System.getProperty("java.io.tmpdir") +  "/";
    
    /**
     * Generate unique swarmId before each test
     */
    @Before
    public void generateSwarmId(){
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        
        // Generate unique swarmId for each test
        sourceName = "rpLogUploadingServiceImplTest_" + simpleDateFormat.format(new Date()) + "_" + ((int)(Math.random()*100));
        
        service.setUploadedLogDirectory(LOG_DIRECTORY);
    }
    
    /**
     * Delete all files this unit test has created
     */
    public void deleteAffectedFiles(){
        
        File file = swarmIdToLogFile(sourceName);
        
        if(file.exists()){
            if(!file.delete()){
                throw new RuntimeException("Failed to clean up after test, could not delete file: " + file.getAbsolutePath());
            }
        }
    }
    
    /**
     * Test case: Log is to be saved using the service
     * 
     * Expected behavior: Log is saved in the log/client/ folder as client-[swarmId].log
     */
    @Test
    public void testSeparateLogFileIsCreated(){
        service.save(sourceName, "RDA2 Error: No such thing as \"schema.items\"");
        assertTrue(swarmIdToLogFile(sourceName).exists());
    }
    
    /**
     * Test case: Log is to be saved using the service
     * 
     * Expected behavior: Log is saved in the log/client/ folder as client-[swarmId].log
     * @throws IOException 
     */
    @Test
    public void testLogMessageIsSaved() throws IOException{
        
        final String message = "!@#$%^&*(";
        
        service.save(sourceName, message);
               
        assertTrue("Message not found in log", FileUtils.readFileToString(swarmIdToLogFile(sourceName)).indexOf(message) >= 0);
    }
    
    /**
     * Helper function converting swarmId to the log file where the client should be logging
     * @param sourceName
     * @return
     */
    private File swarmIdToLogFile(String sourceName){
        return new File(LOG_DIRECTORY + "" + sourceName + "-client.log");
    }
}
