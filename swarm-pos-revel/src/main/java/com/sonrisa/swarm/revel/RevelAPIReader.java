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

package com.sonrisa.swarm.revel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.reader.impl.ExternalOffsetAPIReader;
import com.sonrisa.swarm.posintegration.api.reader.impl.SimpleDataKeyResolver;
import com.sonrisa.swarm.posintegration.api.reader.util.ExternalBackoffApiReader;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;

/**
 * API reader for Revel, responsible for paging over large result sets
 */
@Component("revelAPIReader")
public class RevelAPIReader implements ExternalAPIReader<RevelAccount>{

    private ExternalAPIReader<RevelAccount> reader;
    
    private ExternalOffsetAPIReader<RevelAccount> baseReader;
    
    public static final int PAGE_SIZE = 100;

    @Autowired
    public RevelAPIReader(@Qualifier("revelAPI") ExternalAPI<RevelAccount> api) {
        
        baseReader = new ExternalOffsetAPIReader<RevelAccount>(api);
        baseReader.setDataKeyResolver(new SimpleDataKeyResolver<RevelAccount>("objects"));
        baseReader.setFetchSize(PAGE_SIZE);
        baseReader.setFetchSizeKey("limit");
        baseReader.setOffsetKey("offset");
        
        // Wrap the baseReader in a backoff api reader to prepare for service denial
        ExternalBackoffApiReader<RevelAccount> wrapper = new ExternalBackoffApiReader<RevelAccount>(baseReader);
        wrapper.setInitialBackOffMilliseconds(500);
        wrapper.setBackOffLimit(5);
        this.reader = wrapper;
    }

    @Override
    public ExternalResponse getPage(ExternalCommand<RevelAccount> command, int page) throws ExternalExtractorException {
        return reader.getPage(command, page);
    }

    @Override
    public ExternalDTOPath getDataKey(ExternalCommand<RevelAccount> command) {
        return reader.getDataKey(command);
    }

    @Override
    public int getFetchSize() {
        return reader.getFetchSize();
    }
    
    protected void setFetchSize(int fetchSize) {
        baseReader.setFetchSize(fetchSize);
    }
}
