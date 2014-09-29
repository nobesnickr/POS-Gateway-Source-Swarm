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
package com.sonrisa.swarm.staging.job.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.common.job.scheduler.BaseJobScheduler;

/**
 * Class responsible for scheduling staging related jobs, like jobs which 
 * load stating entities into the legacy tables or the stageCleaning job.
 * 
 * @author Barna
 */
@Component
public class StagingJobScheduler extends BaseJobScheduler {

    @Autowired
    @Qualifier("loaderJob")
    private Job loaderJob;
    
    @Autowired
    @Qualifier("cleanerJob")
    private Job cleanerJob;
    
    /**
     * This method invokes the invoice job based on the given cron expression.
     */
    @Scheduled(cron = "${jobScheduler.staging.loader.job.cron.expession}")
    public void launchLoaderJob(){
        startJob(loaderJob);
    }
    
    /**
     * This method invokes the invoice job based on the given cron expression.
     */
    @Scheduled(cron = "${jobScheduler.staging.cleaner.job.cron.expession}")
    public void launchCleanerJob(){
        startJob(cleanerJob);
    }
}
