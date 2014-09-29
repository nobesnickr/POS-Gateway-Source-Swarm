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

package com.sonrisa.swarm.rics.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.sonrisa.swarm.job.ExtractorLauncher;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.extractor.ExternalExtractor;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;
import com.sonrisa.swarm.rics.RicsAccount;

public class RicsExtractorLauncher extends ExtractorLauncher {

	/**
	 * Service decrypting strings from the MySQL database
	 */
	@Autowired
	private AESUtility aesUtility;

	/** External extractor to launch.  */
    @Autowired
    @Qualifier("ricsExtractor")
    private ExternalExtractor extractor;
    
    /** Datastore to write the received data into. */
    @Autowired
    private SwarmDataWarehouse dataStore;

	@Override
	public ExternalExtractor<RicsAccount> getExtractor() {
		return extractor;
	}

	@Override
	public SwarmDataWarehouse getDataStore() {
		return dataStore;
	}

	/**
	 * Creates new RICS account
	 */
	@Override
	protected SwarmStore createAccount(StoreEntity store) {
		RicsAccount account = new RicsAccount(store.getId());

		final byte[] serialNum = store.getUsername();
		final byte[] loginName = store.getApiKey();
		final byte[] pass = store.getPassword();

		if (serialNum == null) {
			throw new IllegalArgumentException("No serial number for RICS store: " + store);
		}
		
		if(loginName == null){
		    throw new IllegalArgumentException("No loginName for RICS store" + store);
		}

		if (pass == null) {
			throw new IllegalArgumentException("No password for RICS store" + store);
		}

		account.setEncryptedLoginName(loginName, aesUtility);
		account.setEncryptedPassword(pass, aesUtility);
		account.setEncryptedSerialNum(store.getUsername(), aesUtility);
		account.setStoreCode(store.getStoreFilter());
		account.setStoreName(store.getName());

		return account;
	}
}
