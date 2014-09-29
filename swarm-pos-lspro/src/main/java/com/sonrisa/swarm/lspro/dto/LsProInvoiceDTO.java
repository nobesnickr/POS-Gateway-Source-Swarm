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

import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DTO representing Invoices in the LightspeedPro system
 */
public class LsProInvoiceDTO extends InvoiceDTO {
    private static final Logger LOGGER = LoggerFactory.getLogger(LsProInvoiceDTO.class);
    
    /** Channels unique Id for the invoice */
    private Long id;
    
    /** Date/Time invoice was last modified in POS system - local time */
    private Timestamp dateModifiedUtc;
    
    /** Date/Time invoice was created in POS system - local time */
    private Timestamp dateCreatedUtc;
    
    /** Id shown in the POS UI for invoice (i.e. - "I-1105") */
    private String posInvoiceId;
    
    /** Channels unique Id for the store that created the invoice */
    private String locationId;
    
    /** Invoice total (negative if a return for store credit) */
    private BigDecimal total;
    
    /** Customer's ID */
    private Long customerId;

    @ExternalField(value = "Id", required = true)
    public void setId(Long id) {
        this.id = id;
    }

    @ExternalField(value = "DateModifiedUtc")
    public void setDateModified(String dateModified) {
        this.dateModifiedUtc = new Timestamp(ISO8061DateTimeConverter.stringToDate(dateModified).getTime());
    }

    @ExternalField(value = "DateCreatedUtc")
    public void setInvoiceTimestamp(String dateCreated) {
        this.dateCreatedUtc = new Timestamp(ISO8061DateTimeConverter.stringToDate(dateCreated).getTime());
    }

    @ExternalField(value = "PosInvoiceId")
    public void setPosInvoiceId(String posInvoiceId) {
        this.posInvoiceId = posInvoiceId;
    }

    @ExternalField(value = "LocationId")
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    @ExternalField(value = "Total")
    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    /**
     * Customer id is not JSON property of the invoice 
     * with a field of the embedded <code>Customer</code>. 
     * 
     * @param customer
     */
    @ExternalField(value = "Customer")
    public void setCustomer(ExternalDTO customer) {
        try {
            if (customer != null && customer.hasKey("Id")){
                this.customerId = customer.getLong("Id");
            }
        } catch (ExternalExtractorException ex) {
            LOGGER.debug("Failed to parse customer for invoice", ex);
            this.customerId = null;
        }
    }

    @Override
    public long getRemoteId() {
        return this.id;
    }

    @Override
    public Long getCustomerId() {
       return this.customerId;
    }

    @Override
    public String getInvoiceNumber() {
        return this.posInvoiceId;
    }

    @Override
    public double getTotal() {
        return this.total.doubleValue();
    }

    @Override
    public Timestamp getLastModified() {
        return dateModifiedUtc;
    }
    
    @Override
    public Timestamp getInvoiceTimestamp() {
        return dateCreatedUtc;
    }

    public String getShopId() {
        return locationId;
    }
}
