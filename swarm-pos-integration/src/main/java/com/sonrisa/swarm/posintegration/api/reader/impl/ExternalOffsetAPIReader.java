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

package com.sonrisa.swarm.posintegration.api.reader.impl;

import java.util.HashMap;
import java.util.Map;

import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;

/**
 * Base class for multi page data sources, using offset key
 */
public class ExternalOffsetAPIReader<T extends SwarmStore> extends BaseExternalAPIReader<T> {

    /** Request parameter setting the page */
    private String offsetKey = "skip";
    
    /** Request parameter setting the size of the result set */
    private String fetchSizeKey = "take";

    /**
     * {@inheritDoc}
     * @param api
     */
    public ExternalOffsetAPIReader(ExternalAPI<T> api) {
        super(api);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalResponse getPage(ExternalCommand<T> command, int page) throws ExternalExtractorException{
        
        Map<String,String> params = new HashMap<String,String>(command.getParams());
        
        // Specify how many items we wish to fetch
        if (!params.containsKey(fetchSizeKey)) {
            params.put(fetchSizeKey, Integer.toString(getFetchSize()));
        } else {
            throw new IllegalStateException("Parameters already contain " + fetchSizeKey);
        }

        // Specify how many items to skip
        params.put(offsetKey, Integer.toString(page * getFetchSize()));

        // Execute query
        ExternalResponse response = api.sendRequest(command.withParams(params));
        
        // This should be handled within the API class itself,
        // unless the API class is a mock object
        if(response == null){
            throw new ExternalExtractorException("External API returned an illegal value for: " + command.withParams(params));
        }
        
        return response;
    }

    /**
     * Offset key is the param for API to tell how many items to skip
     * @return
     */
    public String getOffsetKey() {
        return offsetKey;
    }

    /**
     * Offset key is the param for API to tell how many items to skip
     * @return
     */
    public void setOffsetKey(String offsetKey) {
        this.offsetKey = offsetKey;
    }

    /**
     * Fetch size key is the param for API to tell how many items there should be on a page
     * @return
     */
    public String getFetchSizeKey() {
        return fetchSizeKey;
    }

    /**
     * Fetch size key is the param for API to tell how many items there should be on a page
     * @return
     */
    public void setFetchSizeKey(String fetchSizeKey) {
        this.fetchSizeKey = fetchSizeKey;
    }
}
