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
import com.sonrisa.swarm.retailpro.dao.impl.RpDynamicMappingDaoImpl;

/**
 * Test the {@link RpDynamicMappingDaoImpl} class
 * @author sonrisa
 *
 */
public class RpDynamicMappingDaoImplTest extends BaseIntegrationTest {
    
    /**
     *  Use tmp directory as custom mapping folder for the Unit tests
     */
    private static final String CUSTOM_MAPPING_FOLDER = System.getProperty("java.io.tmpdir") + "/";
    
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
     * Test case: No custom mapping files are declared, but
     * customer mapping is still requested
     * 
     * Expected: Some mapping is retrieved, supposedly the default mapping
     */
    @Test
    public void testDefaultRpDynamicMappingDao(){
        String mapping = dao.getDynamicMapping("sonrisa123", null);
        
        assertNotNull(mapping);
        
        // Default mapping provides Invoice mapping
        assertTrue(mapping.indexOf("Invoice") > 0);
        
        // And it contains RDA2 fields, as it's for Retail Pro V8
        assertTrue(mapping.indexOf("fidItemSID") > 0);
    }
    
    /**
     * Test case: Custom mapping is set for a given swarmId's invoices
     * 
     * Expected: The custom mapping is returned for this swarm Id
     * @throws IOException 
     */
    @Test
    public void testCustomDynamicMapping() throws IOException{
        final String swarmId = "test_custom_dynamic_mapping_rp_dao_test";
        final String posSoftware = "retailpro8";
        
        final String expectedMapping = "{\"test\":\"test123\"}";
        
        FileUtils.writeStringToFile(new File(CUSTOM_MAPPING_FOLDER + "/" + swarmId + "." + posSoftware + ".json"), expectedMapping);
                         
        final String actualMappingForMatching = dao.getDynamicMapping(swarmId, posSoftware);
        
        final String actualInvoiceMappingForNotMatching = dao.getDynamicMapping("sonrisa123", posSoftware);
        
        assertEquals(expectedMapping, actualMappingForMatching);
        assertNotEquals(expectedMapping, actualInvoiceMappingForNotMatching);
    }
    
    /**
     * Test case: Same swarm Id has two different mappings for Retail Pro 8 and 9
     * 
     * Expected: PosSoftware argument provides access to both of them
     * 
     * @throws IOException
     */
    @Test
    public void testCustomDynamicMappingWithSameSwarmId() throws IOException {
        final String swarmId = "test_custom_dynamic_mapping_with_same_swarmid_rp_dao_test";
        
        final String v8PosSoftware = "retailpro8";
        final String v8Mapping = "{\"test\":\"test123\"}";
        
        final String v9PosSoftware = "retailpro9";
        final String v9Mapping = "{\"Test\":\"test456\"}";
        
        FileUtils.writeStringToFile(new File(CUSTOM_MAPPING_FOLDER + "/" + swarmId + "." + v8PosSoftware + ".json"), v8Mapping);
        FileUtils.writeStringToFile(new File(CUSTOM_MAPPING_FOLDER + "/" + swarmId + "." + v9PosSoftware + ".json"), v9Mapping);
        
        // Act
        final String v8Result = dao.getDynamicMapping(swarmId, v8PosSoftware);
        final String v9Result = dao.getDynamicMapping(swarmId, v9PosSoftware);
        
        // Assert
        assertEquals(v8Mapping, v8Result);
        assertEquals(v9Mapping, v9Result);        
    }
}
