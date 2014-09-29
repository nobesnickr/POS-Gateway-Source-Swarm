/*
 *   Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of
 *  Sonrisa Informatikai Kft. ("Confidential Information").
 *  You shall not disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Sonrisa.
 * 
 *  SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 *  THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *  TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *  PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 *  ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 *  DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.sonrisa.swarm.posintegration.warehouse;

import java.util.List;

import com.sonrisa.swarm.posintegration.extractor.SwarmStore;

/**
 * A Data Warehouse is a consumer of {@link DWTransferable} DTO entities.
 * 
 * Most common implementation of the interface is one which writes
 * the DTO entities directly into the staging table.
 * 
 * @author sonrisa
 * 
 * @TODO refactor
 * WRONG-NAME: This isn't necessarily a Data warehouse, may be a
 * temporary FIFO acting only as consumer and producer of DTO entities.
 */
public interface SwarmDataWarehouse {
	
    /**
     * Save list of entities of the same type into a DataStore
     * @param dataStore Store identifying the source 
     * @param entities Entities to be save
     * @param clazz Class of the internal DTOs
     */
    public <T extends DWTransferable> void save(SwarmStore store, List<? extends T> entities, Class<T> clazz);
	
	/**
	 * Get the latest version for a specific DTO
	 * @param dtoClass The class of the DTO for which the version information is required
	 * @returns 0 if no such element exists in the datastore, the maximum value otherwise
	 * @note The save method doesn't affect this function this is the value at initialization
	 */
	public DWFilter getFilter(SwarmStore store, Class<? extends DWTransferable> dtoClass);
		
}
