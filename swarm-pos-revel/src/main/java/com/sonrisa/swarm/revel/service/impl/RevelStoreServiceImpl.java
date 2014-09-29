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
package com.sonrisa.swarm.revel.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.admin.service.BaseStoreRegistrationService;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.request.SimpleApiRequest;
import com.sonrisa.swarm.posintegration.api.service.exception.StoreScanningException;
import com.sonrisa.swarm.posintegration.exception.ExternalPageIterationException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.revel.RevelAccount;
import com.sonrisa.swarm.revel.service.RevelStoreService;

/**
 * Implementation of the {@link RevelStoreService} class.
 */
@Service
public class RevelStoreServiceImpl extends BaseStoreRegistrationService implements RevelStoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RevelStoreServiceImpl.class);
    
    /**
     * JSON Key in the establishment response for store name
     */
    private static final String ESTABLISHMENT_NAME_KEY = "name";
    
    /**
     * JSON Key in the establishment response for its id, used for store_filter
     */
    private static final String ESTABLISHMENT_ID_KEY = "id";
    
    /**
     * JSON Key in the the timezone
     */
    private static final String ESTABLISHMENT_TIMEZONE_KEY = "time_zone";

    /**
     * Key of the Revel API in the apis table
     */
    @Value("${api.name.revel}")
    private String revelApiName;
    
    /**
     * RevelAPI reader instance used to access the remote server
     */
    @Qualifier("revelAPIReader") 
    @Autowired
    private ExternalAPIReader<RevelAccount> apiReader;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RevelAccount getAccount(String userName, String apiKey, String apiSecret) {
        RevelAccount account = new RevelAccount();
        account.setUsername(userName);
        account.setApiKey(apiKey);
        account.setApiSecret(apiSecret);
        return account;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public StoreEntity getRootStoreEntity(RevelAccount account) {
        StoreEntity store = findOrCreateStore(
                account.getUsername(), 
                account.getAccountId(), 
                "", 
                revelApiName);

        store.setApiKey(aesUtility.aesEncryptToBytes(account.getApiKey()));
        store.setPassword(aesUtility.aesEncryptToBytes(account.getApiSecret()));
        
        return store;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<StoreEntity> scanForLocations(RevelAccount dummyAccount) throws StoreScanningException {
        
        Iterable<ExternalDTO> establishments = new SimpleApiRequest<RevelAccount>(
                apiReader, new ExternalCommand<RevelAccount>(dummyAccount, ESTABLISHMENT_URI));
        
        final String userName = dummyAccount.getUsername();

        List<StoreEntity> retVal = new ArrayList<StoreEntity>();
        try {
            for(ExternalDTO establishment : establishments){
                if(!establishment.hasKey(ESTABLISHMENT_NAME_KEY)){
                    throw new StoreScanningException("Failed to save establishment, because name field is missing for: " + userName);
                }
                if(!establishment.hasKey(ESTABLISHMENT_ID_KEY)){
                    throw new StoreScanningException("Failed to save establishment, because id field is missing for: " + userName);
                }
                if(!establishment.hasKey(ESTABLISHMENT_TIMEZONE_KEY)){
                    throw new StoreScanningException("Failed to save establishment, because timezone field is missing for: " + userName);
                }
                
                final String subStoreName = establishment.getText(ESTABLISHMENT_NAME_KEY);
                final String establishmentFilter = establishment.getText(ESTABLISHMENT_ID_KEY);
                final String timezone = establishment.getText(ESTABLISHMENT_TIMEZONE_KEY);

                StoreEntity store = findOrCreateStore(
                                        storeNameForUsernameAndSubStoreName(userName, subStoreName), 
                                        userName, 
                                        establishmentFilter, 
                                        revelApiName);
                
                store.setApiKey(aesUtility.aesEncryptToBytes(dummyAccount.getApiKey()));
                store.setPassword(aesUtility.aesEncryptToBytes(dummyAccount.getApiSecret()));
                store.setTimeZone(timezone);
                
                retVal.add(store);
            }
            return retVal;
        } catch (ExternalPageIterationException e) {
            LOGGER.warn("Iteration failed over Revel establishments", e);
            throw new StoreScanningException(e.getCauseExternalException().getUserFriendlyError());
        } 
    }
    
    
    // ------------------------------------------------------------------------
    // ~ Private methods
    // ------------------------------------------------------------------------
    
    /**
     * Generates store name for Revel store instances 
     */
    private static String storeNameForUsernameAndSubStoreName(final String userName, final String subStoreName){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(userName);
        if(!StringUtils.isEmpty(subStoreName)){
            stringBuilder.append(" - ");
            stringBuilder.append(subStoreName);
        }
        return stringBuilder.toString();
    }
    
    /**
     * @param revelApiName the revelApiName to set
     */
    public void setRevelApiName(String revelApiName) {
        this.revelApiName = revelApiName;
    }

    public void setApiReader(ExternalAPIReader<RevelAccount> apiReader) {
        this.apiReader = apiReader;
    }
}
