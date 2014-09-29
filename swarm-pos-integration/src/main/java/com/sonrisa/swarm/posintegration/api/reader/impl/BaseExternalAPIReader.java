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

import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.reader.ExternalDataKeyResolver;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;

/**
 * Data source of {@link ExternalDTO} entities 
 */
public abstract class BaseExternalAPIReader<T extends SwarmStore> implements ExternalAPIReader<T> {
    
    /**
     * API used to access remote data
     */
    protected ExternalAPI<T> api;
    
    /**
     * We assume that the ExternalDTO retrieved using {@link ExternalCommand} has
     * meta fields and its data is within dataKey
     */
    private ExternalDataKeyResolver<T> dataKeyResolver;
    
    /**
     * Number of items per page. 
     * Warning: this shouldn't exceed the limitation of the REST API
     */
    private int fetchSize;
    
    /**
     * API reader requires an API to access remote data
     * @param api
     */
    public BaseExternalAPIReader(ExternalAPI<T> api) {
        this.api = api;
        this.fetchSize = 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFetchSize(){
        return this.fetchSize;
    }

    /**
     * Set fetch size, the number of items an a single page
     * 
     * @param fetchSize Non-negative value
     */
    public void setFetchSize(int fetchSize) {
        if(fetchSize <= 0){
            throw new IllegalArgumentException("defaultFetchSize has to be a positive integer");
        }
    
        this.fetchSize = fetchSize;
    }
     
    /** 
     * {@inheritDoc}
     */
    @Override
    public ExternalDTOPath getDataKey(ExternalCommand<T> command){
        if(dataKeyResolver == null){
            throw new IllegalStateException("No datakey resolver defined");
        }
        return dataKeyResolver.getDataKey(command);
    }

    /**
     * Returns the data key resolver
     * @return
     */
    public ExternalDataKeyResolver<T> getDataKeyResolver() {
        return dataKeyResolver;
    }

    /**
     * Sets the data key resolver
     * @param dataKeyResolver
     */
    public void setDataKeyResolver(ExternalDataKeyResolver<T> dataKeyResolver) {
        this.dataKeyResolver = dataKeyResolver;
    }
}
