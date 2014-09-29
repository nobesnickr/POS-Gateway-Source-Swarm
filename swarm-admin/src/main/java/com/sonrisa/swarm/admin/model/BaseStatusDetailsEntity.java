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

package com.sonrisa.swarm.admin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sonrisa.swarm.model.legacy.StoreEntity;

/**
 * Inner class containing details on a {@link StoreEntity}
 */
@JsonInclude(Include.NON_NULL)  
public abstract class BaseStatusDetailsEntity {
    /**
     * Last invoice's date for the store
     */
    @JsonProperty("last_invoice")
    private String lastInvoice;

    /**
     * Number of invoices currently for the store
     */
    @JsonProperty("invoice_count")
    private long invoiceCount;

    public String getLastInvoice() {
        return lastInvoice;
    }

    public void setLastInvoice(String lastInvoice) {
        this.lastInvoice = lastInvoice;
    }

    public long getInvoiceCount() {
        return invoiceCount;
    }

    public void setInvoiceCount(long invoiceCount) {
        this.invoiceCount = invoiceCount;
    }
}
