/**
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

package com.sonrisa.swarm.erply.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.erply.ErplyAPI;
import com.sonrisa.swarm.erply.ErplyAccount;
import com.sonrisa.swarm.erply.service.ErplyStoreService;
import com.sonrisa.swarm.erply.service.exception.ErplyStoreServiceException;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.admin.service.BaseStoreRegistrationService;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;

/**
 * Implementation for registering Erply stores
 *
 * @author Barnabas
 */
@Service
public class ErplyStoreServiceImpl extends BaseStoreRegistrationService implements ErplyStoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErplyStoreServiceImpl.class);

    /**
     * Used API to access the Lightspeed Pro REST service
     */
    @Autowired
    @Qualifier("erplyAPIReader")
    private ExternalAPIReader<ErplyAccount> apiReader;

    /**
     * Name of the Lightspeed Pro API
     */
    @Value("${api.name.erply}")
    private String erplyApiName;

    /**
     * {@inheritDoc}
     */
    @Override
    public StoreEntity getStore(ErplyAccount dummyAccount) {

        // Find store by account name (clientCode)
        StoreEntity store = findOrCreateStore(dummyAccount.getStoreName(), dummyAccount.getAccountId(), null,
                erplyApiName);

        if (store.getId() != null) {
            LOGGER.info("Found existing Erply store for {} with store id:", dummyAccount.getAccountId(), store.getId());
        } else {
            LOGGER.info("Creating new Erply store entry for {}", dummyAccount.getAccountId());
        }

        store.setApiKey(aesUtility.aesEncryptToBytes(dummyAccount.getUsername()));
        store.setPassword(aesUtility.aesEncryptToBytes(dummyAccount.getPassword()));
        store.setTimeZone(dummyAccount.getTimeZone());
        return store;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErplyAccount getAccount(String clientCode, String userName, String password)
            throws ErplyStoreServiceException {
        ErplyAccount account = new ErplyAccount();
        account.setAccountId(clientCode);
        account.setUsername(userName);
        account.setPassword(password);

        try {
            ExternalCommand<ErplyAccount> command = new ExternalCommand<ErplyAccount>(account, "getCompanyInfo");
            ExternalDTOPath dataPath = apiReader.getDataKey(command);
            ExternalResponse response = apiReader.getPage(command, ErplyAPI.FIRST_PAGE);
            account.setStoreName(response.getContent().getNestedItem(dataPath).getNestedArrayItem(0).getText("name"));

            ExternalResponse confResponse = apiReader.getPage(new ExternalCommand<ErplyAccount>(account,
                    "getConfParameters"), ErplyAPI.FIRST_PAGE);
            account.setTimeZone(confResponse.getContent().getNestedItem(dataPath).getNestedArrayItem(0)
                    .getText("timezone"));

            return account;
        } catch (ExternalExtractorException e) {
            throw new ErplyStoreServiceException(e.getUserFriendlyError(), e);
        }
    }

    public void setApiReader(ExternalAPIReader<ErplyAccount> apiReader) {
        this.apiReader = apiReader;
    }

    public void setErplyApiName(String erplyApiName) {
        this.erplyApiName = erplyApiName;
    }
}
