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
package com.sonrisa.swarm.legacy.service.impl;

import hu.sonrisa.backend.service.GenericServiceImpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;

/**
 * Implementation of {@link StoreService} interface.
 *
 * @author joe
 */
@Service
@Transactional
public class StoreServiceImpl extends GenericServiceImpl<Long, StoreEntity, StoreDao> implements StoreService {
    
    /**
     * DAO used to access data in the <code>stores</code> table
     */
    private StoreDao dao;

    /**
     * Initializes new {@link StoreServiceImpl}
     * @param dao
     */
    @Autowired
    public StoreServiceImpl(StoreDao dao) {
        super(dao);
        this.dao = dao;
    }
        
    /**
     * {@inheritDoc }
     * 
     * @param store
     * @return 
     */
    @Override
    public Long save(StoreEntity store) {
        if (store.getId() != null){
            dao.merge(store);
        }else{
            dao.persist(store);
            dao.flush();
        }
        
        return store.getId();
    }
}
