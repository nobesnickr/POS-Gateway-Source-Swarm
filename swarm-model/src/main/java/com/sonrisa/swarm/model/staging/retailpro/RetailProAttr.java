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
package com.sonrisa.swarm.model.staging.retailpro;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.sonrisa.swarm.model.staging.retailpro.converter.RetailProConverter;
import com.sonrisa.swarm.model.staging.retailpro.converter.StringPropertyConverter;

/**
 * WRONG-DESIGN!
 * This is a RetailPro related interface, it should be in the RetailPro module.
 * 
 * 
 * This annotation can be used to mark fields on a swarm entity
 * and to define how its value is derived from the JSON map.
 *
 * @author joe
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RetailProAttr {
    
    /** Name of the field in the JSON map received from the RetailPro. */
    String value() default "";
    
    /** Custom converter class. */
    Class<? extends RetailProConverter> converter() default StringPropertyConverter.class;
    
    /** 
     * Parameters which will be passed to the converter. 
     * The converter implementations could use these parameters to their own discretion.
     */
    String[] params() default {};
    
    /**
     * The maximum length allowed to be written into stage for the given RetailPro attribute
     */
    int maxLength() default 50;
    
    /**
     * Value indicating what happens if {@link #maxLength()} is exceeded
     * 
     * If <i>false</i>, it will not truncate the value, but set it to <i>null</i>
     */
    boolean truncatingAllowed() default true;
    
    /**
     * Fields in relation with the RetailPro attribute, e.g. DocumentTime for DocumentDate
     */
    String[] relatedFields() default {};
    
}
