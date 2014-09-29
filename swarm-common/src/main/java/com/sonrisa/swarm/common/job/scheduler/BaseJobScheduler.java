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
package com.sonrisa.swarm.common.job.scheduler;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class helps the classes responsible for scheduled launching of the spring batch jobs.
 *
 * @author joe
 */
public abstract class BaseJobScheduler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseJobScheduler.class);
    
    @Autowired
    private JobLauncher jobLauncher;
    
    public BaseJobScheduler() {
        LOGGER.debug(BaseJobScheduler.class.getSimpleName() + " has been instantiated");
    }
    
    protected void startJob(Job job){
        startJob(job, createParams());
    }
    
    protected void startJob(Job job, JobParameters jobParameters) {
        try {            
            LOGGER.debug("Attempting to run " + job.getName());
            jobLauncher.run(job, jobParameters);
        } catch (JobExecutionAlreadyRunningException ex) {
            LOGGER.warn("JobExecutionAlreadyRunningException occured during the launching of this job: " + job.getName(), ex);
        } catch (JobRestartException ex) {
            LOGGER.warn("JobRestartException occured during the launching of this job: " + job.getName(), ex);            
        } catch (JobInstanceAlreadyCompleteException ex) {
            LOGGER.warn("JobInstanceAlreadyCompleteException occured during the launching of this job: " + job.getName(), ex);            
        } catch (JobParametersInvalidException ex) {
            LOGGER.warn("JobParametersInvalidException occured during the launching of this job: " + job.getName(), ex);
            
        }
    }
    
    protected JobParameters createParams(){
        final JobParametersBuilder jb = new JobParametersBuilder();
        jb.addString("runId", UUID.randomUUID().toString());
        return jb.toJobParameters();
    }
}
