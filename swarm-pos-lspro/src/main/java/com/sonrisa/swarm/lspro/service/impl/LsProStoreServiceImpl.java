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

package com.sonrisa.swarm.lspro.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.lspro.LsProAccount;
import com.sonrisa.swarm.lspro.service.LsProStoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.admin.service.BaseStoreRegistrationService;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.request.SimpleApiRequest;
import com.sonrisa.swarm.posintegration.api.service.exception.StoreScanningException;
import com.sonrisa.swarm.posintegration.exception.ExternalPageIterationException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;

/**
 * LsProStoreService implementation
 */
@Service("lsProStoreServiceImpl")
public class LsProStoreServiceImpl  extends BaseStoreRegistrationService implements LsProStoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LsProStoreServiceImpl.class);
        
    /**
     * Limit for scanning locations
     */
    private static final int LOCATION_SCAN_LIMIT = 200;
    
    /**
     * Used API to access the Lightspeed Pro REST service
     */
    @Autowired
    @Qualifier("lsProAPIReader")
    private ExternalAPIReader<LsProAccount> apiReader;
    
    /**
     * Name of the Lightspeed Pro API
     */
    @Value("${api.name.lspro}")
    private String lsProApiName;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public LsProAccount getStore(String userName, String password) {
        LsProAccount account = new LsProAccount();
        account.setUserName(userName);
        account.setPassword(password);
        return account;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<StoreEntity> scanForLocations(LsProAccount dummyAccount) throws StoreScanningException {
        
        if(StringUtils.isEmpty(dummyAccount.getUsername())){
            throw new IllegalArgumentException("Username is missing for account");
        }
        
        if(StringUtils.isEmpty(dummyAccount.getPassword())){
            throw new IllegalArgumentException("Password is missing for account");
        }
                  
        Set<String> locations = new HashSet<String>();
        List<StoreEntity> retVal = new ArrayList<StoreEntity>();
        
        ExternalDTO invoice = findInvoiceExcludingLocations(dummyAccount, locations);
        
        // If store is empty
        if(invoice == null){
            StoreEntity store = findOrCreateStore(null,dummyAccount.getUsername(),null,lsProApiName);
            store.setPassword(aesUtility.aesEncryptToBytes(dummyAccount.getPassword()));
            retVal.add(store);
            
            LOGGER.debug("Found single-location LsPro store: {}", store);
            
            return retVal;
        } else {
            int loopCount = 0;
            do {
                // For safety
                if(loopCount >= LOCATION_SCAN_LIMIT){
                    throw new StoreScanningException("Number of location exceeds the limit: " + LOCATION_SCAN_LIMIT);
                }
                loopCount++;
                
                final String location = invoice.getText("LocationName");
                
                if(StringUtils.isEmpty(location)){
                    throw new StoreScanningException("Unexpected LocationName value");
                }
                
                if(locations.contains(location)){
                    throw new StoreScanningException("Filtering based on LocationName failed");
                }
                
                locations.add(location);
                
                StoreEntity store = findOrCreateStore(null,dummyAccount.getAccountId(),location,lsProApiName);
                store.setPassword(aesUtility.aesEncryptToBytes(dummyAccount.getPassword()));
                retVal.add(store);
                LOGGER.debug("Found multi-location LsPro store: {}", store);
                
                // Prepare for next loop
                invoice = findInvoiceExcludingLocations(dummyAccount, locations);
                
            } while(invoice != null);
        }
        
        return retVal;
    }
    
    /**
     * Returns invoices excluding certain locations
     */
    private ExternalDTO findInvoiceExcludingLocations(LsProAccount account, Collection<String> locations) throws StoreScanningException{
        
        Map<String,String> params = new HashMap<String,String>();

        // Build strings like (LocationName)ne('ABC')and(LocationName)ne('DEF') 
        // "ne" is not equals
        StringBuilder filter = new StringBuilder();
        for(String location : locations){
            if(filter.length() > 0){
                filter.append("and");
            }
            
            filter.append("(LocationName)ne('").append(location).append("')");
        }
        params.put("$filter", filter.toString());
        
        ExternalCommand<LsProAccount> command = new ExternalCommand<LsProAccount>(account, "Invoices", params);
        Iterable<ExternalDTO> invoices = new SimpleApiRequest<LsProAccount>(apiReader, command);
        
        // Read first from result
        try {
            Iterator<ExternalDTO> invoiceIterator = invoices.iterator();
            if(invoiceIterator.hasNext()){
                return invoiceIterator.next();
            }
        } catch (ExternalPageIterationException e){
            throw new StoreScanningException("Error while reading remote data -> " + e.getMessage(), e);
        }
        
        return null;
    }

    public void setApiReader(ExternalAPIReader<LsProAccount> apiReader) {
        this.apiReader = apiReader;
    }

    public void setLsProApiName(String lsProApiName) {
        this.lsProApiName = lsProApiName;
    }
}
