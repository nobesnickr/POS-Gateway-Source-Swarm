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

package com.sonrisa.swarm.posintegration.api.util.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.nebhale.jsonpath.JsonPath;
import com.sonrisa.swarm.posintegration.api.util.JSONFieldReader;
import com.sonrisa.swarm.posintegration.api.util.RestResponseVerifier;
import com.sonrisa.swarm.posintegration.exception.ExternalApiBadCredentialsException;
import com.sonrisa.swarm.posintegration.exception.ExternalApiException;
import com.sonrisa.swarm.posintegration.exception.ExternalDeniedServiceException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;


/**
 * Implementation of the {@link RestResponseVerifier} verifying that a certain JSON key isn't present
 * @author Barnabas
 *
 */
public class SimpleRestVerifier<E extends ExternalApiException> extends BaseRestResponseVerifier<E> implements RestResponseVerifier {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleRestVerifier.class);
    
    /**
     * Error keys scanned, <code>JSON path</code> -> <code>Regular expression</code>
     */
    private Map<String,String> scannedPaths = new HashMap<String,String>();
    
    /**
     * These additional JSON fields will be parsed to provide better exception message
     * <code>JSON path<code> -> <code>JSON reader</code>
     */
    private Map<String, JSONFieldReader> metaFields = new HashMap<String,JSONFieldReader>();

    /**
     * Inspected errorCode
     */
    private Set<Integer> errorStatusCodes = null;
    
    /**
     * Only these value are accepted if set isn't empty
     */
    private Set<Integer> expectedStatusCodes = null;

    /**
     * Format for the error message when the HTTP status code fails.
	 * <ul>
     *  <li>The <code><%REASON%></code> will be replaced with the HTTP reason phrase</li>
     *  <li>The <code><%JSON-ERROR%></code> will be replaced with the error parsed from the JSON response</li>
	 * </ul>
     */
    private String statusFailureFormat = "API says: <%REASON%> <%JSON-ERROR%>";
    
    /**
     * Private constructor, use factory methods instead
     */
    private SimpleRestVerifier(Class<E> exceptionThrown){
        super(exceptionThrown);
    }
    
    /**
     * Initialize a {@link RestResponseVerifier} which fails everything
     */
    public static <T extends ExternalApiException> SimpleRestVerifier<T> forAnyPath(Class<T> exceptionThrown){
        return SimpleRestVerifier.forPathMatches(exceptionThrown, "$", ".*");
    }
        
    /**
     * Initialize by setting the JSON path inside the rootKey inspected
     * @param matchedKey
     */
    public static <T extends ExternalApiException> SimpleRestVerifier<T> forPathExists(Class<T> exceptionThrown, String matchedPath){
        return SimpleRestVerifier.forPathMatches(exceptionThrown, matchedPath, ".*");
    }
    
    /**
     * Initialize by setting what the JSON path is expected to be
     * @param matchedKey
     */
    public static <T extends ExternalApiException> SimpleRestVerifier<T> forPathEquals(Class<T> exceptionThrown, String matchedPath, String matchedValue){
        return SimpleRestVerifier.forPathMatches(exceptionThrown, matchedPath, Pattern.quote(matchedValue));
    }
    
    /**
     * Initialize by setting what the JSON path is expected to match
     * @param matchedKey
     */
    public static <T extends ExternalApiException> SimpleRestVerifier<T> forPathMatches(Class<T> exceptionThrown, String matchedPath, String matchedValue){
        SimpleRestVerifier<T> retVal = new SimpleRestVerifier<T>(exceptionThrown);
        retVal.scannedPaths.put(matchedPath, matchedValue);
        return retVal;
    }

    /**
     * Verifier checking that the returned status code is HTTP OK
     */
    public static <T extends ExternalApiException> SimpleRestVerifier<T> forOkStatus(Class<T> exceptionThrown){
        return new SimpleRestVerifier<T>(exceptionThrown)
                .withExpectedStatus(HttpStatus.SC_OK);
    }
    
    /**
     * Verifier checking that the returned status code is HTTP OK
     */
    public static SimpleRestVerifier<ExternalApiBadCredentialsException> forUnauthorized(){
        return new SimpleRestVerifier<ExternalApiBadCredentialsException>(ExternalApiBadCredentialsException.class)
                .withErrorStatus(HttpStatus.SC_UNAUTHORIZED);
    }
    
    /**
     * Verifier checking that the returned status code is HTTP OK
     */
    public static SimpleRestVerifier<ExternalDeniedServiceException> forServiceDenied(){
        return new SimpleRestVerifier<ExternalDeniedServiceException>(ExternalDeniedServiceException.class);
    }
    
    /**
     * Verifier checking nothing
     */
    public static <T extends ExternalApiException> SimpleRestVerifier<T> forException(Class<T> exceptionThrown){
        return new SimpleRestVerifier<T>(exceptionThrown);
    }
    
    /**
     * Add meta field to be appended to the exception's message
     * 
     * @param metaFieldName Meta field will appear like this <code>"Name": "Value"</code>
     * @param metaFieldPath Which field to look for, JSON path
     */
    public SimpleRestVerifier<E> withMetaField(String metaFieldName, String metaFieldPath){
        this.metaFields.put(metaFieldPath, new SimpleJSONFieldReader(metaFieldName));
        return this;
    }

    /**
     * Add meta field to be appended to the exception's message
     * 
     * @param metaFieldName Meta field will appear like this <code>"Name": "Value"</code>
     * @param metaFieldPath Which field to look for, JSON path
     */
    public SimpleRestVerifier<E> withMetaField(JSONFieldReader metaFieldReader, String metaFieldPath){
        this.metaFields.put(metaFieldPath, metaFieldReader);
        return this;
    }
    
    /**
     * Expected status codes
     */
    public SimpleRestVerifier<E> withExpectedStatus(Integer... statusCodes){
        this.expectedStatusCodes = new HashSet<Integer>(Arrays.asList(statusCodes));
        return this;
    }
    
    /**
     * Expected error codes
     */
    public SimpleRestVerifier<E> withErrorStatus(Integer... statusCodes){
        this.errorStatusCodes = new HashSet<Integer>(Arrays.asList(statusCodes));
        return this;
    }
    
    /**
     * Set the error message format in case HTTP status code fails
	 * <ul>
     *  <li>The <code><%REASON%></code> will be replaced with the HTTP reason phrase</li>
     *  <li>The <code><%JSON-ERROR%></code> will be replaced with the error parsed from the JSON response</li>
	 * </ul>
     */
    public SimpleRestVerifier<E> withStatusFailureFormat(String format){
        this.statusFailureFormat = format;
        return this;
    }

    /**
     * {@inheritDoc}
     * @throws ExternalExtractorException 
     */
    @Override
    public void verifyJsonResponse(HttpResponse response, JsonNode rootNode) throws ExternalApiException {
        
        // Verify HTTP status
        if(isFailingHttpStatus(response, expectedStatusCodes, errorStatusCodes)){

            String jsonError = "";
            if(rootNode != null){
                jsonError = parseErrorJsonForMessage(metaFields, rootNode);
            } else {
                jsonError = "parsing JSON failed";
            }
            
            // Status failure format has the default value of:
            // API says: <%REASON%> <%JSON-ERROR%>
            final String message = statusFailureFormat
                    .replaceAll(Pattern.quote("<%REASON%>"), response.getStatusLine().getReasonPhrase())
                    .replaceAll(Pattern.quote("<%JSON-ERROR%>"), jsonError);
                    
            
            throw buildException(message);  
        }
        
        if(rootNode == null){
            LOGGER.debug("No verification done for JSON, as rootNode is null");
            return;
        }
        
        for(Entry<String,String> inspectedEntry : scannedPaths.entrySet()){
                        
            final Object node = JsonPath.read(inspectedEntry.getKey(), rootNode.toString(), Object.class);
                        
            if(node instanceof List){
                final List<Object> nodeList = (List<Object>)node;
                for(Object nodeItem : nodeList){
                    if(!verifySingleJsonValue(nodeItem.toString(), inspectedEntry.getValue())){
                        throw buildException(parseErrorJsonForMessage(metaFields, rootNode));
                    }
                }
            } else if(node != null){
                if(!verifySingleJsonValue(node.toString(), inspectedEntry.getValue())){
                    throw buildException(parseErrorJsonForMessage(metaFields, rootNode));
                }
            }
        }
    }
    
    /**
     * Matches a node's value with a regular expression
     */
    private boolean verifySingleJsonValue(String nodeValue, String expectedExpression){
        if(StringUtils.hasLength(nodeValue)){
            if(Pattern.matches(expectedExpression, nodeValue)){
                LOGGER.debug("Aborting because {} as its value is {}", nodeValue, expectedExpression);
                return false;
            }
        }
        return true;
    }
    
    
}
