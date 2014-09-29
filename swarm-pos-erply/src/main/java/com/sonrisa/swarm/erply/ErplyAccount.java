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
package com.sonrisa.swarm.erply;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.extractor.impl.SimpleSwarmAccount;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;

/**
 * Class for a partner's Erply account.
 * 
 * @note Each ErplyAccount equals one warehouse
 */
public class ErplyAccount extends SimpleSwarmAccount { 
    
	/**
	 * Erply session
	 */
	private ErplySession session;
	
	private String userName;
	
	private String password;

	public ErplyAccount(StoreEntity store, AESUtility aesUtility) {
	    super(store, aesUtility);
	    
	    if(store.getApiKey() == null){
	        throw new IllegalArgumentException("No apiKey for Erply account, this should be Erply userName");
	    }
	    
	    if(store.getPassword() == null){
            throw new IllegalArgumentException("No password for Erply account");
        }
	    
	    this.userName = aesUtility.aesDecrypt(store.getApiKey());
	    this.password = aesUtility.aesDecrypt(store.getPassword());
    }

	public ErplyAccount() {
        super();
    }

    public String getClientCode(){
	    return getAccountId();
	}
	
	public String getUsername() {
	    return userName;
	}

    public String getPassword() {
        return password;
    }

    public ErplySession getSession() {
        return session;
    }

    public void setSession(ErplySession session) {
        this.session = session;
    }

    public void setClientCode(String clientCode) {
        setAccountId(clientCode);        
    }

    public void setUsername(String username) {
        this.userName = username;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
}
