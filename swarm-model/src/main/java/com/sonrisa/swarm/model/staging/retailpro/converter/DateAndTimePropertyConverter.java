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

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.model.staging.retailpro.RetailProAttr;

import java.util.GregorianCalendar;
import org.springframework.util.StringUtils;

/**
 * WRONG-DESIGN!
 * This is a RetailPro related class, it should be in the RetailPro module.
 * 
 * Same as DatePropertyConverter, but if 
 * @author sonrisa
 *
 */
@Component
public class DateAndTimePropertyConverter extends BaseDatePropertyConverter implements RetailProConverter<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateAndTimePropertyConverter.class);    
    
    /**
     * Maps a Retail Pro data value (yyyy-MM-dd) to a date property,
     * which will be passed as a Long (millisecond timestamp)
     */
    @Override
    public String getValueFromMap(Map<String, Object> map, RetailProAttr attrAnnotation) {
        String key = attrAnnotation.value();
        String[] params = attrAnnotation.params();
                
        // Try to read the value of the date
        Object object = map.get(key);
        String textValue = null;
                
        if (object instanceof String){
            textValue = (String)object;
        } else{
            LOGGER.warn("This value can not be converted to Date : " + object);
            return null;
        }
        
        // the year, month and day values derives from this field
        final Date dateValue = convertToDate(textValue, params);                
        final Calendar retValue = Calendar.getInstance();
        retValue.setTime(dateValue);
                
                    
        // the hour, minute, second values derives from the related field
        final String relatedFieldText = getSingleRelatedField(map, attrAnnotation);
        if(StringUtils.hasLength(relatedFieldText)){
            final Date timeField = convertToDate(relatedFieldText, params);                    
            final Calendar timeFieldCal = GregorianCalendar.getInstance();
            timeFieldCal.setTime(timeField);
                            
            retValue.set(Calendar.HOUR_OF_DAY, timeFieldCal.get(Calendar.HOUR_OF_DAY));
            retValue.set(Calendar.MINUTE, timeFieldCal.get(Calendar.MINUTE));
            retValue.set(Calendar.SECOND, timeFieldCal.get(Calendar.SECOND));
        }
        
        LOGGER.debug("Converted '{}' and '{}' to {}", textValue, relatedFieldText, retValue);
        
        return Long.toString(retValue.getTimeInMillis());
    }
    
    /**
     * Read related field from map using the RetailPro attribute annotation
     * @param map Map of the data received from the plugin
     * @param attrAnnotation Annotation on the field
     * @return Value of the related field
     */
    private String getSingleRelatedField(Map<String, Object> map, RetailProAttr attrAnnotation){
        String[] relatedFields = attrAnnotation.relatedFields();

        if(relatedFields != null && relatedFields.length > 0){
            if(relatedFields.length == 1){
                // Retail Pro V9 doesn't have a related field for ModifiedDate
                if(map.containsKey(relatedFields[0])){
                    Object relatedFieldObject = map.get(relatedFields[0]);               
                    if (relatedFieldObject instanceof String){
                        return (String)relatedFieldObject;
                    } else {
                        LOGGER.warn("This related field's value can not be converted to Date: " + relatedFieldObject);
                        return null;
                    }
                }
            } else {
                LOGGER.warn("Invalid annotation {}, relatedFields should have either 0 or 1 values for DateAndTimeProperty", attrAnnotation);
            }
        }
        
        return "";
    }
}
