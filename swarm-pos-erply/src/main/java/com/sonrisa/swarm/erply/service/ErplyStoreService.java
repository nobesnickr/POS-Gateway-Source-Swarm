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
package com.sonrisa.swarm.erply.service;

import com.sonrisa.swarm.erply.ErplyAccount;
import com.sonrisa.swarm.erply.service.exception.ErplyStoreServiceException;
import com.sonrisa.swarm.model.legacy.StoreEntity;

/**
 * An Erply store service is capable of registering new Erply stores in the gateway
 * based on the store's credentials and client code.
 * 
 * @TODO: Possible do warehouse based division later. This might not be soo easy, as 
 * timezone information is only available on an "account" level, we would need a 
 * test account with at least two warehouses in different timezones to try this (which we don't have now).
 * 
 * @author Barnabas
 */
public interface ErplyStoreService {
    
    /**
     * Finds account in the <code>stores</table> or
     * create new.
     */
    StoreEntity getStore(ErplyAccount account);
    
    /**
     * Create new RICS account using its credentials
     */
    ErplyAccount getAccount(String clientCode, String userName, String password) throws ErplyStoreServiceException;

}
