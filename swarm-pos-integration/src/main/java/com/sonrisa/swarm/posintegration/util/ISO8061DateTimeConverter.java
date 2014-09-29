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
package com.sonrisa.swarm.posintegration.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import com.sonrisa.swarm.common.util.DateUtil;

/**
 * Class converting simple Java Date objects
 * into input and output formatted Strings in 
 * http://en.wikipedia.org/wiki/ISO_8601 format
 * 
 * Used for Merchant OS, Shopify
 */
public class ISO8061DateTimeConverter {
    
    /**
     * Date format to send dates to MerchantOS e.g. the time filter
     */
    private static final String ENCODED_DATE_FORMATTER = "yyyy-MM-dd%20HH:mm:ss";
    
    /**
     * Date format for MySQL
     */
    private static final String MYSQL_DATE_FORMATTER = "yyyy-MM-dd HH:mm:ss";

    /**
     * Date format used by Lightspeed Pro, which is the internal date format representation if Microsoft .NET
     */
    private static final String ODATA_DATE_FORMATTER = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    
    /**
     * Convert Date to String
     * 
     * WARNING: This should only be used for testing by Wiremock.
     * 
     * @param date Date to be formatted
     * @return Returns date using OutDateFormatter
     */
    @Deprecated
    public static String dateToMerchantOSURIEncodedString(Date date){
        SimpleDateFormat outDateformat = new SimpleDateFormat(ENCODED_DATE_FORMATTER);
        return outDateformat.format(date);
    }
    
    /**
     * Convert Date to String using format <code>yyyy-MM-dd HH:mm:ss</code>
     * @param date Date to be formatted
     * @return Returns date using OutDateFormatter
     */
    public static String dateToMysqlString(Date date){
        SimpleDateFormat outDateformat = new SimpleDateFormat(MYSQL_DATE_FORMATTER);
        return outDateformat.format(date);
    }
    
    /**
     * Convert Date to String, with no URI encoding
     * @param date Date to be formatted
     * @return Returns date using OutDateFormatter
     */
    public static String dateToOdataString(Date date){
        SimpleDateFormat outDateformat = new SimpleDateFormat(ODATA_DATE_FORMATTER);
        return outDateformat.format(date);
    }
    
    /**
     * Convert Date to String
     * @param date Date to be formatted
     * @return Returns date using OutDateFormatter
     */
    public static String dateToString(Date date, String dateFormatString){
        SimpleDateFormat outDateformat = new SimpleDateFormat(dateFormatString);
        return outDateformat.format(date);
    }
    
    /**
     * Convert String to Date
     * @param source Source string using inDateformat
     * @return Returns date using InDateFormatter
     */
    public static Date stringToDate(String source){
        return DatatypeConverter.parseDate(source).getTime();
    }

    /**
     * Convert String to Date
     * @param source Source string using inDateformat
     * @return Returns date using InDateFormatter
     */
    public static Date stringToDate(String source, String timezone){
        Calendar date = DatatypeConverter.parseDate(source);
        return DateUtil.setTimeZoneWithoutConversion(date, timezone).getTime();
    }
    
    /**
     * Convert String to Date
     * @param source Source string using inDateformat
     * @return Returns date using InDateFormatter
     */
    public static Date stringToDate(Calendar date, String timezone){
        return DateUtil.setTimeZoneWithoutConversion(date, timezone).getTime();
    }
}
