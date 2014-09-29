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

import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;

/**
 * Invoice from the MerchantOS remote system
 */
public class MosInvoiceDTO extends InvoiceDTO {
    
    /** Invoice id */
    private long invoiceId; 
    /** Customer who made the purchase */
    private Long customerId;
    /** The invoice number (???) */
    private String invoiceNumber;
    /** The amount of currency payed for purchasing the items of the invoice */
    private double netTotal;
    /** The time of the purchase */
    private Timestamp timeStamp;
    
    /**  If the sale is completed the inventory will have been removed and the payments will have to equal the total. */
    private boolean completed;
    
    /**
     * @return the invoiceId
     */
    public long getRemoteId() {
        return invoiceId;
    }
    /**
     * @return the customerId
     */
    public Long getCustomerId() {
        return customerId;
    }
    /**
     * @return the invoiceNumber
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }
    /**
     * @return the total
     */
    public double getTotal() {
        return netTotal;
    }
    
    @Override
    public Integer getCompleted() {
        return completed ? 1 : 0;
    }
    
    /**
     * @return the timeStamp
     */
    public Timestamp getLastModified() {
        return this.timeStamp;
    }
    
	public Timestamp getInvoiceTimestamp() {
		// Merchant OS has only one timestamp field
		return this.timeStamp;
	}
    
    /**
     * @param invoiceId the invoiceId to set
     */
    @ExternalField(value = "saleID", required = true)
    public void setInvoiceId(long invoiceId) {
        this.invoiceId = invoiceId;
    }
    /**
     * @param customerId the customerId to set
     */
    @ExternalField("customerID")
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    /**
     * @param invoiceNumber the invoiceNumber to set
     */
    @ExternalField("ticketNumber")
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
    
    /**
     * @param timeStamp the timeStamp to set
     */
    @ExternalField("timeStamp")
    public void setTimeStamp(String lastModifiedString) {
        this.timeStamp = new Timestamp(ISO8061DateTimeConverter.stringToDate(lastModifiedString).getTime());
    }
    /**
     * @param netTotal the netTotal to set
     */
    @ExternalField(value = "calcTotal", required = true)
    public void setNetTotal(double netTotal) {
        this.netTotal = netTotal;
    }
    
    @ExternalField(value = "completed")
    public void setCompleted(String completedString) {
        this.completed = Boolean.parseBoolean(completedString);
    }
}
