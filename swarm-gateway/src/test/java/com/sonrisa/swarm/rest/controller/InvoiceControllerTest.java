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

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.retailpro.model.enums.JsonType;
import com.sonrisa.swarm.retailpro.rest.controller.InvoiceController;
import com.sonrisa.swarm.retailpro.service.RpInvoiceService;
import com.sonrisa.swarm.retailpro.util.mapper.EntityHolder;
import com.sonrisa.swarm.staging.service.InvoiceStagingService;

/**
 * @author joe
 */
public class InvoiceControllerTest extends BaseControllerTest {

    @Autowired
    private InvoiceStagingService invoiceStagingService;
    
    @Autowired 
    private InvoiceController target;
    
    @Autowired
    private RpInvoiceService rpInvoiceService;

    /**
     * Directory where the gateway writes log files
     */
    @Value("${user.home}/swarm/log/rpClient/")
    private String logDirectory;
    
    /**
     * Rebind the {@link RpInvoiceService} to the target after each test,
     * so we may replace it in any test without affecting others.
     */
    @After
    public void rebindRpInvoiceService(){
        target.setRpInvoiceService(rpInvoiceService);
    }

    /**
     * Test case: The invoice controller is invoked with a mock invoice object.
     * 
     * Expected result: After the execution the invoice represented by a JSON
     * object should to be in the staging table.
     * 
     * 
     * @throws Exception
     */
    @Test
    public void createInvoiceTest() throws Exception {
        InputStream invoiceStream = MockDataUtil.getResourceAsStream(MockTestData.MOCK_INVOICE);
        Map<String, Object> userData = objectMapper.readValue(invoiceStream, Map.class);

        // request parameters
        String invoiceJson = objectMapper.writeValueAsString(userData);

        // calls the REST service with a new invoice object
        MockHttpServletRequestBuilder request = put(InvoiceController.URI).content(invoiceJson).contentType(MediaType.APPLICATION_JSON)
                .header("SwarmId", "someId");

        final ResultActions postResultAction = mockMvc.perform(request);
        assertCreatedStatus(postResultAction);

        assertEquals(1, invoiceStagingService.findAllIds().size());
        Object listObj = userData.get(JsonType.Invoices.name());
        Map invoiceMap = (Map) ((List) listObj).get(0);
        assertJsonMapContains(invoiceMap, "CustSid", "123456");
        assertJsonMapContains(invoiceMap, "InvoiceNo", "i123456");
    }

    /**
     * Test case: The invoice controller is invoked with a mock invoice object
     * but without a swarmId in the header.
     * 
     * Expected result: 401 Unauthorized response code
     * 
     * 
     * @throws Exception
     */
    @Test
    public void missingSwarmIdTest() throws Exception {
        InputStream invoiceStream = MockDataUtil.getResourceAsStream(MockTestData.MOCK_INVOICE);
        missingSwarmIdTest(invoiceStream, InvoiceController.URI);
    }

    /**
     * Test case: Attempting to upload invoices to <code>/invoices</code> but an
     * exception occurs within the service.
     * 
     * Expected: A response with HTTP 500 is returned and the exception is
     * logged.
     */
    @Test
    public void testInternalServerErrorIsReturnedOnRuntimeException() throws Exception {

        // Setup controller's service to always fail
        RpInvoiceService faultyService = mock(RpInvoiceService.class);
        doThrow(new RuntimeException("message")).when(faultyService).writeToStage(any(EntityHolder.class));
        target.setRpInvoiceService(faultyService);
        
        // Act
        String json = MockDataUtil.getResourceAsString(MockTestData.MOCK_INVOICE);
        MockHttpServletRequestBuilder request = put(InvoiceController.URI)
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .header("SwarmId", "sonrisa");
        
        // Assert
        mockMvc.perform(request).andExpect(status().isInternalServerError());
    }
    
    /**
     * Test case: send an invoice to the controller
     * 
     * Expected result: the invoice is logged to a file, that contains the
     * clients swarmId
     * 
     * @throws Exception
     */
    @Test
    public void testSeparatedLogging() throws Exception {
        String swarmId = "uniqueSwarmID" + System.currentTimeMillis();

        String json = MockDataUtil.getResourceAsString(MockTestData.MOCK_INVOICE);

        // this name will be the logfile's name (created by the gateway)
        String file = logDirectory.concat(swarmId).concat("-client-seq.log");
        File logFile = new File(file);
        if (logFile.exists()) {
            logFile.delete();
        }

        MockHttpServletRequestBuilder request = put(InvoiceController.URI).content(json).contentType(MediaType.APPLICATION_JSON)
                .header("SwarmId", swarmId);
        mockMvc.perform(request);

        assertTrue("logfile has not been created: " + file, logFile.exists());

        logFile.delete();
    }
}
