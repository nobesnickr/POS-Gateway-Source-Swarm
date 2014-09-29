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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Status details embedded in {@link StatusEntity}
 * 
 * @author Barnabas
 */
public class StatusDetailsEntity extends BaseStatusDetailsEntity {

    /**
     * Last extraction's time
     */
    @JsonProperty("last_extract")
    private String lastExtract;

    public String getLastExtract() {
        return lastExtract;
    }

    public void setLastExtract(String lastExtract) {
        this.lastExtract = lastExtract;
    }

    @Override
    public String toString() {
        return "StatusDetailsEntity [getLastExtract()=" + getLastExtract() + ", getLastInvoice()=" + getLastInvoice()
                + ", getInvoiceCount()=" + getInvoiceCount() + "]";
    }    
}
