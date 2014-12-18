package com.sonrisa.swarm.legacy.service;

import com.sonrisa.swarm.model.legacy.OutletEntity;
import com.sonrisa.swarm.model.staging.OutletStage;
/**
 * Describes the operations with outlets in the data warehouse (aka legacy DB).
 */
public interface OutletService extends BaseLegacyService<OutletStage, OutletEntity> {
	
	/**
     * Creates or updates an outlet in the data warehouse.
     */ 
	void saveEntityFromStaging(OutletEntity outlet);  
}
