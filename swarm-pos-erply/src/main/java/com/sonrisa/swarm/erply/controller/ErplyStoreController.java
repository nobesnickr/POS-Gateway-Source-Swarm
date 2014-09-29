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
package com.sonrisa.swarm.erply.controller;

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

import com.sonrisa.swarm.erply.ErplyAccount;
import com.sonrisa.swarm.erply.controller.dto.ErplyAccountDTO;
import com.sonrisa.swarm.erply.controller.dto.ErplyErrorResponseDTO;
import com.sonrisa.swarm.erply.controller.dto.ErplyRegistrationSuccessDTO;
import com.sonrisa.swarm.erply.service.ErplyStoreService;
import com.sonrisa.swarm.erply.service.exception.ErplyStoreServiceException;
import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.exception.ExternalApiBadCredentialsException;

/**
 * Controller for registering stores
 * 
 * @author Barnabas
 */
@Controller
public class ErplyStoreController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ErplyStoreController.class);

    /** 
     * The URI of this controller. 
     * */
    public static final String CONTROLLER_PATH = "/erply/account";
    
    /**
     * Service to create {@link StoreEntity} instances for username/password
     */
    @Autowired
    private ErplyStoreService erplyStoreService;
    
    /**
     * Service to persist {@link StoreEntity} instances
     */
    @Autowired 
    private StoreService storeService;
    

    /**
     * Creates or updates an Erply store in the DB.
     */
    @RequestMapping(value = CONTROLLER_PATH, method = RequestMethod.PUT)
    public @ResponseBody ErplyRegistrationSuccessDTO registerStores(@RequestBody ErplyAccountDTO json) throws ErplyStoreServiceException {
        
        LOGGER.info("Registering store for {} with username {}", json.getClientCode(), json.getUsername());
        
        // Get account for credentials
        final ErplyAccount account = erplyStoreService.getAccount(json.getClientCode(), json.getUsername(), json.getPassword());
        
        ErplyRegistrationSuccessDTO retVal = new ErplyRegistrationSuccessDTO();
        retVal.setStoreName(account.getStoreName());
        
        // Get store entity for this account
        StoreEntity store = erplyStoreService.getStore(account);
        Long storeId = storeService.save(store);
        retVal.setStoreId(storeId);
        
        return retVal;
    }
    
    /**
     * Handle errors
     */
    @ExceptionHandler(ErplyStoreServiceException.class)
    public @ResponseBody ResponseEntity<ErplyErrorResponseDTO> onError(ErplyStoreServiceException exception){
        LOGGER.warn("An error occured during store registration", exception);

        ErplyErrorResponseDTO errorEntity = new ErplyErrorResponseDTO();
        
        // Set type
        Throwable cause = exception.getCause();
        if(cause instanceof ExternalApiBadCredentialsException){
            errorEntity.setErrorType("bad_credentials");
            errorEntity.setErrorMessage("Invalid username or password");
        } else {
            errorEntity.setErrorType("api_error");
            errorEntity.setErrorMessage(exception.getMessage());
        }
        
        return new ResponseEntity<ErplyErrorResponseDTO>(errorEntity, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle catastrophic errors
     */
    @ExceptionHandler(Exception.class)
    public @ResponseBody ResponseEntity<ErplyErrorResponseDTO> onError(Exception exception){
        LOGGER.warn("An error occured during store registration", exception);

        ErplyErrorResponseDTO errorEntity = new ErplyErrorResponseDTO();
        errorEntity.setErrorType("catastrophic_error");
        errorEntity.setErrorMessage("Unexpected error occured while attempting to register Erply store");
        
        return new ResponseEntity<ErplyErrorResponseDTO>(errorEntity, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
