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
package com.sonrisa.swarm.legacy.service.impl;

import hu.sonrisa.backend.service.GenericServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.legacy.dao.InvoiceDao;
import com.sonrisa.swarm.legacy.service.InvoiceService;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;

/**
 * 
 * Implementation of the {@link InvoiceService} interface.
 *
 * @author joe
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InvoiceServiceImpl extends GenericServiceImpl<Long, InvoiceEntity, InvoiceDao> implements InvoiceService {
    
    /**
     * DAO of invoices in the data warehouse (aka legacy DB).
     */
    private InvoiceDao dao;
       
    
    /**
     * Constructor.
     *
     * @param dao
     */
    @Autowired
    public InvoiceServiceImpl(InvoiceDao dao) {
        super(dao);
        this.dao = dao;
    }

    // ------------------------------------------------------------------------
    // ~ Public methods
    // ------------------------------------------------------------------------

    /**
     * {@inheritDoc }
     */
    @Override
    public void flush() {
        dao.flush();
    }
    
            
    /**
     * {@inheritDoc }
     *
     * @param invoice
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void saveEntityFromStaging(InvoiceEntity invoice) {
        if(invoice == null){
            throw new IllegalArgumentException("invoice shouldn't be null");
        }

        if (invoice != null) {
            if (invoice.getId() != null) {
                dao.merge(invoice);
            } else {
                dao.persist(invoice);  
            }        
        }
    }

    
    
}
