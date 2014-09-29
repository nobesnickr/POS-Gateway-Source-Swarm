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
package com.sonrisa.swarm.revel;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.util.BaseRestAPI;
import com.sonrisa.swarm.posintegration.api.util.RestIOExceptionConverter;
import com.sonrisa.swarm.posintegration.api.util.impl.SimpleRestVerifier;
import com.sonrisa.swarm.posintegration.exception.ExternalApiBadCredentialsException;
import com.sonrisa.swarm.posintegration.exception.ExternalApiException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;


/**
 * Tool to access the Revel REST webservice
 *
 */
@Component("revelAPI")
public class RevelAPI extends BaseRestAPI implements ExternalAPI<RevelAccount> {
    
    /**
     * URI to access the Revel API service
     */
    @Value("${revel.api.rest.uri.base}")
    private String apiUrlBase = "http://localhost/";
    
    /**
     * Intilize by setting base classes's fields
     */
    public RevelAPI(){
        // As Revel base URL depends on the username, we need a special
        // IOException handler in case UnknownHostExceptionOccurs
        setIOExceptionConverter(new RestIOExceptionConverter() {
            @Override
            public ExternalExtractorException convertException(IOException occuredException) {
                if(occuredException instanceof UnknownHostException){
                    return new ExternalApiException("Invalid store name, Revel store doesn't exist");
                } else {
                    return new ExternalExtractorException(occuredException);
                }
            }
        });
        
        // HTTP status 503 means service denial
        setRestVerifiers(
                SimpleRestVerifier.forException(ExternalApiBadCredentialsException.class)
                    .withErrorStatus(HttpStatus.UNAUTHORIZED.value())
                    .withStatusFailureFormat("Wrong API key or API secret, Revel says: <%REASON%>"),
                SimpleRestVerifier.forServiceDenied().withErrorStatus(503), 
                SimpleRestVerifier.forOkStatus(ExternalApiException.class));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalResponse sendRequest(ExternalCommand<RevelAccount> command) throws ExternalExtractorException {
        final String queryUrl = String.format(apiUrlBase, command.getAccount().getAccountId())  + command.getUrlQueryString();
        
        HttpGet httpget = new HttpGet(queryUrl);
        if(!StringUtils.isEmpty(command.getAccount().getApiKey())){
            httpget.addHeader("API-AUTHENTICATION", command.getAccount().getApiKey() + ":" + command.getAccount().getApiSecret());
        }
        
        return executeRequest(httpget);
    }

    /**
     * Set which API url to use as base URl, e.g. <code>http://%s.myrevelup.com/</code>
     * @param apiUrlBase
     */
    public void setApiUrlBase(String apiUrlBase) {
        this.apiUrlBase = apiUrlBase;
    }
}
