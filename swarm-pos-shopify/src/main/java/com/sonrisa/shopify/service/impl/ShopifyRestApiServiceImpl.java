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
package com.sonrisa.shopify.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.shopify.ShopifyAPI;
import com.sonrisa.shopify.model.TokenExchangeResult;
import com.sonrisa.shopify.service.ShopifyRestApiService;
import com.sonrisa.swarm.common.util.JsonUtil;
import com.sonrisa.swarm.posintegration.extractor.util.RestUrlBuilder;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of the {@link ShopifyRestApiService} which
 * encapsulates REST functionality the exchange OAuth tokens
 * and accessing Store information.
 * 
 * @author joe
 */
@Service
public class ShopifyRestApiServiceImpl implements ShopifyRestApiService {


    @Value("${shopify.swarm.app.api.key}")
    private String appApiKey;

    @Value("${shopify.swarm.app.shared.secret}")
    private String appSharedSecret;
    
    /** Base URL of the shop admin sites. */
    @Value("${shopify.shop.admin.base.url.full}")
    private String adminUrlBase;

    @Value("${shopify.rest.shop.url.postfix}")
    private String shopInfoUrlPostfix;
    
    @Value("${shopify.temp.token.exchange.url.postfix}")
    private String exchangeUrlPostfix;

    /**
     * {@inheritDoc }
     * 
     * @param siteName
     * @param tempToken
     * @return 
     */
    @Override
    public TokenExchangeResult exchangeTempTokenToPermanent(String siteName, String tempToken) {
        // constructs the HTTP request URL with the necessary parameters
        final Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("client_id", appApiKey);
        urlParams.put("client_secret", appSharedSecret);
        urlParams.put("code", tempToken);        
        final String url = getTokenExchangeUrl(siteName) + "?" +  RestUrlBuilder.prepareGetFields(urlParams);

        // the HTTP request entity
        final HttpEntity httpEntity = new HttpEntity(null);
        
        // executes the POST request
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.POST, httpEntity, JsonNode.class);  
        
        // TODO handle exceptions or errors

        return resultFromTokenExchangeResponse(response);
    }

    /**
     * {@inheritDoc }
     * 
     * @param siteName
     * @param token
     * @return 
     */
    @Override
    public JsonNode getStoreInfo(String siteName, String token) {
        // constructs the HTTP headers
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(ShopifyAPI.API_OAUTH_HEADER_KEY, token);
        
        // constructs the http request entity
        final HttpEntity httpEntity = new HttpEntity(headers);
        
        // retrieves the shop info REST api URL for this shop
        final String url = getShopInfoUrl(siteName);
        
        // executes the HTTP method
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.GET, httpEntity, JsonNode.class);

        return response.getBody();
    }
    
    

    // ------------------------------------------------------------------------
    // ~ Private methods
    // ------------------------------------------------------------------------
    
    /**
     * Constructs the helper object that encapsulates 
     * the result of the Oauth token exchange process.
     * 
     * @param response
     * @return 
     */
    private TokenExchangeResult resultFromTokenExchangeResponse(final ResponseEntity<JsonNode> response){
        TokenExchangeResult result;
        
        if(response != null && response.getBody() != null){
            final JsonNode body = response.getBody();
            
            // HTTP response status == 200
            if (HttpStatus.OK.equals(response.getStatusCode())){
                final String permToken = JsonUtil.getJsonField(body, ShopifyAPI.API_FIELD_PERM_TOKEN);
                result = TokenExchangeResult.success(permToken);
            }else{
                final HttpStatus statusCode = response.getStatusCode();
                result = TokenExchangeResult.error(statusCode.getReasonPhrase(), statusCode.name(), body.asText());
            }        
        }else{
            result = TokenExchangeResult.error("Null response.");
        }
        
        return result;
        
    }
    
    /**
     * Constructs the rest api URL for this store with the given postfix.
     * 
     * Eg.: 
     * siteName = "SHOP_NAME"
     * urlPostfix = "oauth/authorize"
     * 
     * The result will be:
     * https://SHOP_NAME.myshopify.com/admin/oauth/authorize
     *
     * @param siteName
     * @param urlPostfix 
     * 
     * @return
     */
    private String getRestResourceUrl(final String siteName, final String urlPostfix) {
        // store name substitution into the baseUrl
        final String baseUrl = String.format(adminUrlBase, siteName);

        // appends url postfix
        final StringBuilder exchangeUrl = new StringBuilder(baseUrl);   
        exchangeUrl.append(urlPostfix);
        return exchangeUrl.toString();
    }

    /**
     * Returns temp token exchange URL for the given shop. Eg:
     * https://SHOP_NAME.myshopify.com/admin/oauth/authorize
     *
     * @param siteName
     * @return
     */
    private String getTokenExchangeUrl(String siteName) {
        return getRestResourceUrl(siteName, exchangeUrlPostfix);
    }

    /**
     * Returns shop info URL for the given shop. Eg:
     * https://SHOP_NAME.myshopify.com/admin/shop.json
     *
     * @param siteName
     * @return
     */    
    private String getShopInfoUrl(String siteName) {
        return getRestResourceUrl(siteName, shopInfoUrlPostfix);
    }
}
