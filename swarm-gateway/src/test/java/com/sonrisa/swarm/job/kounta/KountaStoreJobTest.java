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
import com.sonrisa.swarm.kounta.controller.KountaOAuthControllerTest;
import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;

/**
 * Class testing the Spring Batch job which scans for newly created Kounta stores.
 * 
 * @author Barnabas
 */
public class KountaStoreJobTest extends BaseBatchTest {
    
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(5555);

    @Autowired
    @Qualifier("kountaStoreJobTest")
    private JobLauncherTestUtils kountaStoreJobUtil;

    @Value("${api.name.kounta}")
    private String kountaApiName;

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
    }

    /**
     * Test case:
     *  Store exists in <code>stores</code> table with no location.
     *  
     * Expected:
     *  Store job finds an invoice with a location and creates a new store entity.
     */
    @Test
    public void testScanningForSites(){
        
        Long kountaApiId = apiService.findByName(kountaApiName).getApiId();

        StoreEntity existingStore = mockKountaStore(kountaApiId, "1151", "1410");
        storeService.save(existingStore);
                
        // Act
        launchJob(kountaStoreJobUtil);
        
        // Assert
        List<StoreEntity> stores = storeDao.findAll();
        assertEquals("A new location (1411) was supposed to be created", 2, stores.size());
    }
    
    /**
     * Test case:
     *  Two store exists in <code>stores</code> table with the same authentication.
     *  
     * Expected:
     *  The sites are requested only once.
     */
    @Test
    public void testNoCredentailIsReused(){
        
        Long kountaApiId = apiService.findByName(kountaApiName).getApiId();
        
        StoreEntity existingStore = mockKountaStore(kountaApiId, "1151", "1410");
        storeService.save(existingStore);
        
        StoreEntity otherExistingStore = mockKountaStore(kountaApiId, "1151", "1411");
        storeService.save(otherExistingStore);
                
        // Act
        launchJob(kountaStoreJobUtil);
        
        // Assert
        List<StoreEntity> stores = storeDao.findAll();
        assertEquals(2, stores.size());
        verify(1, getRequestedFor(urlEqualTo("/companies/1151/sites.json")));
    }
    
    /**
     * Creates mock Kounta store for the given site
     */
    private StoreEntity mockKountaStore(Long kountaApiId, String company, String site){
        StoreEntity store = new StoreEntity();
        store.setActive(Boolean.TRUE);
        store.setApiId(kountaApiId);
        store.setUsername(aesUtility.aesEncryptToBytes(company));
        store.setPassword(aesUtility.aesEncryptToBytes("abcabc"));
        store.setOauthToken(aesUtility.aesEncryptToBytes("defdef"));
                
        // Not matching company
        store.setStoreFilter(site);
        return store;
    }
}
