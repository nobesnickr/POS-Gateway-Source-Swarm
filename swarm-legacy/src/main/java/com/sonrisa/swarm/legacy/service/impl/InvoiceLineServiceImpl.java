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

import com.sonrisa.swarm.legacy.dao.InvoiceLineDao;
import com.sonrisa.swarm.legacy.service.InvoiceLineService;
import com.sonrisa.swarm.model.legacy.InvoiceLineEntity;

/**
 * 
 * Implementation of the {@link InvoiceLineService} interface.
 *
 * @author joe
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InvoiceLineServiceImpl extends GenericServiceImpl<Long, InvoiceLineEntity, InvoiceLineDao> implements InvoiceLineService {
    
    /** DAO of invoices in the data warehouse (aka legacy DB). */
    private InvoiceLineDao dao;
    

    /**
     * Constructor.
     *
     * @param dao
     */
    @Autowired
    public InvoiceLineServiceImpl(InvoiceLineDao dao) {
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
     * @param line
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void saveEntityFromStaging(InvoiceLineEntity line) {
        if (line != null) {
            if (line.getId() != null) {
                dao.merge(line);
            } else {
                dao.persist(line);
            }
        }
    }
}
