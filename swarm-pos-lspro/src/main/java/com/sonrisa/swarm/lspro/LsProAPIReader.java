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

package com.sonrisa.swarm.lspro;

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
 * API reader to access the API by page for Lightspeed PRo
 */
@Component("lsProAPIReader")
public class LsProAPIReader implements ExternalAPIReader<LsProAccount> {
    
    private ExternalAPIReader<LsProAccount> reader;
    
    private ExternalOffsetAPIReader<LsProAccount> baseReader;

    @Autowired
    public LsProAPIReader(@Qualifier("lsProAPI") ExternalAPI<LsProAccount> api) {
        baseReader = new ExternalOffsetAPIReader<LsProAccount>(api);
        baseReader.setDataKeyResolver(new SimpleDataKeyResolver<LsProAccount>("value"));
        baseReader.setFetchSize(50);
        baseReader.setFetchSizeKey("$top");
        baseReader.setOffsetKey("$skip");
        
        // Wrap the baseReader in a backoff api reader to prepare for service denial
        ExternalBackoffApiReader<LsProAccount> wrapper = new ExternalBackoffApiReader<LsProAccount>(baseReader);
        wrapper.setInitialBackOffMilliseconds(5000);
        wrapper.setTotalBackOff(120);
        this.reader = wrapper;
        
    }

    @Override
    public ExternalResponse getPage(ExternalCommand<LsProAccount> command, int page) throws ExternalExtractorException {
        return reader.getPage(command, page);
    }

    @Override
    public ExternalDTOPath getDataKey(ExternalCommand<LsProAccount> command) {
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
