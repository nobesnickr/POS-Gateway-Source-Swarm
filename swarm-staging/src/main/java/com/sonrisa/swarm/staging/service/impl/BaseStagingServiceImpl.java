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

import hu.sonrisa.backend.dao.filter.FilterParameter;
import hu.sonrisa.backend.dao.filter.QueryFilter;
import hu.sonrisa.backend.entity.SonrisaJPAEntity;
import hu.sonrisa.backend.service.GenericServiceImpl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.legacy.service.surrogate.SurrogateStoreService;
import com.sonrisa.swarm.model.StageBatchInsertable;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.BaseStageEntity;
import com.sonrisa.swarm.staging.dao.StageDaoBaseImpl;
import com.sonrisa.swarm.staging.service.BaseStagingService;

/**
 * Basic CRUD operations and finder methods for the Swarm staging entities.
 *
 * @author joe
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public abstract class BaseStagingServiceImpl<T extends SonrisaJPAEntity<Long> & StageBatchInsertable, 
        X extends StageDaoBaseImpl<T>> extends GenericServiceImpl<Long, T, X> implements BaseStagingService<T>{
    
    /** Maximum number of elements when an 'IN' clause is used in a query filter. */
    public final static int MAX_LENGTH_OF_IN_CLAUSE = 900;
    
    /** Prefix of the query that retrieves objects by their ids. */
    private final static String QUERY_FIND_BY_IDS_PREFIX = "select distinct object(t) from " ;
    /** Postfix of the query that retrieves objects by their ids. */
    private final static String QUERY_FIND_BY_IDS_POSTFIX = " t where t.id in :list order by t.id asc";

    /** DAO to access the entities. */
    protected X dao;
    /** Class of the staging entities. */
    protected Class<T> entityClass;
    
    /** DAO to access the store entities in the data warehouse (aka legavy DB) */
    @Autowired
    protected StoreDao storeDao;
    
    /** Service to access stores by SwarmId/Sbs/StoreNo */
    @Autowired
    protected SurrogateStoreService surrogateStoreService;
    
    /**
     * Constructor.
     * 
     * @param dao
     * @param entityClass 
     */
    public BaseStagingServiceImpl(X dao, Class<T> entityClass) {
        super(dao);
        this.dao = dao;
        this.entityClass = entityClass;
    }        

    /**
     * {@inheritDoc }
     * 
     * @return 
     */
    @Override
    public List<Long> findAllIds() {
        return dao.findAllIds();
    }

    /**
     * {@inheritDoc }
     * 
     * @param ids
     * @return 
     */
    @Override
    public List<T> findByIds(List<Long> ids) {
        if (ids != null && BaseStagingServiceImpl.MAX_LENGTH_OF_IN_CLAUSE < ids.size()){
            throw new IllegalArgumentException("Too much element in the list of 'IN' where clause.");
        }
        
        StringBuilder query = new StringBuilder();
        query.append(QUERY_FIND_BY_IDS_PREFIX);
        query.append(entityClass.getSimpleName());
        query.append(QUERY_FIND_BY_IDS_POSTFIX);
        
        QueryFilter<T> filter = new QueryFilter<T>(query.toString(), new FilterParameter("list", ids));
        
        return dao.find(filter);
    }

    /**
     * {@inheritDoc }
     * 
     * @param entity
     * @return 
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Long save(T entity) {
       if (entity.getId() != null){
            logger().debug("This "+entityClass.getSimpleName()+" is already exist so it will be updated: " + entity);
            dao.merge(entity);
        }else{
            logger().debug("This "+entityClass.getSimpleName()+" does not exist yet so it will be created: " + entity);
            dao.persist(entity);
            dao.flush();
        }
                
        return entity.getId();
    }
    
    /**
     * {@inheritDoc}
     */
    public void create(List<? extends StageBatchInsertable> entities){
        this.create(entities, null);
    }
    
    /**
     * {@inheritDoc }
     * 
     * @param entities 
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void create(List<? extends StageBatchInsertable> entities, Long localStoreId){
    	dao.create(entities, localStoreId);
    }

    /**
     * {@inheritDoc }
     * 
     * @param ids 
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public int delete(Collection<Long> ids) {
        int deleted = 0;
        if (ids != null){
            
            for (Iterator<Long> it = ids.iterator(); it.hasNext();) {
                Long entityId = it.next();
                boolean success = delete(entityId);
                if (success){
                    deleted++;
                }
            }
        }
        
        return deleted;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public StoreEntity findStore(BaseStageEntity stgEntity) {
        StoreEntity store = null;

        if (stgEntity != null) {
            Long storeId;
            
            // first it tries to find the store by the storeId if it presents on the entity
            // normally the staging entity has a storeId except if it has been received from the RetailPro
            storeId = stgEntity.getStoreId();
            
            // if there is no storeId then it tries with the RetailPro store identifiers
            if (storeId == null){
                store = surrogateStoreService.findStoreForStagingEntity(stgEntity);
            } else {
                store = storeDao.findById(storeId);
            }

        } else {
            throw new IllegalArgumentException("Null staging entity parameter is not valid!");
        }
        return store;
    }
    
    /**
     * Retrieves the logger object of the subclass.
     * 
     * @return 
     */
    protected abstract Logger logger();

    /**
     * {@inheritDoc }
     * 
     * @param entityId
     * @return 
     */
    @Override
    public boolean delete(Long entityId) {
        boolean deleted = false;
        final T stgCust = dao.findById(entityId);
        if (stgCust != null){
            dao.remove(stgCust);   
            deleted = true;
            logger().debug(entityClass.getSimpleName() + " has been deleted. Id: " + entityId);
        } else{
            logger().debug(entityClass.getSimpleName() + " can not be deleted because there is no entity with this ID: " + entityId);
        }
        return deleted;
    }
}
