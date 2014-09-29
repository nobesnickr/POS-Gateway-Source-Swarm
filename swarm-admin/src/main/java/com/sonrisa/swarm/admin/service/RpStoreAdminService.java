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

import com.sonrisa.swarm.admin.model.RpStoreAdminServiceEntity;
import com.sonrisa.swarm.admin.service.exception.InvalidAdminRequestException;
import com.sonrisa.swarm.admin.service.exception.UnknownStoreException;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;

public interface RpStoreAdminService {

    /**
     * Updates {@link RpStoreEntity} based on a {@link RpStoreAdminServiceEntity} 
     * 
     * @param storeId Which store to update?
     * @param entity Content for updated store, e.g. new store name
     * 
     * @throws UnknownStoreException If the first argument refers to a non-existing or illegal store
     */
    void update(Long storeId, RpStoreAdminServiceEntity entity) throws InvalidAdminRequestException;
}
