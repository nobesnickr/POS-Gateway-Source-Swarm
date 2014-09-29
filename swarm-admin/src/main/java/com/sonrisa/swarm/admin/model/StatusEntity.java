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

import com.sonrisa.swarm.model.legacy.StoreEntity;

/**
 * Entity containing the status (invoice_count, active, error, etc.) for 
 * a store {@link StoreEntity}.
 * 
 * @author Barnabas
 */
public class StatusEntity extends BaseStatusEntity {

    /**
     * Value indicating whether store is active, either <code>true</code, or <code>false</code>.
     */
    private String active;

    /**
     * Inner object containing details
     */
    private StatusDetailsEntity details;

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public StatusDetailsEntity getDetails() {
        return details;
    }

    public void setDetails(StatusDetailsEntity details) {
        this.details = details;
    }
}
