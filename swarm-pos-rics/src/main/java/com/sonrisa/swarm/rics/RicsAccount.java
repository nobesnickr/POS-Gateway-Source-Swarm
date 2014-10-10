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

import com.sonrisa.swarm.posintegration.extractor.impl.BaseSwarmAccount;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;

/**
 * A class to hold the authentication credentials (serial number, login name, password) and base URL for RICS's REST service.
 */
public class RicsAccount extends BaseSwarmAccount {
    
    /**
     * Store's name
     */
    private String storeName;
    
    /**
     * Username
     */
    private String userName;

	/**
	 * The authorization token for access the RICS's REST service set last time
	 */
	private String token = "";

	/**
	 * store identifier 
	 */
	private String storeCode;

	/**
	 * creates a new account object for accessing the RICS's REST service
	 * @param storeId
	 */
	public RicsAccount(long storeId) {
		super(storeId);
	}
	
	/**
	 * Set token number from encrypted field
	 * @param token encrypted token
	 * @param aesUtility encryption utility
	 */
	public void setEncryptedToken(byte[] token, AESUtility aesUtility) {
		this.token = aesUtility.aesDecrypt(token);
	}
	
	/**
	 * Set username
	 * @param username encrypted username
	 * @param aesUtility encryption utility
	 */
	public void setEncryptedUsername(byte[] userName, AESUtility aesUtility) {
		this.userName = aesUtility.aesDecrypt(userName);
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserName() {
		return userName;
	}

	public String getStoreCode() {
		return storeCode;
	}


	public String getToken() {
		return token;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public String getAccountId() {
        return getUserName();
    }
}
