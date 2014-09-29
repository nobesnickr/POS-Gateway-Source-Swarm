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

package com.sonrisa.swarm.posintegration.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.posintegration.service.ApiService;
import com.sonrisa.swarm.posintegration.service.exception.ApiNotFoundException;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity.ApiType;

/**
 * Implementation of the {@link ApiService} which reads
 * all apis once into the memory, caches it and uses 
 * a map the answer to requests
 * 
 * @author Barnabas
 */
@Service
public class ApiServiceImpl implements ApiService, InitializingBean {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiServiceImpl.class);

    /**
     * Allowed Retail Pro Pos softwares
     */
    @Value("${api.name.allowedpos}")
    private String allowedPos;
    
    /**
     * Cached of all the {@link ApiEntity} values, mapped using <code>api_id</code> 
     */
    private Map<Long, ApiEntity> apiCacheById;

    /**
     * Cached of all the {@link ApiEntity} values, mapped using <code>api_name</code> 
     */
    private Map<String, ApiEntity> apiCacheByName;

    /**
     * DAO used to access data in the <code>stores</code> table
     */
    @Autowired
    private StoreDao dao;
    
    /**
     * API tagged with this in the notes column of the <code>apis</code> will
     * be considered "gateway managed" apis.
     */
    public static final String GATEWAY_API_TAG = "gateway-plugin"; 
    
    /**
     * Initialize the cache
     */
    @Override
    public void afterPropertiesSet() {
        refreshCache();
    }

    /**
     * Initialize the cache
     */
    public void refreshCache() {

        final Set<String> retailProApiNames = getRetailProApiNames();

        List<ApiEntity> allApis = new ArrayList<ApiEntity>();

        // Read all apis
        final EntityManager em = dao.getEntityManager();

        final Query selectApiIdQuery = em.createNativeQuery("select name, api_id, notes from apis");
        
        // Count for logging
        int retailProApi = 0, pullApi = 0, swarmApi = 0;

        final List<Object[]> resultList = selectApiIdQuery.getResultList();
        for (Object[] row : resultList) {
            final String rowApiName = row[0].toString();
            final String notes = row[2] != null ? row[2].toString() : "";

            ApiEntity.ApiType apiType = ApiEntity.ApiType.SWARM_API;
            
            if (retailProApiNames.contains(rowApiName)) {
                apiType = ApiEntity.ApiType.RETAILPRO_API;
                retailProApi++;
            } else if (GATEWAY_API_TAG.equalsIgnoreCase(notes)){
                apiType = ApiEntity.ApiType.PULL_API;
                pullApi++;
            } else {
                swarmApi++;
            }

            // Add new row
            if (row[1] instanceof BigInteger) {
                allApis.add(new ApiEntity(((BigInteger) row[1]).longValue(), rowApiName, apiType));
            } else if (row[1] instanceof Long) {
                allApis.add(new ApiEntity(((Long) row[1]), rowApiName, apiType));
            }
        }
        
        LOGGER.info("Finished build API cache, contains {} Retail Pro API, {} Pull-type GW API and {} other", retailProApi, pullApi, swarmApi);
        
        fillCache(allApis);
    }

    /**
     * Fills cache based an all known APIs
     */
    public void fillCache(List<ApiEntity> allApis) {
        apiCacheById = new HashMap<Long, ApiEntity>();
        apiCacheByName = new HashMap<String, ApiEntity>();

        for (ApiEntity api : allApis) {
            apiCacheById.put(api.getApiId(), api);
            apiCacheByName.put(api.getApiName(), api);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApiEntity findByName(String apiName) {
        if (!apiCacheByName.containsKey(apiName)) {
            throw new ApiNotFoundException(apiName);
        }

        return apiCacheByName.get(apiName);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ApiEntity findById(Long apiId) {
        if (!apiCacheById.containsKey(apiId)) {
            throw new ApiNotFoundException(apiId);
        }

        return apiCacheById.get(apiId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ApiEntity> findManyByName(List<String> apiNames) {
        Set<ApiEntity> retVal = new HashSet<ApiEntity>();
        for (String apiName : apiNames) {
            retVal.add(findByName(apiName));
        }
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ApiEntity> findManyById(List<Long> apiIds) {
        Set<ApiEntity> retVal = new HashSet<ApiEntity>();
        for (Long apiId : apiIds) {
            retVal.add(findById(apiId));
        }
        return retVal;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ApiEntity> findManyByType(ApiType byType) {
        Set<ApiEntity> retVal = new HashSet<ApiEntity>();
        for (ApiEntity api : apiCacheById.values()) {
            if (api.getApiType().equals(byType)) {
                retVal.add(api);
            }
        }
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> findApiNamesByType(ApiType byType) {
        Set<ApiEntity> allowedApis = findManyByType(byType);
        Set<String> retVal = new HashSet<String>();
        for(ApiEntity api : allowedApis){
            retVal.add(api.getApiName());
        }
        return retVal;
    }

    /**
     * Get the API names for APIs which are push type (RetailPro)
     * @return
     */
    private Set<String> getRetailProApiNames() {
        return new HashSet<String>(Arrays.asList(this.allowedPos.split(",")));
    }
}
