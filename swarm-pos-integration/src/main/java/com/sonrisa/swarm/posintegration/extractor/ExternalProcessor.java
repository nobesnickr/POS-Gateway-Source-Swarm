/*
 *   Copyright (c) 2014 Sonrisa Informatikai Kft. All Rights Reserved.
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

import com.sonrisa.swarm.model.legacy.BaseLegacyEntity;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;

/**
 * An external processor is a class that process an unfinished legacy entity
 * by extracting data from foreign location.
 * 
 * It's result is then written into a {@link SwarmDataWarehouse}.
 * 
 * Implementations are expected to be thread safe
 * 
 * @param <S> Account type for the processor, each class can process a single API, e.g. Kounta
 * @param <T> Legacy Entity, each class can process only a single entity type, e.g. {@link InvoiceEntity}
 */
public interface ExternalProcessor<S extends SwarmStore, T extends BaseLegacyEntity> {

    /**
     * Process a DTO by accessing an external service
     * 
     * @param account Account containing authentication information, e.g. an API key
     * @param item Item to be processed, e.g. {@link InvoiceDTO}
     * @param dataWarehouse Processed, and possible additional DTOs will be written here
     * @throws ExternalExtractorException
     */
    void processEntity(S account, T item, SwarmDataWarehouse dataWarehouse) throws ExternalExtractorException;
    
}
