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
import java.util.Calendar;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;

/**
 * Invoice (Sale) retrieved from Erply
 */
public class ErplyInvoiceDTO extends InvoiceDTO {

    /** Invoice id */
    private long invoiceId; 
    /** Customer who made the purchase */
    private Long customerId = 0L;
    /** The invoice number (???) */
    private String invoiceNumber = "";
    /** The amount of currency payed for purchasing the items of the invoice */
    private double netTotal = 0.0;
    /** Currency rate for the netTotal */
    private double currencyRate = 1.0;
    /** The last modification date of the invoice, used for filtering */
    private long timeStamp = 0L;
    /** Time of the invoice */
    private Calendar invoiceTime = Calendar.getInstance();
    /** Timezone of the establishment this invoice represents */
    private String timezone;
        
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
        return netTotal * currencyRate;
    }

    @Override
	public Timestamp getInvoiceTimestamp() {
        if(StringUtils.hasLength(this.timezone)){
            return new Timestamp(ISO8061DateTimeConverter.stringToDate(invoiceTime, timezone).getTime());
        } else {
            return new Timestamp(this.invoiceTime.getTimeInMillis());
        }
	}
    
	/**
     * Get the timestamp of the last modification if the entry
     * 
     * This timestamp is calculated using the remote Unix timestamp of
     * Erply by multiplying it with 1000L, as java.sql.Timestamp expects
     * a timestamp with milliseconds
     * 
     * @return the lastModified
     */
    public Timestamp getLastModified() {
        return new Timestamp(1000L * this.timeStamp);
    }
    /**
     * @param invoiceId the invoiceId to set
     */
    @ExternalField(value = "id", required = true)
    public void setInvoiceId(long invoiceId) {
        this.invoiceId = invoiceId;
    }
    /**
     * @param customerId the customerId to set
     */
    @ExternalField("clientID")
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    /**
     * @param invoiceNumber the invoiceNumber to set
     */
    @ExternalField(value = "referenceNumber", required = true)
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
    
    /**
     * @param timeStamp the timeStamp to set
     */
    @ExternalField("lastModified")
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * @param timeStamp the timeStamp to set
     */
    @ExternalField("date")
    public void setInvoiceDateDay(String dayString) {
    	Calendar day = Calendar.getInstance();
        day.setTime(ISO8061DateTimeConverter.stringToDate(dayString));
        
        this.invoiceTime.set(Calendar.YEAR, day.get(Calendar.YEAR));
        this.invoiceTime.set(Calendar.MONTH, day.get(Calendar.MONTH));
        this.invoiceTime.set(Calendar.DAY_OF_MONTH, day.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * @param timeStamp the timeStamp to set
     */
    @ExternalField("time")
    public void setInvoiceDateTime(String timeString) {
    	Calendar time = Calendar.getInstance();
        time.setTime(ISO8061DateTimeConverter.stringToDate(timeString));
        
        this.invoiceTime.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
        this.invoiceTime.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
        this.invoiceTime.set(Calendar.SECOND, time.get(Calendar.SECOND));
    }
    
    
    /**
     * @param netTotal the netTotal to set
     */
    @ExternalField(value = "netTotal", required = true)
    public void setNetTotal(double netTotal) {
        this.netTotal = netTotal;
    }
    /**
     * @param currencyRate the currencyRate to set
     */
    @ExternalField("currencyRate")
    public void setCurrencyRate(double currencyRate) {
        this.currencyRate = currencyRate;
    }
    public void setTimeZone(String timezone) {
        this.timezone = timezone;
    }
}
