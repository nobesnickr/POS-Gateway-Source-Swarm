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
package com.sonrisa.swarm.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.retailpro.dao.impl.RpLogConfigurationDaoImpl;

/**
 * Test the {@link RpLogConfigurationDaoImpl} class
 */
public class RpLogConfigurationDaoImplTest extends BaseIntegrationTest {
    
    /**
     *  Use tmp directory as custom mapping folder for the Unit tests
     */
    private static final String CUSTOM_JSON_FOLDER = System.getProperty("java.io.tmpdir") + "/";
    
    /**
     * Class to be tested
     */
    @Autowired
    private RpLogConfigurationDaoImpl dao;
    
    /**
     * Setup the custom mapping folder before each test
     */
    @Before
    public void setUpCustomMappingFolder(){
        dao.setCustomLogLevelConfigFolder(CUSTOM_JSON_FOLDER);
    }
    
    /**
     * Test case: No custom logging configuration files are declared, but
     * custom log configuration mapping is still requested
     * 
     * Expected: Some mapping is retrieved, supposedly the default logging configuration 
     */
    @Test
    public void testDefaultLogConfiguration(){
        
        String mapping = dao.getLogLevelConfiguration("sonrisa123");
        
        assertNotNull(mapping);
        
        // Default mapping provides Invoice mapping
        assertTrue(mapping.indexOf("Network") > 0);        
    }
    
    
    /**
     * Test case: Custom loglevel mapping is set for a given swarmId's invoices
     * 
     * Expected: This custom mapping is returned for this swarm Id
     * @throws IOException 
     */
    @Test
    public void testCustomLogConfiguration() throws IOException{
        final String swarmId = "test_custom_log_configuration_rp_log_dao_test";
        
        final String expectedMapping = "{\"Network\":\"Debug\"}";
        
        FileUtils.writeStringToFile(new File(CUSTOM_JSON_FOLDER + "/" + swarmId + ".json"), expectedMapping);
                         
        final String forMatching = dao.getLogLevelConfiguration(swarmId);
        
        final String forNotmatching = dao.getLogLevelConfiguration("sonrisa123");
        
        assertEquals(expectedMapping, forMatching);
        assertNotEquals(expectedMapping, forNotmatching);
    }
    
    /**
     * Test case: Custom loglevel mapping is set for a given swarmId's invoices,
     * but it contains a logging level unacceptable by the Retail Pro plugin
     * 
     * Expected: This custom mapping is returned for this swarm Id
     * @throws IOException 
     */
    @Test
    public void testFaultyLogConfiguration() throws IOException{
        final String swarmId = "test_faulty_log_configuration_rp_log_dao_test";
        
        final String faultyMapping = "{\"Network\":\"SomethingThatDoesntExist\"}";
        
        FileUtils.writeStringToFile(new File(CUSTOM_JSON_FOLDER + "/" + swarmId + ".json"), faultyMapping);
                         
        final String forMatching = dao.getLogLevelConfiguration(swarmId);
        
        final String defaultMapping = dao.getLogLevelConfiguration("sonrisa123");
        
        assertEquals(defaultMapping, forMatching);
    }
}
