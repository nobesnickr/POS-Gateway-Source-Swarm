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
package com.sonrisa.swarm.rest.controller;

import com.fasterxml.jackson.databind.JsonNode;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.retailpro.dao.impl.RpPluginDao;
import com.sonrisa.swarm.retailpro.model.RpPluginEntity;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;
import com.sonrisa.swarm.retailpro.rest.RetailProApiConstants;
import com.sonrisa.swarm.retailpro.rest.controller.RpClientController;
import com.sonrisa.swarm.retailpro.rest.controller.StoreController;
import com.sonrisa.swarm.retailpro.service.impl.RpLogUploadingServiceImpl;
import com.sonrisa.swarm.retailpro.util.ControllerUtil;
import com.sonrisa.swarm.staging.job.listener.SkippedStagingEntityListener;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author joe
 */
public class RpClientControllerTest extends BaseControllerTest {
    
    private static final String URI_OF_VERSION = RpClientController.URI_BASE + RpClientController.URI_OF_VERSION;
    private static final String URI_OF_HEARTBEAT = RpClientController.URI_BASE + RpClientController.URI_OF_HEARTBEAT;

    /**
     * Directory where the gateway writes log files
     */
    private static final String LOG_DIRECTORY =  System.getProperty("java.io.tmpdir") + File.separator;
    
    /** We need this file to access the directory. */
    @Autowired
    private RpLogUploadingServiceImpl clientLogService;
    @Autowired
    private RpPluginDao pluginDao;
    
    @Autowired
    private SkippedStagingEntityListener skipListener;
 
    /**
     * Test case: 
     * The RetailPro client controller is invoked with a mock client object and than 
     * with a modified one.
     * 
     * Expected result:
     * After each execution the client version information must be saved into the client log file.
     * 
     * @throws Exception 
     */
    @Test
    public void createAndUpdateClientInfoTest() throws Exception  {
        // sets the target directory
        clientLogService.setUploadedLogDirectory(LOG_DIRECTORY);
        
        // request parameters
        final String json = MockDataUtil.getResourceAsString(MockTestData.MOCK_CLIENT_INFO);
        final String swarmId = "someId";
        final String posSoftware = "myPosSoftware";
        
        // calls the REST service with a new client object
        final MockHttpServletRequestBuilder request = put(URI_OF_VERSION)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .header(RetailProApiConstants.SWARM_ID, swarmId)
                .header(RetailProApiConstants.POS_SOFTWARE, posSoftware);
        final ResultActions postResultAction = mockMvc.perform(request);
        
        // asserts
        assertCreatedStatus(postResultAction);     
        assertClientLogContains(LOG_DIRECTORY, swarmId, posSoftware, json);
        
        // modification of the existing store
        final String json2 = MockDataUtil.getResourceAsString(MockTestData.MOCK_CLIENT_INFO_MODIFIED);
        
        // calls the REST service with a modified store object
        final MockHttpServletRequestBuilder request2 = put(URI_OF_VERSION)
                .content(json2)
                .contentType(MediaType.APPLICATION_JSON)
                .header(RetailProApiConstants.SWARM_ID, swarmId)
                .header(RetailProApiConstants.POS_SOFTWARE, posSoftware);
        final ResultActions postResultAction2 = mockMvc.perform(request2);
        
        // asserts
        assertCreatedStatus(postResultAction2);     
        assertClientLogContains(LOG_DIRECTORY, swarmId, posSoftware, json2);   
        
      
    }        
    
    /**
     * Test case: The invoice controller is invoked with a mock store object but
     * without a swarmId in the header.
     *
     * Expected result: 401 Unauthorized response code
     *
     *
     * @throws Exception
     */
    @Test
    public void missingSwarmIdTest() throws Exception {
        InputStream invoiceStream = MockDataUtil.getResourceAsStream(MockTestData.MOCK_STORE);
        missingSwarmIdTest(invoiceStream, StoreController.URI);
    }      
    
    /**
     * 
     * @throws Exception 
     */
    @Test
    public void heartbeatTest() throws Exception{
        final String swarmId = "swarmId";
        
        // heartbeat
        String heartbeatJson = "{ \"Version\": \"1.0\" }";
        MockHttpServletRequestBuilder request2 = put(URI_OF_HEARTBEAT)
                .content(heartbeatJson)
                .contentType(MediaType.APPLICATION_JSON)
                .header("SwarmId", swarmId);
        final ResultActions postResultAction2 = mockMvc.perform(request2);
        assertCreatedStatus(postResultAction2);
        
        RpPluginEntity entity = pluginDao.findBySwarmId(swarmId);
        assertNotNull(entity.getHeartbeat());
        assertEquals("1.0", entity.getPluginVersion());
        Date timestamp1 = entity.getHeartbeat();
        
        
        // heartbeat again
        heartbeatJson = "{ \"Version\": \"1.0\" }";
        MockHttpServletRequestBuilder request3 = put(URI_OF_HEARTBEAT)
                .content(heartbeatJson)
                .contentType(MediaType.APPLICATION_JSON)
                .header("SwarmId", swarmId);
        final ResultActions postResultAction3 = mockMvc.perform(request3);
        assertCreatedStatus(postResultAction3);
        entity = pluginDao.findBySwarmId(swarmId);
        Date timestamp2 = entity.getHeartbeat();
        
        assertTrue(timestamp1.before(timestamp2));
    }  
    
    /**
     * Test case that validates the correction of the #16203 bug.
     * A skipped {@link RpStoreEntity} can not be logged due to a casting exception.
     * 
     * Expected result: the skip listener can be invoked without any exception.
     */
    @Test
    public void skipStoreTest(){
        skipListener.onSkipInProcess(new RpStoreEntity(), null);
    }
    
    // ------------------------------------------------------------------------
    // ~ Private methods
    // ------------------------------------------------------------------------
    
    /**
     * Asserts whether the RP client log file contains the
     * <code>value</code> expression. The path of the log file 
     * will be constructed from the given <code>dirPath</code>,
     * <code>swarmId</code> and <code>posSoftware</code> name.
     * See {@link ControllerUtil#getSourceId(java.lang.String, java.lang.String) }
     * 
     * 
     * @param dirPath
     * @param swarmId
     * @param posSoftware
     * @param value 
     */
    private void assertClientLogContains(final String dirPath, final String swarmId, final String posSoftware, final String value){
        final String sourceId = ControllerUtil.getSourceId(swarmId, posSoftware);
        final String path = dirPath + sourceId + "-client.log";
        try {
            final String fileContent = FileUtils.readFileToString(new File(path));
            assertTrue(fileContent.contains(value));
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }
}
