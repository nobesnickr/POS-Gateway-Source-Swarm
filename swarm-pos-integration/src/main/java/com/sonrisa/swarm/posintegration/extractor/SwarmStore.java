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
package com.sonrisa.swarm.posintegration.extractor;

import com.sonrisa.swarm.model.legacy.StoreEntity;

/**
 * A SwarmStore identifies one of the partner's of Swarm, an entity that has
 * customers, invoices, products, etc.
 */
public interface SwarmStore {

    /**
     * Returns the store id of the {@link StoreEntity} linked to this store.
     * 
     * @return
     */
	Long getStoreId();
	
	/**
	 * Token which can determine that two accounts are the same. 
	 *  
	 * @return
	 */
	String getAccountId();
}
