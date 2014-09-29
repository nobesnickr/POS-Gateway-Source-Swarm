package com.sonrisa.swarm.staging.job.exception;

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.staging.job.loader.StagingEntityWriter;

/**
 * Exception thrown when {@link StagingEntityWriter#write(java.util.List)} is
 * called with a non-distinct items  
 * 
 * @author Barnabas
 */
@SuppressWarnings("serial")
public class NonDistinctBatchItemsException extends Exception {

	/**
	 * Initialize exception by providing details for the error message
	 * @param store Store for the legacy entity
	 * @param legacyId Id in the legacy system
	 */
    public NonDistinctBatchItemsException(StoreEntity store, Long legacyId) {
        super("Entity already in the batch: " + legacyId + " for store" + store);
    }
}
