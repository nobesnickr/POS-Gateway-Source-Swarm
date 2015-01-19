package com.sonrisa.swarm.legacy.service;

import com.sonrisa.swarm.model.legacy.RegisterEntity;
import com.sonrisa.swarm.model.staging.RegisterStage;

/**
 * Describes the operations with registers in the data warehouse (aka legacy DB).
 */
public interface RegisterService extends BaseLegacyService<RegisterStage, RegisterEntity> {
	
	/**
     * Creates or updates a register in the data warehouse.
     */ 
	void saveEntityFromStaging(RegisterEntity register);
	
	/**
	 * Retrieves a register based on store id and foreign id 
	 */
	RegisterEntity getResgisterFromStoreAndId(final Long storeId, final Long foreignId);
}
