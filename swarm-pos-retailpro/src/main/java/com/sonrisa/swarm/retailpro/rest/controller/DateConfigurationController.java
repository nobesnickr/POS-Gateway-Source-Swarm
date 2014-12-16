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

import com.sonrisa.swarm.retailpro.model.DateConfigurationEntity;
import com.sonrisa.swarm.retailpro.rest.model.ForcedDateConfiguration;
import com.sonrisa.swarm.retailpro.service.DateConfigurationService;

/**
 * MVC controller responsible for handling the requests
 * for the remote date configuration information. 
 *
 * @author barna
 */
@Controller
public class DateConfigurationController extends BaseRetailProController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateConfigurationController.class);
    
    /**
     * URI of the REST service
     */
    public static final String CONFIG_URI = "/config/lastdate";
    
    /**
     * Service to access the remote configuration entities
     */
    @Autowired
    private DateConfigurationService dateConfigurationService;
    
    /**
     * Returns the LOGGER instance
     */
    @Override
    public Logger logger() {
        return LOGGER;
    }
    
    /**
     * Gets the latest remote configuration entry for the given swarm entity's plugin 
     * <pre>
     * URI: [prot]://[host]/swarm/api/config (GET)
     * 
     * Request body: - 
     * Response body: Remote date reconfiguration and version for the swarmId's plugin
     * 
     * Eg.:
     * { 
     *      "Version": 1382960761512,
     *      "LastInvoice": "2013-10-27 07:29:05",
     *      "LastVersion": "2013-10-27 07:29:06",
     *      "LastStore": "2013-10-27 07:29:05"
     * }
     * </pre>
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = CONFIG_URI)
    public @ResponseBody
    ForcedDateConfiguration config(
            @RequestHeader(value = "SwarmId", required = false) String swarmId,
            @RequestHeader(value = "Pos-Software", required = false) String posSoftware) {
        
        LOGGER.info("Remote date configuration requested by swarmId: {}({})", swarmId,posSoftware);
        return new ForcedDateConfiguration(getRemoteConfigurationEntity(swarmId));
    }
    
    /**
     * Get the most recent {@link DateConfigurationEntity} from DB which applies for swarmId 
     * @param swarmId Plugin's identifier
     * @return DB entity
     */
    private DateConfigurationEntity getRemoteConfigurationEntity(String swarmId){
        DateConfigurationEntity retval = dateConfigurationService.findMostRecentBySwarmId(swarmId);
        LOGGER.debug("Remote date configuration for {} returned as: {}", swarmId, retval);
        return retval;
    }
    
    /**
     * Returns whether this controller needs a swarmId in the HTTP header.
     */
    @Override
    public boolean needsSwarmId(){
        return false;
    }
}
