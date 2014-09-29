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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.admin.model.StatusEntity;
import com.sonrisa.swarm.admin.model.query.StatusQueryEntity;
import com.sonrisa.swarm.admin.service.BaseStatusService;
import com.sonrisa.swarm.admin.service.StatusProcessingService;
import com.sonrisa.swarm.admin.service.StatusService;
import com.sonrisa.swarm.admin.service.exception.InvalidStatusRequestException;
import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.service.ApiService;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity.ApiType;

/**
 * Implementation of the {@link BaseStatusService} interface, which uses the <code>stores</code>
 * table's rows to provide information on non-Retail Pro and gateway managed stores (e.g. shopify or erply).
 * 
 * @author Barnabas
 */
@Service
@Transactional(readOnly=true)
public class StatusServiceImpl extends BaseStatusServiceImpl<StoreEntity, StatusEntity, StatusQueryEntity> implements StatusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusServiceImpl.class);

    /**
     * {@inheritDoc}
     */
    @Autowired
    public StatusServiceImpl(@Qualifier("statusProcessingService") StatusProcessingService<StoreEntity, StatusEntity> statusProcessingService, StoreDao storeDao, ApiService apiService) {
        super(statusProcessingService, storeDao, apiService, StoreEntity.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JpaFilter<StoreEntity> convertStatusQueryToJpaFilter(StatusQueryEntity queryConfig) throws InvalidStatusRequestException {

        // Setup query
        SimpleFilter<StoreEntity> jpaFilter = SimpleFilter.of(StoreEntity.class);
        if (queryConfig.getActive() != null) {
            jpaFilter.addParameter("active", queryConfig.getActive());
        }

        final List<Long> apiIds = convertApiNamesToApiIds(queryConfig, apiService.findApiNamesByType(ApiType.PULL_API));
        jpaFilter.addParameter("apiId", apiIds, " IN ");

        StringBuilder sort = new StringBuilder();
        sort.append(getJpaFieldName(StoreEntity.class, queryConfig.getOrderBy())).append(" ")
                .append(queryConfig.getOrderDir().getValue());
        jpaFilter.setSort(sort.toString());

        return jpaFilter;
    }

    @Override
    public Logger logger() {
        return LOGGER;
    }
}
