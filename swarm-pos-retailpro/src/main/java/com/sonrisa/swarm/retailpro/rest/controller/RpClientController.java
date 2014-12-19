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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sonrisa.swarm.retailpro.rest.RetailProApiConstants;
import com.sonrisa.swarm.retailpro.rest.model.RpHeartbeatJson;
import com.sonrisa.swarm.retailpro.service.RpClientService;
import com.sonrisa.swarm.retailpro.service.RpLogUploadingService;
import com.sonrisa.swarm.retailpro.util.ControllerUtil;

/**
 * MVC controller responsible for handling the RetailPro client version requests.
 * With this controller the RetailPro clients can send information about their versions and etc.
 *
 * @author joe
 */
@Controller
@RequestMapping(RpClientController.URI_BASE)
public class RpClientController extends BaseRetailProController {
    
    /** The URI of this controller. */
    public static final String URI_BASE = "/items";
    public static final String URI_OF_VERSION = "/version";
    public static final String URI_OF_HEARTBEAT = "/heartbeat";

    private static final Logger LOGGER = LoggerFactory.getLogger(RpClientController.class);
    
    @Autowired
    private RpClientService clientService;
    
    @Autowired 
    private RpLogUploadingService rpClientLogService;

    /**
     * Writes the received client version message to his rp client log file.
     * 
     * @param swarmId
     * @param client
     * @return 
     */
    @RequestMapping(method = RequestMethod.PUT, value = URI_OF_VERSION)
    public @ResponseBody
    ResponseEntity<String> version(
            @RequestHeader(value = RetailProApiConstants.SWARM_ID, required = false) String swarmId,
            @RequestHeader(value = RetailProApiConstants.POS_SOFTWARE, required = false) String posSoftware,
            @RequestBody String message) {
        
        final ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.CREATED);
        final String source = ControllerUtil.getSourceId(swarmId, posSoftware);
        
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("{} says its RetailPro version is: {}", source, message);
        }
        
        rpClientLogService.save(source, "My RetailPro version: " + message);
        return response;
    }
            
    /**
     * Receives a heartbeat from the RetailPro client.
     * 
     * @param swarmId
     * @param client
     * @return 
     */
    @RequestMapping(method = RequestMethod.PUT, value = URI_OF_HEARTBEAT)
    public @ResponseBody
    ResponseEntity<String> heartbeat(
            @RequestHeader(value = "SwarmId", required = false) String swarmId,
            @RequestHeader(value = "Pos-Software", required = false) String posSoftware,
            @RequestBody RpHeartbeatJson json) {
        
        if(StringUtils.isEmpty(posSoftware)){
            posSoftware = "N/A";
        }

        LOGGER.info("Heartbeat received from {}({})", swarmId, posSoftware);
        
        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.CREATED);
        clientService.heartbeat(swarmId, json);
        
        return response;
    }    

    @Override
    public Logger logger() {
        return LOGGER;
    }

}
