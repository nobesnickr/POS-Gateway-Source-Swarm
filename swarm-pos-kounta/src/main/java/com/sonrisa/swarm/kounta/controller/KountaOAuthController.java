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
package com.sonrisa.swarm.kounta.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sonrisa.swarm.kounta.KountaAccount;
import com.sonrisa.swarm.kounta.controller.model.StoreInfoEntity;
import com.sonrisa.swarm.kounta.service.KountaStoreService;
import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.api.service.exception.StoreScanningException;
import com.sonrisa.swarm.posintegration.exception.ExternalApiException;

/**
 * Kounta OAuth controller, this controller is used to create new Kounta stores
 * with OAuth 2.0 authentication.
 * 
 * @author Barnabas
 */
@Controller
public class KountaOAuthController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KountaOAuthController.class);
    
    /**
     * OAuth 2.0 client id
     */
    @Value("${kounta.api.rest.oauth.client.id}")
    private String oauthClientId = "0000";
    
    /**
     * Redirect URI
     */
    @Value("${kounta.api.rest.oauth.uri.redirect}")
    private String oauthRedirectUri = "http://localhost:5555/api/";
    
    /**
     * Redirect URI
     */
    @Value("${kounta.api.rest.oauth.uri.authorize}")
    private String oauthAuthorizationUri = "http://localhost:5555/authorize/";

    /**
     * Store service
     */
    @Autowired
    private StoreService storeService;
    
    /**
     * Kounta store service
     */
    @Autowired
    private KountaStoreService kountaStoreService;
    
    /** The URI where the user is redirected after click "Grant access" on Kounta's site */
    public static final String LANDING_PAGE = "/kounta/oauth";
    
    /** The URI where the "Install!" button can be clicked */
    public static final String AUTHORIZATION_PAGE = "/kounta.html";
    
    /**
     * Creates or updates Kounta stores in the DB.
     * 
     * @param tempToken URL parameter containing the OAuth 2.0 temporary token
     * @throws KountaStoreServiceException 
     */
    @RequestMapping(method = RequestMethod.GET, value = LANDING_PAGE)
    public @ResponseBody ModelAndView exchangingTempToken(@RequestParam("code") String tempToken) throws Exception {  
        
        LOGGER.debug("OAuth temporary token has been received for Kounta");
        
        KountaAccount rootAccount = kountaStoreService.createAccountFromTemporaryToken(tempToken);
        List<StoreEntity> stores = kountaStoreService.scanForLocations(rootAccount);
        
        // Save all stores
        List<StoreInfoEntity> storeTable = new ArrayList<StoreInfoEntity>();
        for(StoreEntity store : stores){
            Long storeId = storeService.save(store);
            storeTable.add(new StoreInfoEntity(storeId, store.getName()));
        }
        
        // Prepare model
        ModelMap mm = new ModelMap();
        mm.addAttribute("stores", storeTable.toArray());
        
        LOGGER.info("Registered {} stores for {}", storeTable.size(), rootAccount.getStoreName());
        
        return new ModelAndView("kounta/registration_success", mm);
    }
    
    /**
     * Authorization page for Kounta with a huge "Install" button which
     * redirects user to Kounta to grant access to the Swarm application.
     */
    @RequestMapping(method = RequestMethod.GET, value = AUTHORIZATION_PAGE)
    public @ResponseBody ModelAndView getAuthorizationForm() {  
        
        LOGGER.debug("Requested authorization page for Kounta");
        
        ModelMap mm = new ModelMap();
        mm.addAttribute("authorizeUrl", oauthAuthorizationUri);
        mm.addAttribute("clientId", oauthClientId);
        mm.addAttribute("authorizeUrl", oauthAuthorizationUri);
        mm.addAttribute("redirectUri", oauthRedirectUri);
                
        return new ModelAndView("kounta/registration", mm);
    }
    

    /**
     * Exception handler for exceptions thrown inside the store registration procedure
     */
    @ExceptionHandler(StoreScanningException.class)
    public  @ResponseBody ModelAndView onRegistrationFails(HttpServletRequest request, StoreScanningException exception){
        ModelMap mm = new ModelMap();
        mm.addAttribute("errorMsg", exception.getMessage());

        LOGGER.warn("Kounta registration failed", exception);
        
        return new ModelAndView("kounta/registration_error", mm);
    }
    

    /**
     * Exception handler for exceptions thrown inside the Kounta API
     */
    @ExceptionHandler(ExternalApiException.class)
    public  @ResponseBody ModelAndView onApiFailure(HttpServletRequest request, ExternalApiException exception){
        ModelMap mm = new ModelMap();
        mm.addAttribute("errorMsg", exception.getMessage());
        
        LOGGER.warn("Kounta registration failed", exception);
        
        return new ModelAndView("kounta/registration_error", mm);
    }
}
