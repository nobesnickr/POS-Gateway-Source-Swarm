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
package com.sonrisa.swarm.revel.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.CategoryDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;

/**
 * Category from the Revel systems (ProductCategory)
 */
public class RevelCategoryDTO extends CategoryDTO{

    /** Id of the category */
    private long categoryId;
    
    /** timeStamp of the category */
    private Timestamp lastModified = new Timestamp(0L);
    
    /** Name of the category */
    private String name = "";
    
    /**
     * @return the categoryId
     */
    @Override
    public long getRemoteId() {
        return categoryId;
    }

    /**
     * Get the timestamp of the last modification if the entry
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
     * Returns null, as Revel's category hierarchy is not handled
     * 
     * @return the parent
     */
    @Override
    public Long getParentCategory() {
        return null;
    }

    /**
     * Returns null, as Revel's category hierarchy is not handled
     * 
     * @return the left category
     */
    @Override
    public Long getLeftCategory() {
        return null;
    }

    /**
     * Returns null, as Revel's category hierarchy is not handled
     * 
     * @return the category to the right
     */
    @Override
    public Long getRightCategory() {
        return null;
    }
    
    /**
     * @param categoryId the categoryId to set
     */
    @ExternalField(value = "id", required = true)
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @param lastModified the lastModified to set
     */
    @ExternalField("updated_date")
    public void setLastModified(String lastModified) {
        this.lastModified = new Timestamp(ISO8061DateTimeConverter.stringToDate(lastModified).getTime());
    }

    /**
     * @param name the name to set
     */
    @ExternalField(value="name")
    public void setName(String name) {
        this.name = name;
    }
}
