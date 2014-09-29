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
package com.sonrisa.swarm.retailpro.dao;

import hu.sonrisa.backend.dao.BaseDaoInterface;

import com.sonrisa.swarm.retailpro.model.RpStoreEntity;

/**
 * Data Access Object for the stores_rp database table holding the RetailPro installation information
 * 
 * @author sonrisa
 */
public interface RpStoreDao extends BaseDaoInterface<Long, RpStoreEntity> {
    
    /**
     * This method finds a RetailPro store using the combination of these:
     *  - swarmId (identifies the RetailPro installation)
     *  - sbs number (identifies a subsidiary in the RetailPro)
     *  - store number (identifies a store below the subsidiary in the RetailPro)
     * 
     * @param sbsNo
     * @param storeNo
     * @param swarmId
     * @return 
     */
    RpStoreEntity findBySbsNoAndStoreNoAndSwarmId(final String sbsNo, final String storeNo, final String swarmId);
 
    /**
     * Find store by store id
     */
    RpStoreEntity findByStoreId(Long storeId);
}
