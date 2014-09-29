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
import java.util.Date;

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

import com.sonrisa.swarm.common.rest.controller.BaseSwarmController;
import com.sonrisa.swarm.retailpro.service.RpLogMonitoringService;
import com.sonrisa.swarm.retailpro.service.RpLogUploadingService;
import com.sonrisa.swarm.retailpro.util.ControllerUtil;


/**
 * Controller where Retail Pro sends its log messages
 */
@Controller
public class RpLogUplodingController extends BaseSwarmController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RpLogUplodingController.class);

    @Autowired 
    private RpLogUploadingService service;
    
    @Autowired
    private RpLogMonitoringService monitoringService;
    
    @Override
    public Logger logger() {
        return LOGGER;
    }

    /**
     * URI invoice mapping can be fetched from
     */
    public static final String LOG_UPLOAD_URI = "/log";
    
    /**
     * Upload log of remote plugin to the server
     * <pre>
     * URI: [prot]://[host]/swarm/api/log (PUT)
     * 
     * Request body: Log entry to be saved 
     * </pre>
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT, value = LOG_UPLOAD_URI)
    public @ResponseBody
    ResponseEntity<String> upload(
            @RequestHeader(value = "SwarmId", required = false) String swarmId,
            @RequestHeader(value = "Pos-Software", required = false) String posSoftware,
            @RequestBody String message) {
        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.CREATED);
        
        // Save to monitoring service
        monitoringService.registerLog(swarmId, message, new Date());
 
        // Save to log file
        final String sourceId = ControllerUtil.getSourceId(swarmId, posSoftware);
        service.save(sourceId, message);
        
        return response;
    }
}

