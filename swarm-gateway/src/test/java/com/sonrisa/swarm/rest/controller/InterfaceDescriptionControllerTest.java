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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.retailpro.rest.RetailProApiConstants;

/**
 * Test cases for the {@link  InterfaceDescriptionController} class.
 *
 * @author joe
 */
public class InterfaceDescriptionControllerTest extends BaseIntegrationTest{

    /**
     * Test method for the {@link InterfaceDescriptionController#version() } method.
     * 
     * The request inquires the latest intarface version.
     * 
     * 
     */
    @Test
    public void testInterfaceDescription() {
        final ResultActions result = perfom(get("/version"));
        assertJsonContentType(result);
        assertOkStatus(result);
        
        assertResult("The interfaceVersion has not been received.", result, 
                jsonPath("$."+RetailProApiConstants.JSON_KEY_API_DESCRIPTION_VERSION).exists());
        
        assertResult("The build timestamp has not been received.", result, 
                jsonPath("$."+RetailProApiConstants.JSON_KEY_BUILD_TIMESTAMP).exists());
    }
}