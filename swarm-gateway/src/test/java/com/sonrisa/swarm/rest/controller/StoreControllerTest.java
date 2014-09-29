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
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity.RpStoreState;
import com.sonrisa.swarm.retailpro.rest.controller.StoreController;
import com.sonrisa.swarm.retailpro.service.RpStoreService;

/**
 *
 * @author joe
 */
public class StoreControllerTest extends BaseControllerTest {

    @Autowired
    private RpStoreService storeService;
    
    /**
     * Test case:
     * The store controller is invoked with a mock Retail Pro V9 store.
     * 
     * Expected result:
     * The controller realizes that the header suggests a V9 store,
     * and inserts the store with the correct api id in the store table.
     * 
     */
    @Test
    public void createRetailProV9StoreTest() throws Exception {
        InputStream jsonStream = MockDataUtil.getResourceAsStream(MockTestData.MOCK_STORE);
        Object userData = objectMapper.readValue(jsonStream, Object.class);
        
        // request parameters
        String jsonObj = objectMapper.writeValueAsString(userData);       
        String swarmId = "someId";
        String posSoftware = "retailpro9";
        
        // calls the REST service with a new store object
        MockHttpServletRequestBuilder request = put(StoreController.URI)
                .content(jsonObj)
                .contentType(MediaType.APPLICATION_JSON)
                .header("SwarmId", swarmId)
                .header("Pos-Software", posSoftware)
                ;
        final ResultActions postResultAction = mockMvc.perform(request);
        assertCreatedStatus(postResultAction);     
        
        RpStoreEntity rpStore = storeService.findBySbsNoAndStoreNoAndSwarmId("123", "456", swarmId);
        assertNotNull(rpStore);
        assertEquals(posSoftware, rpStore.getPosSoftware());
        assertEquals("Default status for Retail Pro stores is NORMAL", RpStoreState.NORMAL, rpStore.getState());
    }
 
    /**
     * Test case: 
     * The store controller is invoked with a mock store object.
     * 
     * Expected result:
     * After the execution the store must be saved into the DB.
     */
    @Test
    public void createAndUpdateStoreTest() throws Exception {
        InputStream jsonStream = MockDataUtil.getResourceAsStream(MockTestData.MOCK_STORE);
        Object userData = objectMapper.readValue(jsonStream, Object.class);
        
        // request parameters
        String jsonObj = objectMapper.writeValueAsString(userData);       
        String swarmId = "someId";
        
        // calls the REST service with a new store object
        MockHttpServletRequestBuilder request = put(StoreController.URI)
                .content(jsonObj)
                .contentType(MediaType.APPLICATION_JSON)
                .header("SwarmId", swarmId);
        final ResultActions postResultAction = mockMvc.perform(request);
        assertCreatedStatus(postResultAction);     
        
        RpStoreEntity rpStore = storeService.findBySbsNoAndStoreNoAndSwarmId("123", "456", swarmId);
        assertNotNull(rpStore);
        
        // modification of the existing store
        InputStream jsonStream2 = MockDataUtil.getResourceAsStream(MockTestData.MOCK_STORE_MODIFIED);
        Object userData2 = objectMapper.readValue(jsonStream2, Object.class);
        String jsonObj2 = objectMapper.writeValueAsString(userData2);       
         // calls the REST service with a modified store object
        MockHttpServletRequestBuilder request2 = put(StoreController.URI)
                .content(jsonObj2)
                .contentType(MediaType.APPLICATION_JSON)
                .header("SwarmId", swarmId);
        final ResultActions postResultAction2 = mockMvc.perform(request2);
        assertCreatedStatus(postResultAction2);     
        
        // assert
        RpStoreEntity rpStore2 = storeService.findBySbsNoAndStoreNoAndSwarmId("123", "456", swarmId);
        assertNotNull(rpStore2);
        assertEquals("Mock store - modified", rpStore2.getStoreName());    
    }
    
    /**
     * Test case: The {@link StoreController}  is invoked with a mock store object but
     * without a swarmId in the header.
     *
     * Expected result: 401 Unauthorized response code
     *
     *
     * @throws Exception
     */
    @Test
    public void missingSwarmIdTest() throws Exception {
        InputStream invoiceStream = MockDataUtil.getResourceAsStream(MockTestData.MOCK_STORE);
        missingSwarmIdTest(invoiceStream, StoreController.URI);
    }
    
    /**
     * Test case: The {@link StoreController} is invoked with a mock store object
     * but it contains really long <code>StoreName</code>, <code>PosTimezone</code> and
     * </code>Notes</code> values.
     * 
     * Expected result:
     *  Stores are created but values are trimmed.
     *  
     * 
     * @throws Exception
     */
    @Test
    public void tooLongFieldTest() throws Exception {

        final String swarmId = "Sonrisa-Example";
        String json = MockDataUtil.getResourceAsString(MockTestData.MOCK_STORE_LONG_FIELDS);

        // calls the REST service with a new invoice object
        MockHttpServletRequestBuilder request = put(StoreController.URI)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .header("SwarmId", swarmId);

        // Act
        final ResultActions postResultAction = mockMvc.perform(request);
        
        // Assert
        assertCreatedStatus(postResultAction);
        
        RpStoreEntity rpStore = storeService.findBySbsNoAndStoreNoAndSwarmId("222", "111", swarmId);
        assertNotNull(rpStore);
        assertNotNull(rpStore.getStoreName());
        assertNotNull(rpStore.getPosTimezone());
        assertNotNull(rpStore.getNotes());
    }
    
    
    /**
     * TODO: Implement
     * 
     * Test case:
     *  The {@link StoreController} is invoked with a JSON containing only the <code>SwarmId</code>
     *  field.
     *  
     * Expected:
     *  Other fields are initialized as <code>null</code>, but the store gets created in the 
     *  <code>stores_rp</code> table none the less.
     */
    @Test
    @Ignore
    public void onlySwarmIdTest() throws Exception {
        
        final String swarmId = "Malformatted-Json";
        
        MockHttpServletRequestBuilder request = put(StoreController.URI)
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON)
                .header("SwarmId", swarmId);
        
        // Act
        final ResultActions postResultAction = mockMvc.perform(request);
        
        // Assert
        assertCreatedStatus(postResultAction);
        
        RpStoreEntity rpStore = storeService.findBySbsNoAndStoreNoAndSwarmId("", "", swarmId);
        assertNotNull(rpStore);
    }
}

