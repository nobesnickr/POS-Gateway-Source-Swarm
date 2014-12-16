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
package com.sonrisa.swarm.retailpro.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sonrisa.swarm.retailpro.dao.RpLogConfigurationDao;

/**
 * REST controller for /config/log, used for Retail Pro Log Level configuration
 * 
 * @author barna
 */
@Controller
public class RpLogConfigurationController extends BaseRetailProController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceDescriptionController.class);
    
    /**
     * Dao used to access data
     */
    @Autowired
    private RpLogConfigurationDao dao;

    /**
     * {@inheritDoc}
     */
    @Override
    public Logger logger() {
        return LOGGER;
    }
    
    /**
     * URI the log level configuration for specific swarm partner can be retrieved from
     */
    public static final String LOGGING_LEVEL_URI = "/config/log";
    
    /**
     * Gets the invoice mapping for a certain store
     * <pre>
     * URI: [prot]://[host]/swarm/api/loglevel (GET)
     * 
     * Request body: - 
     * Response body: JSON mapping indicating which target should upload at which loglevel
     * </pre>
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = LOGGING_LEVEL_URI, produces = "application/json; charset=UTF-8")
    public @ResponseBody String loglevel(@RequestHeader(value = "SwarmId", required = false) String swarmId) {
        // If requested without swarmId, then use empty
        final String mappingKey = (swarmId == null) ? "" : swarmId;
        
        LOGGER.debug("Invoice mapping requested by {}", mappingKey);
        return dao.getLogLevelConfiguration(mappingKey);
    }
    

    /**
     * {@inheritDoc}
     * 
     * If triggered with no swarmId, this service returns the defaultMapping
     */
    @Override
    public boolean needsSwarmId(){
        return false;
    }
}
