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

package com.sonrisa.swarm.kounta.job;

import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.common.job.scheduler.BaseJobScheduler;

/**
 * Class responsible for scheduling Kounta extractor jobs
 */
@Component
public class KountaJobScheduler extends BaseJobScheduler {

    @Autowired
    @Qualifier("kountaExtractorJob")
    private Job kountaExtractorJob;
    

    @Autowired
    @Qualifier("kountaProcessingJob")
    private Job kountaProcessingJob;
    
    @Autowired
    @Qualifier("kountaStoreJob")
    private Job kountaStoreJob;
    
    /**
     * This method invokes the invoice job based on the given cron expression.
     */
    @Scheduled(cron = "${jobScheduler.kounta.job.cron.expession}")
    public void launchKountaExtractorJob(){
        startJob(kountaExtractorJob);
    }
    

    /**
     * This method invokes the invoice job based on the given cron expression.
     */
    @Scheduled(cron = "${jobScheduler.kounta.job.processor.cron.expession}")
    public void launchKountaProcessorJob(){
        startJob(kountaProcessingJob);
    }
    

    /**
     * This method invokes the invoice job based on the given cron expression.
     */
    @Scheduled(cron = "${jobScheduler.kounta.job.store.cron.expession}")
    public void launchKountaStoreJob(){
        startJob(kountaStoreJob);
    }
}
