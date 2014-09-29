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

import com.sonrisa.swarm.common.rest.controller.BaseSwarmController;
import com.sonrisa.swarm.retailpro.service.RpDynamicMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * REST controller for /config/mapping, used for Retail Pro V8 
 * RDA2 dynamic mapping.
 * 
 * @author barna
 */
@Controller
public class RpDynamicMappingController extends BaseSwarmController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceDescriptionController.class);
    
    @Autowired
    private RpDynamicMappingService service;

    /**
     * {@inheritDoc}
     */
    @Override
    public Logger logger() {
        return LOGGER;
    }
    
    /**
     * URI invoice mapping can be fetched from
     */
    public static final String MAPPING_URI = "/config/mapping";
    
    
    /**
     * Gets the invoice mapping for a certain store
     * <pre>
     * URI: [prot]://[host]/swarm/api/mapping (GET)
     * 
     * Request body: - 
     * Response body: JSON mapping to be used by client plugin
     * </pre>
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = MAPPING_URI, produces = "application/json; charset=UTF-8")
    public @ResponseBody
    String mapping(
            @RequestHeader(value = "SwarmId", required = false) String swarmId,
            @RequestHeader(value = "Pos-Software", required = false) String posSoftware) {
        
        // If requested without swarmId, then use empty
        final String mappingKey = (swarmId == null) ? "" : swarmId;
        
        LOGGER.debug("Invoice mapping requested by {}", mappingKey);
        return service.getDynamicMapping(mappingKey, posSoftware);
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
