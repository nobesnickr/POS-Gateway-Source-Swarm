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

import com.sonrisa.swarm.model.staging.retailpro.RetailProAttr;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * WRONG-DESIGN!
 * This is a RetailPro related class, it should be in the RetailPro module.
 * 
 * Converter which creates a concatenated string from several values from the JSON map.
 * 
 *
 * @author joe
 */
@Component
public class FieldConcatenationConverter implements RetailProConverter<String>{

    /**
     * 
     * @param map
     * @param delimiter this value is used as a delimiter between the concatenated values
     * @param keysToAppend the values of these key will be contatenated
     * @return 
     */
    @Override
    public String getValueFromMap(Map<String, Object> content, RetailProAttr attrAnnotation) {
        String result = null;
        
        StringBuilder strBuilder = new StringBuilder();
        String delimiter = attrAnnotation.value();
        String[] keysToAppend = attrAnnotation.params();
        final int maxLength = attrAnnotation.maxLength();
        
        if (keysToAppend != null){
         
            // it iterates over the keys and concatenates the values belonged to the keys
            final Iterator<String> paramIterator = Arrays.asList(keysToAppend).iterator();
            while(paramIterator.hasNext()){
                final String keyToAppend = paramIterator.next();
                if (content.containsKey(keyToAppend)){
                    strBuilder.append(content.get(keyToAppend));
                    
                    if (paramIterator.hasNext()){
                        // appending of the delimiter
                        strBuilder.append(delimiter);                    
                    }
                }
            }
            
            String valueStr = strBuilder.toString();
            
            if(attrAnnotation.truncatingAllowed()){
                result = StringUtils.substring(valueStr, 0, maxLength);
            } else {
                result = valueStr.length() <= maxLength ? valueStr : null;
            }
        }
        return result;   
    }
}
