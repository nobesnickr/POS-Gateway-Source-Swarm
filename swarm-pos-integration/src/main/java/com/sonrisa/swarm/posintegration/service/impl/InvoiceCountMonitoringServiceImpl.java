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

import hu.sonrisa.backend.dao.filter.SimpleFilter;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.legacy.dao.InvoiceDao;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.posintegration.service.InvoiceCountMonitoringService;

/**
 * Implementation of the {@link InvoiceCountMonitoringService}
 * 
 * @author Barnabas
 */
@Service
public class InvoiceCountMonitoringServiceImpl implements InvoiceCountMonitoringService {
    
    @Autowired
    private InvoiceDao invoiceDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastInvoiceDate(Long storeId) {
        SimpleFilter<InvoiceEntity> filter = new SimpleFilter<InvoiceEntity>(InvoiceEntity.class);
        filter.addParameter("store.id", storeId);
        filter.setSort("ts DESC");
        InvoiceEntity invoice = invoiceDao.findSingleEntity(filter);
        
        if(invoice != null){
            return invoice.getTs();
        } else {
            return new Date(0L);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getInvoiceCount(Long storeId) {
        SimpleFilter<InvoiceEntity> filter = new SimpleFilter<InvoiceEntity>(InvoiceEntity.class);
        filter.addParameter("store.id", storeId);
        return invoiceDao.count(filter);
    }

    public void setInvoiceDao(InvoiceDao invoiceDao) {
        this.invoiceDao = invoiceDao;
    }
}
