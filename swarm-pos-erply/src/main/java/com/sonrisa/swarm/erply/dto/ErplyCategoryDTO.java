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
package com.sonrisa.swarm.erply.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.CategoryDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;

/**
 * Product category of Erply
 */
public class ErplyCategoryDTO extends CategoryDTO {

    /** Id of the category */
    private long categoryId;
    
    /** timeStamp of the category */
    private long lastModified;
    
    /** Name of the category */
    private String name = "";
    
    /** Parent of the category, null if root */
    private Long parent = null;
    
    /** Category to the left (??? whatever it means) */
    
    private Long leftCategory = null;

    /** Category to the right (??? whatever it means) */
    private Long rightCategory = null;

    /**
     * @return the categoryId
     */
    public long getRemoteId() {
        return categoryId;
    }

    /**
     * Get the timestamp of the last modification if the entry
     * 
     * This timestamp is calculated using the remote Unix timestamp of
     * Erply by multiplying it with 1000L, as java.sql.Timestamp expects
     * a timestamp with milliseconds
     * 
     * @return the lastModified
     */
    @Override
    public Timestamp getLastModified() {
        return new Timestamp(this.lastModified * 1000L);
    }

    /**
     * @return the name
     */
    public String getCategoryName() {
        return name;
    }

    /**
     * @return the parent
     */
    public Long getParentCategory() {
        return parent;
    }

    /**
     * @return the leftCategory
     */
    public Long getLeftCategory() {
        return leftCategory;
    }

    /**
     * @return the rightCategory
     */
    public Long getRightCategory() {
        return rightCategory;
    }

    /**
     * @param categoryId the categoryId to set
     */
    @ExternalField(value = "productCategoryID", required = true)
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @param lastModified the lastModified to set
     */
    @ExternalField("lastModified")
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * @param name the name to set
     */
    @ExternalField(value="productCategoryName")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Long parent) {
        this.parent = parent;
    }

    /**
     * @param leftCategory the leftCategory to set
     */
    public void setLeftCategory(Long leftCategory) {
        this.leftCategory = leftCategory;
    }

    /**
     * @param rightCategory the rightCategory to set
     */
    public void setRightCategory(Long rightCategory) {
        this.rightCategory = rightCategory;
    }
}
