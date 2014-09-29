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
 * {@link ExternalAPIReader} implementation for Kounta which reads single items.
 * 
 * @author Barnabas
 */
@Component("kountaItemAPIReader")
public class KountaItemAPIReader implements ExternalAPIReader<KountaAccount>{
    
    /**
     * Data source
     */
    private ExternalAPI<KountaAccount> api;
    
    /**
     * Data key resolver common for Kounta
     */
    private ExternalDataKeyResolver<KountaAccount> dataKeyResolver = new KountaDataKeyResolver();

    /**
     * Initialize reader
     * @param api API to read content from
     */
    @Autowired
    public KountaItemAPIReader(@Qualifier("kountaAPI") ExternalAPI<KountaAccount> api) {
        this.api = api;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalResponse getPage(ExternalCommand<KountaAccount> command, int page) throws ExternalExtractorException {
        if(page != 0){
            throw new IllegalArgumentException("Item reader can only read from the first page");
        }
        
        return api.sendRequest(command);
    }

    /**
     * {@inheritDoc}
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
        return 0;
    }
}
