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
package com.sonrisa.swarm.common.util;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author joe
 */
public abstract class JsonUtil {
    
    /**
     * Returns the text representation of the given JSON field if any.
     * 
     * It also handles the multi level paths.
     * E.g.:
     * If the JSON looks like:
     * <pre>
     * {
     *   hello : "world",
     *   foo : {
     *      bar : "john" 
     *   }
     * }
     * </pre>
     * And the fieldName is: "foo.bar"
     * The result will be: "john".
     * 
     * <p/>
     * If the json is null or the field doesn't exist empty string will be returned.
     * 
     * @param json
     * @param fieldName
     * @return 
     */
    public static String getJsonField(final JsonNode json, final String fieldName){
        String result = "";
        
        if (fieldName != null && json != null){
            JsonNode jsonToIter = json;
            String[] fieldList = fieldName.split("\\.");
        
            for (String key: fieldList){
                if (jsonToIter.has(key)){
                    jsonToIter = jsonToIter.get(key);
                }
            }
            
            result = jsonToIter.asText();
        }
        return result;
    }
    
    
}
