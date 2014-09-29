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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.model.staging.retailpro.RetailProAttr;

/**
 * WRONG-DESIGN!
 * This is a RetailPro related class, it should be in the RetailPro module.
 * 
 * Date converter which converts the value from the map to date.
 * 
 * The converter expects a parameter which gives the pattern 
 * for the date conversion.
 * 
 * It returns with null if the key does not exists in the map.
 *
 * @author joe
 */
@Component
public class DatePropertyConverter extends BaseDatePropertyConverter implements RetailProConverter<String>{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DatePropertyConverter.class);
    
    /**
     * Maps a Retail Pro data value (yyyy-MM-dd) to a date property,
     * which will be passed as a Long (millisecond timestamp)
     */
    @Override
    public String getValueFromMap(Map<String, Object> map, RetailProAttr attrAnnotation) {
        String key = attrAnnotation.value();
        String[] params = attrAnnotation.params();
                
        Object object = map.get(key);
        String textValue = null;
        
        if (object instanceof String){
            textValue = (String)object;
        } else{
            LOGGER.warn("This value can not be converted to Date : " + object);
            return null;
        }
        
        return Long.toString(convertToDate(textValue, params).getTime());
    }
    
}
