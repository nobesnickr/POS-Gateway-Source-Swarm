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
package com.sonrisa.swarm.retailpro.service;

import com.sonrisa.swarm.retailpro.model.RpStoreEntity;
import com.sonrisa.swarm.retailpro.rest.model.JsonStore;

/**
 * This interface describes the RetailPro specific store operations.
 *
 * @author joe
 */
public interface RpStoreService {

    /**
     * Creates or updates several RetailPro stores in the database.
     * 
     * @param swarmId
     * @param store 
     */
    void save(String swarmId, JsonStore... store); 
    
    /**
     * Creates or updates several RetailPro stores in the database.
     * 
     * @param swarmId
     * @param posSoftware Pos software, e.g. retailpro8
     * @param store 
     */
    void save(String swarmId, String posSoftware, JsonStore... store); 
    
    /**
     * Creates or updates a RetailPro store entity.
     */
    Long save(RpStoreEntity store);
    
    /**
     * Finds a RetailPro story entity by its unique ID.
     * 
     * @param id
     * @return 
     */
    RpStoreEntity find(Long id);
    
    /**
     * This method finds a RetailPro store the combination of these:
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
     * Find Retail Pro store by store id
     * @param storeId
     * @return
     */
    RpStoreEntity findByStoreId(Long storeId);
    
}
