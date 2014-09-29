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

package com.sonrisa.swarm.service.impl;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.posintegration.service.ApiService;
import com.sonrisa.swarm.posintegration.service.impl.ApiServiceImpl;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity.ApiType;

/**
 * Test the {@link ApiServiceImpl} class.
 */
public class ApiServiceImplTest extends BaseIntegrationTest {
    
    /**
     * Target being tested
     */
    @Autowired 
    private ApiService target;

    /**
     * Test case:
     *  Reading all gateway manager api names and ids
     *  from the {@link ApiServiceImpl}
     *  
     * Expected:
     *  It contains all known values by correct type
     */
    @Test
    public void testGettingApis(){
        
        final Set<ApiEntity> pullValues = target.findManyByType(ApiType.PULL_API);
        final Set<ApiEntity> retailproValues = target.findManyByType(ApiType.RETAILPRO_API);
        
        assertContainsApi("shopify", pullValues);
        assertContainsApi("revel", pullValues);
        assertContainsApi("kounta", pullValues);
        assertContainsApi("lightspeed_pro", pullValues);
        assertContainsApi("rics", pullValues);
        
        assertContainsApi("retailpro8", retailproValues);
        assertContainsApi("retailpro9", retailproValues);
    }
    
    /**
     * Asserts that an expected API is among a set of APIs
     */
    private void assertContainsApi(String expectedName, Collection<ApiEntity> apis){
        boolean found = false;
        for(ApiEntity api : apis){
            if(expectedName.equals(api.getApiName())){
                found = true;
            }
        }
        assertTrue("API not found: " + expectedName + " actual values are: " + StringUtils.join(apis,","), found);
    }
}
