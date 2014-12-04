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

package com.sonrisa.swarm.posintegration.api.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.exception.ExternalApiException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalJsonDTO;

/**
 * Base class for implementation of {@link ExternalAPI} instances, with some common functionality.
 * 
 * @author Barnabas
 */
public abstract class BaseRestAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseRestAPI.class);
    
    /**
     * Header key for sending authorization
     */
    public static final String AUTHORIZATION_HEADER = "Authorization";
    
    /**
     * All {@link HttpResponseVerifier}
     */
    private List<RestResponseVerifier> responseVerifiers = new ArrayList<RestResponseVerifier>();
    
    /**
     * Converter for {@link IOException} which might occur during REST communication
     */
    private RestIOExceptionConverter ioExceptionConverter = new DefaultRestIOExceptionConverter();
        
    /**
     * Execute a request, return raw {@link JsonNode} as response
     */
    private JsonAndHeaderHolder executeRequestForJson(HttpClient client, HttpUriRequest request, Collection<String> relevantHeaders) throws ExternalExtractorException {
        HttpResponse response;
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Send HTTP request
            LOGGER.debug("Remote request: {}", request.getURI());
            
            request.addHeader("Accept", MediaType.APPLICATION_JSON_VALUE);
            response = client.execute(request);
            
            final String responseText = EntityUtils.toString(response.getEntity());
            LOGGER.debug("Response: {}", responseText);
            
            // Parse relevant headers
            Map<String,String> headers = new HashMap<String,String>();
            if(relevantHeaders != null){
                for(Header header : response.getAllHeaders()){
                    if(relevantHeaders.contains(header.getName())){
                        headers.put(header.getName(), header.getValue());
                    }
                }
            }

            JsonNode rootNode = null;
            try {
                rootNode = mapper.readValue(responseText, JsonNode.class);                
            } catch (JsonParseException e) {
                LOGGER.debug("Json returned from {} couldn't be parsed", request.getURI(), e);
            } catch (JsonMappingException e) {
                LOGGER.debug("Json is invalid", request.getURI(), e);
            }
            
            // Verify JSON response
            for(RestResponseVerifier verifier : responseVerifiers){
                verifier.verifyJsonResponse(response, rootNode);
            }
            
            // Never allow null to be returned, just to be sure...
            //
            // Normally we'd prefer the httpOkStatusVerifier to catch
            // these problems and give a more comprehensible error message
            if(rootNode == null) {
                throw new ExternalApiException("Response verification failure, JSON parse error");
            }

            return new JsonAndHeaderHolder(rootNode, headers);
        } catch (IOException ioException) {
            LOGGER.debug("IOException while trying to communicate using REST", ioException);
            throw ioExceptionConverter.convertException(ioException);
        }
    }
    
    /**
     * Execute request
     */
    protected ExternalResponse executeRequest(HttpClient client, HttpUriRequest request, Collection<String> relevantHeaders) throws ExternalExtractorException {
        JsonAndHeaderHolder result = executeRequestForJson(client, request, relevantHeaders);
        return new ExternalResponse(new ExternalJsonDTO(result.getContent()), result.getHeaders());
    }
    
    /**
     * Execute a request with default http client
     */
    protected ExternalResponse executeRequest(HttpUriRequest request, Collection<String> relevantHeaders) throws ExternalExtractorException {
        return executeRequest(new DefaultHttpClient(), request, relevantHeaders);
    }
    
    /**
     * Execute a request with default http client
     */
    protected ExternalResponse executeRequest(HttpUriRequest request) throws ExternalExtractorException {
        return executeRequest(new DefaultHttpClient(), request, null);
    }

    /**
     * Execute a request with default http client
     */
    protected JsonNode executeRequestForJson(HttpUriRequest request) throws ExternalExtractorException {
        return executeRequestForJson(new DefaultHttpClient(), request, null).getContent();
    }

    /**
     * Adds OAuth authorization to a client
     */
    protected void addOauthAuthorization(HttpUriRequest request, String oauthToken){
        request.addHeader(AUTHORIZATION_HEADER, "OAuth " + oauthToken);
    }
    
    /**
     * Adds Basic Authentication authorization to a client
     */
    protected void addBasicAuthorization(HttpUriRequest request, String userName, String password){
        try {
            request.addHeader(AUTHORIZATION_HEADER, "Basic " + new String(Base64.encodeBase64((userName + ":" + password).getBytes("UTF-8"))));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void setRestVerifiers(RestResponseVerifier... values){
        this.responseVerifiers = Arrays.asList(values);
    }
    
    protected void setIOExceptionConverter(RestIOExceptionConverter ioExceptionConverter) {
        this.ioExceptionConverter = ioExceptionConverter;
    }

    /**
     * Class holding a JsonNode and a Map for headers
     */
    private class JsonAndHeaderHolder {
        /**
         * Response's JSON content
         */
        private JsonNode content;
        
        /**
         * Response's relevant HTTP headers
         */
        private Map<String,String> headers;

        public JsonAndHeaderHolder(JsonNode content, Map<String, String> headers) {
            super();
            this.content = content;
            this.headers = headers;
        }

        public JsonNode getContent() {
            return content;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }
    }
    
    /**
     * Default implementation of the REST IO Exception converter,
     * which calls the {@link ExternalExtractorException#ExternalExtractorException(Throwable)}
     */
    private class DefaultRestIOExceptionConverter implements RestIOExceptionConverter {
        /**
         * {@inheritDoc}
         */
        @Override
        public ExternalExtractorException convertException(IOException occuredException) {
            return new ExternalExtractorException(occuredException);
        }
    }
}
