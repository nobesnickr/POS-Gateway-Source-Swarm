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
package com.sonrisa.swarm.model;


import com.sonrisa.swarm.model.legacy.BaseLegacyEntity;
import com.sonrisa.swarm.model.staging.BaseStageEntity;

/**
 * This is a helper class to encapsulate the legacy object that
 * has been created from a staging entity and the the staging entity itself.  
 *
 * @author joe
 */
public class StageAndLegacyHolder<U extends BaseStageEntity, T extends BaseLegacyEntity> {
    
    /** The legacy entity that has been created/converted from the staging entity. */
    private T legacyEntity;
    /** The original staging entity. */
    private U stagingEntity;
    
    /** 
     * This flag indicates that the staging entity can not be moved to the 
     * legacy DB and there is no chance to a next successful try.
     * 
     * If this flag is true, the  {@link #legacyEntity} field is definitely null.
     */
    private boolean stagingEntityAbsolutelyUnprocessable = false;
    
    /** 
     * This message is usually belongs to the {@link #stagingEntityAbsolutelyUnprocessable} flag. 
     * It is a short explanation why the staging entity is so desperately unprocessable.
     */
    private String message;

    /**
     * Constructor.
     * 
     * @param legacyEntity The legacy entity that has been created/converted from the staging entity.
     * @param stagingEntity Rhe original staging entity.
     */
    public StageAndLegacyHolder(T legacyEntity, U stagingEntity) {
        this.legacyEntity = legacyEntity;
        this.stagingEntity = stagingEntity;
    }
    
    /**
     * Constructor used if the staging entity is unprocessable.
     * 
     * @param stagingEntity
     * @param stagingEntityNeverBeAbledToMoved
     * @param msg 
     */
    public StageAndLegacyHolder(U stagingEntity, String msg) {
        this.stagingEntity = stagingEntity;
        this.stagingEntityAbsolutelyUnprocessable = true;
        this.message = msg;
    }
    

    public T getLegacyEntity() {
        return legacyEntity;
    }

    public U getStagingEntity() {
        return stagingEntity;
    }

    public Long getStagingEntityId() {
        return stagingEntity != null ? stagingEntity.getId() : null;
    }                    

    public boolean isStagingEntityAbsolutelyUnprocessable() {
        return stagingEntityAbsolutelyUnprocessable;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStagingEntityAbsolutelyUnprocessable(boolean stagingEntityAbsolutelyUnprocessable) {
        this.stagingEntityAbsolutelyUnprocessable = stagingEntityAbsolutelyUnprocessable;
    }


    
    
    
}
