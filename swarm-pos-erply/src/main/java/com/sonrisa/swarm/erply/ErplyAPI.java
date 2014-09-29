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

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.erply.exception.ErplySessionExpiredException;
import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.util.BaseRestAPI;
import com.sonrisa.swarm.posintegration.api.util.RestIOExceptionConverter;
import com.sonrisa.swarm.posintegration.api.util.impl.SimpleJSONFieldTranslator;
import com.sonrisa.swarm.posintegration.api.util.impl.SimpleRestVerifier;
import com.sonrisa.swarm.posintegration.exception.ExternalApiBadCredentialsException;
import com.sonrisa.swarm.posintegration.exception.ExternalApiException;
import com.sonrisa.swarm.posintegration.exception.ExternalDeniedServiceException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;
import com.sonrisa.swarm.posintegration.extractor.util.RestUrlBuilder;

/**
 * Class responsible with communicating the Erply API server, by requesting
 * session via authentication, and then passing REST commands
 *
 * @see http://www.inventoryapi.com/?id=347&page=examples
 */
@Component("erplyAPI")
public class ErplyAPI extends BaseRestAPI implements ExternalAPI<ErplyAccount> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErplyAPI.class);
    
    /**
     * Erply API expects this version information in most requests
     */
    public static final String ERPLY_API_VERSION = "1.0";

    /**
     * Erply API request to verify the user's authentication
     */
    public static final String VERIFY_USER_REQUEST = "verifyUser";

    /**
     * Location of the Erply REST service
     */
    @Value("${erply.api.rest.uri.base}") 
    private String apiBaseUrl = "http://localhost:8089/api/";
    
    /**
     * First page in erply is "1" and not "0";
     */
    public static final int FIRST_PAGE = 1;

    /**
     * Initialize Erply API
     */
    public ErplyAPI(){
        // As Erply base URL depends on the client code, we need a special
        // IOException handler in case UnknownHostExceptionOccurs
        setIOExceptionConverter(new RestIOExceptionConverter() {
            @Override
            public ExternalExtractorException convertException(IOException occuredException) {
                if(occuredException instanceof UnknownHostException){
                    return new ExternalApiException("Invalid client code for Erply");
                } else {
                    return new ExternalExtractorException(occuredException);
                }
            }
        });
        
        // Set JSON response verifier for inspecting odata.error
        setRestVerifiers(
                SimpleRestVerifier.forPathEquals(
                        ExternalDeniedServiceException.class,
                        "$.status.errorCode",
                        "1002"), // Hourly request limit (by default 1000 requests) has been exceeded for this account
                SimpleRestVerifier.forPathEquals(
                        ErplySessionExpiredException.class,
                        "$.status.errorCode",
                        "1054"), // Hourly request limit (by default 1000 requests) has been exceeded for this account
                SimpleRestVerifier.forPathEquals(
                        ExternalApiBadCredentialsException.class,
                        "$.status.errorCode",
                        "1051"), // Login failed
                SimpleRestVerifier.forPathEquals(
                        ExternalApiException.class,
                        "$.status.responseStatus",
                        "error").withMetaField(new SimpleJSONFieldTranslator("erplyErrorCodes.json", "Erply error"), "$.status.errorCode"),        
                SimpleRestVerifier.forOkStatus(ExternalApiException.class)
        );
    }
    
    /**
     * Send request to Erply using the account set
     * 
     * @param queryUrl
     *            The URL query, e.g. products
     * @returns {@link ExternalResponse}
     */
    public ExternalResponse sendRequest(ExternalCommand<ErplyAccount> command) throws ExternalExtractorException {
        
        ErplyAccount account = command.getAccount();
        if(account.getSession() == null){
            account.setSession(getNewSession(account));
        }
        
        Map<String,String> params = new HashMap<String,String>(command.getParams());
        params.put("sessionKey", account.getSession().getSessionKey());
        
        try {
            // Try to send request
            return executeErplyRequest(account, command.getURI(), params);
            
        } catch (ErplySessionExpiredException e){
            LOGGER.debug("Erply session expired, renewing", e);
            account.setSession(getNewSession(account));
            return executeErplyRequest(account, command.getURI(), params);
        }
    }

    /**
     * Verify user and start session
     */
    protected ErplySession getNewSession(ErplyAccount erplyAccount) throws ExternalExtractorException {

        //prepare post parameters
        Map<String, String> params = new HashMap<String, String>();
        
        // Username in Erply is actually an access key, as a single account may have
        // different users accessing that account
        params.put("username", erplyAccount.getUsername());
        params.put("password", erplyAccount.getPassword());

        final ExternalDTO records = executeErplyRequest(erplyAccount,VERIFY_USER_REQUEST,params).getContent();
                
        // Read session key from JSON
        final String sessionKey = records.getNestedItem(new ExternalDTOPath("records")).getNestedArrayItem(0).getText("sessionKey");
        return new ErplySession(sessionKey);
    }
    
    /**
     * Execute request
     */
    private ExternalResponse executeErplyRequest(ErplyAccount account, String request, Map<String,String> params) throws ExternalExtractorException{
        params.put("request", request);
        
        //Each request should contain these fields!
        params.put("clientCode", account.getClientCode());
        params.put("version", ErplyAPI.ERPLY_API_VERSION);
     
        // send request     
        final HttpPost httppost = new HttpPost(getRequestUrl(account));
        
        // Set POST fields
        httppost.setEntity(RestUrlBuilder.preparePostFields(params));
        
        return executeRequest(httppost);
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    private String getRequestUrl(ErplyAccount account) {
        return String.format(apiBaseUrl, account.getClientCode());
    }
}
