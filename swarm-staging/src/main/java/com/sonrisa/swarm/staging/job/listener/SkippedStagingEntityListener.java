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
package com.sonrisa.swarm.staging.job.listener;

import com.sonrisa.swarm.model.BaseSwarmEntity;
import com.sonrisa.swarm.model.StageAndLegacyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;

/**
 * This listener logs if a staging entity has been skipped
 * because of an unexpected exception.
 *
 * @author joe
 */
public class SkippedStagingEntityListener implements SkipListener<BaseSwarmEntity, StageAndLegacyHolder> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkippedStagingEntityListener.class);
    
    @Override
    public void onSkipInRead(Throwable t) {
        if (t != null){
            LOGGER.warn("Item skipped during the reading, because of an exception. ", t);                 
        }
    }

    @Override
    public void onSkipInWrite(StageAndLegacyHolder item, Throwable t) {
        if (t != null){
            LOGGER.warn("Item skipped during the writing, because of an exception. Item: " + item, t);         
        }
    }

    @Override
    public void onSkipInProcess(BaseSwarmEntity item, Throwable t) {    
        if (t != null){
            LOGGER.warn("Item skipped during the processing, because of an exception Item: " + item, t);     
        }
    } 
      
}
