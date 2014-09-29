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

/**
 * This entity represents a record in the staging_categories table.
 * @author Barna
 */
@Entity
@Table(name = "staging_categories")
@StageInsertableType(dbTableName = "categories", storeIdColumnName = "store_id")
public class CategoryStage extends BaseStageEntity {

    private static final long serialVersionUID = 5016385825707446908L;

    /** Private key in the staging_categories table */
    private Long id;
    
    /** Private key in the legacy system */
    private String lsCategoryId;

    /** Swarm id of the instance, normally null as there are no categories for Retail Pro*/
    private String swarmId;
    
    /** Store id for the instance, normally not null, as swarmId isn't provided */
    private Long storeId;    
    
    /** timeStamp of the category */
    private String lastModified;
    
    /** Name of the category */
    private String name;
    
    /** Parent of the category, null if root */
    private String lsParentCategoryId;
    
    /** Category to the left */
    private String lsReftCategoryId = null;

    /** Category to the right */
    private String lsRightCategoryId = null;
    
    // ------------------------------------------------------------------------
    // ~ Getters / setters
    // ------------------------------------------------------------------------    
    
    @StageInsertableAttr(dbColumnName="swarm_id")
    @Column(name = "swarm_id")
    @Override
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
    
    @StageInsertableAttr(dbColumnName="ls_category_id")
    @Column(name = "ls_category_id")
    public String getLsCategoryId(){
        return this.lsCategoryId;
    }

    @StageInsertableAttr(dbColumnName="last_modified")
    @Column(name = "last_modified")
    public String getLastModified() {
        return lastModified;
    }

    @StageInsertableAttr(dbColumnName="name")
    @Column(name = "name")
    public String getName() {
        return name;
    }

    @StageInsertableAttr(dbColumnName="parent_id")
    @Column(name = "parent_id")
    public String getLsParentCategoryId() {
        return lsParentCategoryId;
    }

    @StageInsertableAttr(dbColumnName="ls_lft")
    @Column(name = "ls_lft")
    public String getLsLeftCategoryId() {
        return lsReftCategoryId;
    }

    @StageInsertableAttr(dbColumnName="ls_rgt")
    @Column(name = "ls_rgt")
    public String getLsRightCategoryId() {
        return lsRightCategoryId;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLsParentCategoryId(String parent) {
        this.lsParentCategoryId = parent;
    }

    public void setLsLeftCategoryId(String leftCategory) {
        this.lsReftCategoryId = leftCategory;
    }

    public void setLsRightCategoryId(String rightCategory) {
        this.lsRightCategoryId = rightCategory;
    }

    public void setLsCategoryId(String categoryId) {
        this.lsCategoryId = categoryId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // ------------------------------------------------------------------------
    // ~ Object methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "CategoryStage [id=" + id + ", lsCategoryId=" + lsCategoryId + ", storeId=" + storeId
                + ", lastModified=" + lastModified + ", name=" + name + ", lsParentId=" + lsParentCategoryId
                + ", lsReftCategoryId=" + lsReftCategoryId + ", lsRightCategoryId=" + lsRightCategoryId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lsCategoryId == null) ? 0 : lsCategoryId.hashCode());
        result = prime * result + ((storeId == null) ? 0 : storeId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CategoryStage other = (CategoryStage) obj;
        if (lsCategoryId == null) {
            if (other.lsCategoryId != null)
                return false;
        } else if (!lsCategoryId.equals(other.lsCategoryId))
            return false;
        if (storeId == null) {
            if (other.storeId != null)
                return false;
        } else if (!storeId.equals(other.storeId))
            return false;
        return true;
    }
}
