package com.sonrisa.swarm.vend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.api.service.SwarmStoreFactory;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;
import com.sonrisa.swarm.vend.VendAccount;

/**
 * Service converting {@link StoreEntity} for {@link VendAccount}
 */
@Service
public class VendStoreFactory implements SwarmStoreFactory<VendAccount> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(VendStoreFactory.class);
    
    /**
     * Utility to decrypt columns
     */
    @Autowired
    private AESUtility aesUtility;

    /**
     * {@inheritDoc}
     */
    @Override
    public VendAccount getAccount(StoreEntity store) {
        VendAccount account = new VendAccount(store.getId());
        
        LOGGER.info("Store ID: "+store.getId().toString());
        
        
        final String userName = aesUtility.aesDecrypt(store.getUsername()); 
        if(StringUtils.hasLength(userName)){
            account.setCompany(userName);
        } else {
            account.setCompany(Integer.toString(store.getAccountNumber()));
        }
        
        final String apiUrl = aesUtility.aesDecrypt(store.getApiUrl()); 
        account.setApiUrl(apiUrl);
        
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
