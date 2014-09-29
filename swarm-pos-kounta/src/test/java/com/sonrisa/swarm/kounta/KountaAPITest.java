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
package com.sonrisa.swarm.kounta;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.kounta.MockKountaData;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.exception.ExternalApiBadCredentialsException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;

/**
 * Test class for {@link KountaAPI}.
 */
public class KountaAPITest {

    /**
     * Wiremock's port
     */
    private static final int API_PORT = 26543;

    /**
     * HTTP mocking utility
     */
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(API_PORT); 
    
    /**
     * AESUtility used
     */
    private AESUtility aesUtility = new AESUtility();
    
    /**
     * OAuth token set for account
     */
    private static final String OAUTH_REFRESH_TOKEN = "S0NR1SA";
    
    /**
     * OAuth token in the mock refresh JSON
     */
    private static final String VALID_OAUTH_TOKEN = "60c53fb36a88f27f6c582dee126ca1bae3fe434f";
    
    /**
     * Access token 
     */
    private KountaAccessToken oauthAccessToken;
    
    /**
     * Target being tested
     */
    private KountaAPI target;
    
    /**
     * Subject being tested
     */
    private KountaAccount account;

    /**
     * OAuth redirect URI
     */
    private static final String REDIRECT_URI = "http://swarm.sonrisa/";
    
    
    /**
     * Setup target and account
     */
    @Before
    public void setup(){
        target = new KountaAPI();
        target.setApiBaseUrl(String.format("http://localhost:%s/", API_PORT));
        
        oauthAccessToken = new KountaAccessToken();
        oauthAccessToken.setAccessToken("TEMPORARY");
        oauthAccessToken.setScope("OAuth");
        
        account = new KountaAccount(1L);
        account.setEncryptedOauthRefreshToken(aesUtility.aesEncryptToBytes(OAUTH_REFRESH_TOKEN), aesUtility);
        account.setOauthAccessToken(oauthAccessToken);
    }
        
    /**
     * Test case:
     *  - Account is created and passed to the target, it has refresh and temporary tokens as well
     *  - {@link KountaAPI#sendRequest(String, KountaAccount)} is executed with an irrelevant URL
     * 
     * Expected:
     *  - Account's URLBase and queryUrl is concatenated
     *  - OAuth headers are inserted
     *  - JSON is parsed and returned
     * @throws ExternalExtractorException 
     */
    @Test
    public void testBestCaseScenario() throws ExternalExtractorException{
      
      final String subQuery = "/subQuery/of/something/";
      stubFor(WireMock.get(urlMatching(subQuery))
              .willReturn(aResponse().withBody("true")));
      
      // Act
      target.sendRequest(new ExternalCommand<KountaAccount>(account,subQuery));
      
      // Assert    
      verify(getRequestedFor(urlMatching(subQuery))
              .withHeader("Authorization", equalTo(account.getOauthAccessToken().getAuthorizationString()))
              .withHeader("Accept", equalTo("application/json")));
    }
    
    /**
     * Test case:
     *  The account's access token field is empty
     * 
     * Expected:
     *  The API uses the refresh token to access this
     * 
     * @throws ExternalExtractorException
     */
    @Test
    public void testMissingAccessToken() throws ExternalExtractorException {
        final String clientId = "123";
        final String clientSecret = "ABC";
        final String refreshLocation = "/token.json";
        
        // Setup mocking and set target's fields accordingly
        setupTargetToRefreshToken(clientId,clientSecret,refreshLocation);
        
        // No token will force fetching
        account.setOauthAccessToken(null);
             
        // Sales data location
        final String subQuery = "/subQuery/of/something/";
        stubFor(get(urlMatching(subQuery)).willReturn(aResponse().withBody("true")));
        
        // Act
        target.sendRequest(new ExternalCommand<KountaAccount>(account,subQuery));

        // Assert    
        verify(postRequestedFor(urlMatching(refreshLocation))
                .withRequestBody(containing("client_id=" + clientId))
                .withRequestBody(containing("client_secret=" + clientSecret))
                .withRequestBody(containing("grant_type=refresh_token"))
                .withRequestBody(containing("refresh_token=" + account.getOauthRefreshToken())));
        
        assertNotNull("API should update account's OAuth token", account.getOauthAccessToken());
        assertEquals("Bearer", account.getOauthAccessToken().getTokenType());
        assertEquals(VALID_OAUTH_TOKEN, account.getOauthAccessToken().getToken());
    }
    
    /**
     * Test case:
     *  Account uses an expired access token
     *  
     * Expected: 
     *  API updates the access token by sending a request to the token.json
     *  with the refresh token
     * 
     */
    @Test
    public void testRefreshingTokenIfExpires() throws Exception {
        final String clientId = "123";
        final String clientSecret = "ABC";
        final String refreshLocation = "/token.json";
        
        // Setup mocking and set target's fields accordingly
        setupTargetToRefreshToken(clientId,clientSecret,refreshLocation);
        
        // Expired token!
        KountaAccessToken accessToken = new KountaAccessToken();
        accessToken.setAccessToken("EXPIRED-TOKEN");
        account.setOauthAccessToken(accessToken);
             
        // Sales data location
        final String subQuery = "/subQuery/of/something/";
        
        stubFor(get(urlMatching(subQuery))
                .withHeader("Authorization", equalTo(account.getOauthAccessToken().getAuthorizationString()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_UNAUTHORIZED)
                        .withBody(MockDataUtil.getResourceAsString(MockKountaData.MOCK_KOUNTA_UNAUTHORIZED))));
        

        stubFor(get(urlMatching(subQuery))
                .withHeader("Authorization", equalTo("Bearer " + VALID_OAUTH_TOKEN))
                .willReturn(aResponse().withBody("true")));
        
        // Act
        target.sendRequest(new ExternalCommand<KountaAccount>(account,subQuery));
        

        // Assert    
        verify(1, postRequestedFor(urlEqualTo(refreshLocation)));
        verify(2, getRequestedFor(urlEqualTo(subQuery)));
        assertEquals("Access token should be updated", VALID_OAUTH_TOKEN, account.getOauthAccessToken().getToken());
    }
    
    /**
     * Test case:
     *  Sending request to the Kounta API, which responds with an error
     *  
     * Expected:
     *  The returned Exception's message contains valuable information on
     *  the reason of the failure
     */
    @Test
    public void testDescriptiveErrorMessage() throws Exception {

        final String subQuery = "/subQuery/of/something/";
        stubFor(WireMock.get(urlMatching(subQuery))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_NOT_FOUND)
                        .withBody(MockDataUtil.getResourceAsString(MockKountaData.MOCK_KOUNTA_NOT_FOUND))));
        
        // Act
        try {
            target.sendRequest(new ExternalCommand<KountaAccount>(account,subQuery));
            fail("Should throw exception");
        } catch (ExternalExtractorException exception){
            assertTrue(exception.getMessage().contains("Not Found"));
        }
        
        // Request should not be retried 
        verify(1, getRequestedFor(urlEqualTo(subQuery)));
    }
    
    /**
     * Test case:
     *  Setting the <code>X-Page</code> configuration parameter for the KountaAPI.
     *  
     * Expected:
     *  It's added to the header.
     */
    @Test
    public void testKountaXPageHeader() throws ExternalExtractorException{
        
        final Integer page = 808;
        final String total = "1000";

        final String subQuery = "/subQuery/of/something/";
        stubFor(WireMock.get(urlMatching(subQuery))
                .willReturn(aResponse()
                        .withBody("true")
                        .withHeader("X-Pages", total)));
        
        Map<String,String> config = new HashMap<String,String>();
        config.put("X-Page", page.toString());
        
        // Act
        ExternalResponse response = target.sendRequest(new ExternalCommand<KountaAccount>(account,subQuery).withConfig(config));
        
        assertEquals(total, response.getHeaders().get("X-Pages"));
        
        // Assert    
        verify(getRequestedFor(urlMatching(subQuery))
                .withHeader("X-Page", equalTo(page.toString())));
    }
    
    /**
     * Test case:
     *  - Temporary token is used to access refresh token
     * 
     * Expected:
     *  - OAuth 2.0 fields and other parameters are OK
     */
    @Test
    public void testExchangingTemporaryToken() throws ExternalExtractorException, UnsupportedEncodingException {

        final String clientId = "123";
        final String clientSecret = "ABC";
        final String refreshLocation = "/token.json";
        final String code = "XYZ";

        // Setup mocking and set target's fields accordingly
        setupTargetToRefreshToken(clientId, clientSecret, refreshLocation);

        // Act
        KountaAccount result = target.getAccountForTemporaryToken(code);

        // Assert
        verify(postRequestedFor(urlMatching(refreshLocation))
                .withRequestBody(containing("client_id=" + clientId))
                .withRequestBody(containing("client_secret=" + clientSecret))
                .withRequestBody(containing("redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8")))
                .withRequestBody(containing("code=" + code)).withHeader("Accept", equalTo("application/json")));

        final JsonNode refreshMockJson = MockDataUtil.getResourceAsJson(MockKountaData.MOCK_KOUNTA_REFRESH_TOKEN);

        assertEquals(refreshMockJson.get("refresh_token").asText(), result.getOauthRefreshToken());
    }
    
    /**
     * Test case:
     *  Running against production server
     *  
     * Shouldn't be committed without ignore
     */
    @Test
    @Ignore
    public void testExecute(){
        // Setup account
        KountaAccount account = new KountaAccount(1L);
        
        //account.setEncryptedUserName(aesUtility.aesEncryptToBytes("YDsXsQTIyL9V1Lbw"), aesUtility);
        //account.setEncryptedPassword(aesUtility.aesEncryptToBytes("MCaj2Eofk9kfs5D4jJ74ZbHvHY9HNURZF8d2PixT"), aesUtility);
        account.setEncryptedOauthRefreshToken(aesUtility.aesEncryptToBytes("d24f3dd00d86451ca1b49258b5a96da63d7a216a"), aesUtility);
        account.setCompany("1151");
        account.setSite("1410");
        
        // Setup API
        KountaAPI api = new KountaAPI();
        api.setApiBaseUrl("https://api.kounta.com/v1/");
        api.setOauthClientId("YDsXsQTIyL9V1Lbw");
        api.setOauthClientSecret("MCaj2Eofk9kfs5D4jJ74ZbHvHY9HNURZF8d2PixT");
        api.setTokenRefreshUrl("https://api.kounta.com/v1/token.json");
        
        KountaAccessToken accessToken = new KountaAccessToken();
        accessToken.setAccessToken("a68ee6e48f8e16551eb4a9c924b28f97986607cc");
        //account.setOauthAccessToken(accessToken);
        
        try {
            ExternalCommand<KountaAccount> command = new ExternalCommand<KountaAccount>(account, 
                    KountaUriBuilder.getSiteUri(account, "orders/complete.json?created_gte=2014-01-01"));
            ExternalResponse result = api.sendRequest(command);
            System.out.println(result);
            System.out.println(new Date());
        } catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }
    
    /**
     * Setup target to be capable of refreshing its access token
     */
    private void setupTargetToRefreshToken(String clientId, String clientSecret, String refreshLocation){
        
        // Refreshing the token
        stubFor(post(urlMatching(refreshLocation))
                .withRequestBody(containing("refresh_token=" + OAUTH_REFRESH_TOKEN))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockKountaData.MOCK_KOUNTA_ACCESS_TOKEN))));
        
        // Exchanging temporary token
        stubFor(post(urlMatching(refreshLocation))
                .withRequestBody(containing("code="))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockKountaData.MOCK_KOUNTA_REFRESH_TOKEN))));
        
        target.setTokenRefreshUrl(String.format("http://localhost:%s%s", API_PORT, refreshLocation));

        // Sales data location
        final String subQuery = "/subQuery/of/something/";
        stubFor(get(urlMatching(subQuery))
                .willReturn(aResponse().withBody("true")));
        
        target.setOauthClientId(clientId);
        target.setOauthClientSecret(clientSecret);
        target.setOauthRedirectUri(REDIRECT_URI);
    }
}
