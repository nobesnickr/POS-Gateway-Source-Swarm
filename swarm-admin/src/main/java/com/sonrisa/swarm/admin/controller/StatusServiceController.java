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
package com.sonrisa.swarm.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sonrisa.swarm.admin.model.StatusEntity;
import com.sonrisa.swarm.admin.model.query.StatusQueryEntity;
import com.sonrisa.swarm.admin.service.StatusService;
import com.sonrisa.swarm.admin.service.exception.InvalidStatusRequestException;

/**
 * Controller for accessing store status
 */
@Controller
public class StatusServiceController extends BaseStatusServiceController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StatusServiceController.class);
    
    /**
     * URI to access status for non-Retail Pro stores as list
     */
    public static final String URI = "/admin/stores";
    
    /**
     * Service layer
     */
    @Autowired
    private StatusService statusService;
    
    /**
     * Gets list of status
     */
    @Secured("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.GET, value = StatusServiceController.URI)
    public @ResponseBody Map<String,List<StatusEntity>> getStatusList(
            @RequestParam(value = "skip", defaultValue = "0") int skip, 
            @RequestParam(value = "take", defaultValue = "10000") int take, 
            @RequestParam(value = "order_by", defaultValue = "created") String orderBy,
            @RequestParam(value = "order_dir", defaultValue = "desc") String orderDir,
            @RequestParam(required = false) Boolean active, //Filter for stores with matching active value, possible values are true or false
            @RequestParam(required = false) String api, // Filter for stores with matching api name (shopify, lspro, etc.)
            @RequestParam(required = false) String status // Only stores matching status will be returned, either OK, WARNING or ERROR, can be array if separated by comma
            ) throws InvalidStatusRequestException {
        
        LOGGER.debug("Received request to {} ", URI);
        
        StatusQueryEntity query = new StatusQueryEntity();
        query.setSkip(skip);
        query.setTake(take);
        query.setOrderBy(orderBy);
        query.setOrderDir(requestParamAsOrderDir(orderDir));
        query.setActive(active);
        query.setApi(requestParamsAsSet(api));
        query.setStatus(requestParamsAsStatusSet(status));
        
        LOGGER.info("Reading status for: {}", query);
        
        Map<String,List<StatusEntity>> retVal = new HashMap<String,List<StatusEntity>>();
        retVal.put(DATA_KEY, statusService.getStoreStatuses(query));
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
