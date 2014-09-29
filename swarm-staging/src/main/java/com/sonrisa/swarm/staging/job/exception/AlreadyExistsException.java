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
package com.sonrisa.swarm.staging.job.exception;

import com.sonrisa.swarm.model.staging.BaseStageEntity;

/**
 * This exception is thrown if a staging entity can not be moved to the
 * legacy db beacuse it is already exists.
 *
 * @author joe
 */
@SuppressWarnings("serial")
public class AlreadyExistsException extends Exception{

    /** ID of the legacy entity. */
    private Long existingLegacyEntityId;
    
        /** The entity that causes the exception. */
    private BaseStageEntity stagingEntity;

    public AlreadyExistsException(BaseStageEntity stagingEntity, Long existingLegacyEntityId) {
        super("Entity already exists");
        this.stagingEntity = stagingEntity;
        this.existingLegacyEntityId = existingLegacyEntityId;
    }

    public Long getExistingLegacyEntityId() {
        return existingLegacyEntityId;
    }

    public BaseStageEntity getStagingEntity() {
        return stagingEntity;
    }
}
