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
package com.sonrisa.swarm.mos;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.impl.BaseSwarmAccount;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;

/**
 * Class for a partner's Merchant OS account.
 * @note Each MosAccount equals one store
 * @author sonrisa
 */
public class MosAccount extends BaseSwarmAccount {

    /** URL of the API */
    public static final String DEFAULT_MOS_API_URL = "https://api.merchantos.com/API/";
    
    /** Password to use when authenticating with api key */
    public static final String PASSWORD_FOR_APIKEY = "apikey";
    
    /** Url base to use for sending requests */
    private String urlBase = MosAccount.DEFAULT_MOS_API_URL; 
    
    /** Api key for authenticating */
    private String apiKey = null;
    
    /** OAuth token used for authentication */
    private String oauthToken = null;
    
    /** The account id of the user */
    private String accountId;
    
    /** The store name, normally used for human readable log entries */
    private String storeName = "";
    
    private String shopId = "";
        
    /**
     * Initializes an instance of a MosAccount
     * @param storeId The local id for the store from the Swarm ID
     */
    public MosAccount(long storeId) {
        super(storeId);
    }
    
    /**
     * Returns the Authorization header for HTTP request
     * @return HTTP header for authorizing this account
     * @throws ExternalExtractorException
     */
    public String getAuthorization() throws ExternalExtractorException{
        if(oauthToken != null && oauthToken.length() != 0){
            return "OAuth " + oauthToken;
        } else if(apiKey != null && apiKey.length() != 0){
            try {
                return "Basic " + new String(Base64.encodeBase64((apiKey + ":" + PASSWORD_FOR_APIKEY).getBytes("UTF-8")));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new ExternalExtractorException("Failed to provide authentication for " + this.toString());
        }
    }
    
    /**
     * Returns the Access URL for this particular account
     * @return The base URL, e.g. http://merchantos..something....com/API/Account/id/
     * @throws ExternalExtractorException
     */
    public String getAccessUrl() throws ExternalExtractorException {
        if(StringUtils.isEmpty(accountId)){
            throw new IllegalStateException("Account ID needs to be set for this operation");
        }
        return urlBase + "Account/" + accountId + "/";
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MosAccount [apiKey=" + apiKey + ", oauthToken=" + oauthToken
                + ", accountId=" + accountId + ", storeName=" + storeName + "]";
    }

    /**
     * @return the urlBase
     */
    public String getUrlBase() {
        return urlBase;
    }
    
    /**
     * @return the accountId
     */
    @Override
    public String getAccountId() {
        return accountId;
    }

    /**
     * @param urlBase the urlBase to set
     */
    public void setUrlBase(byte[] urlBase) {
        this.urlBase = new String(urlBase);
    }

    /**
     * @param apiKey the apiKey to set
     */
    public void setApiKey(byte[] apiKey) {
        this.apiKey = new String(apiKey);
    }
    
    /**
     * Set API key using encoded API key
     * @param apiKey Encypted API key
     */
    public void setEncryptedApiKey(byte[] apiKey, AESUtility aesUtility){
        this.apiKey = aesUtility.aesDecrypt(apiKey);
    }

    /**
     * @param oauthToken the oauthToken to set
     */
    public void setOauthToken(byte[] oauthToken) {
        this.oauthToken = new String(oauthToken);
    }

    /**
     * Set OAuth token using encrypted OAuth token
     * @param oauthToken Encrypted OAuth token
     */
    public void setEncryptedOauthToken(byte[] oauthToken, AESUtility aesUtility) {
        this.oauthToken = aesUtility.aesDecrypt(oauthToken);
    }
    
    /**
     * @param accountId the accountId to set
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * @return the storeName
     */
    public String getStoreName() {
        return storeName;
    }

    /**
     * @param storeName the storeName to set
     */
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}
