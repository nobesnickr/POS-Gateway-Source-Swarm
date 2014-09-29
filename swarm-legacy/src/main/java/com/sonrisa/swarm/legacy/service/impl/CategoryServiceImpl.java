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

import com.sonrisa.swarm.legacy.dao.CategoryDao;
import com.sonrisa.swarm.legacy.service.CategoryService;
import com.sonrisa.swarm.model.legacy.CategoryEntity;

/**
 * Implementation of the {@link CategoryService} interface.
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CategoryServiceImpl extends
        GenericServiceImpl<Long, CategoryEntity, CategoryDao> implements
        CategoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);

    /**
     * DAO of categories in the data warehouse (aka legacy DB).
     */
    private CategoryDao categoryDao;

    /**
     * Constructor.
     * 
     * @param dao
     */
    @Autowired
    public CategoryServiceImpl(CategoryDao dao) {
        super(dao);
        this.categoryDao = dao;
    }

    // ------------------------------------------------------------------------
    // ~ Public methods
    // ------------------------------------------------------------------------

    /**
     * {@inheritDoc }
     */
    @Override
    public void flush() {
        categoryDao.flush();
    }
    
    

    /**
     * {@inheritDoc }
     * 
     * @param invoice
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Long save(CategoryEntity category) {
        Long id = null;

        if (category != null) {
            if (category.getId() != null) {
                categoryDao.merge(category);
            } else {
                categoryDao.persist(category);
                categoryDao.flush();
            }
            id = category.getId();
        }
        return id;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveEntityFromStaging(final CategoryEntity categoryFromStaging) {

        if (categoryFromStaging == null){
            LOGGER.warn("Null category parameter, can not be saved.");
            return;
        }
        
        if (categoryFromStaging.getStore() == null){
            LOGGER.warn("Category without store can not be saved, product: {}", categoryFromStaging);
            return;
        }
        
        final Long storeId = categoryFromStaging.getStore().getId();
        final Long foreignId = categoryFromStaging.getLsCategoryId();        
        final CategoryEntity categoryInLegacyDB = categoryDao.findByStoreAndForeignId(storeId, foreignId);

        if(categoryInLegacyDB == null){
            // brand new category
            if (LOGGER.isDebugEnabled()){
                   LOGGER.debug("Category can not be found in the Data warehouse so a new one will be created. StoreId: {}, foreignId: {}",
                    storeId, foreignId);
            }
            categoryDao.persist(categoryFromStaging);
        }else{
            // existing category, has to be updated
            if (LOGGER.isDebugEnabled()){    
                   LOGGER.debug("Category has been found in the Data warehouse so it will be updated. StoreId: {}, foreignId: {}",
                    storeId, foreignId);
            }
            // we'd like to update the existing category, so we need to use the same id
            // but the ID of entityFromStaging is always null
            categoryFromStaging.setId(categoryInLegacyDB.getId());
            categoryDao.merge(categoryFromStaging);
        }    
    }
}
