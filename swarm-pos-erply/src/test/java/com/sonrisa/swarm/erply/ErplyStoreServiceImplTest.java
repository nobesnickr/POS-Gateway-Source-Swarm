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
package com.sonrisa.swarm.erply;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.swarm.erply.service.exception.ErplyStoreServiceException;
import com.sonrisa.swarm.erply.service.impl.ErplyStoreServiceImpl;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.erply.MockErplyData;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.service.exception.StoreScanningException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;
import com.sonrisa.swarm.test.service.store.BaseStoreServiceTest;

/**
 * Unit tests for the {@link ErplyStoreServiceImpl} class.
 */
public class ErplyStoreServiceImplTest extends BaseStoreServiceTest<ErplyAccount> {

    /**
     * Name of the Erply API
     */
    private static final String ERPLY_API_NAME = "erply";

    /**
     * API id of Erply
     */
    private static final Long ERPLY_API_ID = 8L;

    /**
     * Target being tested
     */
    private ErplyStoreServiceImpl target;

    /**
     * Mock Erply API
     */
    @Mock
    protected ExternalAPI<ErplyAccount> mockApi;

    /**
     * Sets up target
     * @throws ExternalExtractorException 
     */
    @Before
    public void setupTarget() throws ExternalExtractorException {
        // Setup StoreService
        setupApiService(ERPLY_API_NAME, ERPLY_API_ID);

        target = new ErplyStoreServiceImpl();
        target.setAesUtility(aesUtility);
        target.setStoreService(mockStoreService);
        target.setApiService(mockApiService);
        target.setErplyApiName(ERPLY_API_NAME);

        // Setup API
        when(mockApi.sendRequest(argThat(new ExternalCommandMatcher<ErplyAccount>("getConfParameters")))).thenReturn(
                MockDataUtil.getResourceAsExternalResponse(MockErplyData.MOCK_ERPLY_CONF_PARAMETERS));

        when(mockApi.sendRequest(argThat(new ExternalCommandMatcher<ErplyAccount>("getCompanyInfo")))).thenReturn(
                MockDataUtil.getResourceAsExternalResponse(MockErplyData.MOCK_ERPLY_COMPANY_INFO));

        target.setApiReader(new ErplyAPIReader(mockApi));
    }

    /**
     * Test case:
     *  Saving Erply stores with userName, password and client code
     *  
     * Expected:
     *  StoreEntity returned has its field filled out as expected
     * @throws ErplyStoreServiceException 
     */
    @Test
    public void testCommonStoreFields() throws StoreScanningException, ErplyStoreServiceException {

        final String userName = "sonrisa";
        final String password = "password";
        final String clientCode = "123456";

        // Act
        ErplyAccount dummyAccount = target.getAccount(clientCode, userName, password);
        StoreEntity store = target.getStore(dummyAccount);

        // Assert
        JsonNode companyJson = MockDataUtil.getResourceAsJson(MockErplyData.MOCK_ERPLY_COMPANY_INFO).get("records")
                .get(0);

        assertEquals(ERPLY_API_ID, store.getApiId());
        assertEquals(clientCode, aesUtility.aesDecrypt(store.getUsername()));
        assertEquals(userName, aesUtility.aesDecrypt(store.getApiKey()));
        assertEquals(password, aesUtility.aesDecrypt(store.getPassword()));
        assertEquals(companyJson.get("name").asText(), store.getName());
        assertEquals("America/New_York", store.getTimeZone());
        assertEquals(Boolean.FALSE, store.getActive());
    }
}
