/*
  Copyright (c) 2014 Sonrisa Informatikai Kft. All Rights Reserved.

 This software is the confidential and proprietary information of
 Sonrisa Informatikai Kft. ("Confidential Information").
 You shall not disclose such Confidential Information and shall use it only in
 accordance with the terms of the license agreement you entered into
 with Sonrisa.

 SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.sonrisa.swarm.rics.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.admin.service.BaseStoreRegistrationService;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.exception.ExternalApiException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.rics.RicsAccount;
import com.sonrisa.swarm.rics.RicsApiReader;
import com.sonrisa.swarm.rics.api.RicsApi;
import com.sonrisa.swarm.rics.constants.RicsUri;
import com.sonrisa.swarm.rics.service.RicsStoreService;
import com.sonrisa.swarm.rics.service.exception.RicsStoreServiceException;

/**
 * Implementation of the Rics Store service, which manages store entities for the RICS POS.
 */
@Service
public class RicsStoreServiceImpl extends BaseStoreRegistrationService implements RicsStoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RicsStoreServiceImpl.class);

    /**
     * Name of the API in the <code>apis</code> table
     */
    @Value("${api.name.rics}")
    private String ricsApiName;

    /**
     * API reader for reading invoices
     */
    @Autowired
    private RicsApiReader ricsApiReader;

    /**
     * {@inheritDoc}
     */
    @Override
    public StoreEntity getStore(RicsAccount dummyAccount) {
        StoreEntity store = findOrCreateStore(dummyAccount.getStoreName(), dummyAccount.getUserName(), dummyAccount.getStoreCode(), ricsApiName);
        
        if(store.getId() != null){
            LOGGER.info("Found existing Rics store for {} with store id:", dummyAccount.getUserName(), store.getId());
        } else {
            LOGGER.info("Creating new Rics store entry for {}", dummyAccount.getUserName());
        }
        
        store.setApiKey(aesUtility.aesEncryptToBytes(dummyAccount.getToken()));
        store.setStoreFilter(dummyAccount.getStoreCode());
        store.setTimeZone(dummyAccount.getTimeZone());
        return store;
    }

    /**
     * {@inheritDoc}
     * @throws RicsStoreServiceException 
     */
    @Override
    public RicsAccount getAccount(String userName, String token, String storeCode, String timeZone) throws RicsStoreServiceException {

        RicsAccount account = new RicsAccount();
        account.setUserName(userName);
        account.setToken(token);
        account.setStoreCode(storeCode);
        account.setTimeZone(timeZone);
        
        LOGGER.info("Resolving account for RICS, userName: {}", userName);
        
        // Store code can't be empty
        if(StringUtils.isEmpty(storeCode)){
            throw new RicsStoreServiceException("Store code missing");
        }
        
        // Timezone has to be valid
        if(StringUtils.isEmpty(timeZone) || !isValidTimeZone(timeZone)){
            throw new RicsStoreServiceException("Invalid timezone: " + timeZone);
        }

        try {
            // Create filter for store
            Map<String, String> params = new HashMap<String, String>();

            params.put("StoreCode", storeCode);
            params.put("BatchStartDate", RicsApi.DATE_MIN);
            params.put("BatchEndDate", RicsApi.DATE_MAX);

            // Read the first page of invoices
            ExternalResponse response = ricsApiReader.getPage(new ExternalCommand<RicsAccount>(account, RicsUri.INVOICES.uri, params), 0);
            Iterable<ExternalDTO> sales = response.getContent().getNestedItems(RicsUri.INVOICES.datakey);

            // Test for first item on the invoice page
            Iterator<ExternalDTO> iterator = sales.iterator();
            if (iterator.hasNext()) {
                account.setStoreName(iterator.next().getText("StoreName"));
                return account;
            } else {
                throw new RicsStoreServiceException(getNoInvoicesError(storeCode));
            }
        } catch (ExternalApiException e) {
            LOGGER.debug("API exception occured for loginName: {}", userName, e);
            throw new RicsStoreServiceException(e.getMessage());
        } catch (ExternalExtractorException e) {
            LOGGER.warn("Failed to resolve account for userName: {}", userName, e);
            throw new RicsStoreServiceException("Unexpected error while trying to communicate with RICS");
        }
    }

    /**
     * Get error message when invoices are missing
     */
    private static final String getNoInvoicesError(String storeCode) {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("No invoices found");
        if (StringUtils.hasLength(storeCode)) {
            errorMsg.append(" for store: ").append(storeCode);
        } else {
            errorMsg.append(" in the store");
        }
        return errorMsg.toString();
    }
    
    private static boolean isValidTimeZone(String timeZone){
        final List<String> allowedValues = Arrays.asList(TimeZone.getAvailableIDs());
        return allowedValues.contains(timeZone);
    }

    public void setRicsApiName(String ricsApiName) {
        this.ricsApiName = ricsApiName;
    }

    public void setRicsApiReader(RicsApiReader ricsApiReader) {
        this.ricsApiReader = ricsApiReader;
    }
}
