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

import com.sonrisa.swarm.staging.dao.CustomerStageDao;
import com.sonrisa.swarm.model.staging.CustomerStage;
import com.sonrisa.swarm.staging.service.CustomerStagingService;

/**
 * Implementation of the {@link CustomerStagingService} interface.
 *
 * @author joe
 */
@Service
public class CustomerStagingServiceImpl extends BaseStagingServiceImpl<CustomerStage, CustomerStageDao> implements CustomerStagingService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerStagingServiceImpl.class);
         
    @Autowired
    public CustomerStagingServiceImpl(CustomerStageDao dao) {
        super(dao, CustomerStage.class);
    }

    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
