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

import com.sonrisa.swarm.legacy.dao.ProductDao;
import com.sonrisa.swarm.legacy.service.ProductService;
import com.sonrisa.swarm.model.legacy.ProductEntity;

/**
 * 
 * Implementation of the {@link ProductService} interface.
 *
 * @author joe
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ProductServiceImpl extends GenericServiceImpl<Long, ProductEntity, ProductDao> implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);
    
    /**
     * DAO of products in the data warehouse (aka legacy DB).
     */
    private ProductDao dao;
    
    

    /**
     * Constructor.
     *
     * @param dao
     */
    @Autowired
    public ProductServiceImpl(ProductDao dao) {
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
     * @param productFromStaging
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void saveEntityFromStaging(final ProductEntity productFromStaging) {
        
        if (productFromStaging == null){
            LOGGER.warn("Null product parameter, can not be saved.");
            return;
        }
        
        if (productFromStaging.getStore() == null){
            LOGGER.warn("Product without store can not be saved, product: {}", productFromStaging);
            return;
        }
        
        final Long storeId = productFromStaging.getStore().getId();
        final Long foreignId = productFromStaging.getLsProductId();        
        final ProductEntity prodInDb = dao.findByStoreAndForeignId(storeId, foreignId);

        if(prodInDb == null){
            // brand new product
            if (LOGGER.isDebugEnabled()){
                   LOGGER.debug("Product can not be found in the Data warehouse so a new one will be created. StoreId: {}, foreignId: {}",
                    storeId, foreignId);
            }
            dao.persist(productFromStaging);
        }else{
            // existing product, has to be updated
            if (LOGGER.isDebugEnabled()){    
                   LOGGER.debug("Product has been found in the Data warehouse so it will be updated. StoreId: {}, foreignId: {}",
                    storeId, foreignId);
            }
            // we'd like to update the existing product, so we need to use the same id
            // but the ID of entityFromStaging is always null
            productFromStaging.setId(prodInDb.getId());
            dao.merge(productFromStaging);
        }               
    }

     /**
     * {@inheritDoc }
     *
     * @param product
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Long save(ProductEntity product) {
        Long id = null;

        if (product != null) {
            if (product.getId() != null) {
                dao.merge(product);
            } else {
                dao.persist(product);
                dao.flush();
            }
            id = product.getId();
        }
        return id;
    }
}
