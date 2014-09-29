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

package com.sonrisa.swarm.retailpro.service.impl;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.model.staging.BaseStageEntity;
import com.sonrisa.swarm.retailpro.model.enums.JsonType;
import com.sonrisa.swarm.retailpro.service.RpInvoiceService;
import com.sonrisa.swarm.retailpro.util.mapper.EntityHolder;
import com.sonrisa.swarm.retailpro.util.mapper.EntityMapper;
import com.sonrisa.swarm.staging.service.CustomerStagingService;
import com.sonrisa.swarm.staging.service.InvoiceLineStagingService;
import com.sonrisa.swarm.staging.service.InvoiceStagingService;
import com.sonrisa.swarm.staging.service.ProductStagingService;

/**
 * This service contains the RetailPro specific invoice operations.
 */
@Service
public class RpInvoiceServiceImpl implements RpInvoiceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpInvoiceServiceImpl.class);

    @Autowired
    private InvoiceStagingService invoiceStgService;

    @Autowired
    private InvoiceLineStagingService invoiceLineStgService;

    @Autowired
    private CustomerStagingService customerStgService;

    @Autowired
    private ProductStagingService productStgService;

    /**
     * Converts JSON entities to swarm entity
     */
    @Autowired
    private EntityMapper entityMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void writeToStage(EntityHolder entities) {


        customerStgService.create(entities.getCustomers());
        productStgService.create(entities.getProducts());
        invoiceStgService.create(entities.getInvoices());
        invoiceLineStgService.create(entities.getItems());

        LOGGER.debug("Done inserting entities into stage");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityHolder processMap(String swarmId, Map<String, Object> jsonMap) {

        EntityHolder result = new EntityHolder();
        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            String jsonTypeStr = entry.getKey();
            JsonType jsonType = JsonType.valueOf(jsonTypeStr);

            // This is actually a collection of JSON object from the given
            // jsonType
            Object jsonCollectionObj = entry.getValue();

            if (jsonCollectionObj instanceof Collection) {
                Collection<?> jsonCollection = (Collection<?>) jsonCollectionObj;
                for (Object jsonObj : jsonCollection) {
                    // entity class to which this JSON object (which is actually
                    // a map) has to be transformed
                    Class<? extends BaseStageEntity> targetClass = jsonType.getSwarmEntityType();

                    BaseStageEntity entity = entityMapper.convertToStageEntity((Map<String, Object>) jsonObj,
                            targetClass);
                    entity.setSwarmId(swarmId);
                    result.addEntity(entity);
                    LOGGER.trace("Item has been processed: " + jsonObj);
                }
            }
        }

        return result;
    }
}
