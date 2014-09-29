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

import com.sonrisa.swarm.legacy.dao.ManufacturerDao;
import com.sonrisa.swarm.legacy.service.ManufacturerService;
import com.sonrisa.swarm.model.legacy.ManufacturerEntity;

/**
 * 
 * Implementation of the {@link ManufacturerService} interface.
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ManufacturerServiceImpl extends GenericServiceImpl<Long, ManufacturerEntity, ManufacturerDao> implements ManufacturerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);

    /**
     * DAO of manufacturers in the data warehouse (aka legacy DB).
     */
    private ManufacturerDao dao;
    
    /**
     * Constructor.
     * 
     * @param dao
     */
    @Autowired
    public ManufacturerServiceImpl(ManufacturerDao dao) {
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
    public Long save(ManufacturerEntity manufacturer) {
        Long id = null;

        if (manufacturer != null) {
            if (manufacturer.getId() != null) {
                dao.merge(manufacturer);
            } else {
                dao.persist(manufacturer);
                dao.flush();
            }
            id = manufacturer.getId();
        }
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveEntityFromStaging(final ManufacturerEntity manufacturerFromStaging) {

        if (manufacturerFromStaging == null){
            LOGGER.warn("Null manufacturer parameter, can not be saved.");
            return;
        }
        
        if (manufacturerFromStaging.getStore() == null){
            LOGGER.warn("Manufacturer without store can not be saved, product: {}", manufacturerFromStaging);
            return;
        }
        
        final Long storeId = manufacturerFromStaging.getStore().getId();
        final Long foreignId = manufacturerFromStaging.getManufacturerId();        
        final ManufacturerEntity manufacturerInLegacyDB = dao.findByStoreAndForeignId(storeId, foreignId);

        if(manufacturerInLegacyDB == null){
            // brand new manufacturer
            if (LOGGER.isDebugEnabled()){
                   LOGGER.debug("Manufacturer can not be found in the Data warehouse so a new one will be created. StoreId: {}, foreignId: {}",
                    storeId, foreignId);
            }
            dao.persist(manufacturerFromStaging);
        }else{
            // existing manufacturer, has to be updated
            if (LOGGER.isDebugEnabled()){    
                   LOGGER.debug("Manufacturer has been found in the Data warehouse so it will be updated. StoreId: {}, foreignId: {}",
                    storeId, foreignId);
            }
            // we'd like to update the existing manufacturer, so we need to use the same id
            // but the ID of entityFromStaging is always null
            manufacturerFromStaging.setId(manufacturerInLegacyDB.getId());
            dao.merge(manufacturerFromStaging);
        }    
    }
}
