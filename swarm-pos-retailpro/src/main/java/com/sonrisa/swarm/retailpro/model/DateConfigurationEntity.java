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
 * This entity encapsulates information about a forced remote date reconfiguration request
 * sent to all or certain Retail Pro V8 plugin installation.
 * 
 * For each swarmId the most recent configuration entity is retrieved from the 
 * database and sent to the client when requested.
 * 
 * @author barna 
 */
@Table(name="retailpro_configuration")
@Entity
public class DateConfigurationEntity extends BaseSwarmEntity {
    
    /**
     * Serial version
     */
    private static final long serialVersionUID = -6006485137377373320L;

    /** Primary key int the DB. */
    private Long id;
    
    /**
     * The swarmId identifies a RetailPro
     * installation and it has to be set in the RetailPro Swarm Client during
     * the installation.
     * 
     * If swarmId is '*', then this remote configuration request is intented for 
     * all installation.
     */
    private String swarmId;
    
    /**
     * Version is a server timestamp the .NET client uses the identify already
     * executed commands.
     */
    private Date timeStampVersion = new Date(0);

    /**
     * The assumed to be last invoice's modification date, by changing this all
     * invoices since a certain date can be forced to be resent
     * 
     * If value is null, the it is not included into the JSON, and remote .NET plugin ignores it.
     */
    private Date lastModifiedInvoiceDate = null;

    /**
     * The assumed to be last store's modification date, by changing this all
     * StoreNumber/SbsNumber since a certain date can be forced to be resent.
     * 
     * If value is null, the it is not included into the JSON, and remote .NET plugin ignores it.
     */
    private Date lastModifiedStoreDate = null;

    /**
     * Currently not used by the RpoV8 .NET plugin
     * 
     * If value is null, the it is not included into the JSON, and remote .NET plugin ignores it.
     */
    private Date lastModifiedVersionDate = null;
    
    /**
     * Author of the request, member of Sonrisa developer team
     */
    private String author = "";
    
    /**
     * Notes describing why the request is being made.
     */
    private String comment = "No configuration data";
    
    /** JPA uses this no-arg constructor. */
    public DateConfigurationEntity() {
    }

    // ------------------------------------------------------------------------
    // ~ Commom methods
    // ------------------------------------------------------------------------
    @Override
    public String toString() {
        return "DateConfigurationEntity [id=" + id + ", swarmId=" + swarmId
                + ", timeStampVersion=" + timeStampVersion
                + ", lastModifiedInvoiceDate=" + lastModifiedInvoiceDate
                + ", lastModifiedStoreDate=" + lastModifiedStoreDate
                + ", lastModifiedVersionDate=" + lastModifiedVersionDate + ", author="
                + author + ", comment=" + comment + "]";
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

    @Column(name = "swarm_id")
    public String getSwarmId() {
        return swarmId;
    }

    @Column(name = "ts")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTimeStampVersion() {
        return timeStampVersion;
    }


    @Column(name = "invoice_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastModifiedInvoiceDate() {
        return lastModifiedInvoiceDate;
    }

    @Column(name = "store_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastModifiedStoreDate() {
        return lastModifiedStoreDate;
    }

    @Column(name = "version_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastModifiedVersionDate() {
        return lastModifiedVersionDate;
    }

    @Column(name = "author")
    public String getAuthor() {
        return author;
    }


    @Column(name = "comment")
    public String getComment() {
        return comment;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSwarmId(String swarmId) {
        this.swarmId = swarmId;
    }

    public void setTimeStampVersion(Date timeStampVersion) {
        this.timeStampVersion = timeStampVersion;
    }

    public void setLastModifiedInvoiceDate(Date lastModifiedInvoiceDate) {
        this.lastModifiedInvoiceDate = lastModifiedInvoiceDate;
    }

    public void setLastModifiedStoreDate(Date lastModifiedStoreDate) {
        this.lastModifiedStoreDate = lastModifiedStoreDate;
    }

    public void setLastModifiedVersionDate(Date lastModifiedVersion) {
        this.lastModifiedVersionDate = lastModifiedVersion;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
