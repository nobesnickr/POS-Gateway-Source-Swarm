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
package com.sonrisa.swarm.posintegration.api.service;

import java.util.List;

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.api.service.exception.StoreScanningException;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;

/**
 * Service which scans for alternative locations/sites for a given
 * SwarmStore.
 * 
 * E.g. If Lightspeed Pro store for location "FreshBikesAF" is present, we 
 * can use its credentails to access invoices for "FreshBikesAR" and create
 * new {@link StoreEntity} in the <code>stores</code> table.
 * 
 * @author Barnabas
 */
public interface SwarmStoreScannerService<T extends SwarmStore> {

    /**
     * Return list of new or updates {@link StoreEntity} related to knownEntity
     *  
     * @param knownEntity Account with credentails for the remote service
     * @return All locations for the multi-store account
     */
    List<StoreEntity> scanForLocations(T knownEntity) throws StoreScanningException;
    
}
