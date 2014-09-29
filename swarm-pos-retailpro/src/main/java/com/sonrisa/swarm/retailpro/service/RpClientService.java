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

import com.sonrisa.swarm.retailpro.model.RpClientEntity;
import com.sonrisa.swarm.retailpro.rest.model.RpClientJson;
import com.sonrisa.swarm.retailpro.rest.model.RpHeartbeatJson;
import java.util.Collection;

/**
 *
 * @author joe
 */
public interface RpClientService {
    
    /**
     * Creates or updates several RetailPro client in the database.
     * 
     * @param swarmId
     * @param rpClientJson 
     */
    void save(String swarmId, Collection<RpClientJson> rpClientJson);
    
    /**
     * Finds a RetailPro client entity by its unique ID.
     * 
     * @param id
     * @return 
     */
    RpClientEntity find(Long id);
    
    /**
     * Updates the heartbeat timestamp and the RetailPro plugin version of the given RetailPro client.
     * 
     * 
     * @param swarmId identifies the RetailPro client
     * @param json 
     */
    void heartbeat(String swarmId, RpHeartbeatJson json);
    
}
