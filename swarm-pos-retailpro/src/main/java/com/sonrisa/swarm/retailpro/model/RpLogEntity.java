/*
 *   Copyright (c) 2014 Sonrisa Informatikai Kft. All Rights Reserved.
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

package com.sonrisa.swarm.retailpro.model;

import java.util.Date;
import java.util.regex.Pattern;

import com.sonrisa.swarm.retailpro.rest.controller.RpLogUplodingController;

/**
 * Retail Pro log upload entity
 * 
 * @author Barnabas
 */
public class RpLogEntity {

    /**
     * Log level, e.g. <code>ERROR</code> or <code>TRACE</code>
     */
    private String level = "INFO";
    
    /**
     * Datetime marked in the log
     */
    private String localTimestamp = "";
    
    /**
     * Stack trace in the log
     */
    private String stackTrace = "";
    
    /**
     * Log details
     */
    private String details = "";
    
    /**
     * Server time when received
     */
    private Date serverTimestamp = new Date();
    
    /**
     * Use {@link #fromClientString} instead of this
     */
    public RpLogEntity(){
    }
    
    /**
     * Initialize using message uploaded to {@link RpLogUplodingController} 
     * 
     * @param clientString
     * @return
     */
    public static RpLogEntity fromClientString(String clientString){
       String[] columns = clientString.split(Pattern.quote("|"), 4);
       
       RpLogEntity entity = new RpLogEntity();
       if(columns.length > 0){
           entity.localTimestamp = columns[0];
       }
       if(columns.length > 1){
           entity.level = columns[1];
       }
       if(columns.length > 2){
           entity.stackTrace = columns[2];
       }
       if(columns.length > 3){
           entity.details = columns[3];
           
           // Format, so NLogger's \r\n value are replaced 
           // with actual line breaks
           columns[3] = columns[3].replace("\\r\\n", "\r\n");
       }
       
       return entity;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLocalTimestamp() {
        return localTimestamp;
    }

    public void setLocalTimestamp(String localTimestamp) {
        this.localTimestamp = localTimestamp;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(Date serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }

    @Override
    public String toString() {
        return "RpLogEntity [level=" + level + ", localTimestamp=" + localTimestamp + ", stackTrace=" + stackTrace
                + ", details=" + details + ", serverTimestamp=" + serverTimestamp + "]";
    }
}
