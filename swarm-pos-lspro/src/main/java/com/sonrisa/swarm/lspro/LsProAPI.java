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

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.util.BaseRestAPI;
import com.sonrisa.swarm.posintegration.api.util.impl.SimpleRestVerifier;
import com.sonrisa.swarm.posintegration.api.util.impl.SimpleVerifierDecorator;
import com.sonrisa.swarm.posintegration.exception.ExternalApiBadCredentialsException;
import com.sonrisa.swarm.posintegration.exception.ExternalApiException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;

/**
 * Tool to access the Light Speed Pro REST API webservice
 * 
 * This class is thread-safe
 */
@Component("lsProAPI")
public class LsProAPI extends BaseRestAPI implements ExternalAPI<LsProAccount>{

    @Value("${lspro.api.rest.uri.base}") 
    private String apiBaseUrl = "http://localhost/";
    
    /**
     * Initialize by setting the API base URL
     */
    protected LsProAPI() {
        
        // Set JSON response verifier for inspecting odata.error
        setRestVerifiers(
                new SimpleVerifierDecorator("odata.error",SimpleRestVerifier.forPathEquals(
                        ExternalApiBadCredentialsException.class,
                        "$.innererror.type",
                        "System.UnauthorizedAccessException")
                        .withMetaField("Inner-Error", "$.innererror.message")),

                // Observed behavior of the Lightspeed Pro server is, that whenever service is denied
                // it throws HTML response with error code 404, although this behavior was reported to be fixed 
                SimpleRestVerifier.forServiceDenied().withErrorStatus(HttpStatus.SC_NOT_FOUND),
                SimpleRestVerifier.forOkStatus(ExternalApiException.class)
        );
    }

    /**
     * Send request to Revel using the account set
     * 
     * @param queryUrl
     *            The URL query, e.g. products
     * @returns JsonNode root of the response
     */
    public ExternalResponse sendRequest(ExternalCommand<LsProAccount> command) throws ExternalExtractorException {
        final String queryUrl = apiBaseUrl + command.getUrlQueryString();

        HttpGet httpget = new HttpGet(queryUrl);
        
        if(StringUtils.hasLength(command.getAccount().getUsername())){
            addBasicAuthorization(httpget, command.getAccount().getUsername(), command.getAccount().getPassword());
        } else {
            throw new ExternalExtractorException("No username provided for:" + command.getAccount());
        }
        
        return executeRequest(httpget);
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }
}
