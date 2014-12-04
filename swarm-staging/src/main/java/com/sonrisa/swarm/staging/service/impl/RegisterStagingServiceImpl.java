package com.sonrisa.swarm.staging.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.model.staging.RegisterStage;
import com.sonrisa.swarm.staging.dao.RegisterStageDao;
import com.sonrisa.swarm.staging.service.RegisterStagingService;

@Service
public class RegisterStagingServiceImpl extends BaseStagingServiceImpl<RegisterStage, RegisterStageDao> implements RegisterStagingService {
	 private static final Logger LOGGER = LoggerFactory.getLogger(ProductStagingServiceImpl.class);
     
	    @Autowired
	    public RegisterStagingServiceImpl(RegisterStageDao dao) {
	        super(dao, RegisterStage.class);
	    }

	    @Override
	    protected Logger logger() {
	        return LOGGER;
	    }
}
