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
package com.sonrisa.swarm.mos;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonrisa.swarm.posintegration.exception.ExternalDeniedServiceException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;


/**
 * Test the behavoir of the MosAPI class, it's ability 
 * to send request, authorizing them, etc.
 */
public class MosApiTest {
    
    private static final int API_PORT = 8092;

    /**
     * @see http://wiremock.org/getting-started.html
     */
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(API_PORT); // No-args constructor defaults to port 8080
    
    /**
     * Instance being tested
     */
    private MosAPI mosApi;
    
    /**
     * Utility for encrypting strings
     */
    private AESUtility utility;
    
    /**
     * Account ID used in the Merchant OS URL
     */
    private static final String accountId = "1"; 
    
    /**
     * Mos account
     */
    private MosAccount account;
    
    /**
     * Setup the class's private fields
     */
    @Before
    public void setupContext(){
        
        this.mosApi = new MosAPI();
        
        this.utility = new AESUtility();
        this.utility.setEncryptionKey("NOKEY");
    }
    
    /**
     * Testing that the REST request contains a header
     * with correctly formatted authorization information,
     * which is generated using the Merchant OS account's 
     * information when OAuth token is set
     */
    @Test
    public void testAuthorizationHeaderOauth() throws ExternalExtractorException{
        
        String oauthToken = "0bc21f57f980ae2a332c74c217a55283";
        
        MosAccount account = new MosAccount(456L);
        account.setOauthToken(oauthToken.getBytes());
        account.setAccountId(accountId);
        account.setUrlBase(new String("http://localhost:" + API_PORT + "/API/").getBytes());
        
        stubFor(WireMock.get(urlMatching("/API/Account/[0-9]+/[A-Za-z0-9]+"))
                .willReturn(aResponse().withBody("true")));
        
        mosApi.sendRequest(account, "Item");

        verify(getRequestedFor(urlMatching("/API/Account/" + accountId + "/[A-Za-z0-9]+"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Authorization", equalTo("OAuth " + oauthToken)));
        
    }
    
    /**
     * Testing that the REST request contains a header
     * with correctly formatted authorization information,
     * which is generated using the Merchant OS account's 
     * information when api key is set
     */
    @Test
    public void testAuthorizationHeaderApiKey() throws ExternalExtractorException{
        
        String apiKey = "d66a3aeb5acadc47ba45b059724ef219be8f467eb32735a023d36e7b99d3a727";
        String base64Encoded = new String(Base64.encodeBase64((apiKey + ":apikey").getBytes()));
        
        MosAccount account = new MosAccount(3L);
        account.setApiKey(apiKey.getBytes());
        account.setAccountId(accountId);
        account.setUrlBase(new String("http://localhost:" + API_PORT + "/API/").getBytes());
        
        stubFor(WireMock.get(urlMatching("/API/Account/[0-9]+/[A-Za-z0-9]+"))
                .willReturn(aResponse().withBody("true")));
        
        mosApi.sendRequest(account, "Item");
        
        verify(getRequestedFor(urlMatching("/API/Account/" + accountId + "/[A-Za-z0-9]+"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Authorization", equalTo("Basic " + base64Encoded)));
        
    }
    

    /**
     * Testing that the REST request contains a header
     * with correctly formatted authorization information,
     * which is generated using the Merchant OS account's 
     * information when api key is set, even if this API key
     * is encoded
     * @throws DecoderException 
     */
    @Test
    public void testAuthorizationHeaderEncryptedApiKey() throws ExternalExtractorException, DecoderException{
        final String apiKey = "qwerty123";
        String base64Encoded = new String(Base64.encodeBase64((apiKey + ":apikey").getBytes()));
        
        MosAccount account = new MosAccount(3L);
        account.setEncryptedApiKey(utility.aesEncryptToBytes(apiKey), utility);
        account.setAccountId(accountId);
        account.setUrlBase(new String("http://localhost:" + API_PORT + "/API/").getBytes());
        
        stubFor(WireMock.get(urlMatching("/API/Account/[0-9]+/[A-Za-z0-9]+"))
                .willReturn(aResponse().withBody("true")));
        
        mosApi.sendRequest(account, "Item");
        
        verify(getRequestedFor(urlMatching("/API/Account/" + accountId + "/[A-Za-z0-9]+"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Authorization", equalTo("Basic " + base64Encoded)));
    }
    
    /**
     * Testing that a server side error causes ExternalExtractorException
     */
    @Test(expected = ExternalExtractorException.class)
    public void testInternalErrorThrowsExternalException() throws ExternalExtractorException {
        String apiKey = "d66a3aeb5acadc47ba45b059724ef219be8f467eb32735a023d36e7b99d3a727";
        
        MosAccount account = new MosAccount(3L);
        account.setApiKey(apiKey.getBytes());
        account.setAccountId(accountId);
        account.setUrlBase(new String("http://localhost:" + API_PORT + "/API/").getBytes());
        
        stubFor(WireMock.get(urlMatching("/API/Account/[0-9]+/[A-Za-z0-9]+"))
                .willReturn(aResponse().withBody("false").withStatus(500)));
        
        mosApi.sendRequest(account, "Item");
    }


    /**
     * Testing that a server side error causes ExternalExtractorException
     */
    @Test(expected = ExternalDeniedServiceException.class)
    public void testServiceDeniedThrowsDeniedServiceException () throws ExternalExtractorException {
        String apiKey = "d66a3aeb5acadc47ba45b059724ef219be8f467eb32735a023d36e7b99d3a727";
        
        MosAccount account = new MosAccount(3L);
        account.setApiKey(apiKey.getBytes());
        account.setAccountId(accountId);
        account.setUrlBase(new String("http://localhost:" + API_PORT + "/API/").getBytes());
        
        stubFor(WireMock.get(urlMatching("/API/Account/[0-9]+/[A-Za-z0-9]+"))
                .willReturn(aResponse().withBody("false").withStatus(503)));
        
        mosApi.sendRequest(account, "Item");
    }
}
