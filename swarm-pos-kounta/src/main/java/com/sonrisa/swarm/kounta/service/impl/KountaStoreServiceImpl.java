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
package com.sonrisa.swarm.kounta.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.kounta.KountaAPI;
import com.sonrisa.swarm.kounta.KountaAccount;
import com.sonrisa.swarm.kounta.KountaUriBuilder;
import com.sonrisa.swarm.kounta.api.util.KountaAPIReader;
import com.sonrisa.swarm.kounta.service.KountaStoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.admin.service.BaseStoreRegistrationService;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.request.SimpleApiRequest;
import com.sonrisa.swarm.posintegration.api.service.exception.StoreScanningException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.exception.ExternalPageIterationException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;

/**
 * Service to register new {@link KountaAccount} into the <code>stores</code> table.

 * @author Barnabas
 */
@Service
public class KountaStoreServiceImpl extends BaseStoreRegistrationService implements KountaStoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KountaStoreServiceImpl.class);
        
    /**
     * Kounta API to access remote content directly
     */
    @Autowired
    private KountaAPI api;
    
    /**
     * Kounta api reader
     */
    @Autowired
    private KountaAPIReader apiReader;
    
    /**
     * Name of Kounta api
     */
    @Value("${api.name.kounta}")
    private String apiName;
    

    /**
     * {@inheritDoc}
     */
    @Override
    public KountaAccount createAccountFromTemporaryToken(String code) throws StoreScanningException, ExternalExtractorException {
        KountaAccount account = api.getAccountForTemporaryToken(code);
        
        LOGGER.debug("Resolving company for temporary code: {}", code);
        
        ExternalResponse response = api.sendRequest(new ExternalCommand<KountaAccount>(account, KountaUriBuilder.COMPANY_INFO_URI));
        ExternalDTO companyInfo = response.getContent();
        
        final String companyId = companyInfo.getText("id"); 
        if(StringUtils.isEmpty(companyId)){
            throw new StoreScanningException("Illegal API response when reading company information");
        }
        account.setCompany(companyId);
        account.setStoreName(companyInfo.getText("name"));
        
        LOGGER.debug("Company resolved: {} ({})", companyInfo.getText("name"), companyId);
        
        return account;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StoreEntity> scanForLocations(KountaAccount account) throws StoreScanningException {
        
        Iterable<ExternalDTO> sites = new SimpleApiRequest<KountaAccount>(
                        apiReader, 
                        new ExternalCommand<KountaAccount>(account, KountaUriBuilder.getCompanyUri(account, "sites.json")));

        List<StoreEntity> retVal = new ArrayList<StoreEntity>();
        try {
            for(ExternalDTO siteNode : sites){
                StoreEntity store = findOrCreateStore(
                        // stores.name
                        getStoreName(account.getStoreName(),siteNode.getText("name")),
                        // stores.username
                        account.getCompany(),
                        // stores.store_filter
                        siteNode.getText("id"),
                        // apis.name
                        apiName);
                
                store.setOauthToken(aesUtility.aesEncryptToBytes(account.getOauthRefreshToken()));
                store.setNotes(siteNode.getText("code"));
                retVal.add(store);
            }
        } catch (ExternalPageIterationException e){
            throw new StoreScanningException("Failed to read sites for company: " + account.getCompany(), e);
        }
        
        return retVal;
    }
    
    /**
     * Get store name from company & site name.
     */
    private static String getStoreName(String rootName, String siteName){
        StringBuilder builder = new StringBuilder();
        return builder.append(rootName).append(" - ").append(siteName).toString();
    }
    
    public void setApi(KountaAPI api) {
        this.api = api;
    }

    public void setApiReader(KountaAPIReader apiReader) {
        this.apiReader = apiReader;
    }

    public void setApiName(String kountaApi) {
        this.apiName = kountaApi;
    }
}
