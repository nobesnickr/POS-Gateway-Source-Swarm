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
package com.sonrisa.swarm.dozer;

import org.dozer.DozerBeanMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.model.legacy.CustomerEntity;
import com.sonrisa.swarm.model.staging.CustomerStage;

/**
 * Test cases that checks the Dozer mapping configuration.
 * 
 * Dozer is a Java Bean to Java Bean mapper that recursively 
 * copies data from one object to another. Typically, these Java Beans 
 * will be of different complex types.
 * 
 * Dozer supports simple property mapping, complex type mapping, bi-directional mapping, 
 * implicit-explicit mapping, as well as recursive mapping. This includes mapping collection 
 * attributes that also need mapping at the element level.
 * http://dozer.sourceforge.net/
 * 
 * The dozer configuration can be found here: 
 * resources/dozermapping/*Mapping.xml
 *
 * @author joe
 */
public class MappingTest extends BaseIntegrationTest {

    /**
     * Util object to map staging and legacy entities.
     */
    @Autowired
    private DozerBeanMapper dozepMapper;

    /**
     * Test of mapping between {@link CustomerStage} and {@link CustomerEntity}.
     */
    @Test
    public void testCustomerMapping() {
        CustomerStage stgCust = MockTestData.mockCustomerStage("swarmId", "sbsNo", "storeNo", "12", "name");
        final CustomerEntity cust = dozepMapper.map(stgCust, CustomerEntity.class);
        
        assertCustomerEquals(stgCust, cust);     
    }
}
