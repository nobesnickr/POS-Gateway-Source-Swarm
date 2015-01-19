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
    
	@StageInsertableAttr(dbColumnName = "ls_register_id")
	public Long getRegisterId() {
		return null;
	}

	@StageInsertableAttr(dbColumnName = "ls_outlet_id")
	public Long getOutletId() {
		return null;
	}

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
