package com.sonrisa.swarm.staging.dao;

import org.springframework.stereotype.Repository;

import com.sonrisa.swarm.model.staging.RegisterStage;

@Repository(value = "RegisterStageDao")
public class RegisterStageDao extends StageDaoBaseImpl<RegisterStage>{

	public RegisterStageDao() {
		super(RegisterStage.class);
	}

	@Override
	public String getTableName() {
		return "staging_registers";
	}

}
