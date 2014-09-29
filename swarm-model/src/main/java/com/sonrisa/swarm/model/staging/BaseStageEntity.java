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
package com.sonrisa.swarm.model.staging;

import com.sonrisa.swarm.model.BaseSwarmEntity;

/**
 * Common base class of all the entities in the stage database.
 *
 * @author joe
 */
public abstract class BaseStageEntity extends BaseSwarmEntity {  

    /**
     * Returns with the swarmId that identifies one RetailPro installation 
     * which communicates our application through the Rest API.
     * 
     * Only entities from Retail Pro have this value, in case of other POS systems this is null. 
     * 
     * @return swarmId
     */
    public abstract String getSwarmId();
    
    /**
     * Sets the swarmId that identifies one RetailPro installation 
     * which communicates our application through the Rest API.
     * 
     * @param swarmId the swarmId belongs to one RetailPro installation
     */
    public abstract void setSwarmId(String swarmId);
       
    /**
     * Returns the inner ID of the store where this staging entity comes from.
     * 
     * Inner means the ID identifies this store within the Swarm System.
     * This ID could be null if the entity does not know the inner ID of its store.
     * E.g.: Entities from RetailPro stores.
     */
    public abstract Long getStoreId();
    
    /**
     * Returns the foreign store number (foreign storeID) of this staging entity.
     * 
     * This field is usually null except on entities received from RetailPro.
     * 
     * @return 
     */
    public abstract String getLsStoreNo();
    
    /**
     * Returns the foreign subsidiary number (foreign subsidiaryID) of this staging entity.
     * 
     * This field is usually null except on entities received from RetailPro.
     * 
     * @return 
     */
    public abstract String getLsSbsNo();
           
}
