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

import com.sonrisa.swarm.staging.dao.InvoiceLineStageDao;
import com.sonrisa.swarm.model.staging.InvoiceLineStage;
import com.sonrisa.swarm.staging.service.InvoiceLineStagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link InvoiceLineStagingService} interface.
 *
 * @author joe
 */
@Service
public class InvoiceLineStagingServiceImpl extends BaseStagingServiceImpl<InvoiceLineStage, InvoiceLineStageDao> implements InvoiceLineStagingService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceLineStagingServiceImpl.class);
         
    @Autowired
    public InvoiceLineStagingServiceImpl(InvoiceLineStageDao dao) {
        super(dao, InvoiceLineStage.class);
    }

    @Override
    protected Logger logger() {
        return LOGGER;
    }
    
}
