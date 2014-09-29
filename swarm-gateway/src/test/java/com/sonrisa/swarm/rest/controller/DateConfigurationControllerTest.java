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

import com.sonrisa.swarm.retailpro.rest.controller.DateConfigurationController;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.retailpro.dao.impl.DateConfigurationDao;
import com.sonrisa.swarm.retailpro.rest.model.ForcedDateConfiguration;
import com.sonrisa.swarm.retailpro.rest.RetailProApiConstants;
import com.sonrisa.swarm.retailpro.model.DateConfigurationEntity;

/**
 * Class testing the {@link DateConfigurationController} class.
 *  
 * @author barna
 */
@Transactional
public class DateConfigurationControllerTest extends BaseIntegrationTest {

    @Autowired
    DateConfigurationDao dao;
    
    /**
     * Test method for the {@link DateConfigurationController#config() } method.
     * 
     * The request inquires the configuration for a given swarmId, and asserts the JSON fields
     * in the received data. 
     * @throws Exception 
     */
    @Test
    public void testDateConfigurationRestControllerBestCaseScenario() throws Exception {
        
        final String swarmId = "sonrisa123";
        
        DateConfigurationEntity entity = new DateConfigurationEntity();
        
        final Long timestamp = (new Date()).getTime()/1000 - 1000000L;
        
        entity.setSwarmId(swarmId);
        entity.setLastModifiedInvoiceDate(new Date());
        entity.setLastModifiedStoreDate(new Date());
        entity.setLastModifiedVersionDate(new Date());
        entity.setTimeStampVersion(new Date(timestamp * 1000));
        
        final SimpleDateFormat dateFormat = new SimpleDateFormat(ForcedDateConfiguration.DATE_FORMAT);
        
        // Insert into configuration
        dao.save(Arrays.asList(entity));
        
        // Trigger REST API
        MockHttpServletRequestBuilder request = get(DateConfigurationController.CONFIG_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header("SwarmId", swarmId);
        
        final ResultActions result = mockMvc.perform(request);
        assertJsonContentType(result);
        assertOkStatus(result);
                
        assertResult("Version field incorrect", result, 
                jsonPath("$."+RetailProApiConstants.JSON_KEY_REMOTE_CONFIG_VERSION).value(timestamp.toString()));

        assertResult("LastInvoice field incorrect", result, 
                jsonPath("$."+RetailProApiConstants.JSON_KEY_REMOTE_CONFIG_LAST_INVOICE).exists());

        assertResult("LastInvoice field incorrect", result, 
                jsonPath("$."+RetailProApiConstants.JSON_KEY_REMOTE_CONFIG_LAST_INVOICE).value(dateFormat.format(entity.getLastModifiedInvoiceDate())));
        

        assertResult("LastStore field incorrect", result, 
                jsonPath("$."+RetailProApiConstants.JSON_KEY_REMOTE_CONFIG_LAST_STORE).value(dateFormat.format(entity.getLastModifiedStoreDate())));
        

        assertResult("LastVersion field incorrect", result, 
                jsonPath("$."+RetailProApiConstants.JSON_KEY_REMOTE_CONFIG_LAST_VERSION).value(dateFormat.format(entity.getLastModifiedVersionDate())));
    }
    
    /**
     * Test method for the {@link DateConfigurationController#config() }
     * method.
     * 
     * The request inquires the configuration for a given swarmId, and asserts
     * that the date values which are null are not listed in the JSON response.
     * 
     * @throws Exception
     */
    @Test
    public void testDateConfigurationNullIsNotShown() throws Exception {

        DateConfigurationEntity entity = new DateConfigurationEntity();
        final String swarmId = "sonrisa123";

        entity.setSwarmId(swarmId);
        entity.setLastModifiedInvoiceDate(new Date());
        entity.setTimeStampVersion(new Date());

        // Insert into configuration
        dao.save(Arrays.asList(entity));
        
        // Trigger REST API
        MockHttpServletRequestBuilder request = get(
                DateConfigurationController.CONFIG_URI).accept(
                MediaType.APPLICATION_JSON).header("SwarmId", swarmId);

        final ResultActions result = mockMvc.perform(request);
        assertJsonContentType(result);
        assertOkStatus(result);
        
        assertResult("Version field incorrect", result, 
                jsonPath("$."+RetailProApiConstants.JSON_KEY_REMOTE_CONFIG_VERSION).exists());
        
        assertResult("LastInvoice field incorrect", result, 
                jsonPath("$."+RetailProApiConstants.JSON_KEY_REMOTE_CONFIG_LAST_INVOICE).exists());
        
        assertResult("LastStore field shouldn't exist", result, 
                jsonPath("$."+RetailProApiConstants.JSON_KEY_REMOTE_CONFIG_LAST_STORE).doesNotExist());
        
        assertResult("LastVersion field shouldn't exist", result, 
                jsonPath("$."+RetailProApiConstants.JSON_KEY_REMOTE_CONFIG_LAST_VERSION).doesNotExist());
    }
    
    /**
     * Test method for the {@link DateConfigurationController#config() }
     * method.
     * 
     * Test case: request is sent to /config, but swarmId is missing.
     * 
     * Expected: The default value is returned
     * @throws Exception 
     */
    @Test
    public void testDateConfigurationWithMissingSwarmId() throws Exception {
        DateConfigurationEntity entity = new DateConfigurationEntity();
        final String swarmId = "sonrisa123";

        entity.setSwarmId(swarmId);
        entity.setLastModifiedInvoiceDate(new Date());
        entity.setTimeStampVersion(new Date());

        // Insert into configuration
        dao.save(Arrays.asList(entity));
        
        // Trigger REST API
        MockHttpServletRequestBuilder request = get(
                DateConfigurationController.CONFIG_URI).accept(MediaType.APPLICATION_JSON);

        final ResultActions result = mockMvc.perform(request);
        assertJsonContentType(result);
        assertOkStatus(result);
        
        assertResult("Version field incorrect", result, 
                jsonPath("$."+RetailProApiConstants.JSON_KEY_REMOTE_CONFIG_VERSION).value("0"));
        
        assertResult("LastInvoice field incorrect", result, 
                jsonPath("$."+RetailProApiConstants.JSON_KEY_REMOTE_CONFIG_LAST_INVOICE).doesNotExist());
        
        assertResult("LastStore field shouldn't exist", result, 
                jsonPath("$."+RetailProApiConstants.JSON_KEY_REMOTE_CONFIG_LAST_STORE).doesNotExist());
        
        assertResult("LastVersion field shouldn't exist", result, 
                jsonPath("$."+RetailProApiConstants.JSON_KEY_REMOTE_CONFIG_LAST_VERSION).doesNotExist());
    }
}
