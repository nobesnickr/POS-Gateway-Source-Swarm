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
package com.sonrisa.swarm.erply;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.reader.impl.ExternalPagedAPIReader;
import com.sonrisa.swarm.posintegration.api.reader.impl.SimpleDataKeyResolver;
import com.sonrisa.swarm.posintegration.api.reader.util.ExternalBackoffApiReader;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;

/**
 * API reader for Erply, responsible for reading Erply content page by page 
 * 
 * @author Barnabas
 */
@Component("erplyAPIReader")
public class ErplyAPIReader implements ExternalAPIReader<ErplyAccount>  {

    private ExternalAPIReader<ErplyAccount> reader;
    
    private ExternalPagedAPIReader<ErplyAccount> baseReader;

    @Autowired
    public ErplyAPIReader(@Qualifier("erplyAPI") ExternalAPI<ErplyAccount> api) {
        baseReader = new ExternalPagedAPIReader<ErplyAccount>(api);
        baseReader.setDataKeyResolver(new SimpleDataKeyResolver<ErplyAccount>("records"));
        baseReader.setFetchSize(100);
        baseReader.setFetchSizeKey("recordsOnPage");
        baseReader.setPageNoKey("pageNo");
        
        // Wrap the baseReader in a backoff api reader to prepare for service denial
        ExternalBackoffApiReader<ErplyAccount> wrapper = new ExternalBackoffApiReader<ErplyAccount>(baseReader);
        wrapper.setInitialBackOffMilliseconds(5000);
        wrapper.setTotalBackOff(120);
        this.reader = wrapper;
        
    }

    @Override
    public ExternalResponse getPage(ExternalCommand<ErplyAccount> command, int page) throws ExternalExtractorException {
        return reader.getPage(command, page);
    }

    @Override
    public ExternalDTOPath getDataKey(ExternalCommand<ErplyAccount> command) {
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
