/*
  Copyright (c) 2014 Sonrisa Informatikai Kft. All Rights Reserved.

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
package com.sonrisa.swarm.rics.service;

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.rics.RicsAccount;
import com.sonrisa.swarm.rics.service.exception.RicsStoreServiceException;

/**
 * Service registering new RICS stores into the <code>stores</code> table.
 */
public interface RicsStoreService {
    
    /**
     * Finds account in the <code>stores</table> or
     * create new.
     */
    StoreEntity getStore(RicsAccount account);
    
    /**
     * Create new RICS account using its credentials
     */
    RicsAccount getAccount(String loginName, String password, String serialNum, String storeCode) throws RicsStoreServiceException;
}
