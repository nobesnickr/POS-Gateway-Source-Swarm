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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.swarm.posintegration.exception.ExternalDeniedServiceException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalApiPagingRequest;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalJsonDTO;
import com.sonrisa.swarm.posintegration.extractor.util.RestUrlBuilder;

/**
 * Class implementing the ExternalApiPagingRequest class for the Merchant OS
 * API. This class is responsible for retrieving data longer than that of the 
 * limitation of API. It acts as an iterator masking the fact that only 100 records
 * can be downloaded at a time from the remote server.
 */
public class MosRequest extends ExternalApiPagingRequest<ExternalDTO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MosRequest.class);

    /** Request uses this API to send requests */
    private MosAPI mosApi;
    
    /**
     * Account for the request
     */
    private MosAccount account;
    
    /** The base request, e.g. Item */
    private String request;
    
    /** Get fields, e.g. filterField: %3E,{TIMESTAMP} */
    private Map<String,String> getFields;
    
    /** The parameter key for setting the current offset within the items*/
    public static final String REST_OFFSET_KEY = "offset";
    
    /** The parameter key for setting the number of records on a page */
    public static final String REST_RECORDS_ON_PAGE_KEY = "limit";
    
    /** Current fetched records */
    private JsonNode currentRecords = null;
    
    /** Sleep for this amount of time on the first 503 */
    public static final int INITIAL_BACKOFF_MILLISECONDS = 500;
    
    /** Retry with x = x*2 backoff time this many times */
    public static final int EXPONENTIAL_BACKOFF_LIMIT = 5;
    
    /**
     * Initializes an instance of a MosRequest which handles
     * downloading large amounts of data from the remote system
     * @param mosApi The API to be used for requests
     * @param request The request, e.g. Item
     * @param getFields Non-changing get parameters, e.g. filterField
     * @warning request should be equal to JSON root element
     * @note If getFields contains offset, it will be used instead of the default value of 100
     */
    public MosRequest(MosAPI mosApi, MosAccount account, String request, Map<String, String> getFields) {
        super();
        this.mosApi = mosApi;
        this.account = account;
        this.request = request;
        this.getFields = getFields;
    }

    /** Fetch a row from current page (Json array) */
    @Override
    protected ExternalDTO getItemFromCurrentPage(int i) {
        if(currentRecords == null){
            throw new IllegalStateException("Current records shouldn't be null!");
        }
        /**
         * Merchant OS doesn't send an array of 1 elements if only 1 item
         * exists, but sends the map instead of the array of maps
         */
        if(currentRecords.get(i) == null){
            return new ExternalJsonDTO(currentRecords);    
        }
        
        return new ExternalJsonDTO(currentRecords.get(i));
    }
    
    /** Fetch data from the remote system using paging to divide data */
    @Override
    protected void fetchPage(int page) throws ExternalExtractorException {
        // Specify the current page
        getFields.put(REST_OFFSET_KEY, Integer.toString(page * getFetchSize()));
        
        if(!getFields.containsKey(REST_RECORDS_ON_PAGE_KEY)) {
            this.fetchSize = DEFAULT_FETCH_SIZE;
            getFields.put(REST_RECORDS_ON_PAGE_KEY, Integer.toString(fetchSize));
        }
        else {
            setFetchSize(Integer.parseInt(getFields.get(REST_RECORDS_ON_PAGE_KEY)));
        }
        
        String queryUrl = request + "?" + RestUrlBuilder.prepareGetFields(getFields);
        

        JsonNode response = null;
        int backOff = getInitialBackoffMilliseconds();
       
        for(int i = 0; i < getExponentialBackofflimit(); i++){
            try {
                response = mosApi.sendRequest(account, queryUrl);
                break;
            } catch (ExternalDeniedServiceException e){
                LOGGER.debug("Merchant OS remote server denied service, backing off for {} ms.", backOff, e);
                try {
                    Thread.sleep(backOff);
                    
                    // Exponentially raise the amount of time to back off
                    // should reach 2 * 2^5 = 64s, the Merchant OS API 
                    // suggests backing off for about 60s upon denial
                    backOff = backOff * 2;
                } catch (InterruptedException ie) {
                    LOGGER.warn("Merchant os request backoff unexpectedly interrupted", ie);
                    throw new RuntimeException(ie);
                }
            }
        }
        
        // Retrying didn't help
        if(response == null){
            throw new ExternalExtractorException(String.format("Failed to retrieve %s from Merchant OS", queryUrl));
        }
        
        setRecordCount(response.get("@attributes").get("count").asInt());
        currentRecords = response.get(request);
    }

    /**
     * @return the initialbackoffmilliseconds
     */
    public int getInitialBackoffMilliseconds() {
        return INITIAL_BACKOFF_MILLISECONDS;
    }

    /**
     * @return the exponentialbackofflimit
     */
    public int getExponentialBackofflimit() {
        return EXPONENTIAL_BACKOFF_LIMIT;
    }
}
