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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.sonrisa.swarm.model.BaseSwarmEntity;

/**
 * This entity encapsulates information about a RetailPro client or 
 * aka RetailPro installation.
 * 
 * With this object we can store the basic information 
 * about our RetailPro clients e.g.: their versions.
 *  
 * 
 * @author joe
 */
@Table(name = "retailpro_client")
@Entity
public class RpClientEntity extends BaseSwarmEntity {

	/** Primary key int the DB. */
    private Long id;
    
    /**
     * The swarmId (see: {@link EntityWithSwatmId} identifies a RetailPro
     * installation and it has to be set 
     * in the RetailPro Swarm Client during the installation.
     */    
    private String swarmId;    
    
    /**
     * TODO: comment me!
     */
    private String componentId;
    
    /**
     * TODO: comment me!
     */
    private String componentType;
    
    /**
     * Version of the RetailPro installation.
     */
    private String rpVersion;
    
    /**
     * TODO: comment me!
     */
    private String comments;
    
    /** Date of the installation of the RetailPro. */
    private Date installDate;
    
    /** Date of the creation of this RetailPro client entity in our DB. */
    private Date createdAt = new Date();
    
    /** Date of the last modification of this RetailPro client entity in our DB. */
    private Date modifiedAt = new Date();
    
    // ------------------------------------------------------------------------
    // ~ Constructor
    // ------------------------------------------------------------------------    
    
    /** JPA uses this no-arg constructor. */
    protected RpClientEntity() {
    }
            
    public RpClientEntity(String swarmId, String componentId) {
        this.swarmId = swarmId; 
        this.componentId = componentId;
    }   
    
    // ------------------------------------------------------------------------
    // ~ Commom methods
    // ------------------------------------------------------------------------
    @Override
    public String toString() {
        return "RetailProClientEntity{" + "id=" + id + ", swarmId=" + swarmId + ", componentId=" + componentId 
                + ", componentType=" + componentType + ", rpVersion=" + rpVersion 
                + ", comments=" + comments + ", installDate=" + installDate + ", createdAt=" + createdAt 
                + ", heartbeat=" + "}";
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

    @Column(name = "swarm_id", unique = true)
    public String getSwarmId() {
        return swarmId;
    }

    public void setSwarmId(String swarmId) {
        this.swarmId = swarmId;
    }

    @Column(name = "component_id")
    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    @Column(name = "component_type")
    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    @Column(name = "rp_version")
    public String getRpVersion() {
        return rpVersion;
    }

    public void setRpVersion(String rpVersion) {
        this.rpVersion = rpVersion;
    }

    @Column(name = "comments")
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Column(name = "install_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getInstallDate() {
        return installDate;
    }

    public void setInstallDate(Date installDate) {
        this.installDate = installDate;
    }

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }        

    @Column(name = "modified_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
