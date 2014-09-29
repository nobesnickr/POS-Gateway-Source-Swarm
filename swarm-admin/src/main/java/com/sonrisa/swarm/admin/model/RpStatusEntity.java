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
 * Retail Pro status entity
 * 
 * @author Barnabas
 */
public class RpStatusEntity extends BaseStatusEntity {

    /**
     * Client's swarm_id, unique per installation
     */
    @JsonProperty("swarm_id")
    private String swarmId;
    
    /**
     * Client's timezone, e.g. <i>US/Central</i>
     */
    private String timezone;
    
    /**
     * Client time offset relatively to the timezone
     */
    private Integer timeOffset;
    
    /**
     * Embedded details object containing details on the store's status
     */
    private RpStatusDetailsEntity details;

    public String getSwarmId() {
        return swarmId;
    }

    public void setSwarmId(String swarmId) {
        this.swarmId = swarmId;
    }

    public String getTimezone() {
        return timezone;
    }
    
    public Integer getTimeOffset() {
        return timeOffset;
    }

    public void setTimeOffset(Integer timeOffset) {
        this.timeOffset = timeOffset;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public RpStatusDetailsEntity getDetails() {
        return details;
    }

    public void setDetails(RpStatusDetailsEntity details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "RpStatusEntity [getSwarmId()=" + getSwarmId() + ", getTimezone()=" + getTimezone()
                + ", getTimeOffset()=" + getTimeOffset() + ", getDetails()=" + getDetails() + ", getStoreId()="
                + getStoreId() + ", getName()=" + getName() + ", getApi()=" + getApi() + ", getCreated()="
                + getCreated() + ", getNotes()=" + getNotes() + ", getStatus()=" + getStatus() + ", getReason()="
                + getReason() + "]";
    }
}
