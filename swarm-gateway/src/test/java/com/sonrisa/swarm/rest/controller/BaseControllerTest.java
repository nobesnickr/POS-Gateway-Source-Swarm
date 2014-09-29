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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.sonrisa.swarm.BaseIntegrationTest;

/**
 * Base class of controller tests.
 *
 * @author joe
 */
public abstract class BaseControllerTest extends BaseIntegrationTest {
    
     /**
     * Test case: 
     * A controller is invoked with a mock object object but without a swarmId in the header.
     * 
     * Expected result:
     * 401 Unauthorized response code
     *      
     * @throws Exception 
     */
    protected void missingSwarmIdTest(InputStream mockObjectStream, String uri) throws Exception {        
        Object userData = objectMapper.readValue(mockObjectStream, Object.class);

        // request parameters
        String invoiceJson = objectMapper.writeValueAsString(userData);

        // calls the REST service with a new invoice object
        MockHttpServletRequestBuilder request = put(uri)
                .content(invoiceJson)
                .contentType(MediaType.APPLICATION_JSON);
        final ResultActions postResultAction = mockMvc.perform(request);
        postResultAction.andExpect(status().isUnauthorized());
    }
    
    protected void assertJsonMapContains(Map<String, Object> map, String expKey, String expValue) {
        assertTrue("The received map does not contain the expected key: " + expKey, map.containsKey(expKey));
        assertEquals("The value with the key " + expKey + ", does not equal with the expected value: " + expValue, expValue, map.get(expKey));
    }
}
