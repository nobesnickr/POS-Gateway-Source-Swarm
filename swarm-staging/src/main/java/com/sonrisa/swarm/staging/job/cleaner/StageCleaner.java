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
package com.sonrisa.swarm.staging.job.cleaner;

import java.sql.Timestamp;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.sonrisa.swarm.model.staging.annotation.StageInsertableType;

/**
 * This class is responsible offers the functionality to delete
 * items from the staging table which are older than a certain age.
 */
public class StageCleaner implements Tasklet {
    private static final Logger LOGGER = LoggerFactory.getLogger(StageCleaner.class);
    
    /**
     * Rows older than ageLimit number of hours will be deleted by this Tasklet
     */
    private int ageLimit;
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    
    /**
     * Clean items from staging tables older then a certain age
     * @param age Age of the deleted items in milliseconds
     */
    public int clean(Long age){
        final String prefix = StageInsertableType.tablePrefix;
        final String tables[] =  {"customers", "invoices", "invoice_lines", "products", "categories"};
        
        final Timestamp criteria = new Timestamp(new Date().getTime() - age);
        
        int total = 0;
        for(String table : tables){
            int rowsChanged = jdbcTemplate.update("DELETE FROM " + prefix + table + " WHERE stage_created_at < ?", criteria);
            if(rowsChanged > 0) {
                LOGGER.debug(String.format("Cleaned %d rows from %s%s as their age exceeded to preset limit.", rowsChanged, prefix, table));
                total += rowsChanged;
            }
        }
        LOGGER.info("Cleaner executed, with total number of deleted rows from staging tables: " + total);
        return total;
    }

    /**
     * Execute cleaning using the limit set in the batch.properties file
     */
    @Override
    public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
        long ageLimitMillis = (long)ageLimit * 3600 * 1000;
        LOGGER.debug(String.format("Starting stageCleaner to clean entires older than %dh", ageLimit));
        clean(ageLimitMillis);
        return RepeatStatus.FINISHED;       
    }

    public int getAgeLimit() {
        return ageLimit;
    }

    public void setAgeLimit(int ageLimit) {
        this.ageLimit = ageLimit;
    }
}
