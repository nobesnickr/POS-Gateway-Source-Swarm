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
package com.sonrisa.swarm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.test.context.ContextConfiguration;

/**
 * Common base class of the test classes responsible for
 * testing the Spring Batch jobs.
 *
 * @author joe
 */
@ContextConfiguration(locations = {"classpath:batch-test-applicationContext.xml"})
public abstract class BaseBatchTest extends BaseIntegrationTest {
    /**
     * 
     * @param jobUtils
     * @return 
     */
    protected JobExecution launchJob(JobLauncherTestUtils jobUtils) {
        JobExecution jobResult = null;
        try {
            jobResult = jobUtils.launchJob();
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
        
        if (jobResult == null){
            fail("Job has not been launched.");
            return null;
        }
        
        assertEquals(ExitStatus.COMPLETED, jobResult.getExitStatus());
        return jobResult;
    }
}
