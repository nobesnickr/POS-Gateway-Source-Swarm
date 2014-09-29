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
package com.sonrisa.swarm.posintegration.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.model.staging.annotation.StageInsertableAttr;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableType;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;

/**
 * Products have categories that group them together in a tree structure
 */
@StageInsertableType(dbTableName = "categories")
public abstract class CategoryDTO implements DWTransferable {

    /** Get the foreign id of the category */
    @StageInsertableAttr(dbColumnName = "ls_category_id")
	public abstract long getRemoteId();
	
	/** Get modification date of the category */
    @StageInsertableAttr(dbColumnName = "last_modified", usedAsTimestamp = true)
	public abstract Timestamp getLastModified();
	
	/** Get name of category */
    @StageInsertableAttr(dbColumnName="name", maxLength = 100)
	public abstract String getCategoryName();
	
	/** Returns parent category (or null if root) */
    @StageInsertableAttr(dbColumnName="parent_id")
	public abstract Long getParentCategory();

    @StageInsertableAttr(dbColumnName="ls_lft")
	public abstract Long getLeftCategory();

    @StageInsertableAttr(dbColumnName="ls_rgt")
	public abstract Long getRightCategory();

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CategoryDTO [getCategoryId()=" + getRemoteId()
                + ", getLastModified()=" + getLastModified()
                + ", getCategoryName()=" + getCategoryName()
                + ", getParentCategory()=" + getParentCategory()
                + ", getLeftCategory()=" + getLeftCategory()
                + ", getRightCategory()=" + getRightCategory() + "]";
    }
}
