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
package com.sonrisa.swarm.mos.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.CategoryDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;

/**
 * Product category of Merchant OS
 */
public class MosCategoryDTO extends CategoryDTO {
    /** Id of the category */
    private long categoryId;
    
    /** timeStamp of the category */
    private Timestamp lastModified;
    
    /** Name of the category */
    private String name;
    
    /** Parent of the category, null if root */
    private Long parent = null;
    
    /** Category to the left (??? whatever it means) */
    
    private Long leftCategory = null;

    /** Category to the right (??? whatever it means) */
    private Long rightCategory = null;

    /**
     * @return the categoryId
     */
    @Override
    public long getRemoteId() {
        return categoryId;
    }

    /**
     * @return the lastModified
     */
    @Override
    public Timestamp getLastModified() {
        return lastModified;
    }

    /**
     * @return the name
     */
    @Override
    public String getCategoryName() {
        return name;
    }

    /**
     * @return the parent
     */
    @Override
    public Long getParentCategory() {
        return parent;
    }

    /**
     * @return the leftCategory
     */
    @Override
    public Long getLeftCategory() {
        return leftCategory;
    }

    /**
     * @return the rightCategory
     */
    @Override
    public Long getRightCategory() {
        return rightCategory;
    }

    /**
     * @param categoryId the categoryId to set
     */
    @ExternalField(value = "categoryID", required = true)
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @param lastModifiedString ISO8061 Date string
     */
    @ExternalField("timeStamp")
    public void setLastModified(String lastModifiedString) {
        this.lastModified = new Timestamp(ISO8061DateTimeConverter.stringToDate(lastModifiedString).getTime());
    }

    /**
     * @param name the name to set
     */
    @ExternalField("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param parent the parent to set
     */
    @ExternalField("parent")
    public void setParent(Long parent) {
        this.parent = parent;
    }

    /**
     * @param leftCategory the leftCategory to set
     */
    @ExternalField("leftNode")
    public void setLeftCategory(Long leftCategory) {
        this.leftCategory = leftCategory;
    }

    /**
     * @param rightCategory the rightCategory to set
     */
    @ExternalField("rightNode")
    public void setRightCategory(Long rightCategory) {
        this.rightCategory = rightCategory;
    }
}
