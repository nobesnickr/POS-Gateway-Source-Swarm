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
import com.sonrisa.swarm.posintegration.extractor.annotation.DWFilteredAs;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;

/**
 * Invoices consist of invoice line, each invoice line is one type product
 * that was purchased by the same customer in the given invoice
 */
@StageInsertableType(dbTableName = "invoice_lines")
@DWFilteredAs(InvoiceDTO.class)
public abstract class InvoiceLineDTO implements DWTransferable {
		
    /** The value that identifies the product within the Invoice, should be unique throughout the store */ 
    @StageInsertableAttr(dbColumnName = "ls_line_id", usedAsTimestamp=true)
	public abstract long getRemoteId();

    /** The invoice this line is present in */
    @StageInsertableAttr(dbColumnName = "ls_invoice_id")
	public abstract Long getInvoiceId();

    /** The product in this line of the invoice*/
    @StageInsertableAttr(dbColumnName = "ls_product_id")
	public abstract Long getProductId();

    /** The quantity purchased from this particular product */
    @StageInsertableAttr(dbColumnName = "quantity")
	public abstract int getQuantity();

    @StageInsertableAttr(dbColumnName = "price")
	public abstract double getPrice();

    @StageInsertableAttr(dbColumnName = "tax")
	public abstract double getTax();
    
    @StageInsertableAttr(dbColumnName = "description")
	public String getName(){
    	return null;
    }
    
    @StageInsertableAttr(dbColumnName = "ts")
	public String getTimestamp(){
    	return null;
    }
    
	public abstract Timestamp getLastModified();

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "InvoiceLineDTO [getLineId()=" + getRemoteId()
                + ", getInvoiceId()=" + getInvoiceId() + ", getProductId()="
                + getProductId() + ", getQuantity()=" + getQuantity()
                + ", getPrice()=" + getPrice() + ", getTax()=" + getTax()
                + ", getLastModified()=" + getLastModified() + "]";
    }
}
