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
package com.sonrisa.swarm.revel;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.util.BaseRestAPI;
import com.sonrisa.swarm.posintegration.exception.ExternalApiException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.revel.service.RevelStoreService;

/**
 * Test the {@link RevelAPI} class.
 * @author sonrisa
 *
 */
public class RevelAPITest {
    
    private static final int API_PORT = 17777;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(API_PORT); 
    
    /** RevelAccount used by the API */
    private RevelAccount account;
    
    /**
     * Target being tested
     */
    private RevelAPI target;
    
    private static final String API_KEY = "APIKEY";
    private static final String API_SECRET = "APISECRET";
    private static final String USER_NAME = "testswarmmobile";
    
    /**
     * Setup target
     */
    @Before
    public void setupTarget(){
        
        account = new RevelAccount();
        account.setApiKey(API_KEY);
        account.setApiSecret(API_SECRET);
        account.setUsername(USER_NAME);
        
        target = new RevelAPI();
        target.setApiUrlBase("http://localhost:" + API_PORT + "/resources/");
    }
    
    /**
     * Testing that when requesting an URI from JSON
     * the API call follows the convention of the remote system 
     * 
     * @throws ExternalExtractorException
     */
    @Test
    public void testApiCallFormat() throws ExternalExtractorException{
        stubFor(WireMock.get(urlMatching("/resources/Order"))
                .willReturn(aResponse().withBody("true")));
        
        target.sendRequest(new ExternalCommand<RevelAccount>(account, "Order"));
        
        verify(getRequestedFor(urlMatching("/resources/Order"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("API-AUTHENTICATION", equalTo(API_KEY + ":" + API_SECRET)));
    }

    
    /**
     * Test case: 
     *  Executing request to Revel establishments, but
     *  API key is invalid and server responds:
     *  Revel says 401 - Unauthorized
     *  
     * Expected:
     *  Thrown exception has user-friendly error message:
     *  <i>Wrong API key or API secret, Revel says</i>
     */
    @Test
    public void testInvalidApiKey() throws Exception {

        stubFor(get(urlMatching("/.*?"))
                .willReturn(aResponse().withStatus(401)));
        
        // Act
        try {
            target.sendRequest(new ExternalCommand<RevelAccount>(account, RevelStoreService.ESTABLISHMENT_URI));
        } catch (ExternalApiException exception){
            assertEquals("Wrong API key or API secret, Revel says: Unauthorized", exception.getUserFriendlyError());        
        }
    }
    
    /**
     * Test case: 
     *  The RevelAPI is attempting 
     *  to execute a request to a host which doesn't exists.
     *  
     * Expected:
     *  {@link ExternalApiException} is thrown
     * 
     */
    @Test(expected = ExternalApiException.class)
    public void testExecutingRequestToUnknownHost() throws Exception {
        
        RevelAPI modifiedRevelApi = new RevelAPI(){
            /**
             * Modify the BaseRestAPI to use an HTTP client which throws an UnknownHostException,
			 * instead of Apache's DefaultHttpClient
             */
            protected ExternalResponse executeRequest(HttpUriRequest request) throws ExternalExtractorException {
                HttpClient badBehavingClient = mock(HttpClient.class);
                
                try {
                    when(badBehavingClient.execute(any(HttpUriRequest.class)))
                        .thenThrow(UnknownHostException.class);
                } catch (IOException e){
                    throw new RuntimeException(e);
                }
                
                return executeRequest(badBehavingClient, request, null);
            }
        };
        modifiedRevelApi.setApiUrlBase("http://google.com/");
        
        // Act
        modifiedRevelApi.sendRequest(new ExternalCommand<RevelAccount>(account, RevelStoreService.ESTABLISHMENT_URI));
    }
    
    /**
     * Testing with live Revel webservice
     * @throws ExternalExtractorException 
     */
    @Test
    @Ignore
    public void testExecute() throws ExternalExtractorException{

        account = new RevelAccount();
        account.setApiKey("77d2c15fd3ed4e1091523aa50e70562c");
        account.setApiSecret("4dee32f63f7245319606eb10b761328d883f4836caa6417d9722980742d899f9");
        account.setUsername("testswarmmobile");
        
        RevelAPI api = new RevelAPI();
        api.setApiUrlBase("https://%s.revelup.com/");
        
        ExternalResponse response = api.sendRequest(new ExternalCommand<RevelAccount>(account, "resources/Order"));
        System.out.println(response.getContent());
    }
}
