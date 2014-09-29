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
package com.sonrisa.swarm.model.staging;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sonrisa.swarm.model.staging.annotation.StageInsertableAttr;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableType;

@Entity
@Table(name = "staging_manufacturers")
@StageInsertableType(dbTableName = "manufacturers", storeIdColumnName = "store_id")
public class ManufacturerStage extends BaseStageEntity {

    private static final long serialVersionUID = 1879237424116749366L;

    public static final String TABLE_NAME = "staging_manufacturers";
    
    private String swarmId;
    
    private Long storeId;    
    
    private Long id;
    
    /**
     * Primary key in the remote system
     */
    private String manufacturerId;
    
    /**
     *  The name/description of the manufacturer.
     */
    private String manufacturerName;
    
    /**
     * Date/time the record was last modified
     */
    private String lastModified;
    
    
    @StageInsertableAttr(dbColumnName="swarm_id")
    @Column(name = "swarm_id")
    public String getSwarmId() {
        return this.swarmId;
    }

    @Override
    public void setSwarmId(String swarmId) {
        this.swarmId = swarmId;
    }

    @StageInsertableAttr(dbColumnName="store_id")
    @Column(name = "store_id")
    @Override
    public Long getStoreId() {
        return this.storeId;
    }

    @Override
    public String getLsStoreNo() {
        return null;
    }

    @Override
    public String getLsSbsNo() {
        return null;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Override
    public Long getId() {
        return this.id;
    }
    
    @StageInsertableAttr(dbColumnName="ls_manufacturer_id")
    @Column(name = "ls_manufacturer_id")
    public String getManufacturerId() {
        return manufacturerId;
    }
    
    @StageInsertableAttr(dbColumnName="name")
    @Column(name = "name")
    public String getManufacturerName() {
        return manufacturerName;
    }
    
    @StageInsertableAttr(dbColumnName="last_modified")
    @Column(name = "last_modified")
    public String getLastModified() {
        return lastModified;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ManufacturerStage [swarmId=" + swarmId + ", storeId=" + storeId
                + ", id=" + id + ", manufacturerId=" + manufacturerId
                + ", manufacturerName=" + manufacturerName + ", lastModified="
                + lastModified + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((manufacturerId == null) ? 0 : manufacturerId.hashCode());
        result = prime * result + ((storeId == null) ? 0 : storeId.hashCode());
        result = prime * result + ((swarmId == null) ? 0 : swarmId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        
        if (obj == null){
            return false;
        }
        
        if (getClass() != obj.getClass()){
            return false;
        }
        
        ManufacturerStage other = (ManufacturerStage) obj;
        if (manufacturerId == null) {
            if (other.manufacturerId != null){
                return false;
            }
        } else if (!manufacturerId.equals(other.manufacturerId)){
            return false;
        }
        
        if (storeId == null) {
            if (other.storeId != null){
                return false;
            }
        } else if (!storeId.equals(other.storeId)){
            return false;
        }
        
        if (swarmId == null) {
            if (other.swarmId != null){
                return false;
            }
        } else if (!swarmId.equals(other.swarmId)){
            return false;
        }
        return true;
    }
}
