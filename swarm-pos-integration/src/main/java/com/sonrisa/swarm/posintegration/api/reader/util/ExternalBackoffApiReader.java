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

package com.sonrisa.swarm.posintegration.api.reader.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.exception.ExternalDeniedServiceException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;

/**
 * Decorator class for {@link ExternalAPIReader} which retries fetching the page if
 * {@link ExternalDeniedServiceException} is thrown (e.g. when Shopify says <code>403 - Too many requests</code> 
 */
public class ExternalBackoffApiReader<T extends SwarmStore> implements ExternalAPIReader<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalBackoffApiReader.class);
    
    /** Sleep for this amount of time on the first service denial */
    public static final int DEFAULT_INITIAL_BACKOFF_MILLISECONDS = 500;
    
    /** Retry with x = x*2 backoff time this many times, default settings will cause timeout after 31.5 seconds */
    public static final int DEFAULT_EXPONENTIAL_BACKOFF_LIMIT = 5;
    
    /** Sleep for this amount of time on the first service denial */
    private int backOffMilliseconds = DEFAULT_INITIAL_BACKOFF_MILLISECONDS;
    
    /** Retry with x = x*2 backoff time this many times */
    private int backOffLimit = DEFAULT_EXPONENTIAL_BACKOFF_LIMIT;
    
    /**
     * Actual reader reading data
     */
    private ExternalAPIReader<T> actualReader;
    

    public ExternalBackoffApiReader(ExternalAPIReader<T> actualReader) {
        this.actualReader = actualReader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalResponse getPage(ExternalCommand<T> command, int page) throws ExternalExtractorException {
        int backOff = backOffMilliseconds;
        
        for(int i = 0; i <= backOffLimit; i++){
            try {
                return actualReader.getPage(command, page);
            } catch (ExternalDeniedServiceException e){
                LOGGER.debug("Remote server denied service for {}, backing off for {} ms.", command, backOff, e);
                try {
                    Thread.sleep(backOff);
                    
                    // Exponentially raise the amount of time to back off
                    backOff = backOff * 2;
                } catch (InterruptedException ie) {
                    LOGGER.warn("Request backoff unexpectedly interrupted for {}", command, ie);
                    throw new RuntimeException(ie);
                }
            }
        }
        
        throw new ExternalExtractorException(String.format("Failed to retrieve %s from remote", command.getUrlQueryString()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalDTOPath getDataKey(ExternalCommand<T> command) {
        return actualReader.getDataKey(command);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFetchSize() {
        return actualReader.getFetchSize();
    }

    /**
     * Set the initial backoff, all subsequent backoffs will be the 2x, 4x, 8x, etc.
     * @param backOffMilliseconds
     */
    public void setInitialBackOffMilliseconds(int backOffMilliseconds) {
        this.backOffMilliseconds = backOffMilliseconds;
    }
    
    /**
     * Set the number of seconds before giving up. Note that this
     * will not be exactly the value set but between backOffSeconds and 2*backOffSeconds - backOffMilliseconds
     * 
     * @param backOffSeconds
     */
    public void setTotalBackOff(int backOffSeconds){
        /*
         *                                      backOffLimit+1
         * solving backbackOffMilliseconds * (2^               - 1) = 1000 * backOffSeconds
         */
        this.backOffLimit = (int)Math.ceil(Math.log(1000.0 * backOffSeconds / backOffMilliseconds)/Math.log(2) - 1);
    }

    /**
     * Set the total number of trials before giving up
     * @param backOffLimit
     */
    public void setBackOffLimit(int backOffLimit) {
        this.backOffLimit = backOffLimit;
    }

    public int getBackOffMilliseconds() {
        return backOffMilliseconds;
    }

    public int getBackOffLimit() {
        return backOffLimit;
    }
}
