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

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.retailpro.model.RpLogEntity;
import com.sonrisa.swarm.retailpro.service.RpLogMonitoringService;

/**
 * Implementation of the {@link RpLogMonitoringService} which stores the
 * recent logs in memory, and updates them when newer one arrives.
 * 
 * This implementation is not persistent.
 * 
 * @author Barnabas
 */
@Service
public class LogMonitoringServiceImpl implements RpLogMonitoringService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogMonitoringServiceImpl.class);
    
    /**
     * Which levels to look out for
     */
    private static final Set<String> WATCHED_LEVELS = new HashSet<String>(
            Arrays.asList(new String[] { "ERROR", "FATAL" }));

    /**
     * Which stack traces should be ignored, even if they are ERRORs
     */
    private static final Set<String> IGNORED_STACK_TRACES = new HashSet<String>(
            Arrays.asList(
            // Stack trace when Settings.xml or SettingsV8.xml is not found,
            // always generated the first time the plugin runs.
            "ReflectionComposablePart.CreateInstance => RuntimeConstructorInfo.Invoke => RuntimeMethodHandle.InvokeMethod => FileConfiguration..ctor => FileConfiguration.Load"));

    /**
     * Cache for the most recent ERROR logs
     */
    private Map<String, RpLogEntity> cache = new ConcurrentHashMap<String, RpLogEntity>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerLog(String swarmId, String message, Date date) {

        RpLogEntity entity = RpLogEntity.fromClientString(message);
        entity.setServerTimestamp(date);
        
        if(WATCHED_LEVELS.contains(entity.getLevel()) && !IGNORED_STACK_TRACES.contains(entity.getStackTrace())){
            
            LOGGER.info("Error log uploaded by {}", swarmId);
            if(!cache.containsKey(swarmId) || cache.get(swarmId).getServerTimestamp().before(entity.getServerTimestamp())){
                cache.put(swarmId, entity);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RpLogEntity getRecentClientError(String swarmId) {
        if(cache.containsKey(swarmId)){
            return cache.get(swarmId);
        } else {
            return null;
        }
    }

}
