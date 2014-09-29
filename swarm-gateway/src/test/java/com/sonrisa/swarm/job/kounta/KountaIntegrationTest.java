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
package com.sonrisa.swarm.job.kounta;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonrisa.swarm.BaseExtractionIntegrationTest;
import com.sonrisa.swarm.job.ExtractorLauncherWriter;
import com.sonrisa.swarm.job.InvoiceProcessorWriter;
import com.sonrisa.swarm.kounta.controller.KountaOAuthController;
import com.sonrisa.swarm.kounta.controller.KountaOAuthControllerTest;
import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.kounta.MockKountaData;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;

/**
 * Kounta integration tests
 * @author Barnabas
 */
public class KountaIntegrationTest extends BaseExtractionIntegrationTest {

    @Autowired
    @Qualifier("kountaExtractorLauncherTest")
    private JobLauncherTestUtils kountaExtratorJobUtil;
    
    @Autowired
    @Qualifier("kountaProcessorLauncherTest")
    private JobLauncherTestUtils kountaProcessorJobUtil;
    
    @Autowired
    @Qualifier("loaderJobTestUtil")
    private JobLauncherTestUtils loaderJobUtil;

    @Value("${api.name.kounta}")
    private String kountaApiName;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private StoreDao storeDao;
    
    /**
     * Initial setup of the mock service
     * @throws ExternalExtractorException 
     */
    @Before
    public void setUp() throws Exception {
        
        // Setup registration page and exchanging tokens
        KountaOAuthControllerTest.setupRegistrationAndTokenPages();
        
        final JsonNode batchInvoices = MockDataUtil.getResourceAsJson(MockKountaData.MOCK_KOUNTA_ORDER_BATCH);
        
        // Matching any query to invoices containing skip=0
        stubFor(get(urlMatching("/companies/[\\d]*/sites/[\\d]*/orders/complete.json.*"))
                .withHeader("X-Page", equalTo("0"))
                .willReturn(
                        aResponse()
                        .withBody(batchInvoices.toString())
                        .withHeader("X-Pages", "1")));

        
        // Generate unique detail page for each top level invoices to avoid key constraint violations
        for(JsonNode invoiceNode : batchInvoices){
            stubFor(get(urlMatching("/companies/[\\d]*/orders/" + invoiceNode.get("id").asText() + ".json.*"))
                    .willReturn(
                            aResponse().withBody((
                            getJsonWithRandomIds(
                                    invoiceNode.get("id").asInt(),
                                    MockDataUtil.getResourceAsString(MockKountaData.MOCK_KOUNTA_DETAILED_ORDER))))));
        }
    }
    
    /**
     * Step 0: register store using OAuth 
     * @throws Exception 
     */
    private void registerStore() throws Exception{

        // Execute request
        mockMvc.perform(get(KountaOAuthController.LANDING_PAGE )
                        .param("code", "risason")).andReturn();
        
        // Set one of the stores to active
        StoreEntity store = storeDao.findAll().get(0);
        store.setActive(Boolean.TRUE);
        storeService.save(store);
    }
    
    /**
     * Step 1: execute extractor
     */
    private void executeExtractor(){
        
        // Run extraction
        final JobExecution extractionResult = launchJob(kountaExtratorJobUtil);
        
        // We expect that 1 store has been fetched because 1 active store exists
        final int numOfExtractedStores = extractionResult.getExecutionContext()
                                            .getInt(ExtractorLauncherWriter.NUM_OF_STORES_EXTRACTED);
        
        assertEquals("Not all stores were extracted", 1, numOfExtractedStores);
        
    }

    /**
     * Test case:
     * <ol> 
     *  <li>Registers Kounta store using OAuth</li>
     *  <li>Extracts batch from mock Kounta server</li>
     *  <li>Saves it to the stage</li>
     *  <li>Stage entities are moved to the legacy tables</li>
     *  <li>Extracts detailed information for each invoice in the batch</li>
     *  <li>Stage entities are moved to the legacy tables</li>  
     * </ol>
     *  
     * Expected:
     *  The number of legacy entities matches the expected number.
     */
    @Test
    public void testExecution() throws Exception {    
        
        // Register two stores, activate one of them
        registerStore();
        
        // Run extractor
        executeExtractor();
        assertStagingCount(MockKountaData.getKountaBatchMockDescriptor());
        
        // Run loader job
        launchJob(loaderJobUtil);
        
        // Assert the legacy DB
        assertNonDummyLegacyCount(MockKountaData.getKountaBatchMockDescriptor());
        assertStagingIsEmpty();

        // Run processing Job
        final JobExecution processingResult = launchJob(kountaProcessorJobUtil);
        final int numOfProcessedInvoices = processingResult.getExecutionContext()
                .getInt(InvoiceProcessorWriter.NUM_OF_ITEMS_EXTRACTED);
        
        assertEquals("Not all batch items were processed", 3, numOfProcessedInvoices);
        assertStagingCount(MockKountaData.getKountaMockProcessedBatchDescriptor());
        
        // Run loader job
        launchJob(loaderJobUtil);
        
        // Assert the legacy DB
        assertNonDummyLegacyCount(MockKountaData.getKountaMockProcessedBatchDescriptor());
        assertStagingIsEmpty();
        
        // Assert that access token was requested only three times,
        // Once during registration, once during extraction and once during processing
        verify(3, postRequestedFor(urlMatching("/token.json")));
    }
    
    /**
     * Test case:
     * When attempting to get detailed invoices, the REST server
     * goes down.
     *  
     * Expected:
     *  The entities are left in the legacy, with lines_processed FALSE.
     */
    @Test
    public void testProcessingFails() throws Exception {

        // Create an active and an inactive store
        saveMockStoreEntities(kountaApiName);
        
        stubFor(get(urlMatching("/companies/[\\d]*/orders/.*"))
                .willReturn(aResponse().withStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR)));
        
        // Run extractor
        executeExtractor();
        
        // Run loader job
        launchJob(loaderJobUtil);
        
        // Assert the legacy DB
        assertNonDummyLegacyCount(MockKountaData.getKountaBatchMockDescriptor());
        assertStagingIsEmpty();
        
        // Process
        launchJob(kountaProcessorJobUtil);

        // Run loader job
        launchJob(loaderJobUtil);
        
        // Assert the legacy DB
        assertNonDummyLegacyCount(MockKountaData.getKountaBatchMockDescriptor());
        assertStagingIsEmpty();
        
        InvoiceEntity invoice = invoiceDao.findAll().get(0);
        assertEquals("All invoices should be left unprocessed", Boolean.FALSE, invoice.getLinesProcessed());
    }
    
    /**
     * Randomizes foreign ids in a JSON to avoid collision
     */
    private String getJsonWithRandomIds(int invoiceId, String jsonText) throws Exception {
        StringBuilder buffer = new StringBuilder();
        
        String[] lines = jsonText.split("\n");
        for(int i = 0; i < lines.length; i++){
            lines[i] = lines[i].trim();
            
            // In the mock JSON file the second line is the invoice's id
            // Force invoiceId in this and not a random value
            if(i == 1){
                lines[i] = String.format("\"id\":%d,", invoiceId);
                
            // Regex for "id" + any whitespace + ":" + any digits + ","
            // We replace each line with a random id
            } else if(lines[i].matches("\"id\":[\\s]*?[\\d]*,")){
                lines[i] = String.format("\"id\":%d,", (int)(Math.random() * 100000L));
            }
            buffer.append(lines[i]).append("\n");
        }
        
        return buffer.toString();
    }
}
