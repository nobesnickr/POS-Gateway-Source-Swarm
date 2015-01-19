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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.nebhale.jsonpath.JsonPath;
import com.sonrisa.swarm.posintegration.api.util.JSONFieldReader;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;

/**
 * Simple response verifier verifies status codes
 * 
 * @author Barnabas
 */
public abstract class BaseRestResponseVerifier<E extends ExternalExtractorException> {
    
    /**
     * Exception thrown when verification fails
     */
    private Class<E> exceptionClass;
    
    /**
     * Inspected errorCode
     */
    protected BaseRestResponseVerifier(Class<E> exceptionThrown){
        this.exceptionClass = exceptionThrown;
    }
    
    /**
     * Verifies that the HTTP response's status is allowed by the rules defined
     */
    protected boolean isFailingHttpStatus(HttpResponse response, Set<Integer> expectedStatusCodes, Set<Integer> errorStatusCodes){
        if(expectedStatusCodes != null && !expectedStatusCodes.isEmpty()){
            return !expectedStatusCodes.contains(response.getStatusLine().getStatusCode());
        } else if(errorStatusCodes != null){
            return errorStatusCodes.contains(response.getStatusLine().getStatusCode());
        } else {
            return false;
        }
    }
    
    /**
     * Parses error message from error JSON response
     * @param node
     * @return
     */
    protected String parseErrorJsonForMessage(Map<String,JSONFieldReader> metaFields, JsonNode node){
        StringBuilder errorMsg = new StringBuilder();
        
        for(Entry<String,JSONFieldReader> metaEntry : metaFields.entrySet()){
            
            // Meta fields are not accepted to have multiple results,
            // in that case this will throw an IllegalArgumentException
            final Object fieldValue = JsonPath.read(metaEntry.getKey(), node, Object.class);
            
            if(fieldValue != null && StringUtils.hasLength(fieldValue.toString())){

                if (errorMsg.length() != 0) {
                    errorMsg.append(" ");
                }
                
                errorMsg.append(metaEntry.getValue().getMeta(fieldValue.toString()));
            }
        }

        return errorMsg.toString();
    }

    /**
     * Assuming that rootNode is rejected build exception to be thrown
     * @param rootNode
     * @return
     */
    protected E buildException(String message) {
        try {
            return exceptionClass.getConstructor(String.class).newInstance(message);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
        	throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
    }
}
