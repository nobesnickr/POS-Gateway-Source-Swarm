package com.sonrisa.swarm.vend.api.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.request.SimpleExternalDTOIterator;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;
import com.sonrisa.swarm.vend.VendAccount;

/**
 * API reader responsible for reading a single page from Vend
 */
@Component("vendAPIReader")
public class VendAPIReader implements ExternalAPIReader<VendAccount> {
	
    /**
     * Key for the number of total pages
     */
    public static final String PAGE_COUNT_KEY = "pages";

    /**
     * Key for the current page, starting from 1...PAGE_NUMBER_KEY
     */
    public static final String PAGE_NUMBER_KEY = "page";
        
    /**
     * Key for the date filter key
     */
    public static final String DATE_KEY = "since";
    
    /**
     * Key for the date filter key
     */
    public static final String PAGE_SIZE_KEY = "page_size";
    
    /**
     * API used to access data
     */
    private ExternalAPI<VendAccount> api;
    
    /**
     * Vend fetch size
     */
    private int fetchSize = 50;
            
    /**
     * Initialize by setting data source
     * @param api
     */
    @Autowired
    public VendAPIReader(@Qualifier("vendAPI") ExternalAPI<VendAccount> api) {
        this.api = api;
    }

	private static final Logger LOGGER = LoggerFactory.getLogger(ExternalAPIReader.class);
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalResponse getPage(ExternalCommand<VendAccount> command, int page) throws ExternalExtractorException {
    	// Removing previous page from fields and adding the new one
    	Map<String, String> params = new HashMap<String, String>();
    	params.putAll(command.getParams());
    	params.remove(PAGE_NUMBER_KEY);
    	params.put(PAGE_NUMBER_KEY, Integer.toString(page));
    	params.remove(PAGE_SIZE_KEY);
    	params.put(PAGE_SIZE_KEY, Integer.toString(fetchSize));
    	
    	// Recovering the account and URI from the command
    	VendAccount acount = command.getAccount();
    	String uri = command.getURI();
    	
        ExternalCommand<VendAccount> newCommand = new ExternalCommand<VendAccount>(acount, uri, params);
        
        return api.sendRequest(newCommand);
    }
    
    /**
     * Data key resolver for Vend.
     */
    @Override
    public ExternalDTOPath getDataKey(ExternalCommand<VendAccount> command) {
    	
    	final String lowerCasePath = command.getURI().toLowerCase();
        final List<String> acceptedURIs = Arrays.asList("register_sales", "customers", "products", "outlets", "registers");

        if(acceptedURIs.contains(lowerCasePath)){
            return new ExternalDTOPath(lowerCasePath);
        } else {
            return ExternalDTOPath.getRootPath();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFetchSize() {
        return fetchSize;
    }
}
