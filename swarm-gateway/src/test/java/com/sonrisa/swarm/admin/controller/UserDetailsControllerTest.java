/*
 *   Copyright (c) 2014 Sonrisa Informatikai Kft. All Rights Reserved.
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

package com.sonrisa.swarm.admin.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import com.sonrisa.swarm.BaseSecurityIntegrationTest;

/**
 * Testing the {@link UserDetailsController} class.
 */
public class UserDetailsControllerTest extends BaseSecurityIntegrationTest {

    /**
     * Test case: GET request to /user with no authentication
     * Expected: HTTP Unauthorized response is given
     */
    @Test
    public void testAccessDenied() throws Exception{
        final ResultActions result = perfom(get("/user"));
        result.andExpect(status().isUnauthorized());
    }
    
    /**
     * Test case: GET request to /user with bad credentials
     * Expected: HTTP Unauthorized response is given
     */
    @Test
    public void testWrongCredentials() throws Exception{
        final String userName = "h@rmfulAttacker";
        final String password = "secre1W0rd";
        
        final ResultActions result = perfom(get("/user").header("Authorization", getAuthorizationHeader(userName,password)));
        result.andExpect(status().isUnauthorized());
    }
    
    /**
     * Test case: GET request to /user with <code>ROLE_USER</code> role
     * Expected: HTTP OK response is given and user is marked as not admin
     */
    @Test
    public void testWithUserCredentials() throws Exception{
        final String userName = "sonrisa";
        final String password = "sonrisa2013";
        
        final ResultActions result = perfom(get("/user").header("Authorization", getAuthorizationHeader(userName,password)));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.role", is("ROLE_USER")));
    }
    
    /**
     * Test case: GET request to /admin/user wth <code>ROLE_USER</code> role
     * Expected: HTTP Forbidden response is given
     */
    @Test
    public void testAdminWithUserCredentials() throws Exception{
        final String userName = "sonrisa";
        final String password = "sonrisa2013";
        
        final ResultActions result = perfom(get("/admin/user").header("Authorization", getAuthorizationHeader(userName,password)));
        result.andExpect(status().isForbidden());
    }
    
    /**
     * Test case: GET request to /user with with <code>ROLE_ADMIN</role>
     * Expected: HTTP OK response is given and user is marked as admin
     */
    @Test
    public void testWithAdminCredentials() throws Exception{
        final String userName = "admin";
        final String password = "sonrisa2013";
        
        final ResultActions result = perfom(get("/user").header("Authorization", getAuthorizationHeader(userName,password)));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.role", is("ROLE_ADMIN")));
    }
    
    /**
     * Test case: GET request to /admin/user with with <code>ROLE_ADMIN</role>
     * Expected: HTTP OK response is given and user is marked as admin
     */
    @Test
    public void testAdminWithAdminCredentials() throws Exception{
        final String userName = "admin";
        final String password = "sonrisa2013";
        
        final ResultActions result = perfom(get("/admin/user").header("Authorization", getAuthorizationHeader(userName,password)));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.role", is("ROLE_ADMIN")));
    }
}
