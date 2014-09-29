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

import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;

/**
 * An external extractor is class that can extract data from foreign location
 * into the local Swarm database. Such extractor could be an extractor for
 * MerchantOS or Erply.
 */
public interface ExternalExtractor<T extends SwarmStore> {

    /**
     * Job to be triggered from a batch process, should be repeatable
     * 
     * @param account 
     *            Account used for authentication and identification in the stores table
     * @param dataStore
     *            The datastore for whom the extractor sends to extracted data to
     * @throws ExternalExtractorException
     *             If execution fails this exception carries the cause of failure
     */
    void fetchData(T account, SwarmDataWarehouse dataStore) throws ExternalExtractorException;

}
