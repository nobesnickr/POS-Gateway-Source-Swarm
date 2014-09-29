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
package com.sonrisa.swarm.staging.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.staging.dao.ManufacturerStageDao;
import com.sonrisa.swarm.model.staging.ManufacturerStage;
import com.sonrisa.swarm.staging.service.ManufacturerStagingService;

/**
 * Implementation of the {@link ManufacturerStagingService} interface.
 */
@Service
public class ManufacturerStagingServiceImpl extends BaseStagingServiceImpl<ManufacturerStage, ManufacturerStageDao> implements ManufacturerStagingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManufacturerStagingServiceImpl.class);
    
    @Autowired
    public ManufacturerStagingServiceImpl(ManufacturerStageDao dao) {
        super(dao, ManufacturerStage.class);
    }

    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
