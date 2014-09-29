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
package com.sonrisa.swarm.retailpro.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *  This class represents the JSON object received by the {@link RpClientController} as a heartbeat.
 *
 * @author joe
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class RpHeartbeatJson {
    
    /** Version of the RetailPro plugin. */
    @JsonProperty("Version")
    private String rpPluginVersion;

    @Override
    public String toString() {
        return "RpHeartbeatJson{" + "rpPluginVersion=" + rpPluginVersion + '}';
    }
        

    public String getRpPluginVersion() {
        return rpPluginVersion;
    }

    public void setRpPluginVersion(String rpPluginVersion) {
        this.rpPluginVersion = rpPluginVersion;
    }
    
    
    
}
