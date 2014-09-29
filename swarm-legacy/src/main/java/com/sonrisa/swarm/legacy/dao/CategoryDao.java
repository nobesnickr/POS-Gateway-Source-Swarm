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
package com.sonrisa.swarm.legacy.dao;

import hu.sonrisa.backend.dao.BaseJpaDao;
import hu.sonrisa.backend.dao.filter.FilterParameter;
import hu.sonrisa.backend.dao.filter.JpaFilter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;

import org.springframework.stereotype.Repository;

import com.sonrisa.swarm.model.legacy.CategoryEntity;

/**
 * DAO class of {@link CategoryEntity}. 
 */
@Repository 
public class CategoryDao  extends BaseJpaDao<Long, CategoryEntity> {

    public CategoryDao(){
        super(CategoryEntity.class);
    }
    
    
    /**
     * Retrieves a category by its store and foreign ID.
     * 
     * @param store
     * @param foreignCustomerId
     * @return 
     */
    public CategoryEntity findByStoreAndForeignId(final Long storeId, final Long foreignId){
        JpaFilter<CategoryEntity> filter = new SimpleFilter<CategoryEntity>(CategoryEntity.class, 
                new FilterParameter("store.id", storeId),
                new FilterParameter("lsCategoryId", foreignId));                              
        
        return findSingleEntity(filter);
    }
}
