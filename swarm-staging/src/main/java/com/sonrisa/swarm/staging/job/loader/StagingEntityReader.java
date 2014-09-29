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
package com.sonrisa.swarm.staging.job.loader;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.sonrisa.swarm.model.staging.BaseStageEntity;
import com.sonrisa.swarm.staging.service.BaseStagingService;

/**
 * This spring batch reader is responsible for reading staging entities.
 * 
 * It implements a paging reader. First it reads all the IDs and then it
 * reads only one batch of entities from the DB.
 *
 * @author joe
 */
public class StagingEntityReader extends AbstractPagingItemReader<BaseStageEntity>{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StagingEntityReader.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private String sql;
    
    
    private List<Long> allIds;
    private int size;
    
    private BaseStagingService stageService;

    @Override
    protected void doOpen() throws Exception {
        super.doOpen();
        allIds = jdbcTemplate.queryForList(sql, Long.class);
        size = allIds.size();        
    }
    
    

    @Override
    protected void doReadPage() {
        final int fromIndex = getPage() * getPageSize();
        final int toIndexCandidate = fromIndex + getPageSize();
        LOGGER.debug("doReadPage() -- toIndexCandidate: " + toIndexCandidate + " batchSize: " + getPageSize());
        
        // high endpoint (exclusive) of the subList
        final int toIndex = toIndexCandidate > size ? size : toIndexCandidate;
        LOGGER.debug("doReadPage() -- toIndex: "+ toIndex + " full size: " + size);
        final List<Long> batch = allIds.subList(fromIndex, toIndex);
        
        if (results == null){
            results = new ArrayList<BaseStageEntity>();
        } else {
            results.clear(); 
        }
        
        if (batch != null && !batch.isEmpty()){
            results.addAll(stageService.findByIds(batch));  
        }
       
    }

    @Override
    protected void doJumpToPage(int itemIndex) {
        
    }

    public void setStageService(BaseStagingService stageService) {
        this.stageService = stageService;
    }
    
    public void setSql(String sql) {
        this.sql = sql;
    }
}
