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

import com.sonrisa.shopify.ShopifyExtractor;
import com.sonrisa.swarm.BaseExtractionIntegrationTest;
import com.sonrisa.swarm.job.ExtractorLauncherWriter;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.MockPosDataDescriptor;
import com.sonrisa.swarm.mock.shopify.MockShopifyData;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.service.ExtractorMonitoringService;

/**
 * Integration test for the {@link ShopifyExtractor} class.
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
		InvoiceStage invoice = invoiceStgService.findSingle(new SimpleFilter<InvoiceStage>(InvoiceStage.class, new FilterParameter("lsInvoiceId", "208831129")));
		assertEquals(Double.parseDouble(invoice.getTotal()), 745, 0.01d);
	}
    
    /**
     * Test case: 
     *  - starts a mock Shopify server
     *  - extracts data from them into the staging db
     *  - asserts the number of the records inserted into the staging db
     *  - launches the loader job which moves the records from the staging db to the legacy db
     *  - asserts the number of the records inserted into the legacy db
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
    }
}
