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

package com.sonrisa.swarm.kounta;

import com.sonrisa.swarm.posintegration.extractor.impl.BaseSwarmAccount;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;

/**
 * Kounta account contains the store's store_id in the <code>stores</code> table,
 * and all the necessary information to authenticate it at the Kounta REST service.
 * 
 */
public class KountaAccount extends BaseSwarmAccount {

    /**
     * Username for basic authentication (only developer accounts).
     * Column: <code> username</code>
     */
    private String userName;

    /**
     * Password for basic authentication (only developer accounts).
     * Column: <code>password</code>
     */
    private String password;

    /**
     * Kounta company the account belongs to (one-to-one for authentication)
     * Column: <code>account_id</code>
     */
    private String company;

    /**
     * Kounta site, similar to Revel's establishment or Merchant OS's shop (many-to-one for authentication)
     * Column: <code>store_filter</code>
     */
    private String site;

    /**
     * OAuth 2.0 authorization token
     * Column: <code>oauth_token</code>
     */
    private String oauthRefreshToken;
    
    /**
     * OAuth access token, generated using the {@link #oauthRefreshToken}
     */
    private KountaAccessToken oauthAccessToken;
    
    /**
     * Store's name.
     * Column: <code>name</code>
     */
    private String storeName;

    /**
     * {@inheritDoc}
     */
    public KountaAccount(long storeId) {
        super(storeId);
    }
    
    public String getUserName() {
        return userName;
    }

    public void setEncryptedUserName(byte[] userName, AESUtility aesUtility) {
        this.userName = aesUtility.aesDecrypt(userName);
    }

    public String getPassword() {
        return password;
    }

    public void setEncryptedPassword(byte[] password, AESUtility aesUtility) {
        this.password = aesUtility.aesDecrypt(password);
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getOauthRefreshToken() {
        return oauthRefreshToken;
    }

    public void setEncryptedOauthRefreshToken(byte[] oauthToken, AESUtility aesUtility) {
        this.oauthRefreshToken = aesUtility.aesDecrypt(oauthToken);
    }
    
    public void setOauthRefreshToken(String oauthRefreshToken) {
        this.oauthRefreshToken = oauthRefreshToken;
    }

    public KountaAccessToken getOauthAccessToken() {
        return oauthAccessToken;
    }

    public void setOauthAccessToken(KountaAccessToken oauthAccessToken) {
        this.oauthAccessToken = oauthAccessToken;
    }    

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public String getAccountId() {
        return getCompany();
    } 
}
