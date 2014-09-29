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
package com.sonrisa.shopify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.reader.impl.ExternalPagedAPIReader;
import com.sonrisa.swarm.posintegration.api.reader.impl.URIBasedDataKeyResolver;
import com.sonrisa.swarm.posintegration.api.reader.util.ExternalBackoffApiReader;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;

/**
 * API reader to access Shopify REST requests by page
 * 
 * @author Barnabas
 */
@Component("shopifyAPIReader")
public class ShopifyAPIReader implements ExternalAPIReader<ShopifyAccount>{

    private ExternalAPIReader<ShopifyAccount> reader;
    
    private ExternalPagedAPIReader<ShopifyAccount> baseReader;
    
    public static final int PAGE_SIZE = 250;

    @Autowired
    public ShopifyAPIReader(@Qualifier("shopifyAPI") ExternalAPI<ShopifyAccount> api) {
        baseReader = new ExternalPagedAPIReader<ShopifyAccount>(api);
        baseReader.setDataKeyResolver(new URIBasedDataKeyResolver<ShopifyAccount>());
        baseReader.setPageNoKey("page");
        baseReader.setFetchSizeKey("limit");
        baseReader.setFetchSize(PAGE_SIZE);
        
        // Wrap the baseReader in a backoff api reader to prepare for service denial
        ExternalBackoffApiReader<ShopifyAccount> wrapper = new ExternalBackoffApiReader<ShopifyAccount>(baseReader);
        wrapper.setInitialBackOffMilliseconds(500);
        wrapper.setBackOffLimit(5);
        this.reader = wrapper;
        
    }

    @Override
    public ExternalResponse getPage(ExternalCommand<ShopifyAccount> command, int page) throws ExternalExtractorException {
        return reader.getPage(command, page);
    }

    @Override
    public ExternalDTOPath getDataKey(ExternalCommand<ShopifyAccount> command) {
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
