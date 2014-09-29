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
package com.sonrisa.swarm.job.erply;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.sonrisa.swarm.BaseExtractionIntegrationTest;
import com.sonrisa.swarm.erply.controller.ErplyStoreController;
import com.sonrisa.swarm.erply.controller.dto.ErplyAccountDTO;
import com.sonrisa.swarm.job.ExtractorLauncherWriter;
import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.erply.MockErplyData;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;

/**
 * Test cases for the whole Erply extraction process.
 *
 * @author Barna
 */
public class ErplyExtractorFullTest extends BaseExtractionIntegrationTest {
    
    @Autowired
    @Qualifier("erplyExtractorLauncherTest")
    private JobLauncherTestUtils erplyExtratorJobUtil;
    @Autowired
    @Qualifier("loaderJobTestUtil")
    private JobLauncherTestUtils loaderJobUtil;

    @Value("${api.name.erply}")
    private String erplyApiName;
    
    @Autowired
    private StoreDao storeDao;
    
    /**
     * Basic setUp loads standard mock responses for Erply requests
     */
    @Before
    public void setUp() {
        // setup responses of the mock server
        stubFor(post(urlEqualTo("/api/"))
                .withRequestBody(WireMock.containing("request=verifyUser"))
                .withRequestBody(containing("username=" + MockErplyData.USERNAME))
                .withRequestBody(containing("password=" + MockErplyData.PASSWORD))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockErplyData.MOCK_ERPLY_VERIFY_USER))));
        
        stubFor(post(urlEqualTo("/api/"))
                .withRequestBody(WireMock.containing("request=getSalesDocuments"))
                .withRequestBody(WireMock.containing("getRowsForAllInvoices=1"))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockErplyData.MOCK_ERPLY_SALES_DOCUMENTS))));
        
        stubFor(post(urlEqualTo("/api/"))
                .withRequestBody(WireMock.containing("request=getCustomers"))
                .withRequestBody(WireMock.containing("responseMode=detail"))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockErplyData.MOCK_ERPLY_CUSTOMERS))));
        
        // products are returned on the pages, as they exceed
        // the recordsOnPage limit of the external service
        stubFor(post(urlEqualTo("/api/"))
                .withRequestBody(WireMock.containing("request=getProducts"))
                .withRequestBody(WireMock.containing("pageNo=1"))
                .withRequestBody(WireMock.containing("recordsOnPage=100"))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockErplyData.MOCK_ERPLY_PRODUCTS_PAGE_1))));
        
        stubFor(post(urlEqualTo("/api/"))
                .withRequestBody(WireMock.containing("request=getProducts"))
                .withRequestBody(WireMock.containing("pageNo=2"))
                .withRequestBody(WireMock.containing("recordsOnPage=100"))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockErplyData.MOCK_ERPLY_PRODUCTS_PAGE_2))));
        
        stubFor(post(urlEqualTo("/api/"))
                .withRequestBody(WireMock.containing("request=getProductCategories"))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockErplyData.MOCK_ERPLY_CATEGORIES))));

        
        stubFor(post(urlEqualTo("/api/"))
                .withRequestBody(containing("getConfParameters"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockErplyData.MOCK_ERPLY_CONF_PARAMETERS)))));
        
        stubFor(post(urlEqualTo("/api/"))
                .withRequestBody(containing("getCompanyInfo"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockErplyData.MOCK_ERPLY_COMPANY_INFO)))));
    }

    /**
     * Test case: 
     *  - register new Erply store
     *  - starts a mock Erply server
     *  - extracts data from them into the staging db
     *  - asserts the number of the records inserted into the staging db
     *  - launches the loader job which moves the records from the staging db to the legacy db
     *  - asserts the number of the records inserted into the legacy db
     */
    @Test
    public void testExecution() throws Exception {     
        
        // Register new Erply store
        ErplyAccountDTO account = new ErplyAccountDTO();
        account.setUsername(MockErplyData.USERNAME);
        account.setPassword(MockErplyData.PASSWORD);
        account.setClientCode(MockErplyData.CLIENT_CODE);
        
        mockMvc.perform(put(ErplyStoreController.CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isOk());
        
        assertEquals("No store was created for Erply", 1, storeDao.findAll().size());

        // Activate the store
        StoreEntity store = storeDao.findAll().get(0);
        store.setActive(Boolean.TRUE);
        storeService.save(store);
        
        final JobExecution extractionResult = launchJob(erplyExtratorJobUtil);
        
        // we expect that 1 store has been fetched because 1 active store exists
        final int numOfExtractedStores = extractionResult.getExecutionContext()
                                            .getInt(ExtractorLauncherWriter.NUM_OF_STORES_EXTRACTED);
        assertEquals(1, numOfExtractedStores);
        
        // assert staging counts
        assertStagingCount(MockErplyData.getCountInMockData());

        // launch the loader job
        launchJob(loaderJobUtil);
        
        // assert the legacy db
        assertNonDummyLegacyCount(MockErplyData.getCountInMockData());
        assertStagingIsEmpty();
    }
}
