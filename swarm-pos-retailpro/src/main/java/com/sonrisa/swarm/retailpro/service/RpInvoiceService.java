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

package com.sonrisa.swarm.retailpro.service;

import java.util.Map;

import com.sonrisa.swarm.retailpro.util.mapper.EntityHolder;

/**
 * The implementor of this class should concentrate on invoice based business logic.
 */
public interface RpInvoiceService {

    /**
     * Process uploaded map which contains invoices, invoice lines, etc. as {@link EntityHolder}
     */
    EntityHolder processMap(String swarmId, Map<String, Object> jsonMap);

    /**
     * Inserts invoices, invoice lines, products and customers to DB scontained by {@code entities}
     * 
     * @param entities contains the entities to save
     */
    void writeToStage(EntityHolder entities);
}
