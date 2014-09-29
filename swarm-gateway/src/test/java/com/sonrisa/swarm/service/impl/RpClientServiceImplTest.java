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
package com.sonrisa.swarm.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.retailpro.dao.impl.RpClientDao;
import com.sonrisa.swarm.retailpro.rest.model.RpClientJson;
import com.sonrisa.swarm.retailpro.model.RpClientEntity;
import com.sonrisa.swarm.retailpro.service.RpClientService;

/**
 * Test cases for this class: {@link RpClientServiceImpl}.
 *
 * @author joe
 */
public class RpClientServiceImplTest extends BaseIntegrationTest {
    
    @Autowired
    private RpClientService clientService;
    
    @Autowired
    private RpClientDao clientDao;
    
    /**
     * Test case: A new JSON client object has been created and then saved with the service.
     * 
     * Expected result: A new RetailPro client will be created in the the DB and
     * the find by id method will find it.
     * 
     */
    @Test
    public void basicSaveAndFindTest() throws JsonProcessingException {
        final Date now = new Date();   
        final String swarmId = "mySwarmId";      
        final String compId1 = "myComponentId1";
        final String compId2 = "myComponentId2";
        
        final List<RpClientJson> list = new ArrayList<RpClientJson>();
        list.add(createClientJson(now, compId1));
        list.add(createClientJson(now, compId2));
        clientService.save(swarmId, list);
        
        assertRpClient(list.get(0), now, swarmId, compId1);
        assertRpClient(list.get(1), now, swarmId, compId2);
    }
    
    private void assertRpClient(RpClientJson expected, Date expectedInstallDate, String swarmId, String componentId){
        final RpClientEntity client = clientDao.findBySwarmIdAndComponentId(swarmId, componentId);
        assertNotNull(client);
        assertEquals(expected.getComments(), client.getComments());
        assertEquals(expected.getComponentId(), client.getComponentId());
        assertEquals(expected.getComponentType(), client.getComponentType());
        assertEquals(expected.getVersion(), client.getRpVersion());
        assertEquals(expectedInstallDate, client.getInstallDate());
        assertEquals(swarmId, client.getSwarmId());
        assertNotNull(client.getCreatedAt());
        assertNotNull(client.getModifiedAt());
    }

    private RpClientJson createClientJson(Date now, String componentId) {
        final RpClientJson json = new RpClientJson();
        json.setComments("myComments");
        json.setComponentId(componentId);
        json.setComponentType("myComponentType");
        json.setInstallDate(now);
        json.setVersion("myVersion");
        return json;
    }
    
}
