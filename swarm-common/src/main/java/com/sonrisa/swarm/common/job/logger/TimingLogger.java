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
package com.sonrisa.swarm.common.job.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class logging the timing of the application
 */
public class TimingLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimingLogger.class);
    
    /** Format of the duration */
    private static final String DATE_FORMAT = "mm:ss.SSS";
    
    /** TimingLogger is a utility class, its constructor is hidden */
    private TimingLogger(){
    }
    
    private static String formatTime(long duration){
        if(duration < 60*60*1000){
            return " [" + new SimpleDateFormat(DATE_FORMAT).format(new Date(duration)) + "]";
        } else {
            return " [" + duration + "ms]";
        }
    }
    
    /**
     * Logs the message into the timing log file
     * @param message Message of the action
     * @param duration Milliseconds to be logged, the time the action took
     * @param params Parameters to be passed for the Logger
     */
    public static void debug(String message, long duration, Object... params){
        LOGGER.debug(message + formatTime(duration) , params);
    }
    
    /**
     * Logs the message into the timing log file
     * @param message Message of the action
     * @param duration Milliseconds to be logged, the time the action took
     */
    public static void debug(String message, long duration){
        LOGGER.debug(message + " [" + new SimpleDateFormat(DATE_FORMAT).format(new Date(duration)) + "]");
    }
}
