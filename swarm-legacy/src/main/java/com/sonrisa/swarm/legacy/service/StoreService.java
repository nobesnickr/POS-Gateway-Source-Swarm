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
package com.sonrisa.swarm.legacy.service;

import hu.sonrisa.backend.service.GenericService;

import java.util.List;
import java.util.Map;

import com.sonrisa.swarm.model.legacy.StoreEntity;

/**
 * This interface describes the operations of the {@link StoreEntity} entities.
 *
 * @author joe
 */
public interface StoreService extends GenericService<Long, StoreEntity>{
	
    /**
     * Creates or updates a store entity.
     * 
     * @param store
     * @return 
     */
    Long save(StoreEntity store);
}
