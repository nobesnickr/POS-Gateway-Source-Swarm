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
package com.sonrisa.swarm.shopify.controller;

import com.sonrisa.shopify.controller.OauthController;
import com.sonrisa.swarm.BaseIntegrationTest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author joe
 */
public class OauthControllerTest extends BaseIntegrationTest {
    
    // TODO: finish this
    @Test
    @Ignore
    public void testSomeMethod() throws Exception {
        MockHttpServletRequestBuilder request = get(OauthController.CONTROLLER_PATH
                +"?code=alma&shop=s1.myshopify.com" );
        
        final ResultActions result = mockMvc.perform(request);
        assertStatus(result, status().isMovedTemporarily());
                
    }
    
     /**
     * Asserts the HTTP status of the result action.
     * <p/>
     * 
     * @param result 
     */    
    protected void assertStatus(ResultActions result, ResultMatcher status){
        try {
            result.andExpect(status);
        } catch (Exception ex) {
           throw new RuntimeException("An exception has been occured during the OK status assertation.", ex);
        }
    }
}