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

import org.apache.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.swarm.posintegration.exception.ExternalApiException;

/**
 * Plugin for {@link BaseRestAPI} to verify the correctness of a response JSON
 */
public interface RestResponseVerifier {

    /**
     * Verifies that response JSON is valid JSON response, or
     * throws {@link ExternalApiException}
     */
    void verifyJsonResponse(HttpResponse response, JsonNode rootNode) throws ExternalApiException;
    
}
