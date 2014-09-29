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
package com.sonrisa.swarm.kounta.service;

import com.sonrisa.swarm.kounta.KountaAccount;
import com.sonrisa.swarm.posintegration.api.service.SwarmStoreScannerService;
import com.sonrisa.swarm.posintegration.api.service.exception.StoreScanningException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;

/**
 * Service to register stores for Kounta
 */
public interface KountaStoreService extends SwarmStoreScannerService<KountaAccount> {
    
    /**
     * Exchange temporary token for refresh token, and create separate
     * Kounta accounts for each site.
     * 
     * @param code Temporary token, received on landing page
     * @return KountaAccounts separated into sites
     * @throws ExternalExtractorException 
     */
    KountaAccount createAccountFromTemporaryToken(String code) throws StoreScanningException, ExternalExtractorException;    
}
