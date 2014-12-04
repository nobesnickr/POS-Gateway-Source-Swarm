package com.sonrisa.swarm.legacy.service;

import com.sonrisa.swarm.model.legacy.OutletEntity;
import com.sonrisa.swarm.model.staging.OutletStage;

public interface OutletService extends BaseLegacyService<OutletStage, OutletEntity> {
	void saveEntityFromStaging(OutletEntity outlet);  
}
