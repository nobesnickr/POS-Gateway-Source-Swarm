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
package com.sonrisa.swarm.shopify;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonrisa.shopify.ShopifyAPI;
import com.sonrisa.shopify.ShopifyAccount;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;

/**
 * Test the {@link ShopifyAPI} class.
 * @author sonrisa
 *
 */
public class ShopifyAPITest {

    private static final int API_PORT = 8092;

    /**
     * Utility for encrypting strings
     */
    private AESUtility utility;
    
    /**
     * @see http://wiremock.org/getting-started.html
     */
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(API_PORT); 
    
    @Before
    public void setupContext(){
        this.utility = new AESUtility();
        this.utility.setEncryptionKey("NOKEY");
    }
    
    /**
     * Testing that when requesting an URI from JSON
     * the API call follows the convention of the remote system 
     * 
     * @throws ExternalExtractorException
     */
    @Test
    public void testApiCallFormat() throws ExternalExtractorException{
        final String oauthToken = "0bc21f57f980ae2a332c74c217a55283";
        
        ShopifyAccount account = new ShopifyAccount();
        account.setOauthToken(oauthToken);
        account.setAccountId("swarm-partner");
        
        ShopifyAPI api = new ShopifyAPI();
        api.setApiBaseUrl("http://localhost:" + API_PORT + "/admin/");
        
        stubFor(WireMock.get(urlMatching("/admin/products.json"))
                .willReturn(aResponse().withBody("true")));
        
        api.sendRequest(new ExternalCommand<ShopifyAccount>(account, "products.json"));
        
        verify(getRequestedFor(urlMatching("/admin/products.json"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("X-Shopify-Access-Token", equalTo("0bc21f57f980ae2a332c74c217a55283")));
    }
    
    /**
     * Sandbox unit test 
     */
    @Test
    @Ignore
    public void testExecute() throws ExternalExtractorException{
        ShopifyAccount account = new ShopifyAccount();
        account.setOauthToken("5423b9dabc330b336b203c91e80db953");
        account.setAccountId("wrightwood-furniture-co");
        
        ShopifyAPI api = new ShopifyAPI();
        api.setApiBaseUrl("https://%s.myshopify.com/admin/");
        
        api.sendRequest(new ExternalCommand<ShopifyAccount>(account, "orders.json?&created_at_min=2014-10-08T02:05:20 CEST&status=any"));
    }
}