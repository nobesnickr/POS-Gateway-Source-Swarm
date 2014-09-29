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
package com.sonrisa.swarm.retailpro.dao.impl;

import hu.sonrisa.backend.dao.BaseJpaDao;
import hu.sonrisa.backend.dao.filter.FilterParameter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.sonrisa.swarm.retailpro.dao.RpStoreDao;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;

/**
 * {@inheritDoc}
 * 
 * @author joe
 */
@Repository("RpStoreDao")
public class RpStoreDaoImpl extends BaseJpaDao<Long, RpStoreEntity> implements RpStoreDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public RpStoreDaoImpl() {
        super(RpStoreEntity.class);
    }

    /**
     * {@inheritDoc}
     */
    public RpStoreEntity findBySbsNoAndStoreNoAndSwarmId(final String sbsNo, final String storeNo, final String swarmId){
        SimpleFilter<RpStoreEntity> filter = new SimpleFilter<RpStoreEntity>(RpStoreEntity.class,
                new FilterParameter("storeNumber", storeNo),
                new FilterParameter("sbsNumber", sbsNo),
                new FilterParameter("swarmId", swarmId));

        return findSingleEntity(filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RpStoreEntity findByStoreId(Long storeId) {
        SimpleFilter<RpStoreEntity> filter = new SimpleFilter<RpStoreEntity>(RpStoreEntity.class,
                new FilterParameter("storeId", storeId));
        return findSingleEntity(filter);
    }
}
