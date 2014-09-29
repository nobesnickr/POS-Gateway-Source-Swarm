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
package com.sonrisa.swarm.mos.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;

/**
 * InvoiceLine (SaleLine) from the Merchant OS system
 */
public class MosInvoiceLineDTO extends InvoiceLineDTO {

    /** The value that identifies the product within the Invoice */ 
    private long lineId;
    /** The invoice this line is present in */
    private Long invoiceId;
    /** The product in this line of the invoice*/
    private Long productId;
    /** The quantity purchased from this particular product */
    private int quantity = 0;
    /** Product price when item was purchased */
    private double price = 0.0;
    /** Tax included for this item */
    private double tax = 0.0;
    
    private Timestamp lastModified;
    
    /**
     * @return the lineId calculated from the invoiceId and the line's position
     */
    @Override
    public long getRemoteId() {
        return 10000 * invoiceId + lineId;
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
        return price;
    }
    /**
     * @return the tax
     */
    @Override
    public double getTax() {
        return tax;
    }
    
    /**
     * @return Last modification timestamp
     */
    @Override
    public Timestamp getLastModified() {
            return lastModified;
    }
	
    /**
     * @param lineId the lineId to set
     */
    @ExternalField(value = "saleLineID", required = true)
    public void setLineId(long lineId) {
        this.lineId = lineId;
    }
    /**
     * @param invoiceId the invoiceId to set
     */
    @ExternalField(value = "saleID", required = true)
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }
    /**
     * @param productId the productId to set
     */
    @ExternalField("itemID")
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    /**
     * @param quantity the quantity to set
     */
    @ExternalField("unitQuantity")
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    /**
     * @param price the price to set
     */
    @ExternalField(value = "calcTotal", required = true)
    public void setPrice(double price) {
        this.price = price;
    }
    /**
     * @param tax the tax to set
     */
    @ExternalField(value = "calcTax1", required = true)
    public void setTax(double tax) {
        this.tax = tax;
    }
	
    @ExternalField("timeStamp")
	public void setLastModified(String lastModified) {
        this.lastModified = new Timestamp(ISO8061DateTimeConverter.stringToDate(lastModified).getTime());
	}
}
