package com.sonrisa.swarm.vend.service;

import com.sonrisa.swarm.vend.VendAccount;
import com.sonrisa.swarm.posintegration.api.service.SwarmStoreScannerService;
import com.sonrisa.swarm.posintegration.api.service.exception.StoreScanningException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;

/**
 * Service to register stores for Vend
 */
public interface VendStoreService extends SwarmStoreScannerService<VendAccount> {
    
    /**
     * Exchange temporary token for refresh token, and create separate
     * Vend accounts for each site.
     * 
     * @param code Temporary token, received on landing page
     * @return VendAccounts separated into sites
     * @throws ExternalExtractorException 
     */
    VendAccount createAccountFromTemporaryToken(String code) throws StoreScanningException, ExternalExtractorException;    
}
