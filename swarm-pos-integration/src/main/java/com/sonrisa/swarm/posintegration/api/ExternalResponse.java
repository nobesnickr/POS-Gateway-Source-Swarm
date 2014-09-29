/*
 *  Copyright (c) 2014 Sonrisa Informatikai Kft. All Rights Reserved.
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
package com.sonrisa.swarm.posintegration.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;

/**
 * Return value of the {@link ExternalAPI#sendRequest(ExternalCommand)} which
 * encapsulates an {@link ExternalDTO} (e.g. a JSON) and the HTTP headers. 
 */
public class ExternalResponse {

    /**
     * Response body
     */
    private ExternalDTO content;

    /**
     * Response header fields,
     * not all, just the ones relevant to POS integration.
     */
    private Map<String, String> headers;

    /**
     * Create new instance of API response
     * @param content Response body
     * @param headers Relevant response headers
     */
    public ExternalResponse(ExternalDTO content, Map<String, String> headers) {
        super();
        this.content = content;
        this.headers = headers;
    }
    
    /**
     * Create new instance of API response
     * @param content Response body
     */
    public ExternalResponse(ExternalDTO content) {
        this(content, new HashMap<String,String>());
    }

    public ExternalDTO getContent() {
        return content;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    @Override
    public String toString() {
        return content.toString();
    }
}
