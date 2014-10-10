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
package com.sonrisa.swarm.job.shopify;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import hu.sonrisa.backend.dao.filter.FilterParameter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;

import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.shopify.ShopifyAccount;
import com.sonrisa.shopify.ShopifyExtractor;
import com.sonrisa.swarm.BaseExtractionIntegrationTest;
import com.sonrisa.swarm.job.ExtractorLauncherWriter;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.MockPosDataDescriptor;
import com.sonrisa.swarm.mock.shopify.MockShopifyData;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.service.ExtractorMonitoringService;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.posintegration.warehouse.DWFilter;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;
 
/**
 *  Integration test for the {@link ShopifyExtractor} class.
 * 
 * @author barna
 */
public class ShopifyExtractorFullTest extends BaseExtractionIntegrationTest {
    @Autowired
    @Qualifier("shopifyExtractorLauncherTest")
    private JobLauncherTestUtils shopifyExtratorJobUtil;
    
    @Autowired
    @Qualifier("loaderJobTestUtil")
    private JobLauncherTestUtils loaderJobUtil;
    
    @Autowired
    private SwarmDataWarehouse dataWarehouse;

    @Value("${api.name.shopify}")
    private String shopifyApiName;

    /**
     * Extractor monitoring service
     */
    @Autowired
    private ExtractorMonitoringService extractorMonitoringService;
    
    /**
     * Store id of the active store
     */
    private Long storeId;
    
    /**
     * Initial setup of the mock service
     * @throws ExternalExtractorException 
     */
    @Before
    public void setUp() throws ExternalExtractorException{
        
        stubFor(get(urlMatching("/products.json?.*"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockShopifyData.MOCK_SHOPIFY_PRODUCTS)))));
     
        stubFor(get(urlMatching("/customers.json?.*"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockShopifyData.MOCK_SHOPIFY_CUSTOMERS)))));
        
        stubFor(get(urlMatching("/orders.json?.*"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockShopifyData.MOCK_SHOPIFY_ORDERS)))));
       
        
        storeId = saveMockStoreEntities(shopifyApiName);
    }
    
    /**
     * Test case:
     * Extract some order and check the {@code total} value in db
     * 
     * Expected result:
     * "total_price" used for {@code total}
     */
    @Test
	public void testUsedPrice() {
		launchJob(shopifyExtratorJobUtil);
		InvoiceStage invoice = invoiceStgService.findSingle(new SimpleFilter<InvoiceStage>(InvoiceStage.class, new FilterParameter("lsInvoiceId", "346656160")));
		assertEquals(Double.parseDouble(invoice.getTotal()), 388.93D, 0.01D);
	}
    
    /**
     * Test case: 
     * <ul>
     *  <li>Starts a mock Shopify server</li>
     *  <li>Extracts data from them into the staging DB</li>
     *  <li>Asserts the number of the records inserted into the staging DB</li>
     *  <li>Launches the loader job which moves the records from the staging DB to the legacy DB
     *  <li>Asserts the number of the records inserted into the legacy DB</li>
     *  <li>Asserts the new timestamp filter for invoices</li>
     * </ul>
     */
    @Test
    public void testExecution() {       
        final Date testStart = new Date(); 
        
        final JobExecution extractionResult = launchJob(shopifyExtratorJobUtil);
        
        // we expect that 1 store has been fetched because 1 active store exists
        final int numOfExtractedStores = extractionResult.getExecutionContext()
                                            .getInt(ExtractorLauncherWriter.NUM_OF_STORES_EXTRACTED);
        
        assertEquals(1, numOfExtractedStores);
        
        // assert staging counts
        Map<String, Integer> correctCount = MockShopifyData.getCountOfMockJsonItems();
        assertStagingCount(new MockPosDataDescriptor(correctCount));
        
        // launch the loader job
        launchJob(loaderJobUtil);
        
        // assert the legacy db
        assertNonDummyLegacyCount(new MockPosDataDescriptor(correctCount));
        assertStagingIsEmpty();
        
        final Date monitoringValue = extractorMonitoringService.getLastSuccessfulExecution(storeId);
        assertNotNull("Monitoring value for " + storeId + " is missing ", monitoringValue);
        assertTrue("New monitoring value should've been added for store " + storeId, testStart.before(monitoringValue));
        
        // Assert new time filter
        JsonNode jsonNode = MockDataUtil.getResourceAsJson(MockShopifyData.MOCK_SHOPIFY_ORDERS).get("orders").get(0);
        
        // Actual timestamp
        ShopifyAccount nextAccount = mock(ShopifyAccount.class);
        when(nextAccount.getStoreId()).thenReturn(storeId);
        final DWFilter filter = dataWarehouse.getFilter(nextAccount, InvoiceDTO.class);
        
        // Expected timestamp
        final Date expected = ISO8061DateTimeConverter.stringToDate(jsonNode.get("updated_at").asText());
        
        assertEquals(ISO8061DateTimeConverter.dateToMySqlStringWithTimezone(expected),
                    ISO8061DateTimeConverter.dateToMySqlStringWithTimezone(new Date(filter.getTimestamp().getTime())));
        
    }
}
