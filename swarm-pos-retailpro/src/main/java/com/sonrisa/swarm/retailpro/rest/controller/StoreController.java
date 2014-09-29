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
import com.sonrisa.swarm.retailpro.rest.model.JsonStore;
import com.sonrisa.swarm.retailpro.service.RpStoreService;
import java.util.List;
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

/**
 * MVC controller responsible for handling the requests related to the RetailPro stores.
 *
 * @author joe
 */
@Controller
@RequestMapping(StoreController.URI)
public class StoreController extends BaseSwarmController {
    
    /** The URI of this controller. */
    public static final String URI = "/items/store";

    private static final Logger LOGGER = LoggerFactory.getLogger(StoreController.class);
    
    @Autowired
    private RpStoreService storeService;

    /**
     * Creates or updates a RetailPro store in the DB.
     * 
     * @param swarmId
     * @param store
     * @return 
     */
    @RequestMapping(method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<String> create(
            @RequestHeader(value = "SwarmId", required = false) String swarmId,
            @RequestHeader(value = "Pos-Software", required = false) String posSoftware,
            @RequestBody List<JsonStore> stores) {
        
        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.CREATED);
        
        LOGGER.debug("Store data uploaded by {}", swarmId);
        storeService.save(swarmId, posSoftware, stores.toArray(new JsonStore[stores.size()]));
        LOGGER.info("Received {} stores from swarmId: {} ({})", stores.size(), swarmId, getStoreNamesFromList(stores));        
        
        return response;
    }
    
    /**
     * Concatenate list of {@link JsonStore#getStoreNumber()) 
     * @param stores
     * @return
     */
    private String getStoreNamesFromList(final List<JsonStore> stores){
        StringBuilder sb = new StringBuilder();
        for(JsonStore store : stores){
            sb.append(sb.length() == 0 ? "" : ",");
            sb.append(store.getName() == null ? "null" : store.getName());
        }
        return sb.toString();
    }

    @Override
    public Logger logger() {
        return LOGGER;
    }
}
