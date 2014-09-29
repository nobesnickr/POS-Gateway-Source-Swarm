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
 * An invoice is a sale document generated at the point of sale
 */
@StageInsertableType(dbTableName = "invoices")
public abstract class InvoiceDTO implements DWTransferable {
    /** Invoice id */
    @StageInsertableAttr(dbColumnName = "ls_invoice_id", usedAsRemoteId = true)
	public abstract long getRemoteId();

    /** Customer who made the purchase */
    @StageInsertableAttr(dbColumnName = "ls_customer_id")
	public abstract Long getCustomerId();

    /** The invoice number (???) */
    @StageInsertableAttr(dbColumnName = "invoice_no", maxLength = 50)
	public abstract String getInvoiceNumber();

    /** The amount of currency payed for purchasing the items of the invoice */
    @StageInsertableAttr(dbColumnName = "total")
	public abstract double getTotal();
    
    /**  If the sale is completed the inventory will have been removed and the payments will have to equal the total. */
    @StageInsertableAttr(dbColumnName = "completed")
    public Integer getCompleted(){
        /** If set to null the {@link InvoiceStagingConverter} will use the system wide business logic to fill in this field */
        return null;
    }
    
    @StageInsertableAttr(dbColumnName = "lines_processed")
    public Integer getLinesProcessed(){
        /** If set to null the {@link InvoiceStagingConverter} will use the system wide business logic to fill in this field */
        return null;
    }

    /** The time of the purchase */
    @StageInsertableAttr(dbColumnName = "last_modified", usedAsTimestamp = true)
	public Timestamp getLastModified(){
    	return getInvoiceTimestamp(); 
    }
    
    @StageInsertableAttr(dbColumnName = "ts")
	public abstract Timestamp getInvoiceTimestamp();

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "InvoiceDTO [getInvoiceId()=" + getRemoteId()
                + ", getCustomerId()=" + getCustomerId()
                + ", getInvoiceNumber()=" + getInvoiceNumber()
                + ", getTotal()=" + getTotal() + ", getLastModified()="
                + getLastModified() + "]";
    }
}
