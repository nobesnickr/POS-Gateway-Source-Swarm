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

package com.sonrisa.swarm.job.partitioner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.sonrisa.swarm.posintegration.service.ApiService;

/**
 * Partitioner for POS accounts so extraction processes
 * can run asynchronously 
 */
public class SimpleExtractorJobPartitioner implements Partitioner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleExtractorJobPartitioner.class);

    /**
     * Store service to access the <code>stores</code> table
     */
    @Autowired
    private ApiService service;
    
    /**
     * JDBC template to the <code>pos_production</code> database
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * Name of the API, e.g. "erply"
     */
    private String apiName;
    
    /**
     * Query to access the stores, e.g. <code>SELECT store_id FROM stores WHERE api_id = ? AND active='1' ORDER BY store_id ASC</code>
     */
    private String queryString;

    /**
     * Partitions job using gridSize
     */
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        
        Map<String,ExecutionContext> retval = new HashMap<String, ExecutionContext>();
        
        Long apiId = service.findByName(apiName).getApiId();
        
        List<Long> ids = jdbcTemplate.queryForList(queryString, Long.class, apiId);
        final int size = ids.size();
        
        int chunkSize = (int)Math.ceil(1.0D * size / gridSize);
        
        if(chunkSize < 1){
            chunkSize = 1;
        }
        int fromIndex = 0;
        int toIndex = chunkSize-1;
        
        for (int i = 1; i <= gridSize && fromIndex < size; i++) {
            ExecutionContext value = new ExecutionContext();
 
            final long fromId = ids.get(fromIndex);
            final long toId = ids.get(Math.min(toIndex,size-1));
            
            // Prepare list of actual ids
            List<Long> idsForSegment = new ArrayList<Long>();
            for(int k = fromIndex; k <= Math.min(toIndex,size-1); k++){
                idsForSegment.add(ids.get(k));
            }
            
            value.putLong("fromId", fromId);
            value.putLong("toId", toId);
            value.put("idList", idsForSegment);
             
            // give each thread a name, thread 1,2,3
            value.putString("name", "Thread" + i);
 
            retval.put("partition" + i, value);
            
            LOGGER.debug("Partition (API: {}) {} is {} -> {}", apiName, i,fromId,toId);
 
            fromIndex = toIndex + 1;
            toIndex += chunkSize;
        }
        
        return retval;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
}
