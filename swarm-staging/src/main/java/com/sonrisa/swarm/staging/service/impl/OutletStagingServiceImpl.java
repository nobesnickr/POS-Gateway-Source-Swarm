package com.sonrisa.swarm.staging.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.model.staging.OutletStage;
import com.sonrisa.swarm.staging.dao.OutletStageDao;
import com.sonrisa.swarm.staging.service.OutletStagingService;

@Service
public class OutletStagingServiceImpl extends BaseStagingServiceImpl<OutletStage, OutletStageDao> implements OutletStagingService{
    private static final Logger LOGGER = LoggerFactory.getLogger(OutletStagingServiceImpl.class);
    
    @Autowired
    public OutletStagingServiceImpl(OutletStageDao dao) {
        super(dao, OutletStage.class);
    }

    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
