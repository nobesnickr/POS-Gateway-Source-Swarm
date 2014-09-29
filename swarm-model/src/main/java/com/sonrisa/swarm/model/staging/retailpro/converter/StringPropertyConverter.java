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
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * WRONG-DESIGN!
 * This is a RetailPro related class, it should be in the RetailPro module.
 * 
 * Default converter which simply returns the value from the map
 * identified by the given key.
 * 
 * It returns with null if the key does not exists in the map.
 *
 * @author joe
 */
@Component
public class StringPropertyConverter implements RetailProConverter<Object>{

    @Override
    public Object getValueFromMap(Map<String, Object> map, RetailProAttr attrAnnotation) {
        String result = null;
        
        final String key = attrAnnotation.value();
        final int maxLength = attrAnnotation.maxLength();
        Object value = map.get(key);
                
        if (value != null ){            
            String valueStr = value.toString();
            
            if(attrAnnotation.truncatingAllowed()){
                result = StringUtils.substring(valueStr, 0, maxLength);
            } else {
                result = valueStr.length() <= maxLength ? valueStr : null;
            }
        }
        
        return  result;
    }
    
    
}
