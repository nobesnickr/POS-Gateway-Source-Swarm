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
package com.sonrisa.swarm.posintegration.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.model.staging.annotation.StageInsertableAttr;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableType;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;

/**
 * The manufacturer is a property of a product. Manufacturers are usually only dummy objects,
 * so their data is not stored separately but with the product data, and thus without proper database normalization.
 */
@StageInsertableType(dbTableName="manufacturers")
public abstract class ManufacturerDTO implements DWTransferable {

    /** Identifier of the manufacturer in the remote system */
    @StageInsertableAttr(dbColumnName = "ls_manufacturer_id")
    public abstract long getRemoteId();
    
    /** Name of the manufacturer, e.g. "Sonrisa" */
    @StageInsertableAttr(dbColumnName = "name")
	public abstract String getManufacturerName();

	/** Timestamp of the manufacturer entry in the remote system */
    @StageInsertableAttr(dbColumnName = "last_modified", usedAsTimestamp = true)
	public Timestamp getLastModified(){
	    return new Timestamp(0);
	}

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ManufacturerDTO [getManufacturerId()=" + getRemoteId()
                + ", getManufacturerName()=" + getManufacturerName()
                + ", getLastModified()=" + getLastModified() + "]";
    }
	
	
}
