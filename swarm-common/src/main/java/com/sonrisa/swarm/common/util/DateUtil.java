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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author joe
 */
public abstract class DateUtil {
    
    public static Calendar setTimeZoneWithoutConversion(Calendar date, String timeZone){
        // sets the time zone of the target calendar
        Calendar targetCalendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
        // copies all the main fields to the target calendar
        targetCalendar.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE)
                , date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), date.get(Calendar.SECOND));
        return targetCalendar;
    }
    
    public static Date setTimeZoneWithoutConversion(Date date, String timeZone){
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        return setTimeZoneWithoutConversion(dateCal, timeZone).getTime();
    }
    
    /**
     * Returns the code of the default timezone.
     * E.g.: UTC
     * 
     * @return 
     */
    public static String getDefaultTimeZoneCode(){
        // timezone pattern eg.: PST
        final SimpleDateFormat sdf = new SimpleDateFormat("z");
        return sdf.format(new Date());
    }
}
