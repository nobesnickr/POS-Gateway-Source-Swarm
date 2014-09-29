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
 * Customer entity. It represents a record from the "categories" table in the legacy DB.
 */
@Entity
@Table(name = CategoryEntity.TABLE_NAME)
public class CategoryEntity extends BaseLegacyEntity {

    private static final long serialVersionUID = -1475274588788221256L;

    /** Name of the DB table. */
    public static final String TABLE_NAME = "categories";
    
    /** Local id of the category */
    private Long id;
    
    /** timeStamp of the category */
    private Date lastModified;
    
    /** Name of the category */
    private String name;
    
    /** Parent of the category id, null if root */
    private Long lsParentCategoryId;
    
    /** Parent of the category id, null if root */
    private Long lsLeftCategoryId;

    /** Parent of the category id, null if root */
    private Long lsRightCategoryId;
    
    // ------------------------------------------------------------------------
    // ~ Getters / setters
    // ------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    @Override
    public Long getId() {
        return id;
    }

    @Column(name = "ls_category_id")
    public Long getLsCategoryId() {
        return getLegacySystemId();
    }
    
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    @Override
    public StoreEntity getStore() {
        return super.getStore();
    }

    @Column(name = "last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastModified() {
        return lastModified;
    }

    @Column(name = "name")
    @Size(max=100)
    public String getName() {
        return name;
    }

    @Column(name = "parent_id")
    public Long getLsParentCategoryId() {
        return lsParentCategoryId;
    }

    @Column(name = "lft")
    @Min(0) // Unsigned
    @Max((1 << 20) - 1) // Bigint(20)
    public Long getLsLeftCategoryId() {
        return lsLeftCategoryId;
    }

    @Column(name = "rgt")
    @Min(0) // Unsigned
    @Max((1 << 20) - 1) // Bigint(20)
    public Long getLsRightCategoryId() {
        return lsRightCategoryId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLsCategoryId(Long categoryId) {
        setLegacySystemId(categoryId);
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLsParentCategoryId(Long lsParentCategoryId) {
        this.lsParentCategoryId = lsParentCategoryId;
    }

    public void setLsLeftCategoryId(Long lsLeftCategoryId) {
        this.lsLeftCategoryId = lsLeftCategoryId;
    }

    public void setLsRightCategoryId(Long lsRightCategoryId) {
        this.lsRightCategoryId = lsRightCategoryId;
    }
}
