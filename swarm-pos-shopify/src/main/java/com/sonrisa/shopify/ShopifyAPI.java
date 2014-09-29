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
package com.sonrisa.shopify;

import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.util.BaseRestAPI;
import com.sonrisa.swarm.posintegration.api.util.impl.SimpleRestVerifier;
import com.sonrisa.swarm.posintegration.exception.ExternalApiException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;


/**
 * Tool to access the Shopify REST webservice
 */
@Component("shopifyAPI")
public class ShopifyAPI extends BaseRestAPI implements ExternalAPI <ShopifyAccount>{
    
    /** 
     * Response from Shopify when the server denies service
     * 
     * http://docs.shopify.com/api/tutorials/learning-to-respect-the-api-call-limit 
     */
    public static final int TOO_MANY_REQUESTS = 429;
    
    /**
     * JSON key for accessing the permanent access token while exchanging tokens
     */
    public static final String API_FIELD_PERM_TOKEN = "access_token";
    
    /**
     * JSON path for accessing the timezone when reading Shop information
     */
    public static final String API_FIELD_SHOP_TIMEZONE = "shop.timezone";
    
    /**
     * JSON path for accessing the shop's name when reading Shop information
     */
    public static final String API_FIELD_SHOP_NAME = "shop.name";
    
    /** 
     * Header parameter key used by HTTP requests with Oauth2 authentication.  
     * 
     * WARNING: Shopify isn't an OAuth2 standard implementation, there are some differences
     */
    public static final String API_OAUTH_HEADER_KEY = "X-Shopify-Access-Token";
    
    /** 
     * Name of Shopify API. It is also defined in the liquibase XML. 
     * 
     * BAD-DESIGN: This value should be injected
     */
    public static final String SHOPIFY_API_NAME = "shopify";
    
    /**
     * Base URL for the Shopify service
     */
    @Value("${shopify.api.rest.uri.base}")
    private String apiBaseUrl = "http://localhost/";
    
    /**
     * Initializes the API, sets up the base classes verifiers
     */
    public ShopifyAPI() {
        setRestVerifiers(
                SimpleRestVerifier.forServiceDenied().withErrorStatus(TOO_MANY_REQUESTS), 
                SimpleRestVerifier.forOkStatus(ExternalApiException.class).withMetaField("Error", "$.errors"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalResponse sendRequest(ExternalCommand<ShopifyAccount> command) throws ExternalExtractorException {
        final String queryUrl = String.format(apiBaseUrl, command.getAccount().getAccountId()) + command.getUrlQueryString();
        
        HttpGet httpget = new HttpGet(queryUrl);
        
        // Custom Shopify OAuth token
        if(StringUtils.hasLength(command.getAccount().getOauthToken())){
            httpget.addHeader(API_OAUTH_HEADER_KEY, command.getAccount().getOauthToken());
        }
        
        return executeRequest(httpget);
    }

    /**
     * Sets the base URL for the Shopify service
     * @param apiBaseUrl 
     */
    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }
}
