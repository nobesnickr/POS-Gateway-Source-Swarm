/*
 *   Copyright (c) 2014 Sonrisa Informatikai Kft. All Rights Reserved.
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

import java.util.Date;

import com.sonrisa.swarm.retailpro.model.RpLogEntity;

/**
 * Service to monitor the logs for a certain store
 * 
 * @author Barnabas
 */
public interface RpLogMonitoringService {

    /**
     * Register log from swarmId
     * 
     * @param swarmId Swarm id of the source
     * @param message Message of the log
     * @param date Date this log was received
     */
    public void registerLog(String swarmId, String message, Date date);
    
    /**
     * Get recent Retail Pro errors sent by the client
     *  
     * @param swarmId Swarm id identifing the client
     */
    public RpLogEntity getRecentClientError(String swarmId);
    
}
