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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sonrisa.swarm.admin.model.StoreAdminServiceEntity;
import com.sonrisa.swarm.admin.service.StoreAdminService;
import com.sonrisa.swarm.admin.service.exception.InvalidAdminRequestException;

/**
 * Contoller for updating stores
 * @author Barnabas
 *
 */
@Controller
public class StoreAdminController extends BaseStoreAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoreAdminController.class);
    
    /**
     * Admin service
     */
    @Autowired
    private StoreAdminService storeAdminService;
    
    /**
     * Controller at /admin/stores/ where store fields can be updated (e.g. stores can be activated).
     */
    @Secured("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.PUT, value = StatusServiceController.URI + "/{storeId}")
    public @ResponseBody ResponseEntity<String> updateStore(
            @PathVariable Long storeId, 
            @RequestBody StoreAdminServiceEntity entity) throws InvalidAdminRequestException {
        
        storeAdminService.update(storeId, entity);
        return new ResponseEntity<String>(HttpStatus.CREATED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
