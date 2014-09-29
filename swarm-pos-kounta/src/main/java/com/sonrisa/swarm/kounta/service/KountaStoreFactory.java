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
package com.sonrisa.swarm.kounta.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.kounta.KountaAccount;
import com.sonrisa.swarm.kounta.service.impl.KountaStoreServiceImpl;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.api.service.SwarmStoreFactory;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;

/**
 * Service converting {@link StoreEntity} for {@link KountaAccount}
 */
@Service
public class KountaStoreFactory implements SwarmStoreFactory<KountaAccount> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KountaStoreFactory.class);
    
    /**
     * Utility to decrypt columns
     */
    @Autowired
    private AESUtility aesUtility;

    /**
     * {@inheritDoc}
     */
    @Override
    public KountaAccount getAccount(StoreEntity store) {
        KountaAccount account = new KountaAccount(store.getId());
        
        final String userName = aesUtility.aesDecrypt(store.getUsername()); 
        if(StringUtils.hasLength(userName)){
            account.setCompany(userName);
        } else {
            account.setCompany(Integer.toString(store.getAccountNumber()));
        }
        account.setSite(store.getStoreFilter());
        account.setStoreName(store.getName());
        
        if(store.getOauthToken() != null){
            account.setEncryptedOauthRefreshToken(store.getOauthToken(), aesUtility);
        } else {
            LOGGER.warn("No OAuth 2.0 token for {}", store);
        }
        return account;
    }

}
