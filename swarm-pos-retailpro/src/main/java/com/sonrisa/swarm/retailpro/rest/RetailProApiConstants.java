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
package com.sonrisa.swarm.retailpro.rest;

import com.sonrisa.swarm.retailpro.rest.controller.InterfaceDescriptionController;
import com.sonrisa.swarm.retailpro.rest.model.ForcedDateConfiguration;

/**
 * This class contains all the constant string literales used by the RetailPro JSON API.
 * 
 * E.g.:
 *   - the keys in the JSON maps (TODO!)
 *   - the key of the swarmId in the HTTP header
 *   - etc...
 *
 * @author joe
 */
public abstract class RetailProApiConstants {
    
    /** The key of the SwarmId in the HTTP request header. */
    public static final String SWARM_ID = "SwarmId";
    /** The key of the SwarmId in the HTTP request header. */
    public static final String POS_SOFTWARE = "Pos-Software";
    
    /** 
     * This is the key of the interface version value in the JSON object 
     * used by the {@link InterfaceDescriptionController}.     
     */
    public static final String JSON_KEY_API_DESCRIPTION_VERSION = "interfaceVersion";
    
    /** 
     * This is the key of the build's timestamp in the JSON object 
     * used by the {@link InterfaceDescriptionController}.     
     */
    public static final String JSON_KEY_BUILD_TIMESTAMP = "buildTimestamp";
    
    
    /**
     * This is the key of the remote configuration json field 
     * sent to Retail Pro V8 clients to change their SettingsV8.xml. 
     * Used by {@link ForcedDateConfiguration}
     * 
     * Version is a server timestamp the .NET client uses the identify
     * already executed commands.
     */
    public static final String JSON_KEY_REMOTE_CONFIG_VERSION = "Version";
    
    /**
     * This is the key of the remote configuration json field 
     * sent to Retail Pro V8 clients to change their SettingsV8.xml. 
     * Used by {@link ForcedDateConfiguration}
     * 
     * The assumed to be last invoice's modification date, by changing this
     * all invoices since a certain date can be forced to be resent
     */
    public static final String JSON_KEY_REMOTE_CONFIG_LAST_INVOICE = "LastInvoice";
    
    
    /**
     * This is the key of the remote configuration json field 
     * sent to Retail Pro V8 clients to change their SettingsV8.xml. 
     * Used by {@link ForcedDateConfiguration}
     * 
     * Currently not used by the RpoV8 .NET plugin 
     */
    public static final String JSON_KEY_REMOTE_CONFIG_LAST_VERSION = "LastVersion";
    
    
    /**
     * This is the key of the remote configuration json field 
     * sent to Retail Pro V8 clients to change their SettingsV8.xml. 
     * Used by {@link ForcedDateConfiguration}
     * 
     * The assumed to be last store's modification date, by changing this
     * all StoreNumber/SbsNumber since a certain date can be forced to be resent.     
     */
    public static final String JSON_KEY_REMOTE_CONFIG_LAST_STORE = "LastStore";
}
