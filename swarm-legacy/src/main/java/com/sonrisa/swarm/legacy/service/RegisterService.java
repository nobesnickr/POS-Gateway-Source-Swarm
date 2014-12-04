package com.sonrisa.swarm.legacy.service;

import com.sonrisa.swarm.model.legacy.RegisterEntity;
import com.sonrisa.swarm.model.staging.RegisterStage;


public interface RegisterService extends BaseLegacyService<RegisterStage, RegisterEntity> {
	void saveEntityFromStaging(RegisterEntity register);
}
