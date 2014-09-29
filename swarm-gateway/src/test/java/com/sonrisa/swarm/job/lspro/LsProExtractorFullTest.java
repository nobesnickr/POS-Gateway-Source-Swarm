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

package com.sonrisa.swarm.job.lspro;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.sonrisa.swarm.BaseExtractionIntegrationTest;
import com.sonrisa.swarm.job.ExtractorLauncherWriter;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.lspro.MockLsProData;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;

/**
 * Integration test for the Lightspeed Pro extractor
 */
public class LsProExtractorFullTest extends BaseExtractionIntegrationTest {
    @Autowired
    @Qualifier("lsProExtractorLauncherTest")
    private JobLauncherTestUtils lsProExtratorJobUtil;
    
    @Autowired
    @Qualifier("loaderJobTestUtil")
    private JobLauncherTestUtils loaderJobUtil;

    @Value("${api.name.lspro}")
    private String lsProApiName;

    /**
     * Initial setup of the mock service
     * @throws ExternalExtractorException 
     */
    @Before
    public void setUp() throws ExternalExtractorException{
        
        // Matching any query to invoices containing skip=0
        stubFor(get(urlMatching("/Invoices\\?.*?\\$skip=0.*?"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockLsProData.MOCK_LSPRO_INVOICES_PAGE_1)))));
        
        // Matching any query to invoices containing skip=50
        stubFor(get(urlMatching("/Invoices\\?.*?\\$skip=50.*?"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockLsProData.MOCK_LSPRO_INVOICES_PAGE_2)))));

        stubFor(get(urlMatching("/Customers.*?"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockLsProData.MOCK_LSPRO_CUSTOMERS)))));
        
        stubFor(get(urlMatching("/Products.*?"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockLsProData.MOCK_LSPRO_PRODUCTS)))));
        
        stubFor(get(urlMatching("/LineItems.*?"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockLsProData.MOCK_LSPRO_LINE_ITEMS)))));
    }
    
    /**
     * Test case: 
     *  - starts a mock Lightspeed Pro server
     *  - extracts data from them into the staging db
     *  - asserts the number of the records inserted into the staging db
     *  - launches the loader job which moves the records from the staging db to the legacy db
     *  - asserts the number of the records inserted into the legacy db
     */
    @Test
    public void testExecution() {         
        // Create an active and an inactive store
        saveMockStoreEntities(lsProApiName);
        
        final JobExecution extractionResult = launchJob(lsProExtratorJobUtil);
        
        // we expect that 1 store has been fetched because 1 active store exists
        final int numOfExtractedStores = extractionResult.getExecutionContext()
                                            .getInt(ExtractorLauncherWriter.NUM_OF_STORES_EXTRACTED);
        
        assertEquals(1, numOfExtractedStores);
        
        // assert staging counts
        assertStagingCount(MockLsProData.getLsProMockDescriptor());
        
        // launch the loader job
        launchJob(loaderJobUtil);
        
        // assert the legacy db
        assertNonDummyLegacyCount(MockLsProData.getLsProMockDescriptor());
        assertStagingIsEmpty();
    }
    
    /**
     * Test case: 
     *  - The number of active stores exceeds the grid size (5)
     *    of the parallel execution
     *    
     * Expected:
     *  - All active stores are extracted
     */
    @Test
    public void testManyStoresExecution() {  
    	
    	// Create 21 >> 5 stores and 21 % 5 != 0
    	final Long[] storeIds = new Long[21];
    	for(int i = 0; i < storeIds.length; i++){
    		storeIds[i] = saveSingleMockStoreEntity(lsProApiName, "store" + i, true);
    	}
    	
        final JobExecution extractionResult = launchJob(lsProExtratorJobUtil);
        
        // we expect that 1 store has been fetched because 1 active store exists
        final int numOfExtractedStores = extractionResult.getExecutionContext()
                                            .getInt(ExtractorLauncherWriter.NUM_OF_STORES_EXTRACTED);
        
        assertEquals(storeIds.length, numOfExtractedStores);
    }
}
