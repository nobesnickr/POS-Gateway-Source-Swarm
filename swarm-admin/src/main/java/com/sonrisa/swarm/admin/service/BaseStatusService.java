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

import java.util.List;

import com.sonrisa.swarm.admin.model.BaseStatusEntity;
import com.sonrisa.swarm.admin.model.StatusEntity;
import com.sonrisa.swarm.admin.model.query.BaseStatusQueryEntity;
import com.sonrisa.swarm.admin.service.exception.InvalidStatusRequestException;

/**
 * Service to access status details on stores.
 *  
 * @author Barnabas
 */
public interface BaseStatusService<S extends BaseStatusEntity, Q extends BaseStatusQueryEntity> {
	
	/**
	 * Request {@link StatusEntity} from database.
	 */
	List<S> getStoreStatuses(Q queryConfig) throws InvalidStatusRequestException;
}
