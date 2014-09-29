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
package com.sonrisa.swarm.model.mapper.converter;

import com.sonrisa.swarm.model.staging.retailpro.converter.DateAndTimePropertyConverter;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.model.staging.retailpro.RetailProAttr;

/**
 * Class testing the {@link DateAndTimePropertyConverter} converter.
 */
public class DateAndTimePropertyConverterTest extends BaseIntegrationTest{
    
    /**
     * Object being tested
     */
    @Autowired
    DateAndTimePropertyConverter converter;

    /**
     * Test case: CreatedDate and CreatedTime is both recieved
     * 
     * Expected: The date of CreatedDate and the time of CreatedTime is used
     * during the conversion 
     * 
     * @throws NumberFormatException
     * @throws ParseException
     */
    @Test
    public void testRetailProV8DateAndTimeIsRecieved() throws NumberFormatException, ParseException{
        
        // creation of a json object
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        
        final String createdDate = "11/7/2013";
        final String createdTime = "2:36:07 PM";
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a",Locale.US);
        
        jsonMap.put("CreatedDate", createdDate + " 12:00:00 AM");
        jsonMap.put("CreatedTime", "1899.12.30 " + createdTime);
        
        final String convertedValue = converter.getValueFromMap(jsonMap, getExampleClassAnnotation("CreatedDate"));
                
        assertEquals(dateFormat.parse(createdDate + " " + createdTime), new Date(Long.parseLong(convertedValue)));
    }
    
    
    /**
     * Test case: Only the CreatedDate is recieved
     * 
     * Expected: The date of CreatedDate and the time of CreatedDate is used
     * during the conversion 
     * 
     * @throws NumberFormatException
     * @throws ParseException
     */
    @Test
    public void testRetailProV9OnlyDateIsRecieved() throws NumberFormatException, ParseException{
        
        // creation of a json object
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        
        final String createdDate = "2013-09-07 23:30:51";
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
        
        jsonMap.put("CreatedDate", createdDate);
        
        final String convertedValue = converter.getValueFromMap(jsonMap, getExampleClassAnnotation("CreatedDate"));
        assertEquals(dateFormat.parse(createdDate), new Date(Long.parseLong(convertedValue)));
    }
    
    /** 
     * Get annotation of a specific field of the {@link RpExampleEntity} class
     * @param value E.g. "CreatedDate"
     * @return
     */
    private RetailProAttr getExampleClassAnnotation(String value){
        for(Field field : RpExampleEntity.class.getDeclaredFields()){
            RetailProAttr annotation = field.getAnnotation(RetailProAttr.class);
            if(annotation.value().equals(value)){
                return annotation;
            }
        }
        return null;
    }
    
    /**
     * Annotated example class
     */
    private static class RpExampleEntity {
        
        @RetailProAttr(value = "CreatedDate", converter = DateAndTimePropertyConverter.class, relatedFields = "CreatedTime")
        private String createdDate;

        @RetailProAttr(value = "CreatedTime")
        private String createdTime;
    }
}
