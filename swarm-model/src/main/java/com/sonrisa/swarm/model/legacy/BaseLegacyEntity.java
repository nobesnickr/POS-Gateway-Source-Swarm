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

package com.sonrisa.swarm.model.legacy;

import com.sonrisa.swarm.model.BaseSwarmEntity;
import com.sonrisa.swarm.model.staging.BaseStageEntity;

/**
 * Common base class for legacy entities, entities which represent foreign entities
 * from an other POS system, and are stored in the Swarm data warehouse when converted
 * from {@link BaseStageEntity}.
 */
public abstract class BaseLegacyEntity extends BaseSwarmEntity {

	/**
	 * Store of the legacy entity
	 */
	private StoreEntity store;
	
	/**
	 * Id of the entity in the foreign system
	 */
	private Long legacySystemId;
		
	public StoreEntity getStore() {
		return store;
	}

	public void setStore(StoreEntity store) {
		this.store = store;
	}

	public Long getLegacySystemId() {
		return legacySystemId;
	}

	public void setLegacySystemId(Long legacySystemId) {
		this.legacySystemId = legacySystemId;
	}

	@Override
	public String toString() {
		return "BaseLegacyEntity [store=" + store + ", legacySystemId="
				+ legacySystemId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((legacySystemId == null) ? 0 : legacySystemId.hashCode());
		result = prime * result + ((store == null) ? 0 : store.hashCode());
		return result;
	}

	/**
	 * Auto generated equals for instance.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		// Only matching classes can be equal (e.g. InvoiceEntity can be equal with an other Invoice Entity only)
		if (getClass() != obj.getClass())
			return false;
		
		BaseLegacyEntity other = (BaseLegacyEntity) obj;
		if (legacySystemId == null) {
			if (other.legacySystemId != null)
				return false;
		} else if (!legacySystemId.equals(other.legacySystemId))
			return false;
		if (store == null) {
			if (other.store != null)
				return false;
		} else if (!store.equals(other.store))
			return false;
		return true;
	}
}
