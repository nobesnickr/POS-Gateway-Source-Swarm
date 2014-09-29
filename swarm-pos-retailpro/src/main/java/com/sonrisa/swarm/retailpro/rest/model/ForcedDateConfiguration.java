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
package com.sonrisa.swarm.retailpro.rest.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sonrisa.swarm.retailpro.model.DateConfigurationEntity;
import com.sonrisa.swarm.retailpro.rest.RetailProApiConstants;

/**
 * Model object which encapsulates a forced override of the remote date reconfiguration
 * of an installed data exporter plugin (e.g. Retail Pro V8).
 * 
 * @author barna
 */
public class ForcedDateConfiguration {
    /**
     * Version is a server timestamp the .NET client uses the identify already
     * executed commands.
     */
    @JsonProperty(RetailProApiConstants.JSON_KEY_REMOTE_CONFIG_VERSION)
    private String timeStampVersion = "0";

    /**
     * The assumed to be last invoice's modification date, by changing this all
     * invoices since a certain date can be forced to be resent
     * 
     * If value is null, the it is not included into the JSON, and remote .NET plugin ignores it.
     */
    @JsonInclude(Include.NON_NULL)
    @JsonProperty(RetailProApiConstants.JSON_KEY_REMOTE_CONFIG_LAST_INVOICE)
    private String lastModifiedInvoiceDate = null;

    /**
     * The assumed to be last store's modification date, by changing this all
     * StoreNumber/SbsNumber since a certain date can be forced to be resent.
     * 
     * If value is null, the it is not included into the JSON, and remote .NET plugin ignores it.
     */
    @JsonProperty(RetailProApiConstants.JSON_KEY_REMOTE_CONFIG_LAST_STORE)
    @JsonInclude(Include.NON_NULL)
    private String lastModifiedStoreDate = null;

    /**
     * Currently not used by the RpoV8 .NET plugin
     * 
     * If value is null, the it is not included into the JSON, and remote .NET plugin ignores it.
     */
    @JsonProperty(RetailProApiConstants.JSON_KEY_REMOTE_CONFIG_LAST_VERSION)
    @JsonInclude(Include.NON_NULL)
    private String lastModifiedVersion = null;
    
    /**
     * Date format used to format dates
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    
    /**
     * Initialize an instance of the ForcedDateConfiguration to be the 
     * response to the server.
     */
    public ForcedDateConfiguration(DateConfigurationEntity configurationEntity){
        
        if(configurationEntity == null){
            throw new IllegalArgumentException("Argument configurationEntity shouldn't be null!");
        }
        
        if(configurationEntity.getTimeStampVersion() == null){
            throw new IllegalArgumentException("Argument's configurationEntity.getTimeStampVersion() shouldn't be null!");
        }
        
        // The timestampVersion is the timestamp's seconds (division by 1000L is so it's not milliseconds)
        this.timeStampVersion = Long.toString(configurationEntity.getTimeStampVersion().getTime() / 1000L);
        
        this.lastModifiedInvoiceDate = dateToStringIfNotNull(configurationEntity.getLastModifiedInvoiceDate());
        this.lastModifiedStoreDate = dateToStringIfNotNull(configurationEntity.getLastModifiedStoreDate());
        this.lastModifiedVersion = dateToStringIfNotNull(configurationEntity.getLastModifiedVersionDate());
    }
    
    /**
     * Attempts to format date using DATE_FORMAT, or returns null if date is null
     * @param date Date to be formatted
     * @return NULL if date is null, the formatted date otherwise
     */
    private static String dateToStringIfNotNull(final Date date){
        if(date != null){
            return new SimpleDateFormat(DATE_FORMAT).format(date);
        }
        return null;
    }
}
