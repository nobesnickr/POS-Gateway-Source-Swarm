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

package com.sonrisa.swarm.lspro.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;

/**
 * DTO for LineItems in Lightspeed Pro
 */
public class LsProInvoiceLineDTO extends InvoiceLineDTO {
    
    /** Channels unique Id for the line item */
    private Long id;
    
    /** Date/Time line item was last modified in POS system - local time */
    private Long dateModified;
    
    /** Channels unique Id for the invoice that the line item belongs to */
    private Long invoiceId;
    
    /** Channels unique Id for the product sold */
    private Long productId;
    
    /** Price per item sold */
    private BigDecimal sellPrice;
    
    /** Total value of all taxes on this line item */
    private BigDecimal totalTaxes;
    
    /** Number of items sold.  Negative if a return */
    private int quantity;

    @Override
    public long getRemoteId() {
        return this.id;
    }

    @Override
    public Long getInvoiceId() {
        return this.invoiceId;
    }

    @Override
    public Long getProductId() {
        return this.productId;
    }

    @Override
    public int getQuantity() {
        return this.quantity;
    }

    @Override
    public double getPrice() {
        return this.sellPrice.doubleValue();
    }

    @Override
    public double getTax() {
        return this.totalTaxes.doubleValue();
    }

    @Override
    public Timestamp getLastModified() {
        return new Timestamp(this.dateModified);
    }
    
    @ExternalField(value = "Id", required = true)
    public void setId(Long id) {
        this.id = id;
    }

    @ExternalField(value = "DateCreatedUtc")
    public void setDateModified(String dateModified) {
        this.dateModified = ISO8061DateTimeConverter.stringToDate(dateModified).getTime();
    }

    @ExternalField(value = "InvoiceId", required = true)
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    @ExternalField(value = "ProductId")
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @ExternalField(value = "SellPrice")
    public void setSellPrice(BigDecimal sellPrice) {
        this.sellPrice = sellPrice;
    }

    @ExternalField(value = "TotalTaxes")
    public void setTotalTaxes(BigDecimal totalTaxes) {
        this.totalTaxes = totalTaxes;
    }

    @ExternalField(value = "Quantity")
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
