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

package com.sonrisa.swarm.lspro;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.lspro.MockLsProData;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.exception.ExternalApiBadCredentialsException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;

/**
 * Instance testing the {@link LsProAPI} class.
 *
 */
public class LsProAPITest {
  
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
   * Target being tested
   */
  private LsProAPI target;
  
  /**
   * Account
   */
  private LsProAccount account;

  /**
   * Sets up target
   */
  @Before
  public void setupTarget(){
    target = new LsProAPI();
    target.setApiBaseUrl(String.format("http://localhost:%d/", API_PORT));
    
    account = new LsProAccount();
    account.setUserName("userName");
    account.setPassword("password");
  }
  
  /**
   * Test case:
   *  - Account is created and passed to the target
   *  - {@link LsProAPI#sendRequest(String, LsProAccount)} is executed with an irrelevant URL
   * 
   * Expected:
   *  - Account's URLBase and queryUrl is concatenated
   *  - Basic authentication headers are inserted
   *  - JSON is parsed and returned
   * @throws ExternalExtractorException 
   */
  @Test
  public void testBestCaseScenario() throws ExternalExtractorException{

    final String subQuery = "/subQuery/of/something/";
    stubFor(WireMock.get(urlMatching(subQuery))
            .willReturn(aResponse().withBody("true")));
    
    // Act
    target.sendRequest(new ExternalCommand<LsProAccount>(account,subQuery));
    
    // Assert    
    verify(getRequestedFor(urlMatching(subQuery))
            .withHeader("Accept", equalTo("application/json")));
  }
  
  /**
   * Test case:
   *  - Account is created and passed to the target
   *  - {@link LsProAPI#sendRequest(String, LsProAccount)} is executed with URL containing a space character
   * 
   * Expected:
   *  - Account's URLBase is escaped appropriately using <code>%20</code>
   *  
   * @throws ExternalExtractorException 
   */
  @Test
  public void testURIForFilteringIsEncoded() throws ExternalExtractorException{

    stubFor(get(urlMatching("/Invoices?.*"))
            .willReturn(aResponse().withBody("true")));
    
    // Act
    Map<String,String> params = new HashMap<String,String>();
    params.put("$filter", "DateCreated gt 2014-01-01 UTC+2");
    target.sendRequest(new ExternalCommand<LsProAccount>(account,"Invoices",params));
    
    // Assert    
    verify(getRequestedFor(urlEqualTo("/Invoices?$filter=DateCreated%20gt%202014-01-01%20UTC+2")));
  }
  
  /**
   * Test case:
   *  - Request to REST service responds HTTP 500 (Internal Server Error)
   * 
   * Expected:
   *  - ExternalExtractorException is thrown
   *  
   * @throws ExternalExtractorException 
   */
  @Test(expected=ExternalApiBadCredentialsException.class)
  public void testLsProUnauthorized() throws Exception {

    JsonNode errorNode = MockDataUtil.getResourceAsJson(MockLsProData.MOCK_LSPRO_UNAUTHORIZED);
      
    stubFor(WireMock.get(urlMatching(".*"))
            .willReturn(aResponse().withBody(errorNode.toString())));
    
    // Act
    target.sendRequest(new ExternalCommand<LsProAccount>(account, "Invoices"));
  }
  
    /**
     * Debugging tool to test the execution of the Lightspeed Pro API
     * with a live REST service
     */
    @Test
    @Ignore
    public void testExecute() {
        LsProAccount account = new LsProAccount();
        LsProAPI target = new LsProAPI();
        target.setApiBaseUrl("https://accumula.co/data/oData.svc/");

        account.setUserName("simplymac");
        account.setPassword("b9e45382-6eee-4eec-bb1b-e03c9352feef");

        try {
            System.out.println(target.sendRequest(new ExternalCommand<LsProAccount>(account,
            // "Products?$orderby=(DateModified)asc&$filter=(DateModified)gt(DateTime'1970-01-01T01:00:00.000')&$top=50&$skip=0")));
            "Invoices?$filter=(DateCreated)eq(DateTime'2014-03-01T01:00:00.000')")));
            // "Invoices?$filter=(PosInvoiceId)eq('I-7660')")));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
