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

package com.sonrisa.swarm.posintegration.service;

import java.util.List;
import java.util.Set;

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity;

/**
 * Service to access the contents of the <code>apis</code>
 * which stores meta information for the {@link StoreEntity#getApiId()}  
 * 
 * @author Barnabas
 */
public interface ApiService {
    
    /** 
     * Finds the ID of an API by name from the 'apis' table.
     * Including non-gateway related and RetailPro APIs 
     */
    ApiEntity findByName(String apiName);
    
    /** 
     * Finds the name of an API by name from the 'apis' table.
     * Including non-gateway related and RetailPro APIs 
     */
    ApiEntity findById(Long apiId);

    /**
     * Finds all APIs for api names
     * including non-gateway related and RetailPro APIs
     */
    Set<ApiEntity> findManyByName(List<String> apiNames);
    
    /**
     * Finds all APIs by api id
     * including non-gateway related and RetailPro APIs
     */
    Set<ApiEntity> findManyById(List<Long> apiIds);
    
    /**
     * Find all APIs, including non-gateway related and RetailPro APIs
     * @return
     */
    Set<ApiEntity> findManyByType(ApiEntity.ApiType byType);
    
    /**
     * Find all API names, including non-gateway related and RetailPro APIs
     * @return
     */
    Set<String> findApiNamesByType(ApiEntity.ApiType byType);
}
