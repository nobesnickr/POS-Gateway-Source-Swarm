package com.sonrisa.swarm.legacy.service.impl;

import hu.sonrisa.backend.service.GenericServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.legacy.dao.RegisterDao;
import com.sonrisa.swarm.legacy.service.RegisterService;
import com.sonrisa.swarm.model.legacy.RegisterEntity;


@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class RegisterServiceImpl  extends GenericServiceImpl<Long, RegisterEntity, RegisterDao> implements RegisterService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterServiceImpl.class);

	/**
     * DAO of register in the data warehouse (aka legacy DB).
     */
    private RegisterDao dao;
	
    @Autowired
	public RegisterServiceImpl(RegisterDao dao) {
		super(dao);
		this.dao = dao;
	}

	@Override
	public void flush() {
		dao.flush();
	}

	@Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void saveEntityFromStaging(RegisterEntity register) {
        if(register == null){
            throw new IllegalArgumentException("register shouldn't be null");
        }

        if (register != null) {
            if (register.getId() != null) {
            	LOGGER.debug("Doing merge for register with ID: "+ register.getId());
                dao.merge(register);
            } else {
            	LOGGER.debug("Doing persist for register with ID: "+ register.getId());
                dao.persist(register);  
            }        
        }
	}
}
