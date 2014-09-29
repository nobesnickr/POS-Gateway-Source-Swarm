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
package com.sonrisa.swarm.mos;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonrisa.swarm.posintegration.exception.ExternalDeniedServiceException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;

/**
 * Class responsible with communacting the Merchant OS API server, by 
 * passing REST commands and parsing the returned JSON
 * @see http://www.lightspeedretail.com/cloud/help/developers/
 */
@Component
public class MosAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(MosAPI.class);

    /**
     * Send request to Merchant OS using the account set
     * @param queryUrl The URL query, e.g. Item
     * @returns JsonNode root of the response
     */
    public JsonNode sendRequest(MosAccount account, String url) throws ExternalExtractorException {
        final String queryUrl = account.getAccessUrl()  + url;
        
        HttpClient client = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(queryUrl);
        
        final String authorization = account.getAuthorization();
        httpget.addHeader("Authorization", authorization);
        httpget.addHeader("Accept","application/json");

        HttpResponse response;
        ObjectMapper mapper = new ObjectMapper();
        try {
            //Send HTTP request
            LOGGER.debug("Merchant OS remote request: {}", queryUrl);
            response = client.execute(httpget);
            
            final String responseText = EntityUtils.toString(response.getEntity());
            LOGGER.trace("Merchant OS response: {}", responseText);
            JsonNode rootNode = mapper.readValue(responseText, JsonNode.class);
            
            if(response.getStatusLine().getStatusCode() == 503){ // HTTP/1.1 503 Service Unavailable
                throw new ExternalDeniedServiceException("Merchant OS server denied service: " + response.getStatusLine().getReasonPhrase());
            } else if (response.getStatusLine().getStatusCode() != 200) { // 200 is OK
                throw new ExternalExtractorException("Merchant OS server error: " + response.getStatusLine().getReasonPhrase());
            }
            
            return rootNode;
        } catch (JsonParseException e) {
            throw new ExternalExtractorException(e);
        } catch (JsonMappingException e) {
            throw new ExternalExtractorException(e);
        } catch (ClientProtocolException e) {
            throw new ExternalExtractorException(e);
        } catch (IOException e) {
            throw new ExternalExtractorException(e);
        } catch (ParseException e) {
            throw new ExternalExtractorException(e);
        }
    }
}
