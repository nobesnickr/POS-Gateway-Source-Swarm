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
package com.sonrisa.swarm.model.legacy;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;


/**
 * Manufacturer entity.
 */
@Entity
@Table(name = ManufacturerEntity.TABLE_NAME)
public class ManufacturerEntity extends BaseLegacyEntity {
    private static final long serialVersionUID = 5007377037953418526L;

    /** Name of the DB table. */
    public static final String TABLE_NAME = "manufacturers";
    
    /** Local id of the category */
    private Long id;

    /**
     *  The name/description of the manufacturer.
     */
    private String manufacturerName;
    
    /**
     * Date/time the record was last modified
     */
    private Date lastModified;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manufacturer_id")
    @Override        
    public Long getId() {
        return id;
    }
    
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    @Override
    public StoreEntity getStore() {
        return super.getStore();
    }
    
    @Column(name = "ls_manufacturer_id")
    @Min(0)
    @Max((1 << 20) - 1) // Bigint(20)
    public Long getManufacturerId() {
        return getLegacySystemId();
    }

    @Column(name = "name")
    @Size(max=100)
    public String getManufacturerName() {
        return manufacturerName;
    }

    @Column(name = "last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastModified() {
        return lastModified;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setManufacturerId(Long manufacturerId) {
        setLegacySystemId(manufacturerId);
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
