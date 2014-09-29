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
 * This entity stores the timestamp of the last time 
 * we received a heartbeat from a client and the version
 * of the RetailPro plugin.
 *
 * @author joe
 */
@Table(name = "retailpro_plugin")
@Entity
public class RpPluginEntity extends BaseSwarmEntity {
    
    /**
     * Primary key int the DB.
     */
    private Long id;
    
    /**
     * The swarmId (see: {@link EntityWithSwatmId} identifies a RetailPro
     * installation and it has to be set in the RetailPro Swarm Client during
     * the installation.
     */
    private String swarmId;
    
    /**
     * Date of the last received heartbeat from this client.
     */
    private Date heartbeat;
    
    /**
     * Version of the installed RetailPro plugin at this client.
     */
    private String pluginVersion;
    
    // ------------------------------------------------------------------------
    // ~ Constructors
    // ------------------------------------------------------------------------
    
    public RpPluginEntity() {
    }        

    public RpPluginEntity(String swarmId) {
        this.swarmId = swarmId;
    }
    
    // ------------------------------------------------------------------------
    // ~ Setters / getters
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
    
    @Column(name = "heartbeat")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(Date heartbeat) {
        this.heartbeat = heartbeat;
    }

    @Column(name = "plugin_version")
    public String getPluginVersion() {
        return pluginVersion;
    }

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

}
