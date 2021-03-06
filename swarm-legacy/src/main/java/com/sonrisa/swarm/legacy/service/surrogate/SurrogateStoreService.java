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
package com.sonrisa.swarm.legacy.service.surrogate;

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.BaseStageEntity;

/**
 * This interface provides access to the StoreEntity if SwarmId/SbsNo/StoreNumber is provided
 * 
 * @author barna
 */
public interface SurrogateStoreService {
    /**
     * This method finds a store using the combination of these:
     *  - swarmId (identifies the RetailPro installation)
     *  - sbs number (identifies a subsidiary in the RetailPro)
     *  - store number (identifies a store below the subsidiary in the RetailPro)
     * 
     * @param stagingEntity Entity from staging
     * @return 
     */
    StoreEntity findStoreForStagingEntity(BaseStageEntity stagingEntity);
}
