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
package com.sonrisa.swarm.model.staging;


import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sonrisa.swarm.model.staging.annotation.StageInsertableAttr;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableType;
import com.sonrisa.swarm.model.staging.retailpro.RetailProAttr;

/**
 * This entity represents a record in the staging_invoice_lines table.
 *  
 * @author joe
 */
@Entity
@Table(name = "staging_invoice_lines")
@StageInsertableType(dbTableName = "invoice_lines")
public class InvoiceLineStage extends BaseStageEntity {
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceLineStage.class);
    private static final long serialVersionUID = -5078889627224799866L;

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
    @RetailProAttr
    private String swarmId;    
    
    @RetailProAttr(value = "StoreNo",  maxLength = 100)
    private String lsStoreNo;

    @RetailProAttr(value = "SbsNo", maxLength = 100)
    private String lsSbsNo;    
        
    @RetailProAttr(value = "InvoiceSid", maxLength = 100)
    private String lsInvoiceId;

    @RetailProAttr(value = "ItemPos",  maxLength = 100)
    private String lsLineId;

    @RetailProAttr(value = "ProductSid", maxLength = 100)
    private String lsProductId;    

    @RetailProAttr(value = "Quantity", maxLength = 10)
    private String quantity;

    @RetailProAttr(value = "Price", maxLength = 20, truncatingAllowed = false)
    private String price;

    @RetailProAttr(value = "TaxAmt", maxLength = 10, truncatingAllowed = false)
    private String tax;
        
    private String description;
    
    private Timestamp ts;
        
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
    	LOGGER.info("Staging invoice line id:"+id);
        this.id = id;
    }

    @StageInsertableAttr(dbColumnName="ls_invoice_id")
    @Column(name = "ls_invoice_id")    
    public String getLsInvoiceId() {
        return lsInvoiceId;
    }

    public void setLsInvoiceId(String lsInvoiceId) {
        this.lsInvoiceId = lsInvoiceId;
    }

    @StageInsertableAttr(dbColumnName="ls_line_id")
    @Column(name = "ls_line_id")    
    public String getLsLineId() {
        return lsLineId;
    }

    public void setLsLineId(String lsLineId) {
        this.lsLineId = lsLineId;
    }

    @StageInsertableAttr(dbColumnName="ls_product_id")
    @Column(name = "ls_product_id")    
    public String getLsProductId() {
        return lsProductId;
    }

    public void setLsProductId(String lsProductId) {
        this.lsProductId = lsProductId;
    }

    @StageInsertableAttr(dbColumnName="quantity")
    @Column(name = "quantity")    
    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @StageInsertableAttr(dbColumnName="price")
    @Column(name = "price")    
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @StageInsertableAttr(dbColumnName="tax")
    @Column(name = "tax")    
    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }      

    @Override
    @StageInsertableAttr(dbColumnName="swarm_id")
    @Column(name = "swarm_id")    
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

    @StageInsertableAttr(dbColumnName="description")
    @Column(name = "description")
	public String getDescription() {
		return description;
	}

    @StageInsertableAttr(dbColumnName="ts")
    @Column(name = "ts")
	public Timestamp getTs() {
		return ts;
	}

	public void setDescription(String name) {
		this.description = name;
	}

	public void setTs(Timestamp ts) {
		this.ts = ts;
	}

    // ------------------------------------------------------------------------
    // ~ Object methods
    // ------------------------------------------------------------------------   

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lsInvoiceId == null) ? 0 : lsInvoiceId.hashCode());
        result = prime * result + ((lsLineId == null) ? 0 : lsLineId.hashCode());
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
        InvoiceLineStage other = (InvoiceLineStage) obj;
        if (lsInvoiceId == null) {
            if (other.lsInvoiceId != null)
                return false;
        } else if (!lsInvoiceId.equals(other.lsInvoiceId))
            return false;
        if (lsLineId == null) {
            if (other.lsLineId != null)
                return false;
        } else if (!lsLineId.equals(other.lsLineId))
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
        return "InvoiceLineStage [id=" + id + ", storeId=" + storeId + ", swarmId=" + swarmId + ", lsStoreNo="
                + lsStoreNo + ", lsSbsNo=" + lsSbsNo + ", lsInvoiceId=" + lsInvoiceId + ", lsLineId=" + lsLineId
                + ", lsProductId=" + lsProductId + ", quantity=" + quantity + ", price=" + price + ", tax=" + tax + "]";
    }

}
