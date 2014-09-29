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

package com.sonrisa.swarm.admin.service;

import com.sonrisa.swarm.admin.model.BaseStatusEntity;
import com.sonrisa.swarm.model.BaseSwarmEntity;

/**
 * Service which calculates the status of a store
 * 
 * @param <E> Input
 * @param <S> Output
 * @author Barnabas
 */
public interface StatusProcessingService<E extends BaseSwarmEntity, S extends BaseStatusEntity> {

    /**
     * Date format for formatting dates
     */
    String STORE_STATUS_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    
    /**
     * Process store entity and create status entity
     * @param store
     * @return
     */
    S processStore(E store);
}