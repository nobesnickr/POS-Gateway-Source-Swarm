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
package com.sonrisa.swarm.retailpro.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.sonrisa.swarm.model.BaseSwarmEntity;
import com.sonrisa.swarm.model.staging.retailpro.RetailProAttr;

/**
 * This entity encapsulates information about a "RetailPro store".
 * 
 * In this case a "RetailPro store" means a RetailPro installation
 * which communicates with our application through the Rest API.
 * 
 * A swarmId (see: {@link EntityWithSwatmId} identifies a 
 * RetailPro installation and it has to be set 
 * in the RetailPro Swarm Client during the installation. 
 * 
 * On the other hand a RetailPro installation could consist of 
 * more than one "store". In this sense "store" means a substore
 * below the (master) RetailPro installation. 
 * 
 * The substores are identified by the combination of these:
 *  - swarmId (identifies the RetailPro installation)
 *  - sbs number (identifies a subsidiary in the RetailPro)
 *  - store number (identifies a store below the subsidiary in the RetailPro)
 * 
 * @author joe
 *
 */
@Table(name = "stores_rp")
@Entity
public class RpStoreEntity extends BaseSwarmEntity {

    private static final long serialVersionUID = 6991307590849287475L;
    
    /** Primary key int the DB. */
    private Long id;
    
    /** Foreign key to store_id column in the legacy Stores table. */  
    private Long storeId;
    
    /** Subsidiary number, identifies a subsidiary in the RetailPro. */ 
    @RetailProAttr(value = "SbsNo")
    private String sbsNumber;
    
    /** Store number, identifies a store below the subsidiary in the RetailPro. */
    @RetailProAttr(value = "StoreNo")
    private String storeNumber;
    
    /**
     * The swarmId (see: {@link EntityWithSwatmId} identifies a RetailPro
     * installation and it has to be set 
     * in the RetailPro Swarm Client during the installation.
     */    
    @RetailProAttr(value = "SwarmId")
    private String swarmId;
    
    /** Name of the store. */    
    @RetailProAttr(value = "StoreName", maxLength = 255)
    private String storeName;
    
    /** POS software, which is the data source for the client */
    private String posSoftware;
    
    /** 
     * Timezone of the current RetailPro substore.
     * 
     * If it isn't null, it is used as a timezone of the invoice's timestsamp 
     * during the conversion from staging to legacy entity. 
     */
    private String timeZone;
    
    /** 
     * Invoice time offset in minutes. 
     * 
     * Added to the timestamp of the invoice 
     * during the conversion from staging to legacy entity. 
     */
    private Integer timeOffset;
    
    /**
     * Timezone of the POS software which is the Windows
     * timezone of the OS running the client
     */
    @RetailProAttr(value = "PosTimezone", maxLength = 255)
    private String posTimezone;
    
    /**
     * Notes sent by the Retail Pro client, e.g. RetailPro8
     * sends to city in which the store is operating:
     * E.g. <code> Sunrise, FL 33323</code>
     */
    @RetailProAttr(value = "Notes", maxLength = 255)
    private String notes;
    
    /**
     * Time of creating for the store
     */
    private Date created;
    
    /**
     * Store's state
     */
    private RpStoreState state = RpStoreState.NORMAL;
        
    // ------------------------------------------------------------------------
    // ~ Commom methods
    // ------------------------------------------------------------------------
    @Override
    public String toString() {
        return "RpStoreEntity{" + "id=" + id + ", storeId=" + storeId + ", sbsNumber=" 
                + sbsNumber + ", storeNumber=" + storeNumber + ", swarmId=" 
                + swarmId + ", storeName=" + storeName + '}';
    }        
    
    // ------------------------------------------------------------------------
    // ~ Getters / setters
    // ------------------------------------------------------------------------
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "store_id")
    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    @Column(name = "ls_sbs_no")
    public String getSbsNumber() {
        return sbsNumber;
    }

    public void setSbsNumber(String sbsNumber) {
        this.sbsNumber = sbsNumber;
    }

    
    @Column(name = "ls_store_no")
    public String getStoreNumber() {
        return storeNumber;
    }

    public void setStoreNumber(String storeNumber) {
        this.storeNumber = storeNumber;
    }

    @Column(name = "swarm_id")
    public String getSwarmId() {
        return swarmId;
    }

    public void setSwarmId(String swarmId) {
        this.swarmId = swarmId;
    }

    @Column(name = "store_name")
    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    
    @Column(name = "timezone")
    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Column(name = "time_offset")
    public Integer getTimeOffset() {
        return timeOffset;
    }

    /**
     * 
     * @param offset offset in minutes
     */
    public void setTimeOffset(Integer offset) {
        this.timeOffset = offset;
    }

    @Column(name = "pos_software")
    public String getPosSoftware() {
        return posSoftware;
    }

    public void setPosSoftware(String posSoftware) {
        this.posSoftware = posSoftware;
    }

    /**
     * @return the posTimezone
     */
    @Column(name = "pos_timezone")
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
    @Column(name = "notes")
    public String getNotes() {
        return notes;
    }

    /**
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    public RpStoreState getState() {
        return state;
    }

    public void setState(RpStoreState state) {
        this.state = state;
    }

    /**
     * Possible store state for Retail Pro
     */
    public enum RpStoreState {
        NORMAL, IGNORED
    };
}
