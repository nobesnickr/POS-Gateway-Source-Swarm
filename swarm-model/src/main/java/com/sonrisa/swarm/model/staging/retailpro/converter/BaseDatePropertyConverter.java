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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



/**
 * WRONG-DESIGN!
 * This is a RetailPro related class, it should be in the RetailPro module.
 * 
 * 
 * Abstract superclass for DatePropertyConverter and DateAndTimeProperty converter
 */
public abstract class BaseDatePropertyConverter {

    /**
     * If DateProperty converters are used without specified format, it
     * tries to convert the date with these formats 
     */
    private static final String[] ACCEPTED_FORMATS = new String[]{
        "yyyy-MM-dd HH:mm:ss", // Erply format
        "yyyy-MM-dd'T'HH:mm:ss", // .NET
        "yyyy-MM-dd'T'HH:mm:ssz", // Atom (ISO 8601)
        "MM/dd/yyyy hh:mm:ss a", // RDA2
        "yyyy.MM.dd. hh:mm:ss a", // RDA2
        "yyyy.MM.dd hh:mm:ss a", // RDA2
        "yyyy.MM.dd. HH:mm:ss", // RDA2
        "yyyy.MM.dd HH:mm:ss", // RDA2
        "MM/dd/yyyy", // Retail Pro V9
        "yyyy.MM.dd" // Retail Pro V9
    };

    /**
     * Attempts to convert a String into a Date
     * @param textValue String value of the date
     * @param params Params of the RetailProAttr annotation containing DateFormet
     * @return new Date(0) if fails or the converted date
     */
    protected static Date convertToDate(String textValue, String[] params){
        
        Date result = null;
        if (params == null || params.length == 0){
            result = tryParseDate(textValue);
        } else {
            result = tryParseDate(textValue, params[0]);            
        }
        
        return result != null ? result : new Date(0);
    }
    
    /**
     * Try to parse date with all internal patterns
     * @param date Date to be parsed
     * @return NULL if fails, the date otherwise
     */
    protected static Date tryParseDate(String date){
        Date result = null;
        for(String pattern : ACCEPTED_FORMATS){
            result = tryParseDate(date, pattern);
            if(result != null){
                return result;
            } 
        }
        return null;
    }
    
    /**
     * Try to parse date with pattern
     * @param date Date to be parsed
     * @param pattern Pattern to be used
     * @return NULL if fails, the date otherwise
     */
    protected static Date tryParseDate(String date, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
        try {
            return sdf.parse(date);
        } catch (ParseException ex) {
            return null;
        }
    }
}
