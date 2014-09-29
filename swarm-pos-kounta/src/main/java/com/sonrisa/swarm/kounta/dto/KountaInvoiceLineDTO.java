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
package com.sonrisa.swarm.kounta.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;

/**
 * Representation of a Kounta order item
 * 
 * @author Barnabas
 */
public class KountaInvoiceLineDTO extends InvoiceLineDTO {

    /**
     * Invoice's ID
     */
    private Long invoiceId;
    
    /**
     * Line number inside the invoice
     */
    private Integer lineNumber;
    
    /**
     * Product relating to this invoice
     */
    private Long productId;
    
    /**
     * Price per unit
     */
    private double unitPrice = 0.0D;
    
    /**
     * Tax per unit
     */
    private double unitTax = 0.0D;
    
    /**
     * Quantity of the product purchased
     */
    private int quantity = 0;
    
    /**
     * Last modified
     */
    private Timestamp lastModified;

    @Override
    public long getRemoteId() {
        if(invoiceId == null){
            throw new IllegalStateException("Invoice id isn't set");
        }
        return 10000L * invoiceId + lineNumber;
    }

    @Override
    public Long getInvoiceId() {
        return invoiceId;
    }

    @Override
    public Long getProductId() {
        return productId;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public double getPrice() {
        return unitPrice;
    }

    @Override
    public double getTax() {
        return unitTax;
    }

    @Override
    public Timestamp getLastModified() {
        return lastModified;
    }

    /**
     * Should be derived from invoice
     */
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    @ExternalField(value = "number", required = true)
    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Should be derived from product
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @ExternalField("unit_price")
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    @ExternalField("unit_tax")
    public void setUnitTax(double unitTax) {
        this.unitTax = unitTax;
    }

    @ExternalField("quantity")
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Should be derived from invoice
     */
    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }
}
