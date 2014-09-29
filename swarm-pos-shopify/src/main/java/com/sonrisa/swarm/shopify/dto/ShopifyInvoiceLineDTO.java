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
package com.sonrisa.swarm.shopify.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;

/**
 * InvoiceLine  from the Shopify POS system
 */ 
public class ShopifyInvoiceLineDTO extends InvoiceLineDTO {

    /** The value that identifies the product within the Invoice */ 
    private long lineId;
    /** The invoice this line is present in */
    private Long invoiceId;
    /** The product in this line of the invoice*/
    private Long productId;
    /** The quantity purchased from this particular product */
    private int quantity = 0;
    /** Product price when item was purchased */
    private double localCurrencyPrice = 0.0;
    /** Tax included for this item */
    private double tax = 0.0;
    
    private Timestamp lastModified;
    
    /**
     * @return the lineId
     */
    @Override
    public long getRemoteId() {
        return lineId;
    }
    /**
     * @return the invoiceId
     */
    @Override
    public Long getInvoiceId() {
        return invoiceId;
    }
    /**
     * @return the productId
     */
    @Override
    public Long getProductId() {
        return productId;
    }
    /**
     * @return the quantity
     */
    @Override
    public int getQuantity() {
        return quantity;
    }
    /**
     * @return the price
     */
    @Override
    public double getPrice() {
        return localCurrencyPrice;
    }
    /**
     * @return the tax
     */
    @Override
    public double getTax() {
        return tax;
    }

    @Override
    public Timestamp getLastModified() {
            return lastModified;
    }
    
    /**
     * @param lineId the lineId to set
     */
    @ExternalField(value = "id", required = true)
    public void setLineId(long lineId) {
        this.lineId = lineId;
    }
    /**
     * @param invoiceId the invoiceId to set
     */
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }
    /**
     * @param productId the productId to set
     */
    @ExternalField("variant_id")
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    /**
     * @param quantity the quantity to set
     */
    @ExternalField("quantity")
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    /**
     * @param price the price to set
     */
    @ExternalField(value = "price", required = true)
    public void setLocalCurrencyPrice(double price) {
        this.localCurrencyPrice = price;
    } 
    /**
     * @param tax the tax to set
     */
    public void setTax(double tax) {
        this.tax = tax;
    }

    /**
     * @param lastModifiedString The timestamp to be set
     */
    @ExternalField("created_at")
    public void setTimeStamp(String lastModifiedString) {
        this.lastModified = new Timestamp(ISO8061DateTimeConverter.stringToDate(lastModifiedString).getTime());
    }
}
