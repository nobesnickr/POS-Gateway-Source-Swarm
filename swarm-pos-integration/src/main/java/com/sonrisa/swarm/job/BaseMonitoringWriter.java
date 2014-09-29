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
package com.sonrisa.swarm.job;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import com.sonrisa.swarm.model.BaseSwarmEntity;

/**
 * Base implementation for {@link ItemWriter}, which doesn't actually write anything, 
 * but logs the that the processing was finished
 */
public abstract class BaseMonitoringWriter<T extends BaseSwarmEntity> implements ItemWriter<T> {

    public static final String NUM_OF_ITEMS_EXTRACTED = "numOfItemsExtracted";

    private StepExecution stepExecution;
    
    @Override
    public void write(List<? extends T> items) throws Exception {
        for (T item : items){
            logger().info("Extraction of this item has been finished: " + item);
        }
        
        countFetchedItems(items);
        
        // TODO:
        // we could update the UPDATES table here if we'd like to
    }
    
    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution){
        this.stepExecution = stepExecution;
    }
    
    @AfterStep
    public void afterStep(StepExecution stepExecution){
        ExecutionContext execContext = getExecContext();
        final int numOfItems = execContext.getInt(NUM_OF_ITEMS_EXTRACTED, 0);
        
        logger().debug("Total finised: " + numOfItems);
    }
    

    /**
     * The writer counts the number of the fetched stores and 
     * records this value in the job execution context.
     * 
     * This is useful for logging and debugging purposes.
     * 
     * @param items 
     */
    private synchronized void countFetchedItems(List<? extends T> items) {
        ExecutionContext execContext = getExecContext();
        final int numOfItems = execContext.getInt(NUM_OF_ITEMS_EXTRACTED, 0);
        execContext.putInt(NUM_OF_ITEMS_EXTRACTED, numOfItems + items.size());        
    }

    /**
     * Returns the job execution context.
     * 
     * @return 
     */
    private ExecutionContext getExecContext() {
        final JobExecution jobExecution = stepExecution.getJobExecution();        
        return jobExecution.getExecutionContext();        
    }
    
    /**
     * Child logger
     * @return
     */
    protected abstract Logger logger();
}
