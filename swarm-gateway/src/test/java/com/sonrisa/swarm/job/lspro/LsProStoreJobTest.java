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

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonrisa.swarm.BaseBatchTest;
import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.lspro.MockLsProData;
import com.sonrisa.swarm.model.legacy.StoreEntity;


/**
 * Class testing the {@link LsProStoreProcessor}
 * 
 * @author Barnabas
 */
public class LsProStoreJobTest extends BaseBatchTest {
    /**
     * HTTP mocking utility
     */
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(5555);

    @Autowired
    @Qualifier("lsProStoreJobTest")
    private JobLauncherTestUtils lsProStoreJobUtil;
    
    @Value("${api.name.lspro}")
    private String lsProApiName;
    
    /**
     * Service for accessing the <code>stores</code> table
     */
    @Autowired
    public StoreDao storeDao;
    
    /**
     * Setup HTTP mocking
     */
    @Before
    public void setupHttpMocking(){
        // Matching any query to invoices for any LocationFilter
        stubFor(get(urlMatching("/Invoices\\?.*?\\$filter=\\(LocationName\\).*?"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockLsProData.MOCK_LSPRO_EMPTY_INVOICES)))));
        
        // Matching any query to invoices with empty filter
        stubFor(get(urlMatching("/Invoices\\?.*?\\$filter=\\&.*?"))
                .willReturn(aResponse().withBody((MockDataUtil.getResourceAsString(MockLsProData.MOCK_LSPRO_SAMPLE)))));

    }
    
    /**
     * Test case:
     *  Store exists in <code>stores</code> table with no location.
     *  
     * Expected:
     *  Store job finds an invoice with a location and creates a new store entity.
     */
    @Test
    public void testScanningForStores(){
        
        Long lsProApiId = apiService.findByName(lsProApiName).getApiId();
        
        StoreEntity existingStore = new StoreEntity();
        existingStore.setActive(Boolean.TRUE);
        existingStore.setApiId(lsProApiId);
        existingStore.setUsername(aesUtility.aesEncryptToBytes("sonrisa"));
        existingStore.setPassword(aesUtility.aesEncryptToBytes("abcabc"));
        
        // No location
        existingStore.setStoreFilter(null);
        
        storeService.save(existingStore);
        
        // Act
        launchJob(lsProStoreJobUtil);
        
        // Assert
        List<StoreEntity> stores = storeDao.findAll();
        assertEquals(2, stores.size());
    }
}
