package com.sonrisa.swarm.vend;

import com.sonrisa.swarm.posintegration.extractor.impl.BaseSwarmAccount;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;

/**
 * Vend account contains the store's store_id in the <code>stores</code> table,
 * and all the necessary information to authenticate it at the Vend REST service.
 * 
 */
public class VendAccount extends BaseSwarmAccount {

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
     * Vend company the account belongs to (one-to-one for authentication)
     * Column: <code>account_id</code>
     */
    private String company;

    /**
     * Vend site, similar to Revel's establishment or Merchant OS's shop (many-to-one for authentication)
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
    private VendAccessToken oauthAccessToken;
    
    /**
     * Store's name.
     * Column: <code>name</code>
     */
    private String storeName;
    
    /**
     * API Url stored for request Url creation.
     * Column: <code> api_url</code>
     */
    private String apiUrl;

    /**
     * {@inheritDoc}
     */
    public VendAccount(long storeId) {
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

    public VendAccessToken getOauthAccessToken() {
        return oauthAccessToken;
    }

    public void setOauthAccessToken(VendAccessToken oauthAccessToken) {
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

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	} 
	
	public String getApiUrl() {
		return this.apiUrl;
	} 
}
