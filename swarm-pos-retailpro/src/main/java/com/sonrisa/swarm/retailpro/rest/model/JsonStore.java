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

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sonrisa.swarm.retailpro.rest.controller.StoreController;

/**
 * This class represents the JSON object received by the {@link StoreController}.
 *
 * @author joe
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonStore implements Serializable {
    
    @JsonProperty("SbsNo")
    private String sbsNumber;
    
    @JsonProperty("StoreNo")
    private String storeNumber;
    
    @JsonProperty("ModifiedDate")
    private String modifiedDate;

    @JsonProperty("StoreName")
    private String name;
    
    @JsonProperty("PosTimezone")
    private String posTimezone;
    
    @JsonProperty("Notes")
    private String notes;
    
    // ------------------------------------------------------------------------
    // ~ Object methods
    // ------------------------------------------------------------------------
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.sbsNumber != null ? this.sbsNumber.hashCode() : 0);
        hash = 83 * hash + (this.storeNumber != null ? this.storeNumber.hashCode() : 0);
        hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JsonStore other = (JsonStore) obj;
        if ((this.sbsNumber == null) ? (other.sbsNumber != null) : !this.sbsNumber.equals(other.sbsNumber)) {
            return false;
        }
        if ((this.storeNumber == null) ? (other.storeNumber != null) : !this.storeNumber.equals(other.storeNumber)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "JsonStore [sbsNumber=" + sbsNumber + ", storeNumber="
                + storeNumber + ", modifiedDate=" + modifiedDate + ", name="
                + name + "]";
    }

    // ------------------------------------------------------------------------
    // ~ Setters / getters
    // ------------------------------------------------------------------------
    public String getSbsNumber() {
        return sbsNumber;
    }

    public void setSbsNumber(String sbsNumber) {
        this.sbsNumber = sbsNumber;
    }

    public String getStoreNumber() {
        return storeNumber;
    }

    public void setStoreNumber(String storeNumber) {
        this.storeNumber = storeNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    /**
     * @return the posTimezone
     */
    public String getPosTimezone() {
        return posTimezone;
    }

    /**
     * @param posTimezone the posTimezone to set
     */
    public void setPosTimezone(String posTimezone) {
        this.posTimezone = posTimezone;
    }

    /**
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
