package com.sonrisa.swarm.vend;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.util.BaseRestAPI;
import com.sonrisa.swarm.posintegration.api.util.impl.SimpleRestVerifier;
import com.sonrisa.swarm.posintegration.exception.ExternalApiBadCredentialsException;
import com.sonrisa.swarm.posintegration.exception.ExternalApiException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.util.RestUrlBuilder;
import com.sonrisa.swarm.vend.api.util.VendAPIReader;

/**
 * Tool to access the Vend REST API webservice
 * 
 * This class is thread-safe
 */
@Component("vendAPI")
public class VendAPI extends BaseRestAPI implements ExternalAPI<VendAccount>{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(VendAPI.class);

    /**
     * Base URI for accessing the Vend API
     */
    @Value("${vend.api.rest.uri.base}") 
    private String apiBaseUrl = "http://localhost/";
    
    /**
     * URL where access tokens can be refreshed
     */
    @Value("${vend.api.rest.uri.token.refresh}") 
    private String tokenRefreshUrl = "http://localhost/token.json";
    
    /**
     * OAuth 2.0 client id
     */
    @Value("${vend.api.rest.oauth.client.id}")
    private String oauthClientId = "0000";
    
    /**
     * OAuth 2.0 client secret
     */
    @Value("${vend.api.rest.oauth.client.secret}")
    private String oauthClientSecret = "1111";
    
    @Value("${vend.api.rest.oauth.uri.redirect}")
    private String oauthRedirectUri = "http://localhost:5555/api/";
    
    /**
     * Relevant HTTP headers for Vend
     */
    private static final List<String> VEND_HEADERS = Arrays.asList(VendAPIReader.PAGE_COUNT_KEY);
    
    /**
     * Initialize by setting verifiers
     */
    protected VendAPI() {
        setRestVerifiers(
                SimpleRestVerifier.forUnauthorized().withMetaField("Details", "$.error_description"),
                SimpleRestVerifier.forOkStatus(ExternalApiException.class).withMetaField("Reason", "$.error_description"));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalResponse sendRequest(ExternalCommand<VendAccount> command) throws ExternalExtractorException {
        final VendAccount account = command.getAccount();
                
        // Build REST URI
        StringBuilder queryUrl = new StringBuilder();
        queryUrl.append(getRequestUrl(command.getAccount()));
        queryUrl.append(command.getUrlQueryString());
        
        LOGGER.debug("Complete URL: "+ queryUrl);
        
        HttpGet httpget = new HttpGet(queryUrl.toString());
        
        // Configuration is interpreted as headers in Vend
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
        
        return executeRequest(httpget, VEND_HEADERS);
    }
    
    /**
     * Get Vend account using the temporary token
     */
    public VendAccount getAccountForTemporaryToken(String code) throws ExternalExtractorException{

        HttpPost httpPost = new HttpPost(getRefreshUrl(apiBaseUrl));
        
        // Prepare fields
        Map<String,String> params = new HashMap<String,String>();
        params.put("client_id", oauthClientId);
        params.put("client_secret", oauthClientSecret);
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", code);
        
        HttpEntity preparePost = RestUrlBuilder.preparePostFields(params);
        
        httpPost.setEntity(preparePost);
        LOGGER.debug("Refresh token request:"+ httpPost.getURI());
        
        // Execute POST request
        final JsonNode response = executeRequestForJson(httpPost);
        LOGGER.info("Refresh token response:"+ preparePost);
        ObjectMapper mapper = new ObjectMapper();
        
        VendAccount acc = new VendAccount(0);
        acc.setOauthAccessToken(mapper.convertValue(response, VendAccessToken.class));
        return acc;
    }
    
    
    /**
     * Get access token from remote location
     * @param refreshToken
     * @param site 
     * @return
     */
    private VendAccessToken getAccessToken(String refreshToken, String apiUrl) throws ExternalExtractorException {
        HttpPost httpPost = new HttpPost(getRefreshUrl(apiUrl));
        
        // Prepare fields
        Map<String,String> params = new HashMap<String,String>();
        params.put("client_id", oauthClientId);
        params.put("client_secret", oauthClientSecret);
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", refreshToken);
        
        UrlEncodedFormEntity preparePost = (UrlEncodedFormEntity) RestUrlBuilder.preparePostFields(params);
        
        httpPost.setEntity(preparePost);
        LOGGER.debug("Refresh token request:"+ httpPost.getURI());
        
        // Execute POST request
        final JsonNode response = executeRequestForJson(httpPost);
        LOGGER.info("Refresh token response:"+ preparePost);
        ObjectMapper mapper = new ObjectMapper();
        
        return mapper.convertValue(response, VendAccessToken.class);
    }
    
    /**
     * Updates account token for account
     * @param force If set to false, only updates if missing
     */
    private void updateOauthToken(VendAccount account, boolean force) throws ExternalExtractorException{
        synchronized(account){
            if(force || account.getOauthAccessToken() == null){
                account.setOauthAccessToken(getAccessToken(account.getOauthRefreshToken(), account.getApiUrl()));
            }
        }
    }

    /**
     * Attempts to execute request, but if fails with {@link ExternalApiBadCredentialsException}
     * it refreshes the token and tries again 
     */
    private ExternalResponse executeOauthRequest(VendAccount account, HttpUriRequest request) throws ExternalExtractorException{
        updateOauthToken(account, false);
        
        // Set OAuth header
        try {
            request.addHeader(BaseRestAPI.AUTHORIZATION_HEADER, account.getOauthAccessToken().getAuthorizationString());
            return executeRequest(request, VEND_HEADERS);
        } catch (ExternalApiBadCredentialsException e){
            LOGGER.info("Vend API denied access token", e.getMessage(), e);
            
            // Force refresh access token
            updateOauthToken(account, true);
            request.addHeader(BaseRestAPI.AUTHORIZATION_HEADER, account.getOauthAccessToken().getAuthorizationString());
            
            return executeRequest(request, VEND_HEADERS);
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
    
    private String getRequestUrl(VendAccount account) {
        return String.format(apiBaseUrl, account.getApiUrl());
    }
    
    private String getRefreshUrl(String apiUrl) {
    	return String.format(tokenRefreshUrl, apiUrl);
    }
}
