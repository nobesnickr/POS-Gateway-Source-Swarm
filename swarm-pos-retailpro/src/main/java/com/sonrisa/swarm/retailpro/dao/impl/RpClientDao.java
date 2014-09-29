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

import com.sonrisa.swarm.retailpro.model.RpClientEntity;
import hu.sonrisa.backend.dao.BaseJpaDao;
import hu.sonrisa.backend.dao.filter.FilterParameter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;
import org.springframework.stereotype.Repository;

/**
 * Data Access Object of the {@link RpClientEntity} class.
 *
 * @author joe
 */
@Repository
public class RpClientDao extends BaseJpaDao<Long, RpClientEntity>{

    public RpClientDao() {
        super(RpClientEntity.class);
    }
    
    /**
     * Finds a RetailPro client component by its swarmId and component ID.
     * 
     * (We can do this, because swarmId and componentId together are unique.)
     * 
     * @param swarmId
     * @return 
     */
    public RpClientEntity findBySwarmIdAndComponentId(final String swarmId, final String componentId){
         SimpleFilter<RpClientEntity> filter = new SimpleFilter<RpClientEntity>(RpClientEntity.class,
                new FilterParameter("swarmId", swarmId),
                new FilterParameter("componentId", componentId));
        return findSingleEntity(filter);
    }
            
}
