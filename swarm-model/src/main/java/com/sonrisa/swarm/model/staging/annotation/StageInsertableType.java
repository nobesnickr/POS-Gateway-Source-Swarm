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
package com.sonrisa.swarm.model.staging.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation notes that a certain object implementing the DataStoreTransferable
 * interface is inserted into which stage table.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface StageInsertableType {
    
    /**
     * The staging tables are all like {prefix}_customers, {prefix}_products, etc.
     */
	public static final String tablePrefix = "staging_";
	/**
	 * Values of doubles should be formatted to match the decimal(8,2) format
	 * @see http://dev.mysql.com/doc/refman/5.0/en/precision-math-decimal-changes.html
	 */
	public static final int doublePrecision = 2;
	public static final int doubleExponent = 6;
	
	/**
	 * The staging database table the annotated classes data will stored in is 
	 * calculated as tablePrefix + dbTableName()
	 */
	String dbTableName();
	
	
	/**
	 * The class shouldn't annotate a store_id column variable, this is passed
	 * to the StageService by the process who started the extraction. But the 
	 * name of the column that stores the storeId in the staging table has to be 
	 * defined.
	 */
	String storeIdColumnName() default "store_id";
}
