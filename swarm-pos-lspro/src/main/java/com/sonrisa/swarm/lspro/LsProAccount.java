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

package com.sonrisa.swarm.lspro;

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.extractor.impl.SimpleSwarmAccount;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;

/**
 * LsPro account contains the store's store_id in the <code>stores</code> table,
 * and all the necessary information to authenticate it at the Lightspeed Pro REST service.
 * 
 */
public class LsProAccount extends SimpleSwarmAccount {
    
    /**
     * Password for basic authentication
     */
    private String password;

    /**
     * Constructor for creating dummy account, that are not actually 
     * associated with any Swarm stores
     */
    public LsProAccount() {
        super();
    }
    
    /**
     * Constructor for creating actual accounts
     * @param store
     * @param aesUtility
     */
    public LsProAccount(StoreEntity store, AESUtility aesUtility) {
        super(store, aesUtility);

        if(store.getPassword() == null){
            throw new IllegalArgumentException("No password for LsPro store" + store);
        }
        
        password = aesUtility.aesDecrypt(store.getPassword());
    }

    public String getUsername() {
        return getAccountId();
    }
    
    public void setUserName(String userName) {
        setAccountId(userName);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
