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

import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Invoice from the Shopify remote system
 */
public class ShopifyInvoiceDTO extends InvoiceDTO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShopifyInvoiceDTO.class);

    /**
     * Invoice id
     */
    private long invoiceId;
    /**
     * Customer who made the purchase
     */
    private Long customerId;
    /**
     * The invoice number
     */
    private String invoiceNumber;
    /**
     * The amount of currency payed for purchasing the items of the invoice
     */
    private double total;
    
    /**
     * The amount of current payed for the sum of the lines
     */
    private double lineNetTotal;
    
    /**
     * The time of the purchase
     */
    private Timestamp createdDate;

    /**
     * The time the invoice was updated
     */
    private Timestamp updatedDate;

    /**
     * E.g. pending, paid, refunded, voided
     */
    private String financialStatus;

    /**
     * @return the invoiceId
     */
    @Override
    public long getRemoteId() {
        return invoiceId;
    }

    /**
     * @return the customerId
     */
    @Override
    public Long getCustomerId() {
        return customerId;
    }

    /**
     * @return the invoiceNumber
     */
    @Override
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * @return the total
     */
    @Override
    public double getTotal() {
        return total;
    }

    /**
     * @return A value indicating whether invoice is completed, and if so it
     * will be included in the total
     */
    @Override
    public Integer getCompleted() {
        return "paid".equals(this.financialStatus) ? 1 : 0;
    }

    /**
     * @return the timeStamp
     */
    @Override
    public Timestamp getLastModified() {
        return this.updatedDate;
    }

    @Override
    public Timestamp getInvoiceTimestamp() {
        return this.createdDate;
    }

    public double getLineNetTotal() {
        return lineNetTotal;
    }

    /**
     * @param invoiceId the invoiceId to set
     */
    @ExternalField(value = "id", required = true)
    public void setInvoiceId(long invoiceId) {
        this.invoiceId = invoiceId;
    }

    /**
     * @param customer JSON object containing the customer
     */
    @ExternalField("customer")
    public void setCustomerId(ExternalDTO customer) {
        try {
            if (customer != null) {
                this.customerId = customer.getLong("id");
            }
        } catch (ExternalExtractorException ex) {
            LOGGER.debug("Failed to parse customer for Shopify", ex);
            this.customerId = null;
        }
    }

    /**
     * @param invoiceNumber the invoiceNumber to set
     */
    @ExternalField("order_number")
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * @param financialStatus Set financial status
     */
    @ExternalField("financial_status")
    public void setFinancialStatus(String financialStatus) {
        this.financialStatus = financialStatus;
    }

    /**
     * @param createdAtString The timeStamp to set
     */
    @ExternalField("created_at")
    public void setCreatedAt(String createdAtString) {
        this.createdDate = new Timestamp(ISO8061DateTimeConverter.stringToDate(createdAtString).getTime());
    }

    /**
     * @param updatedAt The timeStamp to set
     */
    @ExternalField("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedDate = new Timestamp(ISO8061DateTimeConverter.stringToDate(updatedAt).getTime());
    }

    /**
     * @param total the netTotal to set
     */
    @ExternalField(value = "total_price", required = true)
    public void setTotal(double total) {
        this.total = total;
    }
    
    @ExternalField(value = "total_line_items_price", required = true)
    public void setLineNetTotal(double lineNetTotal) {
        this.lineNetTotal = lineNetTotal;
    }
}
