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
package com.sonrisa.swarm.revel.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.api.service.exception.StoreScanningException;
import com.sonrisa.swarm.revel.RevelAccount;
import com.sonrisa.swarm.revel.service.RevelStoreService;

/**
 * Controller responsible for creating {@link RevelAccount} entities into the stores table
 */
@Controller
@RequestMapping(RevelStoreController.CONTROLLER_PATH)
public class RevelStoreController {
    
    /** The URI of this controller. */
    public static final String CONTROLLER_PATH = "/revel/register";

    private static final Logger LOGGER = LoggerFactory.getLogger(RevelStoreController.class);
    
    @Autowired
    private RevelStoreService revelStoreService;
    
    @Autowired 
    private StoreService storeService;

    public RevelStoreController() {
        LOGGER.debug(RevelStoreController.class.getSimpleName() + " has been instantiated.");
    }
    
    /**
     * Creates or updates a Revel store in the DB.
     * 
     * @param swarmId
     * @param store
     * @return 
     */
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody ModelAndView registerStore(HttpServletRequest request) throws StoreScanningException {
        
        final String userName = request.getParameter("username");
        final String apiKey = request.getParameter("apikey");
        final String apiSecret = request.getParameter("apisecret");
        
        // Timezone conversion requires establishment based division, I'm not removing this
        // feature from service classes, but the controller will always use it
        final boolean establishmentDivision = true;

        LOGGER.info("Revel Store registration request was received: {} with {}establishment division", 
                userName, 
                (establishmentDivision ? "" : "no "));
        
        RevelAccount baseAccount = revelStoreService.getAccount(userName, apiKey, apiSecret);
        
        List<StoreEntity> stores = null;
        if(establishmentDivision){
            stores = revelStoreService.scanForLocations(baseAccount);
        } else {
            stores = new ArrayList<StoreEntity>();
            stores.add(revelStoreService.getRootStoreEntity(baseAccount));
        }

        final ModelMap mm = new ModelMap("succeeded", true);
        
        List<Long> storeIds = new ArrayList<Long>();
        
        // Save stores
        for(StoreEntity storeEntity : stores){
            Long storeId = storeService.save(storeEntity);
            storeIds.add(storeId);
        }
        
        LOGGER.debug("Registered {} stores for {}", storeIds.size(), userName);
        mm.addAttribute("message", org.apache.commons.lang.StringUtils.join(storeIds.toArray(),","));
        return new ModelAndView("revel/registration_success", mm);            
    }
    

    /**
     * Exception handler for exceptions thrown inside the store registration procedure
     */
    @ExceptionHandler(StoreScanningException.class)
    public @ResponseBody ModelAndView onRegistrationFails(HttpServletRequest request, StoreScanningException exception){
        
        final ModelMap mm = new ModelMap("succeeded", false);
        mm.addAttribute("errorMsg", exception.getUserFriendlyErrorMessage());
        
        LOGGER.warn("Registration failed for Revel", exception);
        return new ModelAndView("revel/registration_error", mm);
    }
}
