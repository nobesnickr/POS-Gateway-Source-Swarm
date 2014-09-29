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
import hu.sonrisa.backend.dao.filter.QueryFilter;

import java.util.Collection;

import org.springframework.stereotype.Repository;

import com.sonrisa.swarm.retailpro.model.DateConfigurationEntity;

/**
 * Data Access Object of the {@link DateConfigurationEntity} class.
 */
@Repository
public class DateConfigurationDao extends BaseJpaDao<Long, DateConfigurationEntity> {

    /**
     * Initialize the dao instance
     */
    public DateConfigurationDao() {
        super(DateConfigurationEntity.class);
    }
    
    
    /**
     * Finds a date configuration entity by its unique id
     * 
     * @param swarmId Swarm id, the most recent entity with matching swarmId or '*' as swarmId will be returned  
     * @return The most recent configuration entity matching the criteria
     */
    public DateConfigurationEntity findMostRecentBySwarmId(final String swarmId){
        final String query = "SELECT r FROM DateConfigurationEntity r WHERE r.swarmId = '*' OR r.swarmId = :swarmId ORDER BY r.timeStampVersion DESC";
        QueryFilter<DateConfigurationEntity> filter = new QueryFilter<DateConfigurationEntity>(query, new FilterParameter("swarmId", swarmId));
        return findSingleEntity(filter);
    }

    /**
     * {@inheritDoc}
     * 
     * Note: This function is only used in unit tests, use direct
     * access to database if you wish to save DateConfigurationEntity
     * entities.
     */
    public void save(Collection<DateConfigurationEntity> entities) {
        if(entities == null){
            throw new IllegalArgumentException("Argument entities is null");
        }
        
        for(DateConfigurationEntity entity : entities){
            if (entity.getId() != null){
                throw new UnsupportedOperationException("Already existing DateConfigurationEntities should never be modified!");
            }else{
                persist(entity);
            }
        }
    }
}
