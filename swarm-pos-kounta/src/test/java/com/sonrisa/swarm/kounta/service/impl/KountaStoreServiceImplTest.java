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
package com.sonrisa.swarm.kounta.service.impl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.sonrisa.swarm.kounta.KountaAPI;
import com.sonrisa.swarm.kounta.KountaAccount;
import com.sonrisa.swarm.kounta.KountaUriBuilder;
import com.sonrisa.swarm.kounta.api.util.KountaAPIReader;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.kounta.MockKountaData;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.service.exception.StoreScanningException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;
import com.sonrisa.swarm.test.service.store.BaseStoreServiceTest;

/**
 * Class testing the {@link KountaStoreServiceImpl}
 * 
 * @author Barnabas
 */
public class KountaStoreServiceImplTest extends BaseStoreServiceTest<KountaAccount> {
    
    /**
     * Mocked API
     */
    @Mock
    private KountaAPI mockApi;
    
    /**
     * Target being tested
     */
    private KountaStoreServiceImpl target;
    
    /**
     * API name for Kounta
     */
    private static final String API_NAME = "kounta_pos";
    
    /**
     * API ID for Kounta
     */
    private static final Long API_ID = 10L;
    
    /**
     * Temporary OAuth code
     */
    private final String CODE = "sonrisaSONRISAsonrisa";
    
    /**
     * Mock account containing a refresh token (as API with responsd from {@link KountaAPI#getAccountForTemporaryToken(String)}
     */
    private KountaAccount account;
    
    /**
     * Sets up target
     * @throws ExternalExtractorException 
     */
    @Before
    public void setupTarget() throws ExternalExtractorException{
        setupApiService(API_NAME, API_ID);

        account = new KountaAccount(0L);
        account.setOauthRefreshToken("abc");
        
        // Exchanging token
        when(mockApi.getAccountForTemporaryToken(eq(CODE)))
            .thenReturn(account);
        
        // Fetching company
        when(mockApi.sendRequest(argThat(new ExternalCommandMatcher<KountaAccount>(KountaUriBuilder.COMPANY_INFO_URI).andAccount(account))))
            .thenReturn(MockDataUtil.getResourceAsExternalResponse(MockKountaData.MOCK_KOUNTA_COMPANY));
        
        // Fetching sites
        when(mockApi.sendRequest(
                argThat(new ExternalCommandMatcher<KountaAccount>(KountaUriBuilder.getCompanyUri("1151", "sites.json"))
                        .andAccount(account))))
            .thenReturn(new ExternalResponse(
                    MockDataUtil.getResourceAsExternalJson(MockKountaData.MOCK_KOUNTA_SITES),
                    new HashMap<String,String>(){{
                        put("X-Pages", "1");
                    }}));
        
        target = new KountaStoreServiceImpl();
        target.setStoreService(mockStoreService);
        target.setApiService(mockApiService);
        target.setAesUtility(aesUtility);
        target.setApi(mockApi);
        target.setApiReader(new KountaAPIReader(mockApi));
        target.setApiName(API_NAME);
    }

    /**
     * Test case:
     *  Saving Kounta stores using temporary OAuth code
     *  
     * Expected:
     *  Token service is used to convert temporary token to
     *  permanent.
     *  
     *  <i>Company</i> REST service is used to access to access
     *  company id and store name.
     */
    @Test
    public void testCommonStoreFields() throws Exception {
        
        // Act
        KountaAccount result = target.createAccountFromTemporaryToken(CODE);
        
        // Assert
        assertEquals("1151", result.getCompany());
        assertEquals("Swarm-Mobile", result.getStoreName());        
        assertNotNull(result.getOauthRefreshToken());
    }
    
    /**
     * Test case:
     *  Given a company and an access token, we can start create sites for the company
     *  
     * Expected:
     *  * Sites are fetched from <code>companies/.../sites.json
     *  * Separate {@link StoreEntity} is created for the new one
     */
    @Test
    public void testCreatingStoreEntities() throws StoreScanningException{
        
        final String companyId = "1151";
        final String newLocation = "1410";
        
        account.setCompany(companyId);
        account.setStoreName("Sonrisa");
        
        // Act
        List<StoreEntity> result = target.scanForLocations(account);
        
        assertEquals(2, result.size());
        
        assertNull("New entity should be created", result.get(0).getId());
        assertEquals(API_ID, result.get(0).getApiId());
        assertEquals(account.getCompany(), aesUtility.aesDecrypt(result.get(0).getUsername()));
        assertEquals(newLocation, result.get(0).getStoreFilter());
        assertEquals(account.getStoreName() + " - First", result.get(0).getName());
        assertEquals("New entities should be created as inactive", Boolean.FALSE, result.get(0).getActive());
        assertEquals(account.getOauthRefreshToken(), aesUtility.aesDecrypt(result.get(0).getOauthToken()));
    }
    

    /**
     * Test case:
     *  Given a company and an access token, we can start create sites for the company
     *  
     * Expected:
     *  * Sites are fetched from <code>companies/.../sites.json
     *  * Already existing entities are found and updated
     */
    @Test
    public void testUpdatingStoreEntities () throws StoreScanningException{

        final String companyId = "1151";
        final String existingLocation = "1410";
        
        StoreEntity existingEntity = setupSingleActiveExistingStore(API_ID, companyId, null, existingLocation);

        account.setCompany(companyId);
        account.setStoreName("Sonrisa");
        
        // Act
        List<StoreEntity> result = target.scanForLocations(account);
        
        assertEquals(2, result.size());
        
        assertEquals(existingEntity.getId(), result.get(0).getId());
        assertEquals(API_ID, result.get(0).getApiId());
        assertEquals(account.getCompany(), aesUtility.aesDecrypt(result.get(0).getUsername()));
        assertEquals(existingLocation, result.get(0).getStoreFilter());
        assertEquals(existingEntity.getName(), result.get(0).getName());
        assertEquals(Boolean.TRUE, result.get(0).getActive());
        assertEquals(account.getOauthRefreshToken(), aesUtility.aesDecrypt(result.get(1).getOauthToken()));
    }
}
