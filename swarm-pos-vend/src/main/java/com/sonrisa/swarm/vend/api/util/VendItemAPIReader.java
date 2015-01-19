package com.sonrisa.swarm.vend.api.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.vend.VendAccount;
import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.reader.ExternalDataKeyResolver;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;

/**
 * {@link ExternalAPIReader} implementation for Vend which reads single items.
 * 
 * @author Barnabas
 */
@Component("vendItemAPIReader")
public class VendItemAPIReader implements ExternalAPIReader<VendAccount>{
    
    /**
     * Data source
     */
    private ExternalAPI<VendAccount> api;
    
    /**
     * Data key resolver common for Vend
     */
    private ExternalDataKeyResolver<VendAccount> dataKeyResolver = new VendDataKeyResolver();

    /**
     * Initialize reader
     * @param api API to read content from
     */
    @Autowired
    public VendItemAPIReader(@Qualifier("vendAPI") ExternalAPI<VendAccount> api) {
        this.api = api;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalResponse getPage(ExternalCommand<VendAccount> command, int page) throws ExternalExtractorException {
        if(page != 0){
            throw new IllegalArgumentException("Item reader can only read from the first page");
        }
        
        return api.sendRequest(command);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalDTOPath getDataKey(ExternalCommand<VendAccount> command) {
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
