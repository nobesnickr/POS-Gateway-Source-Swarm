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
package com.sonrisa.swarm.legacy.dao;

import hu.sonrisa.backend.dao.BaseJpaDao;
import hu.sonrisa.backend.dao.filter.FilterParameter;
import hu.sonrisa.backend.dao.filter.JpaFilter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;

import org.springframework.stereotype.Repository;

import com.sonrisa.swarm.model.legacy.ManufacturerEntity;

/**
 * DAO class of {@link ManufacturerEntity}. 
 */
@Repository
public class ManufacturerDao extends BaseJpaDao<Long, ManufacturerEntity> {

    public ManufacturerDao(){
        super(ManufacturerEntity.class);
    }
    
    /**
     * Retrieves a ManufacturerEntity by its store and foreign ID.
     * 
     * @param store
     * @param foreignCustomerId
     * @return 
     */
    public ManufacturerEntity findByStoreAndForeignId(final Long storeId, final Long foreignId){
        JpaFilter<ManufacturerEntity> filter = new SimpleFilter<ManufacturerEntity>(ManufacturerEntity.class, 
                new FilterParameter("store.id", storeId),
                new FilterParameter("manufacturerId", foreignId));                              
        
        return findSingleEntity(filter);
    }
}
