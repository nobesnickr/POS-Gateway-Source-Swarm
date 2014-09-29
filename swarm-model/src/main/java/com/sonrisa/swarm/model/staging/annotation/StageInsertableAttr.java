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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to mark fields on a swarm entity
 * and to define how its value is mapped to a stage table's column.
 *
 * @author joe
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StageInsertableAttr {
	
	/** Db column name in our database. We need this because we only use columns in the insert script which has this value. Required field. */
    String dbColumnName();
 
    /**
     * Is this field used as a timestamp variable to filter this kind of object in the stage table?
     */
    boolean usedAsTimestamp() default false;
    
    /**
     * Is this field used as a timestamp variable to filter this kind of object in the stage table?
     */
    boolean usedAsRemoteId() default false;
    
    /**
     * Values longer then this will be trimmed to the appropriate value
     */
    int maxLength() default 50;
}
