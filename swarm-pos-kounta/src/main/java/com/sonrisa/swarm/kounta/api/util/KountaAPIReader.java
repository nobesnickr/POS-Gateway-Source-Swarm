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

package com.sonrisa.swarm.kounta.api.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.kounta.KountaAccount;
import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.reader.ExternalDataKeyResolver;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;

/**
 * API reader responsible for reading a single page from Kounta
 * @author Barnabas
 */
@Component("kountaAPIReader")
public class KountaAPIReader implements ExternalAPIReader<KountaAccount> {
    
    /**
     * Response's header containing the number of total pages
     */
    public static final String PAGE_COUNT_KEY = "X-Pages";
    
    /**
     * API used to access data
     */
    private ExternalAPI<KountaAccount> api;
    
    /**
     * Which page, starting from 0...X-Pages-1
     */
    private String pageNoKey = "X-Page";
        
    /**
     * Kounta has fixed fetch size of 25
     */
    private int fetchSize = 25;
    
    /**
     * Data key resolver common for Kounta
     */
    private ExternalDataKeyResolver<KountaAccount> dataKeyResolver = new KountaDataKeyResolver();
        
    /**
     * Initialize by setting data source
     * @param api
     */
    @Autowired
    public KountaAPIReader(@Qualifier("kountaAPI") ExternalAPI<KountaAccount> api) {
        this.api = api;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalResponse getPage(ExternalCommand<KountaAccount> command, int page) throws ExternalExtractorException {
                
        Map<String,String> newConfig = new HashMap<String,String>(command.getConfig());
        newConfig.put(pageNoKey, Integer.toString(page));
        
        return api.sendRequest(command.withConfig(newConfig));
    }
    
    /**
     * Data key resolver for Kounta.
     * 
     * As there is no data key for Kounta this class returns a path to the data
     * to be the top level.
     */
    @Override
    public ExternalDTOPath getDataKey(ExternalCommand<KountaAccount> command) {
        return dataKeyResolver.getDataKey(command);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFetchSize() {
        return fetchSize;
    }    
}
