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
package com.sonrisa.swarm.kounta.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;

/**
 * Representation of a Kounta order's batch item
 * 
 * @author Barnabas
 */
public class KountaInvoiceDTO extends InvoiceDTO {

    /**
     * The id field
     */
    private long id;
    
    /**
     * The saleNumber field
     */
    private String saleNumber;
    
    /**
     * The total field
     */
    private double total = 0.0;
    
    /**
     * Creation date of the invoice
     */
    private Timestamp createdDate;
    
    /**
     * Update timestamp 
     */
    private Timestamp updatedDate;
    
    /**
     * Customer's id. 
     * Only available for detailed requests
     */
    private Long customerId;
    
    /**
     * Value indicating whether DTO is finished (lines, etc.)
     */
    private boolean detailed;

    @Override
    public long getRemoteId() {
        return id;
    }

    @Override
    public Long getCustomerId() {
        return customerId;
    }

    @Override
    public String getInvoiceNumber() {
        return saleNumber;
    }

    @Override
    public double getTotal() {
        return total;
    }

    @Override
    public Timestamp getInvoiceTimestamp() {
        return createdDate;
    }
    
    @Override
    public Timestamp getLastModified(){
        return updatedDate;
    }

    @Override
    public Integer getLinesProcessed(){
        return detailed ? 1 : 0;
    }

    @ExternalField(value="id", required=true)
    public void setId(long id) {
        this.id = id;
    }

    @ExternalField(value="sale_number")
    public void setSaleNumber(String saleNumber) {
        this.saleNumber = saleNumber;
    }

    /**
     * Total from the batch, note that <strong>this is field is missing</strong> in the detailed page
     */
    @ExternalField(value="total")
    public void setTotal(double total) {
        this.total = total;
    }

    @ExternalField(value="created_at")
    public void setCreatedDate(String createdDateString) {
        this.createdDate = new Timestamp(ISO8061DateTimeConverter.stringToDate(createdDateString).getTime());
    }

    @ExternalField(value="updated_at")
    public void setUpdatedDate(String updatedDateString) {
        this.updatedDate = new Timestamp(ISO8061DateTimeConverter.stringToDate(updatedDateString).getTime());
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setDetailed(boolean detailed) {
        this.detailed = detailed;
    }
}
