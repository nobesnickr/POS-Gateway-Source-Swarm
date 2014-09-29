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

import org.apache.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.swarm.posintegration.api.util.RestResponseVerifier;
import com.sonrisa.swarm.posintegration.exception.ExternalApiException;

/**
 * Simple verifier for JsonField
 * @author Barnabas
 *
 */
public class SimpleVerifierDecorator implements RestResponseVerifier  {
    
    /**
     * Inspected key, if present throws exception, otherwise not
     */
    private String inspectedKey;
    
    /**
     * Embedded verifier
     */
    private RestResponseVerifier embeddedVerfier = null;

    /**
     * Initialize by setting key to be inspected
     * @param inspectedKey
     */
    public SimpleVerifierDecorator(String inspectedKey){
        this(inspectedKey, null);
    }
    
    /**
     * Initialize by setting key to be inspected and how
     * @param inspectedKey
     */
    public SimpleVerifierDecorator(String inspectedKey, RestResponseVerifier embeddedVerifier) {
        this.inspectedKey = inspectedKey;
        this.embeddedVerfier = embeddedVerifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void verifyJsonResponse(HttpResponse response, JsonNode rootNode) throws ExternalApiException {
        JsonNode node = rootNode != null ? rootNode.get(inspectedKey) : null;
        if(node != null){
            // If custom verifier is set
            if(embeddedVerfier != null){
                embeddedVerfier.verifyJsonResponse(response, node);
            } else {
                throw new ExternalApiException("Illegal JSON response");
            }
        }
    }
}
