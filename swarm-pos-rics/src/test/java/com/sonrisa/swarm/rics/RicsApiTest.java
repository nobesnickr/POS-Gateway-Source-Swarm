/*
  Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.

 This software is the confidential and proprietary information of
 Sonrisa Informatikai Kft. ("Confidential Information").
 You shall not disclose such Confidential Information and shall use it only in
 accordance with the terms of the license agreement you entered into
 with Sonrisa.

 SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package com.sonrisa.swarm.rics;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.rics.MockRicsData;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.exception.ExternalApiException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalJsonDTO;
import com.sonrisa.swarm.rics.api.RicsApi;
import com.sonrisa.swarm.rics.constants.RicsUri;

public class RicsApiTest {
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
	 * account to test with
	 */
	private RicsAccount acc;

	/**
	 * Encrytion tool for login credentials
	 */
	private AESUtility aesUtility;

	/**
	 * API being tested
	 */
	private RicsApi api;

	@Before
	public void setUpTarget() throws Exception {
		aesUtility = new AESUtility();
		api = new RicsApi();

		acc = new RicsAccount(1L);
		acc.setEncryptedSerialNum(aesUtility.aesEncryptToBytes("10446"), aesUtility);
		acc.setEncryptedLoginName(aesUtility.aesEncryptToBytes("APITestUser"), aesUtility);
		acc.setEncryptedPassword(aesUtility.aesEncryptToBytes("R1csT3st!ng"), aesUtility);
		api.setApiBaseUrl(String.format("http://localhost:%d/", API_PORT));

		// Successful response on invoice request
		stubFor(WireMock.post(urlMatching(RicsUri.INVOICES.uri))
				.willReturn(aResponse().withBody(MockDataUtil.getResourceAsJson(MockRicsData.MOCK_INVOICES_ONE_PAGE_RESPONSE).toString())));

		// login service (responds a token)
		stubFor(WireMock.post(urlMatching(RicsUri.LOGIN.uri))
				.willReturn(aResponse().withBody(MockDataUtil.getResourceAsJson(MockRicsData.MOCK_TOKEN).toString())));

		// unsuccessful invocation
		stubFor(WireMock.post(urlMatching("/testUnauthorized"))
				.willReturn(aResponse().withBody(MockDataUtil.getResourceAsJson(MockRicsData.MOCK_UNSUCCESS).toString())));

		// empty result received
		stubFor(WireMock.post(urlMatching("/testEmptyResponse"))
				.willReturn(aResponse().withBody(MockDataUtil.getResourceAsJson(MockRicsData.MOCK_EMPTY_RESPONSE).toString())));
	}

	/**
	 * Test case:
	 * send a request that not contains the required authorization token
	 * 
	 * Expected:
	 * the extractor throws an exception
	 * 
	 * @throws ExternalExtractorException
	 */
	@Test(expected = ExternalApiException.class)
	public void testUnauthorized() throws ExternalExtractorException {
		api.sendRequest(new ExternalCommand<RicsAccount>(acc, "/testUnauthorized", new HashMap<String, String>()));
	}

	/**
	 * Test case:
	 * fire a request for invoices without token. The api must request for one and fire the request after we received it.
	 * 
	 *  Expected:
	 *  at the end of the request, the account contains the token given by mocked LOGIN service
	 * @throws ExternalExtractorException 
	 */
	@Test
	public void testTokenRequest() throws ExternalExtractorException {
		acc.setToken("");
		ExternalDTO response = api.sendRequest(new ExternalCommand<RicsAccount>(acc, RicsUri.INVOICES.uri, new HashMap<String, String>())).getContent();
		assertEquals("DD55F8AF-27AB-41C2-AB38-688B7E87734B", acc.getLastToken());
		assertNotNull(response);
	}

	/**
	 * Testing on the live REST service
	 */
	@Test
	@Ignore
	public void testExecute() throws ExternalExtractorException, JsonParseException, JsonMappingException, ParseException, IOException {
	    final String url = "https://api-demo.pre-enterprise.ricssoftware.com/api/";
	    
		acc = new RicsAccount(1L);
		acc.setEncryptedSerialNum(aesUtility.aesEncryptToBytes("10446"), aesUtility);
		acc.setEncryptedLoginName(aesUtility.aesEncryptToBytes("APITestUser"), aesUtility);
		acc.setEncryptedPassword(aesUtility.aesEncryptToBytes("R1csT3st!ng"), aesUtility);
		api.setApiBaseUrl(url);
		acc.setToken(getToken(url));

		Map<String, String> jsonData = new HashMap<String, String>();
		jsonData.put("BatchStartDate", "1/5/2014");
		jsonData.put("BatchEndDate", "1/1/9999");
		jsonData.put("TicketDateStart", "1/5/2014");
		jsonData.put("TicketDateEnd", "1/1/9999");
		jsonData.put("Token", acc.getLastToken());
		ExternalDTO response = api.sendRequest(new ExternalCommand<RicsAccount>(acc, "POS/GetPOSTransaction", jsonData)).getContent();
		System.out.println(response);
	}
    
    /**
     * Sends a request for a new authentication token from RICS's live service
     * Note: Only used by {{@link #testExecute()} 
     * 
     * @return The token
     */
    private String getToken(String urlBase) throws ExternalExtractorException, JsonParseException, JsonMappingException, ParseException, IOException {
        Map<String, String> jsonData = new HashMap<String, String>();
        jsonData.put("SerialNumber", acc.getSerialNum());
        jsonData.put("Login", acc.getLoginName());
        jsonData.put("Password", acc.getPassword());

        ObjectMapper mapper = new ObjectMapper();

        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(urlBase.concat("Authenticate/Login"));
        HttpResponse response;
        StringEntity json = new StringEntity(mapper.writeValueAsString(jsonData), Charset.forName("UTF-8"));
        json.setContentType(MediaType.APPLICATION_JSON_VALUE);
        request.setEntity(json);
        response = client.execute(request);

        ExternalDTO root = new ExternalJsonDTO(mapper.readValue(EntityUtils.toString(response.getEntity()), JsonNode.class));
        return root.getText("Token");
    }
}
