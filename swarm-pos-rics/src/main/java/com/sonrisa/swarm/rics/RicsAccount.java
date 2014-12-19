/*
  Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.

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

package com.sonrisa.swarm.rics;

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.extractor.impl.SimpleSwarmAccount;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;

/**
 * A class to hold the authentication credentials (serial number, login name, password) and base URL for RICS's REST service.
 */
public class RicsAccount extends SimpleSwarmAccount {
    
    /**
	 * The authorization token for access the RICS's REST service set last time
	 */
	private String token = "";

	/**
	 * Creates a new account object for accessing the RICS's REST service
	 * @param storeId
	 */
	public RicsAccount(StoreEntity store, AESUtility aesUtility) {
	    super(store, aesUtility);

        final byte[] token = store.getApiKey();

        if(token == null){
            throw new IllegalArgumentException("No token for RICS store" + store);
        }
        
        this.token = aesUtility.aesDecrypt(token);
	}
	
	/**
	 * Create dummy account, one which isn't created using the store table
	 * but for instance when attempting to register a store
	 */
	public RicsAccount(){
	    super();
	}
	
	public void setUserName(String userName) {
		setAccountId(userName);
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserName() {
		return getAccountId();
	}

	public String getStoreCode() {
		return getStoreFilter();
	}

	public String getToken() {
		return token;
	}

	public void setStoreCode(String storeCode) {
		setStoreFilter(storeCode);
	}
}
