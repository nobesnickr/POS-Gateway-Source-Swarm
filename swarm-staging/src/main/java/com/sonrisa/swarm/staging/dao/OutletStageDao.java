package com.sonrisa.swarm.staging.dao;

import org.springframework.stereotype.Repository;

import com.sonrisa.swarm.model.staging.OutletStage;

@Repository(value = "OutletStageDao")
public class OutletStageDao extends StageDaoBaseImpl<OutletStage>{

    public OutletStageDao() {
        super(OutletStage.class);
    }
    
    /* (non-Javadoc)
     * @see com.sonrisa.swarm.dao.impl.BaseSwarmDaoImpl#getTableName()
     */
    @Override
    public String getTableName() {
        return "staging_outlets";
    }
    
}
