package com.sonrisa.swarm.legacy.dao;

import org.springframework.stereotype.Repository;

import hu.sonrisa.backend.dao.BaseJpaDao;
import hu.sonrisa.backend.dao.filter.FilterParameter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;

import com.sonrisa.swarm.model.legacy.RegisterEntity;

@Repository
public class RegisterDao extends BaseJpaDao<Long, RegisterEntity>{

	public RegisterDao() {
		super(RegisterEntity.class);
	}

    public RegisterEntity findByStoreAndForeignId(final Long storeId, final Long foreignId){
        SimpleFilter<RegisterEntity> filter = new SimpleFilter<RegisterEntity>(RegisterEntity.class, 
                new FilterParameter("store.id", storeId),
                new FilterParameter("registerId", foreignId));                              
        
        return findSingleEntity(filter);
    }
}
