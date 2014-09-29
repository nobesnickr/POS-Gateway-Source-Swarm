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

package com.sonrisa.swarm.security;
import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.sonrisa.swarm.BaseSecurityIntegrationTest;

/**
 * Class testing the {@link JSONAccessExceptionHandler} class.
 */
public class UserDetailsServiceTest extends BaseSecurityIntegrationTest {

    @Autowired
    private UserDetailsService target;

    /**
     * Test case: There is an expression engine user in the <i>userDataSource</i> 
     * Expected: When requested from {@link #target} it returns it.
     */
    @Test
    public void testUserExists(){
        final String userName = "sonrisa";
        UserDetails userDetails = target.loadUserByUsername(userName);
        
        // Assert
        assertEquals(userName, userDetails.getUsername());
    }
}
