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
 * Status details embedded into the {@link RpStatusEntity}
 */
public class RpStatusDetailsEntity extends BaseStatusDetailsEntity {

    /**
     * Date of the last known execution of the client
     */
    @JsonProperty("last_heartbeat")
    private String lastHeartbeat;
    
    /**
     * Installed clients version, e.g. <i>1.7.0.0</i>
     */
    @JsonProperty("client_version")
    private String clientVersion;

    public String getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(String lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    @Override
    public String toString() {
        return "RpStatusDetailsEntity [getLastHeartbeat()=" + getLastHeartbeat() + ", getClientVersion()="
                + getClientVersion() + ", getLastInvoice()=" + getLastInvoice() + ", getInvoiceCount()="
                + getInvoiceCount() + "]";
    }
}
