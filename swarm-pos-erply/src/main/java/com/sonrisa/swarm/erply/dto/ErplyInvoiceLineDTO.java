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
package com.sonrisa.swarm.erply.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.erply.ErplyExtractor;
import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;

/**
 * Invoice line, e.g. one product's information within a customer
 * purchase 
 */
public class ErplyInvoiceLineDTO extends InvoiceLineDTO {
    
    /** The value that identifies the product within the Invoice */ 
    private long lineId;
    /** The invoice this line is present in */
    private Long invoiceId;
    /** The product in this line of the invoice*/
    private Long productId = 0L;
    /** The quantity purchased from this particular product */
    private int quantity = 0;
    /** Product price when item was purchased */
    private double price = 0.0;
    /** Tax included for this item */
    private double tax = 0.0;
    /** Currency rate of the invoice */
    private double currencyRate = 1.0;
    /** Set timestamp */
    private Timestamp timestamp;
    
    /**
     * LineID is not an external field, it is manually created in {@link ErplyExtractor},
     * and is consequantly 0,1,2... In order for this to a be a unique key in the database,
     * it is contains the invoiceId variable too.
     * 
     * @return the lineId
     */
    public long getRemoteId() {
        return 10000 * invoiceId + lineId;
    }
    /**
     * @return the invoiceId
     */
    public Long getInvoiceId() {
        return invoiceId;
    }
    /**
     * @return the productId
     */
    public Long getProductId() {
        return productId;
    }
    /**
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }
    /**
     * @return the price
     */
    public double getPrice() {
        return price * currencyRate;
    }
    /**
     * @return the tax
     */
    public double getTax() {
        return tax * currencyRate;
    }
    /**
     * @param lineId the lineId to set
     */
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
    @ExternalField(value = "productID", required = true)
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    /**
     * @param quantity the quantity to set
     */
    @ExternalField(value = "amount", required = true)
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    /**
     * @param price the price to set
     */
    @ExternalField(value = "rowNetTotal", required = true)
    public void setPrice(double price) {
        this.price = price;
    }
    /**
     * @param tax the tax to set
     */
    @ExternalField(value = "rowVAT", required = true)
    public void setTax(double tax) {
        this.tax = tax;
    }
    /**
     * @param currencyRate the currencyRate to set
     */
    public void setCurrencyRate(double currencyRate) {
        this.currencyRate = currencyRate;
    }
	public Timestamp getLastModified() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
    
    
}
