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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.ParseException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.rics.MockRicsData;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.exception.ExternalApiException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;
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
	private RicsAccount ricsAccount;

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

		ricsAccount = new RicsAccount();
		ricsAccount.setToken("SONRISA");
		api.setApiBaseUrl(String.format("http://localhost:%d/", API_PORT));

		// Successful response on invoice request
		stubFor(WireMock.post(urlMatching(RicsUri.INVOICES.uri))
				.willReturn(aResponse().withBody(MockDataUtil.getResourceAsJson(MockRicsData.MOCK_INVOICES_ONE_PAGE_RESPONSE).toString())));

		// Unsuccessful invocation
		stubFor(WireMock.post(urlMatching("/testUnauthorized"))
				.willReturn(aResponse().withBody(MockDataUtil.getResourceAsJson(MockRicsData.MOCK_UNSUCCESS).toString())));

		// Empty result received
		stubFor(WireMock.post(urlMatching("/testEmptyResponse"))
				.willReturn(aResponse().withBody(MockDataUtil.getResourceAsJson(MockRicsData.MOCK_EMPTY_RESPONSE).toString())));
	}

	/**
	 * Test case: Sending a request to POSTransactions
	 * 
	 * Expected: The <code>Token</code> header is set.
	 * 
	 * @throws ExternalExtractorException
	 */
	@Test
	public void testTokenIsInHeader()
			throws ExternalExtractorException {

		// Act
		api.sendRequest(new ExternalCommand<RicsAccount>(ricsAccount,
				RicsUri.INVOICES.uri, new HashMap<String, String>()));

		// Assert
		verify(1, postRequestedFor(urlEqualTo(RicsUri.INVOICES.uri)).withHeader(
				"Token", equalTo(ricsAccount.getToken())).withHeader("Accept",
				equalTo("application/json")));
		
		// Never attempts login
		verify(0, postRequestedFor(urlMatching(".*?Login.*?")));
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
		api.sendRequest(new ExternalCommand<RicsAccount>(ricsAccount, "/testUnauthorized", new HashMap<String, String>()));
	}

	/**
	 * Testing on the live REST service
	 */
	@Ignore
	@Test
	public void testExecute() throws ExternalExtractorException, JsonParseException, JsonMappingException, ParseException, IOException {
		final String url = "https://enterprise.ricssoftware.com/api/";
	    
		ricsAccount = new RicsAccount();
		ricsAccount.setToken("232812B6-5838-4A2E-B5FE-CA37A6705653");
		ricsAccount.setUserName("Swarm201$");
		api.setApiBaseUrl(url);

		Map<String, String> jsonData = new HashMap<String, String>();
		jsonData.put("BatchStartDate", "1/9/2014");
		jsonData.put("BatchEndDate", "1/1/9999");
		jsonData.put("TicketDateStart", "1/9/2014");
		jsonData.put("TicketDateEnd", "1/1/9999");
		ExternalDTO response = api.sendRequest(new ExternalCommand<RicsAccount>(ricsAccount, "POS/GetPOSTransaction", jsonData)).getContent();
		System.out.println(response);
	}
}
