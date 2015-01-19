package com.sonrisa.swarm.vend;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.vend.MockVendData;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;

/**
 * Test class for {@link VendAPI}.
 */
public class VendAPITest {

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
    private static final String VALID_OAUTH_TOKEN = "urViBlVGS5Wt79mXATEDMyyTgDuGRg4fEOU2PWN9";
    
    /**
     * Access token 
     */
    private VendAccessToken oauthAccessToken;
    
    /**
     * Target being tested
     */
    private VendAPI target;
    
    /**
     * Subject being tested
     */
    private VendAccount account;
    
    
    /**
     * Setup target and account
     */
    @Before
    public void setup(){
        target = new VendAPI();
        target.setApiBaseUrl(String.format("http://localhost:%s/", API_PORT));
        
        oauthAccessToken = new VendAccessToken();
        oauthAccessToken.setAccessToken("TEMPORARY");
        oauthAccessToken.setScope("OAuth");
        
        account = new VendAccount(1L);
        account.setEncryptedOauthRefreshToken(aesUtility.aesEncryptToBytes(OAUTH_REFRESH_TOKEN), aesUtility);
        account.setOauthAccessToken(oauthAccessToken);
    }
        
    /**
     * Test case:
     *  - Account is created and passed to the target, it has refresh and temporary tokens as well
     *  - {@link VendAPI#sendRequest(String, VendAccount)} is executed with an irrelevant URL
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
      target.sendRequest(new ExternalCommand<VendAccount>(account,subQuery));
      
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
        target.sendRequest(new ExternalCommand<VendAccount>(account,subQuery));

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
        VendAccessToken accessToken = new VendAccessToken();
        accessToken.setAccessToken("EXPIRED-TOKEN");
        account.setOauthAccessToken(accessToken);
             
        // Sales data location
        final String subQuery = "/subQuery/of/something/";
        
        stubFor(get(urlMatching(subQuery))
                .withHeader("Authorization", equalTo(account.getOauthAccessToken().getAuthorizationString()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_UNAUTHORIZED)
                        .withBody(MockDataUtil.getResourceAsString(MockVendData.MOCK_VEND_UNAUTHORIZED))));
        

        stubFor(get(urlMatching(subQuery))
                .withHeader("Authorization", equalTo("Bearer " + VALID_OAUTH_TOKEN))
                .willReturn(aResponse().withBody("true")));
        
        // Act
        target.sendRequest(new ExternalCommand<VendAccount>(account,subQuery));
        

        // Assert    
        verify(1, postRequestedFor(urlEqualTo(refreshLocation)));
        verify(2, getRequestedFor(urlEqualTo(subQuery)));
        assertEquals("Access token should be updated", VALID_OAUTH_TOKEN, account.getOauthAccessToken().getToken());
    }
    
    /**
     * Test case:
     *  Sending request to the Vend API, which responds with an item not found error 
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
                        .withBody(MockDataUtil.getResourceAsString(MockVendData.MOCK_VEND_NOT_FOUND))));
        
        // Act
        try {
            target.sendRequest(new ExternalCommand<VendAccount>(account,subQuery));
            fail("Should throw exception");
        } catch (ExternalExtractorException exception){
            assertTrue(exception.getMessage().contains("Not Found"));
        }
        
        // Request should not be retried 
        verify(1, getRequestedFor(urlEqualTo(subQuery)));
    }
    
    /**
     * Test case:
     *  Running test against vend production server
     */
    @Test
    public void testExecute(){
        // Setup account
        VendAccount account = new VendAccount(1L);
        account.setEncryptedOauthRefreshToken(aesUtility.aesEncryptToBytes("SVKnkZcoKeLttZ9XAuev3nvgvSkqrdd5un8ZDK9o"), aesUtility);
        account.setCompany("72");
        account.setSite("72");
        
        // Setup API
        VendAPI api = new VendAPI();
        api.setApiBaseUrl("https://superette.vendhq.com/api/outlets");
        api.setOauthClientId("MYvGY6FP9NOB6K6UJAb1Ek8zQzCcu2RO");
        api.setOauthClientSecret("pWz1QY7Qxz06gxzZ0Pvo6S9kGj6bSKOw");
        api.setTokenRefreshUrl("https://superette.vendhq.com/api/1.0/token");
        
        VendAccessToken accessToken = new VendAccessToken();
        accessToken.setAccessToken("a68ee6e48f8e16551eb4a9c924b28f97986607cc");
        
        try {
            ExternalCommand<VendAccount> command = new ExternalCommand<VendAccount>(account, "");
            ExternalResponse result = api.sendRequest(command);
            System.out.println(result);
            System.out.println(new Date());
        } catch (Exception e){
        	System.out.println("ERROR");
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
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockVendData.MOCK_VEND_ACCESS_TOKEN))));
        
        // Exchanging temporary token
        stubFor(post(urlMatching(refreshLocation))
                .withRequestBody(containing("code="))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockVendData.MOCK_VEND_ACCESS_TOKEN))));
        
        target.setTokenRefreshUrl(String.format("http://localhost:%s%s", API_PORT, refreshLocation));

        // Sales data location
        final String subQuery = "/subQuery/of/something/";
        stubFor(get(urlMatching(subQuery))
                .willReturn(aResponse().withBody("true")));
        
        target.setOauthClientId(clientId);
        target.setOauthClientSecret(clientSecret);
    }
}
