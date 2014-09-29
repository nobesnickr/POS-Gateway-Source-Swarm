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

package com.sonrisa.swarm.erply;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.erply.MockErplyData;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.exception.ExternalApiException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;

/**
 * Test class for the ErplyAPI class
 */
public class ErplyAPITest {

    /** 
     * The ErplyAPI communication with the remote server 
     */
	private ErplyAPI api;
	
	/**
	 * Mock sessionKey "received" from the web service
	 */
	public static String sessionKey = "qwertyuiopasdfghjk";
	
	/**
	 * @see http://wiremock.org/getting-started.html
	 */
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089); 
	
	/** 
	 * The store 
	 */
	private ErplyAccount erplyAccount;
	
	/** 
	 * The response text for a verifyUser request 
	 */
	private String verifyJsonResponse;
	
	/** 
	 * The response text for a getSalesDocuments request 
	 */
	private String salesDocumentsJsonResponse;
		
	/**
	 * Initial setup sets up the account, passes the authentication information
	 * for the ErplyAPI singleton, and reads the json files for the mock responses
	 */
	@Before
	public void setUp(){
	    this.api = new ErplyAPI();
	    
		erplyAccount = new ErplyAccount();
		erplyAccount.setClientCode(MockErplyData.CLIENT_CODE);
		erplyAccount.setUsername(MockErplyData.USERNAME);
		erplyAccount.setPassword(MockErplyData.PASSWORD);
		
		verifyJsonResponse = MockDataUtil.getResourceAsString(MockErplyData.MOCK_ERPLY_VERIFY_USER);
		salesDocumentsJsonResponse = MockDataUtil.getResourceAsString(MockErplyData.MOCK_ERPLY_SALES_DOCUMENTS);
	}
	
	
	/**
	 * Simulates that the Swarm system sends a basic (verifyUser) message
	 * to the remote web service
	 */
	@Test
	public void testUserVerify() throws ExternalExtractorException {
		stubFor(post(urlEqualTo("/api/"))
				.withRequestBody(WireMock.containing("request=verifyUser"))
				.willReturn(aResponse().withBody(verifyJsonResponse)));
		
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("username",MockErplyData.USERNAME);
		params.put("password",MockErplyData.PASSWORD);
        api.sendRequest(new ExternalCommand<ErplyAccount>(erplyAccount,"verifyUser",params));
		
		// Verify that all the necessary authentication information
		// was included in the request
		verify(postRequestedFor(urlMatching("/api/"))
				.withRequestBody(WireMock.containing("request=verifyUser"))
				.withRequestBody(WireMock.containing("clientCode=" + MockErplyData.CLIENT_CODE))
				.withRequestBody(WireMock.containing("username=" + MockErplyData.USERNAME))
				.withRequestBody(WireMock.containing("password=" + MockErplyData.PASSWORD))
				.withRequestBody(WireMock.containing("version=" + ErplyAPI.ERPLY_API_VERSION)));
	}
	
	/**
	 * Test verifies that when the API receives a requests, it requests a sessionKey
	 * using the verifyUser method, and than uses that session key for the further requests
	 */
	@Test
	public void testSessionKey() throws ExternalExtractorException{
		stubFor(post(urlEqualTo("/api/"))
				.withRequestBody(WireMock.containing("request=verifyUser"))
				.willReturn(aResponse().withBody(verifyJsonResponse)));
		
		stubFor(post(urlEqualTo("/api/"))
				.withRequestBody(WireMock.containing("request=getSalesDocuments"))
				.willReturn(aResponse().withBody(salesDocumentsJsonResponse)));
		
        api.sendRequest(new ExternalCommand<ErplyAccount>(erplyAccount,"getSalesDocuments"));
		
		//Session key was sent to the server!
		verify(postRequestedFor(urlMatching("/api/"))
				.withRequestBody(WireMock.containing("request=getSalesDocuments"))
				.withRequestBody(WireMock.containing("clientCode=" + MockErplyData.CLIENT_CODE))
				.withRequestBody(WireMock.containing("sessionKey=" + sessionKey))
	            .withHeader("Content-Type", notMatching("application/json")));
	}
	
	/**
	 * Test that ExternalExtractorException is generated upon wrong response
	 */
	@Test(expected = ExternalExtractorException.class)
	public void testExceptionThrown() throws ExternalExtractorException{
		stubFor(post(urlEqualTo("/api/"))
				.withRequestBody(WireMock.containing("request=verifyUser"))
				.willReturn(aResponse().withBody(verifyJsonResponse)));
		
		stubFor(post(urlEqualTo("/api/"))
				.withRequestBody(WireMock.containing("request=getSalesDocuments"))
				.willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockErplyData.MOCK_ERPLY_SALES_DOCUMENTS_ERROR))));
		
		api.sendRequest(new ExternalCommand<ErplyAccount>(erplyAccount,"getSalesDocuments"));
	}
	
   /**
     * Test that excecption has human readable error massage
     */
    @Test
    public void testTranslatedErrorMessages() throws ExternalExtractorException{
        stubFor(post(urlEqualTo("/api/"))
                .withRequestBody(WireMock.containing("request=verifyUser"))
                .willReturn(aResponse().withBody(verifyJsonResponse)));
        
        stubFor(post(urlEqualTo("/api/"))
                .withRequestBody(WireMock.containing("request=getSalesDocuments"))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockErplyData.MOCK_ERPLY_INVALID_RESOURCE))));
        
        try {
            api.sendRequest(new ExternalCommand<ErplyAccount>(erplyAccount,"getSalesDocuments"));
            fail("Should've failed with ExternalApiException");
        } catch (ExternalApiException exception){
            assertTrue(exception.getUserFriendlyError().contains("API call (input parameter 'request') not specified, or unknown API call"));
        }
    }
    
    
	/**
     * Test execution with live Erply server
     */
    @Test
    @Ignore
    public void testExecute() throws Exception {
     
        erplyAccount.setClientCode("211690");
        erplyAccount.setUsername("swarm");
        erplyAccount.setPassword("Swarm1234");

        //long lastInvoice = ISO8061DateTimeConverter.stringToDate("2014-07-07").getTime() / 1000;
        
        Map<String,String> params = new HashMap<String,String>();
        //params.put("changedSince", Long.toString(lastInvoice));
        params.put("recordsOnPage", "4");
        params.put("dateFrom", "2014-01-27");
        params.put("getRowsForAllInvoices", "1");
        
        api.setApiBaseUrl("https://%s.erply.com/api/");
        
        // Act
        ExternalResponse response = api.sendRequest(new ExternalCommand<ErplyAccount>(erplyAccount,"getSalesDocuments",params));
        System.out.println(response.getContent());
    }
}
