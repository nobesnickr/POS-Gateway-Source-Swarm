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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sonrisa.swarm.retailpro.rest.RetailProApiConstants;

/**
 * Model object which encapsulates the meta description
 * of the Swarm REST api.
 *
 * @author joe
 */
public class InterfaceDescription {
    
    /** The largest supported interface version. */
    @JsonProperty(RetailProApiConstants.JSON_KEY_API_DESCRIPTION_VERSION)
    private String interfaceVersion;
    
    /** The timestamp of the build. */
    @JsonProperty(RetailProApiConstants.JSON_KEY_BUILD_TIMESTAMP)
    private String buildTimestamp;

    /**
     * Constructor.
     * 
     * @param interfaceVersion The largest supported interface version
     */
    public InterfaceDescription(String interfaceVersion, String buildTimestamp) {
        this.interfaceVersion = interfaceVersion;
        this.buildTimestamp = buildTimestamp;
    }
    
    // ------------------------------------------------------------------------
    // ~ Setters / getters
    // ------------------------------------------------------------------------

    public String getInterfaceVersion() {
        return interfaceVersion;
    }

    public void setInterfaceVersion(String interfaceVersion) {
        this.interfaceVersion = interfaceVersion;
    }

    public String getBuildTimestamp() {
        return buildTimestamp;
    }
        
    
}
