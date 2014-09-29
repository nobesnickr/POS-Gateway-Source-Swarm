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

import com.sonrisa.swarm.retailpro.model.RpPluginEntity;
import hu.sonrisa.backend.dao.BaseJpaDao;
import hu.sonrisa.backend.dao.filter.FilterParameter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;
import org.springframework.stereotype.Repository;

/**
 * Data Access Object of the {@link RpPluginEntity} class.
 *
 * @author joe
 */
@Repository
public class RpPluginDao extends BaseJpaDao<Long, RpPluginEntity>{

    public RpPluginDao() {
        super(RpPluginEntity.class);
    }
    
    /**
     * Finds a RetailPro plugin by its swarmId.
     * 
     * (We can do this, because swarmId is unique.)
     * 
     * @param swarmId
     * @return 
     */
    public RpPluginEntity findBySwarmId(final String swarmId){
         SimpleFilter<RpPluginEntity> filter = new SimpleFilter<RpPluginEntity>(RpPluginEntity.class,
                new FilterParameter("swarmId", swarmId));
        return findSingleEntity(filter);
    }
    
}
