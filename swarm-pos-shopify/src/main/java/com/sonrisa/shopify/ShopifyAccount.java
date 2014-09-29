/*
 *   Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.
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

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.extractor.impl.SimpleSwarmAccount;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;


/**
 * Class for a partner's Shopify account
 * @note Each ShopifyAccount equals one store
 * @author sonrisa
 */
public class ShopifyAccount extends SimpleSwarmAccount {
    
    /**
     * OAuth token
     */
    private String oauthToken; 

    /**
     * {@inheritDoc}
     */
    public ShopifyAccount(StoreEntity store, AESUtility aesUtility) {
        super(store, aesUtility);
        
        final byte[] encryptedOauthToken = store.getOauthToken();
        
        if(encryptedOauthToken != null){
            oauthToken = aesUtility.aesDecrypt(encryptedOauthToken);
        }
    }

    /**
     * Dummy account, not actually associated with any Swarm store
     */
    public ShopifyAccount() {
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }
}
