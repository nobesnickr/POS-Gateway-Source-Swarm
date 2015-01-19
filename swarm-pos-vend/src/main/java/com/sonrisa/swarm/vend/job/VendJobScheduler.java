package com.sonrisa.swarm.vend.job;

import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.common.job.scheduler.BaseJobScheduler;

/**
 * Class responsible for scheduling Vend extractor jobs
 */
@Component
public class VendJobScheduler extends BaseJobScheduler {

    @Autowired
    @Qualifier("vendExtractorJob")
    private Job vendExtractorJob;
    
    /**
     * This method invokes the invoice job based on the given cron expression.
     */
    @Scheduled(cron = "${jobScheduler.vend.job.cron.expession}")
    public void launchVendExtractorJob(){
        startJob(vendExtractorJob);
    }
}
