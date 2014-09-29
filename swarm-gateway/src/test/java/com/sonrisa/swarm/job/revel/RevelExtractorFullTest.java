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
package com.sonrisa.swarm.job.revel;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonrisa.swarm.BaseExtractionIntegrationTest;
import com.sonrisa.swarm.job.ExtractorLauncherWriter;
import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.revel.MockRevelData;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.InvoiceLineStage;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.revel.RevelExtractor;
import com.sonrisa.swarm.revel.controller.RevelStoreController;
import com.sonrisa.swarm.revel.service.RevelStoreService;
/**
 * Integration test for the {@link RevelExtractor} class.
 */
public class RevelExtractorFullTest extends BaseExtractionIntegrationTest {
    /** Port used by the Mock Revel server in this test class. */
    private static final int MOCK_REVEL_PORT = 2145;

    @Autowired
    @Qualifier("revelExtractorLauncherTest")
    private JobLauncherTestUtils revelExtratorJobUtil;
    
    @Autowired
    @Qualifier("loaderJobTestUtil")
    private JobLauncherTestUtils loaderJobUtil;
        
    @Autowired
    private StoreService storeService;
            
    @Autowired
    private StoreDao storeDao;
    
    /**
     * @see http://wiremock.org/getting-started.html
     */
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_REVEL_PORT);
    
    /**
     * Initial setup of the mock service
     * @throws ExternalExtractorException 
     */
    @Before
    public void setUp() throws ExternalExtractorException{
        
        // Matching any query to establishment returns the mock JSON
        stubFor(get(urlMatching("/" + RevelStoreService.ESTABLISHMENT_URI + ".*?"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockRevelData.MOCK_ESTABLISHMENTS)))));
        
        // Setup mock REST service
        stubFor(WireMock.get(urlMatching("/products/ProductCategory.*"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockRevelData.MOCK_PRODUCT_CATEGORIES)))));

        stubFor(WireMock.get(urlMatching("/resources/Product.*"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockRevelData.MOCK_PRODUCTS)))));
        
        stubFor(WireMock.get(urlMatching("/resources/Customer.*"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockRevelData.MOCK_CUSTOMERS)))));
        
        stubFor(WireMock.get(urlMatching("/resources/Order\\?.*"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockRevelData.MOCK_ORDERS)))));

        stubFor(WireMock.get(urlMatching("/resources/OrderItem\\?.*"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockRevelData.MOCK_ORDER_ITEMS)))));
    }
    
    /**
     * Test case: 
     *  - starts a mock Revel server
     *  - registers two new establishments
     *  - activates one of them
     *  - extracts data from them into the staging db
     *  - asserts the number of the records inserted into the staging db
     *  - launches the loader job which moves the records from the staging db to the legacy db
     *  - asserts the number of the records inserted into the legacy db
     * @throws Exception 
     */
    @Test
    public void testExecution() throws Exception {
        
        // Step 1.
        //
        // Register new store
        // 
        mockMvc.perform(post(RevelStoreController.CONTROLLER_PATH)
                .param("username", "sonrisa-user")
                .param("apikey", "key")
                .param("apisecret", "secret")
                .param("division", "true"));
        
        assertEquals("Two stores should've been created for Revel", 2, storeDao.findAll().size());
        
        // Activate the first store
        StoreEntity store = storeDao.findAll().get(0);
        store.setActive(Boolean.TRUE);
        storeService.save(store);
        
        
        // Step 2.
        //
        // Start extraction and move entities into staging
        //        
        final JobExecution extractionResult = launchJob(revelExtratorJobUtil);
        
        // we expect that 1 store has been fetched because 1 active store exists
        final int numOfExtractedStores = extractionResult.getExecutionContext()
                                            .getInt(ExtractorLauncherWriter.NUM_OF_STORES_EXTRACTED);
        assertEquals(1, numOfExtractedStores);
        
        // assert staging counts
        Map<String, Integer> correctCount = MockRevelData.getCountInMockData();
        assertEquals((int) correctCount.get("CategoryDTO"), categoryStgService.findAllIds().size());
        assertEquals((int) correctCount.get("CustomerDTO"), customerStgService.findAllIds().size());
        assertEquals((int) correctCount.get("InvoiceDTO"), invoiceStgService.findAllIds().size());
        assertEquals((int) correctCount.get("InvoiceLineDTO"), invoiceLineStgService.findAllIds().size());
        assertEquals((int) correctCount.get("ProductDTO"), productStgService.findAllIds().size());                
        
        // Step 3.
        //
        // Launch the loader job and move entities in to legacy tables
        // 
        launchJob(loaderJobUtil);
        
        // Assert the legacy DB
        assertEquals(0, categoryStgService.findAllIds().size());
        assertEquals(0, customerStgService.findAllIds().size());
        assertEquals(0, invoiceStgService.findAllIds().size());

        List<String> legacyInvoiceId = new ArrayList<String>();
        for(InvoiceLineStage line : invoiceLineStgService.findByIds(invoiceLineStgService.findAllIds())){
            legacyInvoiceId.add(line.getLsInvoiceId());
        }
        
        assertEquals("Few invoice lines have remained in stage for invoices: " + StringUtils.join(legacyInvoiceId.toArray(),","),
                     0, invoiceLineStgService.findAllIds().size());
        
        assertEquals(0, productStgService.findAllIds().size());
        assertEquals((int) correctCount.get("CategoryDTO"), categoryDao.findAll().size());
        assertEquals((int) correctCount.get("CustomerDTO"), customerDao.findAll().size());
        assertEquals((int) correctCount.get("InvoiceDTO"), invoiceDao.findAll().size());
        assertEquals((int) correctCount.get("InvoiceLineDTO"), invoiceLineDao.findAll().size());
        assertEquals((int) correctCount.get("ProductDTO"), productDao.findAll().size());
        
        // Assert timezone was used
        JsonNode invoice = MockDataUtil.getResourceAsJson(MockRevelData.MOCK_ORDERS).get("objects").get(0);
        final Long lsInvoiceId = invoice.get("id").asLong();
        final String dateOnInvoice = invoice.get("created_date").asText();
        
        assertCreatedDateWasAdjustedToPST(dateOnInvoice, invoiceDao.findByStoreAndForeignId(store.getId(), lsInvoiceId));
    }
    
    /**
     * Asserts that invoice's timestamp was adjusted with timezone difference
     */
    private static void assertCreatedDateWasAdjustedToPST(String actualString, InvoiceEntity invoice){
        // 1. Retail Pro 8
        // For this invoice: "-2854630826819796213"
        // The literal RP date was CreatedDate":"7/27/2014 7:00:46 PM
        // 
        // Value in DB is: 2014-07-28 02:00:46
        // Citadel outlets timezone is 'US Pacific'
        // So, time difference between the native string and the DB value is: <strong>7 hours</strong>
        //
        // 2. Revel
        // Revel establishment's timezone is US Pacific, 
        // timestamp in the JSON is: 2013-03-08T15:28:25
        // so we expect: 2013-03-08T22:28:25 UTC
        
        SimpleDateFormat pstDateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        pstDateformat.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
        
        assertEquals("Value not modified correctly: " + actualString + " for " + invoice.getLsInvoiceId(),
                actualString, 
                pstDateformat.format(invoice.getTs()));
    }
}

