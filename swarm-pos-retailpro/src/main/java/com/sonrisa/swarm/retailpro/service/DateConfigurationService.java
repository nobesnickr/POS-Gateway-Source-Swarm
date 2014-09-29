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

import java.util.Collection;

import com.sonrisa.swarm.retailpro.model.DateConfigurationEntity;

/**
 * Service to access the date configuration entities
 * 
 * @author sonrisa
 */
public interface DateConfigurationService {

    /**
     * Creates or updates several DateConfiguration entities in the database.
     * 
     * @param swarmId
     * @param entities 
     */
    void save(Collection<DateConfigurationEntity> entities);
    
    /**
     * Finds a date reconfiguration entity by its unique id
     * 
     * @param id
     * @return 
     */
    DateConfigurationEntity find(Long id);
    
    /**
     * Finds a date reconfiguration entity by its unique id
     * 
     * @param swarmId Swarm id, the most recent entity with matching swarmId or '*' as swarmId will be returned  
     * @return The most recent configuration entity matching the criteria
     */
    DateConfigurationEntity findMostRecentBySwarmId(String swarmId);
}
