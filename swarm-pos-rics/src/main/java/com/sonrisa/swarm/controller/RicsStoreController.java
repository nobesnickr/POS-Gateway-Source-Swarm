/*
  Copyright (c) 2014 Sonrisa Informatikai Kft. All Rights Reserved.

 This software is the confidential and proprietary information of
 Sonrisa Informatikai Kft. ("Confidential Information").
 You shall not disclose such Confidential Information and shall use it only in
 accordance with the terms of the license agreement you entered into
 with Sonrisa.

 SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.sonrisa.swarm.controller;

import java.util.ArrayList;
import java.util.List;

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

import com.sonrisa.swarm.controller.entity.RicsAccountEntity;
import com.sonrisa.swarm.controller.entity.RicsAccountErrorEntity;
import com.sonrisa.swarm.controller.entity.RicsAccountResponseEntity;
import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.rics.RicsAccount;
import com.sonrisa.swarm.rics.service.RicsStoreService;
import com.sonrisa.swarm.rics.service.exception.RicsStoreServiceException;

/**
 * Controller for registering new stores for RICS
 * 
 * @author Barnabas
 */
@Controller
public class RicsStoreController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RicsStoreController.class);

    /** 
     * The URI of this controller. 
     * */
    public static final String CONTROLLER_PATH = "/rics/account";

    /**
     * Account info path
     */
    public static final String INFO_PATH = "/rics/info";
    
    /**
     * Service to create {@link StoreEntity} instances for username/password
     */
    @Autowired
    private RicsStoreService ricsStoreService;
    
    /**
     * Service to persist {@link StoreEntity} instances
     */
    @Autowired 
    private StoreService storeService;

    /**
     * Creates or updates a RICS store in the DB.
     */
    @RequestMapping(value = CONTROLLER_PATH, method = RequestMethod.PUT)
    public @ResponseBody List<RicsAccountResponseEntity> registerStores(@RequestBody List<RicsAccountEntity> accountList) throws RicsStoreServiceException {
        
        List<RicsAccountResponseEntity> retVal = new ArrayList<RicsAccountResponseEntity>();
        
        for(RicsAccountEntity account : accountList){
            RicsAccount realAccount = ricsStoreService.getAccount(
                    account.getLoginName(),
                    account.getPassword(),
                    account.getSerialNum(), 
                    account.getStoreCode());
            
            LOGGER.debug("Found valid RICS store: {} for userName: {}", realAccount.getStoreName(), account.getLoginName());
            
            // Create or find store for account
            StoreEntity store = ricsStoreService.getStore(realAccount);
            Long storeId = storeService.save(store);
            
            RicsAccountResponseEntity response = new RicsAccountResponseEntity();
            response.setStoreId(storeId);
            response.setStoreCode(realAccount.getStoreCode());
            response.setName(realAccount.getStoreName());
            retVal.add(response);
        }
        
        return retVal;
    }

    /**
     * Creates Rics account, fetches a single invoice from it and returns with store's information
     */
    @RequestMapping(value = INFO_PATH, method = RequestMethod.POST)
    public @ResponseBody RicsAccountResponseEntity testStore(@RequestBody RicsAccountEntity request) throws RicsStoreServiceException {
        RicsAccount account = ricsStoreService.getAccount(
                request.getLoginName(),
                request.getPassword(),
                request.getSerialNum(), 
                request.getStoreCode());
        
        RicsAccountResponseEntity response = new RicsAccountResponseEntity();
        response.setStoreCode(account.getStoreCode());
        response.setName(account.getStoreName());
        return response;
    }
    
    /**
     * Handle errors
     */
    @ExceptionHandler(Exception.class)
    public @ResponseBody ResponseEntity<RicsAccountErrorEntity> onError(Exception exception){
        LOGGER.warn("An error occured during store registration", exception);

        RicsAccountErrorEntity errorEntity = new RicsAccountErrorEntity();
        if(exception instanceof RicsStoreServiceException){
            errorEntity.setErrorMsg(exception.getMessage());
            return new ResponseEntity<RicsAccountErrorEntity>(errorEntity, HttpStatus.BAD_REQUEST);
        } else {
            errorEntity.setErrorMsg("Unexpected error");
            return new ResponseEntity<RicsAccountErrorEntity>(errorEntity, HttpStatus.BAD_REQUEST);
        }
    }
}
