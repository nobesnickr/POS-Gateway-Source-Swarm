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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonrisa.swarm.kounta.api.util.KountaAPIReader;
import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.util.BaseRestAPI;
import com.sonrisa.swarm.posintegration.api.util.impl.SimpleRestVerifier;
import com.sonrisa.swarm.posintegration.exception.ExternalApiBadCredentialsException;
import com.sonrisa.swarm.posintegration.exception.ExternalApiException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.util.RestUrlBuilder;

/**
 * Tool to access the Kounta REST API webservice
 * 
 * This class is thread-safe
 */
@Component("kountaAPI")
public class KountaAPI extends BaseRestAPI implements ExternalAPI<KountaAccount>{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KountaAPI.class);

    /**
     * Base URI for accessing the Kounta API
     */
    @Value("${kounta.api.rest.uri.base}") 
    private String apiBaseUrl = "http://localhost/";
    
    /**
     * URL where access tokens can be refreshed
     */
    @Value("${kounta.api.rest.uri.token.refresh}") 
    private String tokenRefreshUrl = "http://localhost/token.json";
    
    /**
     * OAuth 2.0 client id
     */
    @Value("${kounta.api.rest.oauth.client.id}")
    private String oauthClientId = "0000";
    
    /**
     * OAuth 2.0 client secret
     */
    @Value("${kounta.api.rest.oauth.client.secret}")
    private String oauthClientSecret = "1111";
    
    @Value("${kounta.api.rest.oauth.uri.redirect}")
    private String oauthRedirectUri = "http://localhost:5555/api/";
    
    /**
     * Relevant HTTP headers for Kounta
     */
    private static final List<String> KOUNTA_HEADERS = Arrays.asList(KountaAPIReader.PAGE_COUNT_KEY);
    
    /**
     * Initialize by setting verifiers
     */
    protected KountaAPI() {
        setRestVerifiers(
                SimpleRestVerifier.forUnauthorized().withMetaField("Details", "$.error_description"),
                SimpleRestVerifier.forOkStatus(ExternalApiException.class).withMetaField("Reason", "$.error_description"));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalResponse sendRequest(ExternalCommand<KountaAccount> command) throws ExternalExtractorException {
        final KountaAccount account = command.getAccount();
                
        // Build REST URI
        StringBuilder queryUrl = new StringBuilder();
        queryUrl.append(apiBaseUrl);
        queryUrl.append(command.getUrlQueryString());
        
        HttpGet httpget = new HttpGet(queryUrl.toString());
        
        // Configuration is interpreted as headers in Kounta
        for(Entry<String,String> entry : command.getConfig().entrySet()){
            httpget.addHeader(entry.getKey(), entry.getValue());
        }
        
        // Setup authentication
        if(StringUtils.hasLength(account.getOauthRefreshToken())){
            return executeOauthRequest(account, httpget);             
        } 
        
        // No OAuth token
        if(StringUtils.hasLength(account.getUserName())){
            // Only for development accounts.
            addBasicAuthorization(httpget, account.getUserName(), account.getPassword());
        } 
        
        return executeRequest(httpget, KOUNTA_HEADERS);
    }
    
    /**
     * Get Kounta account using the temporary token
     */
    public KountaAccount getAccountForTemporaryToken(String code) throws ExternalExtractorException{
        KountaAccount retVal = new KountaAccount(0L);
        
        Map<String,String> params = new HashMap<String,String>();
        params.put("client_id", oauthClientId);
        params.put("client_secret", oauthClientSecret);
        params.put("redirect_uri", oauthRedirectUri);
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        
        ExternalResponse response = sendSimplePostRequest(tokenRefreshUrl, params);
        ExternalDTO refreshToken = response.getContent();
        
        retVal.setOauthRefreshToken(refreshToken.getText("refresh_token"));
        
        // REST service sends generated access_token
        KountaAccessToken accessToken = new KountaAccessToken();
        accessToken.setAccessToken(refreshToken.getText("access_token"));
        accessToken.setTokenType(refreshToken.getText("token_type"));
        retVal.setOauthAccessToken(accessToken);
        
        return retVal;
    }
    
    /**
     * Send post request to Kounta
     * @throws ExternalExtractorException 
     * @returns Root JsonNode as ExternalDTO
     */
    private ExternalResponse sendSimplePostRequest(String uri, Map<String,String> params) throws ExternalExtractorException{
        HttpPost httppost = new HttpPost(uri);
        httppost.setEntity(RestUrlBuilder.preparePostFields(params));
        return executeRequest(httppost);
    }
    
    /**
     * Get access token from remote location
     * @param refreshToken
     * @return
     */
    private KountaAccessToken getAccessToken(String refreshToken) throws ExternalExtractorException {
        
        HttpPost httpPost = new HttpPost(tokenRefreshUrl);
        
        // Prepare fields
        Map<String,String> params = new HashMap<String,String>();
        params.put("client_id", oauthClientId);
        params.put("client_secret", oauthClientSecret);
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", refreshToken);
        
        httpPost.setEntity(RestUrlBuilder.preparePostFields(params));
        
        // Execute POST request
        final JsonNode response = executeRequestForJson(httpPost);

        ObjectMapper mapper = new ObjectMapper();
        
        return mapper.convertValue(response, KountaAccessToken.class);
    }
    
    /**
     * Updates account token for account
     * @param force If set to false, only updates if missing
     */
    private void updateOauthToken(KountaAccount account, boolean force) throws ExternalExtractorException{
        synchronized(account){
            if(force || account.getOauthAccessToken() == null){
                account.setOauthAccessToken(getAccessToken(account.getOauthRefreshToken()));
            }
        }
    }

    /**
     * Attempts to execute request, but if fails with {@link ExternalApiBadCredentialsException}
     * it refreshes the token and tries again 
     */
    private ExternalResponse executeOauthRequest(KountaAccount account, HttpUriRequest request) throws ExternalExtractorException{
        updateOauthToken(account, false);
        
        // Set OAuth header
        try {
            request.addHeader(BaseRestAPI.AUTHORIZATION_HEADER, account.getOauthAccessToken().getAuthorizationString());
            return executeRequest(request, KOUNTA_HEADERS);
        } catch (ExternalApiBadCredentialsException e){
            LOGGER.debug("Kounta API denied access token", e.getMessage(), e);
            
            // Force refresh access token
            updateOauthToken(account, true);
            request.addHeader(BaseRestAPI.AUTHORIZATION_HEADER, account.getOauthAccessToken().getAuthorizationString());
            
            return executeRequest(request, KOUNTA_HEADERS);
        }
    }
    
    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public void setTokenRefreshUrl(String tokenRefreshUrl) {
        this.tokenRefreshUrl = tokenRefreshUrl;
    }

    public void setOauthClientId(String oauthClientId) {
        this.oauthClientId = oauthClientId;
    }

    public void setOauthClientSecret(String oauthClientSecret) {
        this.oauthClientSecret = oauthClientSecret;
    }

    public void setOauthRedirectUri(String oauthRedirectUri) {
        this.oauthRedirectUri = oauthRedirectUri;
    }
}
