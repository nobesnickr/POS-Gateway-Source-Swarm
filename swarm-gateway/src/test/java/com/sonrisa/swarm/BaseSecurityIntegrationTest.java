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

package com.sonrisa.swarm;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Base Spring test class for integration tests testing the security features, like authentication
 */
public abstract class BaseSecurityIntegrationTest extends BaseIntegrationTest {
    
    /**
     * Admin credentials
     */
    protected static final String USERNAME = "admin";
    
    /**
     * Admin credentials
     */
    protected static final String PASSWORD  = "sonrisa2013";

    @Resource
    private FilterChainProxy springSecurityFilterChain;
    
    @Autowired
    @Qualifier("userDataSource")
    protected DataSource userDataSource;
    
    protected org.springframework.core.io.Resource generateScript = new ClassPathResource("eeuser-generate.sql");
    
    @Before
    @Override
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webContext).addFilter(springSecurityFilterChain).build();
    }
    
    @Before
    public void setupUsersTable(){
        JdbcTemplate userTemplate = new JdbcTemplate(userDataSource);
        JdbcTestUtils.executeSqlScript(userTemplate, generateScript, false);
    }
    
    @After
    public void deleteUsersTable(){
        JdbcTemplate userTemplate = new JdbcTemplate(userDataSource);
        userTemplate.execute("TRUNCATE TABLE exp_members");
        userTemplate.execute("TRUNCATE TABLE exp_member_groups");
    }
    
    
    /**
     * Get basic authentication header
     * @return
     */
    protected String getAuthorizationHeader(String userName, String password){
        return "Basic " + new String(Base64.encodeBase64((userName + ":" + password).getBytes()));
    }
}
