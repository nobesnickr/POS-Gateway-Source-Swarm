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
package com.sonrisa.swarm.retailpro.model.enums;

import com.sonrisa.swarm.model.staging.BaseStageEntity;
import com.sonrisa.swarm.model.staging.CustomerStage;
import com.sonrisa.swarm.model.staging.InvoiceLineStage;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.model.staging.ProductStage;

/**
 * Json object type. We use this property in the controller level (in {@link GenericJsonMap} class).
 * 
 * @author Béla Szabó
 *
 */
public enum JsonType {
    
	
	Invoices(InvoiceStage.class),
	
	Items(InvoiceLineStage.class),
	
	Customers(CustomerStage.class),
	
	Products(ProductStage.class);
	
    /**
     * Swarm entity to which this json type would be mapped.
     */    
    private Class<? extends BaseStageEntity> swarmEntityType;

    private JsonType(Class<? extends BaseStageEntity> entity) {
        this.swarmEntityType = entity;
    }

    /**
     * Returns the swarm entity type to which this json type would be mapped.
     * 
     * @return 
     */
    public Class<? extends BaseStageEntity> getSwarmEntityType() {
        return swarmEntityType;
    }
    
  

}
