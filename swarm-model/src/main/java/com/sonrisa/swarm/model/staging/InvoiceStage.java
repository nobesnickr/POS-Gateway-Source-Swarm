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
 *  ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OFs USING, MODIFYING OR
 *  DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.sonrisa.swarm.model.staging;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sonrisa.swarm.model.StageBatchInsertable;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableAttr;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableType;
import com.sonrisa.swarm.model.staging.retailpro.RetailProAttr;
import com.sonrisa.swarm.model.staging.retailpro.converter.DateAndTimePropertyConverter;

/**
 * Staging invoice entity.
 *
 * @author joe
 */
@Entity
@Table(name = "staging_invoices")
@StageInsertableType(dbTableName = "invoices", storeIdColumnName = "store_id")
public class InvoiceStage extends BaseStageEntity implements StageBatchInsertable {

    private static final long serialVersionUID = -6247107871024226208L;
    
    private Long id;
       
    /**
     * Inner ID of the store where this staging entity comes from.
     * 
     * Inner means the ID identifies this store within the Swarm System.
     * This ID could be null if the entity does not know the inner ID of its store.
     * E.g.: Entities from RetailPro stores.
     */
    private Long storeId;          

    // ------------------------------------------------------------------------
    // ~ Attributes from RetailPro
    // ------------------------------------------------------------------------
    
    /**
     * Each Retail Pro store is identified by three values:
     * <ul>
     *  <li>SwarmId (picked for each swarm-partner)</li>
     *  <li>Store number (e.g. NAS)</li>
     *  <li>Subsidiary (e.g. 001), for Rp9 this is optional but clients sends it anyway</li>
     * </ul>
     * 
     * These values are only used for Retail Pro stores.
     */
    @RetailProAttr()
    private String swarmId;    
    
    @RetailProAttr(value = "StoreNo",  maxLength = 100)
    private String lsStoreNo;

    @RetailProAttr(value = "SbsNo", maxLength = 100)
    private String lsSbsNo;    

    @RetailProAttr(value = "InvoiceSid", maxLength = 100)
    private String lsInvoiceId;

    @RetailProAttr(value = "CustSid", maxLength = 100)
    private String lsCustomerId;

    @RetailProAttr(value = "InvoiceNo", maxLength = 50)
    private String invoiceNo;

    @RetailProAttr(value = "CreatedDate", maxLength = 20, converter = DateAndTimePropertyConverter.class, relatedFields="CreatedTime")
    private String ts;   

    @RetailProAttr(value = "Total", maxLength = 20, truncatingAllowed = false)
    private String total;
    
    @RetailProAttr(value = "SONumber", maxLength = 100)
    private String soNumber;
    
    @RetailProAttr(value = "ReceiptType", maxLength = 20)
    private String receiptType;
    
    @RetailProAttr(value = "ReceiptStatus", maxLength = 20)
    private String receiptStatus;
    
    @RetailProAttr(value = "Tender", maxLength = 20)
    private String tender;

    /**  
     *  If the sale is completed the inventory will have been removed and the payments will have to equal the total. 
     */
    private String completed;
    
    // ------------------------------------------------------------------------
    // ~ Attributes from Shopify
    // ------------------------------------------------------------------------
    
    private String lastModified;
    
    // ------------------------------------------------------------------------
    // ~ Attributes from Kounta
    // ------------------------------------------------------------------------
    
    /**
     * Not-processed invoices still need additional REST queries to get all fields
     * 
     * This value will be written into the <code>lines_processed</code> column, therefore
     * until this is <i>0</i>, it doesn't count in the revenue in the UI.
     */
    private String linesProcessed;
    
    // ------------------------------------------------------------------------
    // ~ Getters / setters
    // ------------------------------------------------------------------------    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }   

    @StageInsertableAttr(dbColumnName="ls_store_no")
    @Column(name = "ls_store_no")
    @Override
    public String getLsStoreNo() {
        return lsStoreNo;
    }

    public void setLsStoreNo(String lsStoreNo) {
        this.lsStoreNo = lsStoreNo;
    }

    @StageInsertableAttr(dbColumnName="ls_sbs_no")
    @Column(name = "ls_sbs_no")
    @Override
    public String getLsSbsNo() {
        return lsSbsNo;
    }

    public void setLsSbsNo(String lsSbsNo) {
        this.lsSbsNo = lsSbsNo;
    }

    @StageInsertableAttr(dbColumnName="ls_invoice_id")
    @Column(name = "ls_invoice_id")    
    public String getLsInvoiceId() {
        return lsInvoiceId;
    }

    public void setLsInvoiceId(String lsInvoiceId) {
        this.lsInvoiceId = lsInvoiceId;
    }

    @StageInsertableAttr(dbColumnName="ls_customer_id")
    @Column(name = "ls_customer_id")    
    public String getLsCustomerId() {
        return lsCustomerId;
    }

    public void setLsCustomerId(String lsCustomerId) {
        this.lsCustomerId = lsCustomerId;
    }

    @StageInsertableAttr(dbColumnName="invoice_no")
    @Column(name = "invoice_no")    
    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    @StageInsertableAttr(dbColumnName="ts")
    @Column(name = "ts")    
    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    @StageInsertableAttr(dbColumnName="total")
    @Column(name = "total")    
    public String getTotal() {
        return total;
    }
    
    @StageInsertableAttr(dbColumnName="so_number")
    @Column(name = "so_number")   
    public String getSoNumber() {
        return soNumber;
    }

    @StageInsertableAttr(dbColumnName="receipt_type")
    @Column(name = "receipt_type")    
    public String getReceiptType() {
        return receiptType;
    }
    
    @StageInsertableAttr(dbColumnName="receipt_status")
    @Column(name = "receipt_status") 
    public String getReceiptStatus() {
        return receiptStatus;
    }

    @StageInsertableAttr(dbColumnName="tender")
    @Column(name = "tender") 
    public String getTender() {
        return tender;
    }

    public void setSoNumber(String soNumber) {
        this.soNumber = soNumber;
    }
    
    public void setReceiptType(String receiptType) {
        this.receiptType = receiptType;
    }

    public void setReceiptStatus(String receiptStatus) {
        this.receiptStatus = receiptStatus;
    }

    public void setTender(String tender) {
        this.tender = tender;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @StageInsertableAttr(dbColumnName="swarm_id")
    @Column(name = "swarm_id")    
    @Override
    public String getSwarmId() {
        return swarmId;
    }

    @Override
    public void setSwarmId(String swarmId) {
        this.swarmId = swarmId;
    }    
    
    /**
     * Returns the inner ID of the store where this staging entity comes from.
     * 
     * Inner means the ID identifies this store within the Swarm System.
     * This ID could be null if the entity does not know the inner ID of its store.
     * E.g.: Entities from RetailPro stores.
     */
    @Column(name = "store_id")
    @Override
    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    /**
     * Legacy field for Merchant OS, mapped as is
     * 
     * Documentation says:  
     * If the sale is completed the inventory will have been removed and the payments will have to equal the total.
     * 
     * No {@link StageInsertableAttr} annotation is required as Merchant OS staging entities are inserted
     * using the {@link InvoiceDTO}. 
     */
    @Column(name = "completed")
    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }
    
    
    // ------------------------------------------------------------------------
    // ~ Object methods
    // ------------------------------------------------------------------------     

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lsCustomerId == null) ? 0 : lsCustomerId.hashCode());
        result = prime * result + ((lsSbsNo == null) ? 0 : lsSbsNo.hashCode());
        result = prime * result + ((lsStoreNo == null) ? 0 : lsStoreNo.hashCode());
        result = prime * result + ((storeId == null) ? 0 : storeId.hashCode());
        result = prime * result + ((swarmId == null) ? 0 : swarmId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        InvoiceStage other = (InvoiceStage) obj;
        if (lsCustomerId == null) {
            if (other.lsCustomerId != null)
                return false;
        } else if (!lsCustomerId.equals(other.lsCustomerId))
            return false;
        if (lsSbsNo == null) {
            if (other.lsSbsNo != null)
                return false;
        } else if (!lsSbsNo.equals(other.lsSbsNo))
            return false;
        if (lsStoreNo == null) {
            if (other.lsStoreNo != null)
                return false;
        } else if (!lsStoreNo.equals(other.lsStoreNo))
            return false;
        if (storeId == null) {
            if (other.storeId != null)
                return false;
        } else if (!storeId.equals(other.storeId))
            return false;
        if (swarmId == null) {
            if (other.swarmId != null)
                return false;
        } else if (!swarmId.equals(other.swarmId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "InvoiceStage [id=" + id + ", storeId=" + storeId + ", swarmId=" + swarmId + ", lsStoreNo=" + lsStoreNo
                + ", lsSbsNo=" + lsSbsNo + ", lsInvoiceId=" + lsInvoiceId + ", lsCustomerId=" + lsCustomerId
                + ", invoiceNo=" + invoiceNo + ", ts=" + ts + ", total=" + total + ", soNumber=" + soNumber
                + ", receiptType=" + receiptType + ", receiptStatus=" + receiptStatus + ", tender=" + tender
                + ", completed=" + completed + "]";
    }

    /**
     * Invoices have two timestamps, the timestamp for the purchase,
     * and the timestamp when the invoice entity was last modified.
     * 
     * Some POS systems (e.g. Shopify) change invoices, because e.g. 
     * they became paid/canceled, therefore both timestamps have to
     * stored in certain cases.
     */
    @Column(name = "last_modified")
    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    @Column(name = "lines_processed")
    public String getLinesProcessed() {
        return linesProcessed;
    }

    public void setLinesProcessed(String linesProcessed) {
        this.linesProcessed = linesProcessed;
    } 
}
