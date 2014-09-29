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
package com.sonrisa.swarm.model.staging.retailpro.converter;

import java.util.Map;

import com.sonrisa.swarm.model.staging.retailpro.RetailProAttr;

/**
 * WRONG-DESIGN!
 * This is a RetailPro related interface, it should be in the RetailPro module.
 * 
 * Interface of the converter classes which are responsible for converting
 * the JSON map elements to swarm entity fields.
 *
 * @author joe
 */
public interface RetailProConverter<T> {
    
    /** 
     * It gets a value from the json map.
     * It can use the key and the params to produce the value.
     * (The returned object can be a calculated value.)
     */
    T getValueFromMap(Map<String, Object> map, RetailProAttr attrAnnotation);
    
}
