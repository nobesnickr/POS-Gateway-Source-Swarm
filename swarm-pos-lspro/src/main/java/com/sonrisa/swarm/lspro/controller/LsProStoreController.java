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

package com.sonrisa.swarm.lspro.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.lspro.LsProAccount;
import com.sonrisa.swarm.lspro.controller.entity.LsProAccountEntity;
import com.sonrisa.swarm.lspro.controller.entity.LsProRegistrationErrorEntity;
import com.sonrisa.swarm.lspro.service.LsProStoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.api.service.exception.StoreScanningException;

/**
 * REST controller for saving new Lightspeed Pro stores
 * @author Barnabas
 */
@Controller
@RequestMapping(LsProStoreController.CONTROLLER_PATH)
public class LsProStoreController {

    /** The URI of this controller. */
    public static final String CONTROLLER_PATH = "/lspro/account";

    private static final Logger LOGGER = LoggerFactory.getLogger(LsProStoreController.class);
    
    /**
     * Service to create {@link StoreEntity} instances for username/password
     */
    @Autowired
    private LsProStoreService lsProStoreService;
    
    /**
     * Service to persist {@link StoreEntity} instances
     */
    @Autowired 
    private StoreService storeService;

    /**
     * Initialize controller
     */
    public LsProStoreController() {
        LOGGER.debug(LsProStoreController.class.getSimpleName() + " has been instantiated.");
    }

    /**
     * Creates or updates a Lightspeed Pro store in the DB.
     * @throws LsProLocationScanningException 
     */
    @RequestMapping(method = RequestMethod.PUT)
    public @ResponseBody ResponseEntity<Map<String,Long>> registerStore(@RequestBody LsProAccountEntity account) throws StoreScanningException {
        
        LOGGER.info("Registering LsPro store for {}", account.getUserName());
        
        final LsProAccount dummyAccount = lsProStoreService.getStore(account.getUserName(), account.getToken());
        List<StoreEntity> stores = lsProStoreService.scanForLocations(dummyAccount);
        
        // Map for store_id -> store_name
        Map<String,Long> result = new HashMap<String,Long>();
        
        for(StoreEntity store : stores){
            final Long storeId = storeService.save(store);
            result.put(store.getName(), storeId);
        }
        
        return new ResponseEntity<Map<String,Long>>(result, HttpStatus.OK);
    }
    
    /**
     * Exception handler for exceptions thrown while communication with
     * the Lightspeed Pro REST service to scan for locations.
     */
    @ExceptionHandler(StoreScanningException.class)
    public @ResponseBody ResponseEntity<LsProRegistrationErrorEntity> onScanningFails(HttpServletRequest request, StoreScanningException exception){
        LsProRegistrationErrorEntity result = new LsProRegistrationErrorEntity();
        result.setError(exception.getMessage());
        result.setInnerError(exception.getCause().getMessage());        
        return new ResponseEntity<LsProRegistrationErrorEntity>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
