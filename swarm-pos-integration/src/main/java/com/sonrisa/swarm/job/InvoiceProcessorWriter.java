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
package com.sonrisa.swarm.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sonrisa.swarm.model.legacy.InvoiceEntity;

/**
 * Writer for processing unfinished {@link InvoiceEntity}.
 * 
 * As updating invoices is done via the staging tables, this
 * implementation only logs the fact that processing was finished.
 */
public class InvoiceProcessorWriter extends BaseMonitoringWriter<InvoiceEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceProcessorWriter.class);
    
    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
