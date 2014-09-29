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


package com.sonrisa.swarm.admin.service.impl;

import hu.sonrisa.backend.dao.filter.JpaFilter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.admin.model.RpStatusEntity;
import com.sonrisa.swarm.admin.model.query.RpStatusQueryEntity;
import com.sonrisa.swarm.admin.service.RpStatusService;
import com.sonrisa.swarm.admin.service.StatusProcessingService;
import com.sonrisa.swarm.admin.service.exception.InvalidStatusRequestException;
import com.sonrisa.swarm.posintegration.service.ApiService;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity.ApiType;
import com.sonrisa.swarm.retailpro.dao.RpStoreDao;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity.RpStoreState;

/**
 * Status service implementation which uses the <code>stores_rp</code> table's 
 * rows to provide status information on Retail Pro stores.
 * 
 * @author Barnabas
 */
@Service
@Transactional(readOnly=true)
public class RpStatusServiceImpl extends BaseStatusServiceImpl<RpStoreEntity, RpStatusEntity, RpStatusQueryEntity> implements RpStatusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpStatusServiceImpl.class);
    
    /**
     * Some Retail Pro stores are built-in Retail Pro V9 stores, and should be ignored
     */
    @Value("${admin.status.retailpro.ignore.names}")
    private String ignoredStoreNames;

    /**
     * {@inheritDoc}
     */
    @Autowired
    public RpStatusServiceImpl(@Qualifier("rpStatusProcessingService") StatusProcessingService<RpStoreEntity, RpStatusEntity> statusProcessingService, RpStoreDao storeDao, ApiService apiService) {
        super(statusProcessingService, storeDao, apiService, RpStoreEntity.class);
    }
        
    /**
     * {@inheritDoc}
     */
    @Override
    protected JpaFilter<RpStoreEntity> convertStatusQueryToJpaFilter(RpStatusQueryEntity queryConfig) throws InvalidStatusRequestException {

        // Setup query
        SimpleFilter<RpStoreEntity> jpaFilter = SimpleFilter.of(RpStoreEntity.class);
        
        List<String> posSoftwares = null;
        final Set<String> allowedPosSoftwares = apiService.findApiNamesByType(ApiType.RETAILPRO_API);
        
        // Query swarm id
        if(!StringUtils.isEmpty(queryConfig.getSwarmId())){
            jpaFilter.addParameter("swarmId", queryConfig.getSwarmId());
        }
        
        // Query api (pos-software)
        if(!queryConfig.getApi().isEmpty()){
            posSoftwares = new ArrayList<String>();
        
            for(String queryApi : queryConfig.getApi()){
                if (!allowedPosSoftwares.contains(queryApi)) {
                    throw new InvalidStatusRequestException("API is not associated with the gateway: "
                            + queryApi + " allowed values are: " + StringUtils.join(allowedPosSoftwares, ','));
                }
                posSoftwares.add(queryApi);
            }
        } else {
            posSoftwares = new ArrayList<String>(allowedPosSoftwares);
        }
        
        // When query contains the fallback API for empty API, 
        // then include empty and null too as they are assumed
        // to be the ASSUMED_EMPTY_API
        if(posSoftwares.contains(ASSUMED_EMPTY_API)){
            posSoftwares.add(null);
            posSoftwares.add("");
        }
    
        jpaFilter.addParameter("posSoftware", posSoftwares, " IN ");
        
        // Ignore certain stores
        if(!StringUtils.isEmpty(ignoredStoreNames)){
            String[] ignoredStores = ignoredStoreNames.split(",");
            if(ignoredStores.length > 0){
                jpaFilter.addParameter("storeName", Arrays.asList(ignoredStores), " NOT IN ");
            }
        }
        
        // Unless including all stores, only return NORMAL stores
        if(!queryConfig.getIncludeAll()){
            jpaFilter.addParameter("state", RpStoreState.NORMAL);
        }
              
        StringBuilder sort = new StringBuilder();
        sort.append(getJpaFieldName(RpStoreEntity.class, queryConfig.getOrderBy())).append(" ").append(queryConfig.getOrderDir().getValue());
        jpaFilter.setSort(sort.toString());
        
        return jpaFilter;
    }

    public void setIgnoredStoreNames(String ignoredStoreNames) {
        this.ignoredStoreNames = ignoredStoreNames;
    }

    @Override
    public Logger logger() {
        return LOGGER;
    }
}
