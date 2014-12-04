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
 * Products are sold at the point of sale, invoices contain multiple products. Products carry information
 * about their manufacturer, price, their product codes in various product classification systems, etc.
 */
@StageInsertableType(dbTableName = "products")
public abstract class ProductDTO implements DWTransferable {
    
    /** The product id in the remote system */
    @StageInsertableAttr(dbColumnName = "ls_product_id", usedAsRemoteId = true)
	public abstract long getRemoteId();

    /** Category of the product */
    @StageInsertableAttr(dbColumnName = "ls_category_id")
	public abstract Long getCategoryId();

    @StageInsertableAttr(dbColumnName = "ls_manufacturer_id")
    public abstract Long getManufacturerId();

    @StageInsertableAttr(dbColumnName = "description", maxLength = 250)
	public abstract String getDescription();

    @StageInsertableAttr(dbColumnName = "price")
	public abstract double getPrice();

    @StageInsertableAttr(dbColumnName = "last_modified", usedAsTimestamp = true)
	public abstract Timestamp getLastModified();

    @StageInsertableAttr(dbColumnName = "category", maxLength = 100)
	public abstract String getCategoryName();

    @StageInsertableAttr(dbColumnName = "manufacturer", maxLength = 50)
	public abstract String getManufacturerName();

    @StageInsertableAttr(dbColumnName = "upc", maxLength = 14)
	public abstract String getUpc();

    @StageInsertableAttr(dbColumnName = "ean", maxLength = 14)
	public abstract String getEan();

    @StageInsertableAttr(dbColumnName = "store_sku", maxLength = 50)
	public abstract String getStoreSku();
    
    // Default behavior needed to avoid changes in previous integrations
    @StageInsertableAttr(dbColumnName = "sku", maxLength = 50)
	public String getSku(){return null;}
}
