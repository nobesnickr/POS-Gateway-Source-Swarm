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
package com.sonrisa.swarm.retailpro.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.retailpro.dao.impl.DateConfigurationDao;
import com.sonrisa.swarm.retailpro.model.DateConfigurationEntity;
import com.sonrisa.swarm.retailpro.service.DateConfigurationService;

/**
 * Implementation of the {@link DateConfigurationService} interface.
 *
 * @author barna
 */
@Service
@Transactional
public class DateConfigurationServiceImpl implements DateConfigurationService {

    /**
     * Dao to access the DB
     */
    @Autowired
    private DateConfigurationDao dao;

    /**
     * {@inheritDoc }
     * 
     * @param id
     * @return 
     */
    @Override
    public DateConfigurationEntity find(Long id) {
        return dao.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateConfigurationEntity findMostRecentBySwarmId(String swarmId) {
        DateConfigurationEntity entity = dao.findMostRecentBySwarmId(swarmId);
        
        // If not matching entity found return one with
        // version set to 0L and all dates to null, so
        // it will not affect to plugin's configuration
        if(entity == null){
            entity = new DateConfigurationEntity();
        }
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Collection<DateConfigurationEntity> entities) {
        throw new UnsupportedOperationException("DateConfigurationEntities can only be created manually");
    }
}
