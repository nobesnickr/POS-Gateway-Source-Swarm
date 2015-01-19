package com.sonrisa.swarm.legacy.dao;

import hu.sonrisa.backend.dao.BaseJpaDao;
import hu.sonrisa.backend.dao.filter.FilterParameter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;
import org.springframework.stereotype.Repository;
import com.sonrisa.swarm.model.legacy.OutletEntity;

/**
 * DAO class of outlets.  
 */
@Repository
public class OutletDao extends BaseJpaDao<Long, OutletEntity>{

	public OutletDao() {
		super(OutletEntity.class);
	}
	
    /**
     * Retrieves an outlet by its store and foreign ID.
     */
    public OutletEntity findByStoreAndForeignId(final Long storeId, final Long foreignId){
        SimpleFilter<OutletEntity> filter = new SimpleFilter<OutletEntity>(OutletEntity.class, 
                new FilterParameter("store.id", storeId),
                new FilterParameter("lsOutletId", foreignId));                              
        
        return findSingleEntity(filter);
    }

}
