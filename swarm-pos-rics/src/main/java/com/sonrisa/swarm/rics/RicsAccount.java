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
	 * Used for get the authorization token
	 */
	private String loginName = "";

	/**
	 * Used for get the authorization token
	 */
	private String password = "";

	/**
	 * Used for get the authorization token
	 */
	private String serialNum = "";

	/**
	 * The authorization token for access the RICS's REST service set last time
	 */
	private String lastToken = "";

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

	public String getSerialNum() {
		return serialNum;
	}

	public String getStoreCode() {
		return storeCode;
	}

	/**
	 * Set serial number from encrypted database
	 * @param serialNum encrypted serial number
	 * @param aesUtility encryption utility
	 */
	public void setEncryptedSerialNum(byte[] serialNum, AESUtility aesUtility) {
		this.serialNum = aesUtility.aesDecrypt(serialNum);
	}

    /**
	 * Set userName from encrypted database value
	 * @param apiKey Encrypted username
	 * @param aesUtility Encryption utility
	 */
	public void setEncryptedLoginName(byte[] apiKey, AESUtility aesUtility) {
		this.loginName = aesUtility.aesDecrypt(apiKey);
	}

	/**
	 * Set password from encrypted database value
	 * @param password Encrypted password
	 * @param aesUtility Encryption utility
	 */
	public void setEncryptedPassword(byte[] password, AESUtility aesUtility) {
		this.password = aesUtility.aesDecrypt(password);
	}

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

	public String getLoginName() {
		return loginName;
	}

	public String getPassword() {
		return password;
	}

	/**
	 * sets the token, that is used in every request sent to RICS
	 * Note: this class remembers the Date when the token was last set. You can use {@code getTokenBirth()} to determine its age
	 * @param token the token received from RICS authentication service.
	 */
	public void setToken(String token) {
		this.lastToken = token;
	}

	public String getLastToken() {
		return lastToken;
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
        return getSerialNum();
    }
}
