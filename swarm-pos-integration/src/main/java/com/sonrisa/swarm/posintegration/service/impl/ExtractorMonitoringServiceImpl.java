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

package com.sonrisa.swarm.posintegration.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.sonrisa.swarm.posintegration.service.ExtractorMonitoringService;

/**
 * Implementation of the {@link ExtractorMonitoringService} interface, which
 * stores the last successful date in a {@link HashMap}.
 * 
 * This implementation is non-persistent and needs a few minutes to start working.
 * 
 * @author Barnabas
 */
@Service
public class ExtractorMonitoringServiceImpl implements ExtractorMonitoringService {
    
    /**
     * Cache to store values
     */
    private Map<Long, Date> cache = new ConcurrentHashMap<Long, Date>();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastSuccessfulExecution(Long storeId) {
        if(storeId == null){
            throw new IllegalArgumentException("storeId is null");
        }
        if(cache.containsKey(storeId)){
            return cache.get(storeId);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSuccessfulExecution(Long storeId, Date date) {
        cache.put(storeId, date);
    }
}
