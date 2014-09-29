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

import com.sonrisa.swarm.retailpro.rest.controller.RpLogConfigurationController;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import com.sonrisa.swarm.retailpro.dao.impl.RpLogConfigurationDaoImpl;

/**
 * Test class for RpLogConfigurationControllor which accepts
 * REST request to /api/config/log
 */
public class RpLogConfigurationControllerTest extends BaseControllerTest {
    
    /**
     *  Use tmp directory as custom mapping folder for the Unit tests
     */
    private static final String CUSTOM_LOG_CONFIG_FOLDER = System.getProperty("java.io.tmpdir") +  "/";
    
    /**
     * Dao is needed to alter the custom log folder
     */
    @Autowired
    private RpLogConfigurationDaoImpl dao;

    /**
     * Setup the custom mapping folder before each test
     */
    @Before
    public void setUpCustomMappingFolder(){
        dao.setCustomLogLevelConfigFolder(CUSTOM_LOG_CONFIG_FOLDER);
    }
    
    /**
     * Test case: There is a custom log level configuration for one of the swarmId's, but not for an other
     * 
     * Expected: The REST interface returns the custom log level configuration for the one where it exists,
     * but returns the default mapping for the other
     * @throws IOException 
     */
    @Test
    public void testLogConfigurationControllerBestCaseScenario() throws IOException{

        final String swarmId = "test_log_configuration_controller_best_case_scenario";
        
        final String expectedCustomLogLevelConfiguration = "{\"RDA2\":\"Info\"}";

        FileUtils.writeStringToFile(new File(CUSTOM_LOG_CONFIG_FOLDER + "/" + swarmId + ".json"), expectedCustomLogLevelConfiguration);
        
        final ResultActions result = perfom(get(RpLogConfigurationController.LOGGING_LEVEL_URI).header("SwarmId", swarmId));
        
        // Assert
        assertJsonContentType(result);
        assertOkStatus(result);
        
        // Matching should contain
        assertResult("Custom mapping contain RDA2 log level", result, jsonPath("$RDA2").exists());
    }
}
