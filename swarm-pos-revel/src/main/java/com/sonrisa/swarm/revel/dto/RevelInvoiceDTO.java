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
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.revel.exception.RevelResourcePathFormatException;
import com.sonrisa.swarm.revel.util.RevelResourcePathConverter;

/**
 * Invoice (Order) from Revel
 */
public class RevelInvoiceDTO extends InvoiceDTO {
    private static final Logger LOGGER = LoggerFactory.getLogger(RevelInvoiceDTO.class);

    /** Invoice id */
    private long invoiceId; 
    /** Customer who made the purchase */
    private Long customerId = 0L;
    /** The amount of currency payed for purchasing the items of the invoice */
    private double total = 0.0;
    /** The time the invoice record was last modified, used for filtering */
    private Timestamp updatedAt = new Timestamp(0L);
    /** The  time the invoice record was created */
    private String createdAtString = null;
    /** Timezone of the establishment this invoice represents */
    private String timezone;
    
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
        return Long.toString(invoiceId);
    }
    /**
     * @return the total
     */
    @Override
    public double getTotal() {
        return total;
    }
    
    /**
     * @return the lastModified
     */
    @Override
    public Timestamp getLastModified() {
        return this.updatedAt;
    }
    
    /**
     * @return the lastModified
     */
    @Override
    public Timestamp getInvoiceTimestamp() {
        if(StringUtils.isEmpty(createdAtString)){
            return null;
        }
        
        Date retVal;
        if(StringUtils.hasLength(this.timezone)){
            retVal = ISO8061DateTimeConverter.stringToDate(createdAtString, timezone);
        } else {
            retVal = ISO8061DateTimeConverter.stringToDate(createdAtString);
        }
        
        return new Timestamp(retVal.getTime());
    }
    
    /**
     * @param orderPath REST URL containing the invoice id
     * @throws RevelResourcePathFormatException 
     */
    @ExternalField(value = "resource_uri", required = true)
    public void setInvoiceId(String orderPath) throws RevelResourcePathFormatException {
        this.invoiceId = RevelResourcePathConverter.resourcePathToLong("/resources/Order/", orderPath);
    }
    
    /**
     * @param customerPath
     */
    @ExternalField("customer")
    public void setCustomerId(String customerPath) {
        try {
            if(StringUtils.hasLength(customerPath)){
                this.customerId = RevelResourcePathConverter.resourcePathToLong("/resources/Customer/", customerPath);
            }
        } catch (RevelResourcePathFormatException e){
            LOGGER.debug("Failed to parse customer from {}", customerPath, e);
            this.customerId = null;
        }
    }
    
    /**
     * @param date the timeStamp to set
     */
    @ExternalField("updated_date")
    public void setUpdatedDate(String date) {
        this.updatedAt = new Timestamp(ISO8061DateTimeConverter.stringToDate(date).getTime());
    }
    

    /**
     * @param date the timeStamp to set
     */
    @ExternalField("created_date")
    public void setCreatedDate(String date) {
        this.createdAtString = date;
    }
    
    /**
     * @param netTotal the netTotal to set
     */
    @ExternalField(value = "final_total", required=true)
    public void setTotal(double netTotal) {
        this.total = netTotal;
    }
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
