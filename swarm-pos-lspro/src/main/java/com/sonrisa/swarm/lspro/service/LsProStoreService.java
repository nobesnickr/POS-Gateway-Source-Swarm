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

package com.sonrisa.swarm.lspro.service;

import com.sonrisa.swarm.lspro.LsProAccount;
import com.sonrisa.swarm.posintegration.api.service.SwarmStoreScannerService;

/**
 * Service to create new entries for Lightspeed Pro.
 * 
 * @author Barnabas
 */
public interface LsProStoreService extends SwarmStoreScannerService<LsProAccount>{
    
    /**
     * Get dummy account for given userName and password containing only 
     * authentication.
     */
    LsProAccount getStore(String userName, String token);
}
