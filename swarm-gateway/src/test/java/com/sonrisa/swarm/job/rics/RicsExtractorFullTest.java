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

package com.sonrisa.swarm.job.rics;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

import com.sonrisa.swarm.BaseExtractionIntegrationTest;
import com.sonrisa.swarm.controller.RicsStoreController;
import com.sonrisa.swarm.controller.entity.RicsAccountEntity;
import com.sonrisa.swarm.job.ExtractorLauncherWriter;
import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.rics.MockRicsData;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.rics.constants.RicsUri;

/**
 * Integration test for RICS
 */
public class RicsExtractorFullTest extends BaseExtractionIntegrationTest {
    
    /**
     * DAO for the <code>stores</code> table
     */
    @Autowired
    private StoreDao storeDao;
    
	@Value("${api.name.rics}")
	private String ricsApiName;

	@Autowired
	@Qualifier("loaderJobTestUtil")
	private JobLauncherTestUtils loaderJobUtil;

	@Autowired
	@Qualifier("ricsExtractorLauncherTest")
	private JobLauncherTestUtils ricsExtratorJobUtil;
	
	/**
	 * Account entity sent to the registration REST service
	 */
	private RicsAccountEntity account;

	@Before
	public void setUp() {
	    
	    // Register new store
        account = new RicsAccountEntity();
        account.setLoginName("sonrisa");
        account.setPassword("password");
        account.setSerialNum("88888888");
	    
		/**
		 * REQUEST:
		 * get invoices skip: 0
		 * 
		 * RESPONSE:
		 * a JSON that contains some invoice, invoiceLine and product
		 */
		stubFor(post(urlMatching(RicsUri.INVOICES.uri)).willReturn(
				aResponse().withBody(MockDataUtil.getResourceAsString(MockRicsData.MOCK_INVOICES_ONE_PAGE_RESPONSE)).withStatus(200)));
		/**
		 * Request:
		 * get customers
		 * 
		 * Response:
		 * a JSON with customer data
		 */
		stubFor(post(urlMatching(RicsUri.CUSTOMERS.uri))
		        .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockRicsData.MOCK_CUSTOMERS)).withStatus(200)));

		/**
		 * Request:
		 * authentication data. Called before the extraction begins
		 * 
		 * Response:
		 * token, that used in later queries
		 */
		stubFor(post(urlMatching(RicsUri.LOGIN.uri))
                .withRequestBody(containing(getQuotedJsonLine("SerialNumber", account.getSerialNum())))
                .withRequestBody(containing(getQuotedJsonLine("Login", account.getLoginName())))
                .withRequestBody(containing(getQuotedJsonLine("Password", account.getPassword())))
		        .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockRicsData.MOCK_TOKEN)).withStatus(200)));
	}

	/**
	 * Test case:
	 * mock some invoice, invoiceLine, product and customer data and give it to the extractor
	 * 
	 * Expected:
	 * the correct amount of data appears in stage and legacy tables
	 */
	@Test
	public void testExecution() throws Exception {
        mockMvc.perform(put(RicsStoreController.CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(account))));
        
        List<StoreEntity> stores = storeDao.findAll();
        assertEquals("Store wasn't registered", 1,  stores.size());
        
        // Activate store
        stores.get(0).setActive(Boolean.TRUE);
        storeService.save(stores.get(0));
	    
	    // Run extraction
		final JobExecution extractionResult = launchJob(ricsExtratorJobUtil);

		// we expect that 1 store has been fetched because 1 active store exists
		final int numOfExtractedStores = extractionResult.getExecutionContext()
				.getInt(ExtractorLauncherWriter.NUM_OF_STORES_EXTRACTED);

		// only the active store should be extracted
		assertEquals(1, numOfExtractedStores);

		assertStagingCount(MockRicsData.getExtractionDescriptor(true));

		// load staging content to legacy
		launchJob(loaderJobUtil);

		// assert the legacy
		assertNonDummyLegacyCount(MockRicsData.getExtractionDescriptor(false));
		assertStagingIsEmpty();
	}

	/**
	 * Test case: 
	 *  - The number of active stores exceeds the grid size (5) of the parallel execution
	 *    
	 * Expected:
	 *  - All active stores are extracted
	 */
	@Test
	public void testManyStoresExecution() {
	    
        // Ignore matching credentials
        stubFor(post(urlMatching(RicsUri.LOGIN.uri))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockRicsData.MOCK_TOKEN)).withStatus(200)));

		// Create 21 >> 5 stores and 21 % 5 != 0
		final Long[] storeIds = new Long[21];
		for (int i = 0; i < storeIds.length; i++) {
			storeIds[i] = saveSingleMockStoreEntity(ricsApiName, "store" + i, true);
		}

		final JobExecution extractionResult = launchJob(ricsExtratorJobUtil);

		// we expect that 21 stores have been fetched
		final int numOfExtractedStores = extractionResult.getExecutionContext()
				.getInt(ExtractorLauncherWriter.NUM_OF_STORES_EXTRACTED);

		assertEquals(storeIds.length, numOfExtractedStores);
	}
	
	/**
	 * Returns JSON like value, like this <code>"key":"value"</code> 
	 */
	private static String getQuotedJsonLine(String key, String value){
	    return String.format("\"%s\":\"%s\"", key, value);
	}
}
