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
package com.sonrisa.swarm.revel.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.revel.exception.RevelResourcePathFormatException;
import com.sonrisa.swarm.revel.util.RevelResourcePathConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InvoiceLine from Revel (OrderLine)
 */
public class RevelInvoiceLineDTO extends InvoiceLineDTO {
    private static final Logger LOGGER = LoggerFactory.getLogger(RevelInvoiceLineDTO.class);
    
    private long lineId;
    
    private long invoiceId;
    
    private long productId;
    
    private int quantity;
    
    private double price;
    
    private double tax;

    private Timestamp lastModified;
    
    /**
     * @param lineId the lineId to set
     */
    @ExternalField(value="id", required=true)
    public void setLineId(long lineId) {
        this.lineId = lineId;
    }
    /**
     * @param orderPath REST URL for the Order
     */
    @ExternalField(value="order", required=true)
    public void setInvoiceId(String orderPath) throws RevelResourcePathFormatException {
        this.invoiceId = RevelResourcePathConverter.resourcePathToLong("/resources/Order/", orderPath);
    }
    /**
     * @param productPath Product URL for the product
     */
    @ExternalField(value = "product")
    public void setProductId(String productPath) {
        try {
            this.productId = RevelResourcePathConverter.resourcePathToLong("/resources/Product/", productPath);
        } catch (RevelResourcePathFormatException e){
            LOGGER.debug("Failed to product for invoice line from {}", e, productPath);
            this.productId = 0L;
        }
    }
    
    /**
     * @param quantity the quantity to set
     */
    @ExternalField(value = "quantity", required = true)
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    /**
     * @param price the price to set
     */
    @ExternalField(value = "price", required = true)
    public void setPrice(double price) {
        this.price = price;
    }
    
    /**
     * @param tax the tax to set
     */
    @ExternalField(value = "tax_amount", required = true)
    public void setTax(double tax) {
        this.tax = tax;
    }

    /**
     * @param date the timeStamp to set
     */
    @ExternalField("updated_date")
    public void setTimeStamp(String date) {
        this.lastModified = new Timestamp(ISO8061DateTimeConverter.stringToDate(date).getTime());
    }

    /**
     * @return the lineId
     */
    @Override
    public long getRemoteId() {
        return  lineId;
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
    
    @Override
    public Timestamp getLastModified() {
            return lastModified;
    }
}
