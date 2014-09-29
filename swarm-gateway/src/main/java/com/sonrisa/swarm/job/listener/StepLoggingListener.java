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
package com.sonrisa.swarm.job.listener;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

import com.sonrisa.swarm.common.job.logger.TimingLogger;

/**
 * Step execution logging for debugging purposes.
 *
 * @author joe
 */
public class StepLoggingListener extends StepExecutionListenerSupport {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StepLoggingListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug(stepExecution.getStepName() + " step execution begins.");            
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        final String stepName = stepExecution.getStepName();            
        final Long stepId = stepExecution.getId();            
           
        long executionTimeMillis = 0;
        Date startTime = stepExecution.getStartTime();
        Date endTime = stepExecution.getEndTime();
        if(startTime != null ){
            if(endTime == null){
                endTime = new Date();
            }
            executionTimeMillis = endTime.getTime() - startTime.getTime();
        }
        
        if (LOGGER.isDebugEnabled()){
              final ExitStatus exitStatus = stepExecution.getExitStatus();            
            final int readCount = stepExecution.getReadCount();            
            final int writeCount = stepExecution.getWriteCount();            
            final int commitCount = stepExecution.getCommitCount();  
    
            
            
            LOGGER.debug("{}(ID:{}) finished in {}ms, Read/write/commit count: {}/{}/{}, Exit status: {}",
                    stepName, stepId, executionTimeMillis, readCount, writeCount, commitCount, exitStatus);
        }
        
        TimingLogger.debug("{}(ID:{}) finished", executionTimeMillis, stepName, stepId);
        
        return null;
    }
}
