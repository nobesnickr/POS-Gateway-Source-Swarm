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

import com.sonrisa.swarm.model.legacy.CustomerEntity;

import hu.sonrisa.backend.dao.BaseJpaDao;
import hu.sonrisa.backend.dao.filter.FilterParameter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;

import org.springframework.stereotype.Repository;

/**
 * DAO class of customers.  
 *
 * @author joe
 */
@Repository
public class CustomerDao extends BaseJpaDao<Long, CustomerEntity> {

    public CustomerDao() {
        super(CustomerEntity.class);
    }
    

    /**
     * Retrieves a customer by its store and foreign customer ID.
     * 
     * @param store
     * @param foreignCustomerId
     * @return 
     */
    public CustomerEntity findByStoreAndForeignId(final Long storeId, final Long foreignCustomerId){
        SimpleFilter<CustomerEntity> filter = new SimpleFilter<CustomerEntity>(CustomerEntity.class, 
                new FilterParameter("store.id", storeId),
                new FilterParameter("lsCustomerId", foreignCustomerId));                              
        
        return findSingleEntity(filter);
    }
}
