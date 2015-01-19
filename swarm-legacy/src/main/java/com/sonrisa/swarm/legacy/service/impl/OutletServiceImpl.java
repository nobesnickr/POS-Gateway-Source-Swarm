package com.sonrisa.swarm.legacy.service.impl;

import hu.sonrisa.backend.service.GenericServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.legacy.dao.OutletDao;
import com.sonrisa.swarm.legacy.service.OutletService;
import com.sonrisa.swarm.model.legacy.OutletEntity;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class OutletServiceImpl extends GenericServiceImpl<Long, OutletEntity, OutletDao> implements OutletService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutletServiceImpl.class);
	
	/**
     * DAO of outlet in the data warehouse (aka legacy DB).
     */
    private OutletDao dao;
	
    @Autowired
	public OutletServiceImpl(OutletDao dao) {
		super(dao);
		this.dao = dao;
	}

	@Override
	public void flush() {
		dao.flush();
	}

	@Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void saveEntityFromStaging(OutletEntity outlet) {
        if(outlet == null){
            throw new IllegalArgumentException("outlet shouldn't be null");
        }

        if (outlet != null) {
            if (outlet.getId() != null) {
            	LOGGER.debug("Doing merge for outlet with ID: "+ outlet.getId());
                dao.merge(outlet);
            } else {
            	LOGGER.debug("Doing persist for outlet with ID: "+ outlet.getId());
                dao.persist(outlet);  
            }        
        }
	}

}
