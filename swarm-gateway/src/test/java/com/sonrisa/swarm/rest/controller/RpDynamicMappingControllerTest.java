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

import com.sonrisa.swarm.retailpro.rest.controller.RpDynamicMappingController;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import com.sonrisa.swarm.retailpro.dao.impl.RpDynamicMappingDaoImpl;

/**
 * Class testing the {@link RpDynamicMappingController} class, a REST
 * service where Retail Pro V8 plugin can fetch its dynamic mapping from.
 */
public class RpDynamicMappingControllerTest extends BaseControllerTest {
    
    /**
     *  Use tmp directory as custom mapping folder for the Unit tests
     */
    private static final String CUSTOM_MAPPING_FOLDER = System.getProperty("java.io.tmpdir") +  "/";
    
    /**
     * Dao being tested
     */
    @Autowired
    private RpDynamicMappingDaoImpl dao;
    
    /**
     * Setup the custom mapping folder before each test
     */
    @Before
    public void setUpCustomMappingFolder(){
        dao.setCustomMappingFolder(CUSTOM_MAPPING_FOLDER);
    }
    
    /**
     * Test case: There is a custom mapping for one of the swarmIds
     * 
     * Expected: The REST interface returns the custom log level configuration for this
     * @throws IOException 
     */
    @Test
    public void testDynamicMappingControllerBestCaseScenario() throws IOException{

        final String swarmId = "test_dynamic_mapping_controller_best_case_scenario";
        final String posSoftware = "retailpro9";
        
        final String expectedInvoiceMapping = "{\"test456\":\"test789\"}";

        FileUtils.writeStringToFile(new File(CUSTOM_MAPPING_FOLDER + "/" + swarmId + "." + posSoftware + ".json"), expectedInvoiceMapping);
        
        final ResultActions resultForMatching = perfom(get(RpDynamicMappingController.MAPPING_URI)
                                                    .header("Pos-Software", posSoftware)
                                                    .header("SwarmId", swarmId));
        
        final ResultActions resultForOther = perfom(get(RpDynamicMappingController.MAPPING_URI)
                                                .header("Pos-Software", posSoftware)
                                                .header("SwarmId", "sonrisa123"));
        
        // Assert
        assertJsonContentType(resultForMatching);
        assertOkStatus(resultForMatching);
        
        assertJsonContentType(resultForOther);
        assertOkStatus(resultForOther);
        
        // Matching should contain
        assertResult("Custom mapping should be returned.", resultForMatching, 
                jsonPath("$test456").exists());
        
        // Not matching should not contain
        assertResult("Default mapping should be returned", resultForOther, 
                jsonPath("$test456").doesNotExist());
    }
    
    /**
     * Test case: There is a custom mapping for one of the swarmIds
     * 
     * Expected: The REST interface returns the default mapping when triggered with no swarmId
     * 
     * @throws IOException 
     */
    @Test
    public void testDynamicMappingControllerWithoutSwarmId() throws IOException{

        final String swarmId = "test_dynamic_mapping_controller_best_case_scenario";
        
        final String expectedInvoiceMapping = "{\"test456\":\"test789\"}";

        FileUtils.writeStringToFile(new File(CUSTOM_MAPPING_FOLDER + "/" + swarmId + ".retailpro8.json"), expectedInvoiceMapping);
        
        final ResultActions result = perfom(get(RpDynamicMappingController.MAPPING_URI));
        
        // Assert
        assertJsonContentType(result);
        assertOkStatus(result);
        
        
        // Not matching should not contain
        assertResult("Default mapping should be returned", result, 
                jsonPath("$test456").doesNotExist());
        
    }
}
