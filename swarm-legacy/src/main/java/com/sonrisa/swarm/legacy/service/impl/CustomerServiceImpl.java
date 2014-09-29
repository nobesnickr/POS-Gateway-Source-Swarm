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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.legacy.dao.CustomerDao;
import com.sonrisa.swarm.legacy.service.CustomerService;
import com.sonrisa.swarm.legacy.util.IdConverter;
import com.sonrisa.swarm.model.legacy.CustomerEntity;

/**
 * 
 * Implementation of the {@link CustomerService} interface.
 *
 * @author joe
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CustomerServiceImpl extends GenericServiceImpl<Long, CustomerEntity, CustomerDao> implements CustomerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);
    
    /** DAO of customers in the data warehouse (aka legacy DB). */
    private CustomerDao dao;
    
    
    /**
     * Constructor.
     * 
     * @param dao 
     */
    @Autowired
    public CustomerServiceImpl(CustomerDao dao) {
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
     * @param cust
     * @return 
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void saveEntityFromStaging(CustomerEntity customerFromStaging) {

        if (customerFromStaging == null){
            LOGGER.warn("Null customer parameter, can not be saved.");
            return;
        }
        
        if (customerFromStaging.getStore() == null){
            LOGGER.warn("Customer without store can not be saved, customer: {}", customerFromStaging);
            return;
        }
        
        final Long storeId = customerFromStaging.getStore().getId();
        final Long foreignId = customerFromStaging.getLsCustomerId();        
        final CustomerEntity customerInDb = dao.findByStoreAndForeignId(storeId, IdConverter.positiveCustomerId(foreignId));

        if(customerInDb == null){
            // brand new product
            if (LOGGER.isDebugEnabled()){
                   LOGGER.debug("Customer can not be found in the Data warehouse so a new one will be created. StoreId: {}, foreignId: {}",
                    storeId, foreignId);
            }
            dao.persist(customerFromStaging);
        }else{
            // existing product, has to be updated
            if (LOGGER.isDebugEnabled()){    
                   LOGGER.debug("Customer has been found in the Data warehouse so it will be updated. StoreId: {}, foreignId: {}",
                    storeId, foreignId);
            }
            // we'd like to update the existing product, so we need to use the same id
            // but the ID of entityFromStaging is always null
            customerFromStaging.setId(customerInDb.getId());
            dao.merge(customerFromStaging);
        }     
    }
    
    /**
     * {@inheritDoc }
     * 
     * @param cust
     * @return 
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void save(CustomerEntity cust) {
        if (cust != null) {
            if (cust.getId() != null) {
                dao.merge(cust);
            } else {
                dao.persist(cust);
            }           
        }
    }
}
