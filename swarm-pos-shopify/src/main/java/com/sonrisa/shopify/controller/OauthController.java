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
package com.sonrisa.shopify.controller;

import com.sonrisa.shopify.model.TokenExchangeResult;
import com.sonrisa.shopify.service.ShopifyRestApiService;
import com.sonrisa.shopify.service.ShopifyStoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the landing page where the user is redirected from Shopify
 * with the temporary token.
 * 
 * @author joe
 */
@Controller
@RequestMapping(OauthController.CONTROLLER_PATH)
public class OauthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OauthController.class);
    
    /** The URI of this controller. */
    public static final String CONTROLLER_PATH = "/shopify/oauth";
    
    @Autowired
    private ShopifyRestApiService shopifyRestApiService;
    @Autowired
    private ShopifyStoreService shopifyStoreService;
    @Autowired
    private AESUtility aesUtility;
    
    /** Base URL of the shop admin sites. */
    @Value("${shopify.shop.admin.base.url}")
    private String baseShopifyUrl;

    public OauthController() {
        LOGGER.debug(OauthController.class.getSimpleName() + " has been instantiated.");
    }
    
    /**
     * Controller for the landing page where the user is redirected from Shopify
     * with the temporary token.
     * 
     * @param tempToken Temporary token which can be exchanged for a permanent token
     * @param shopUrl Site name for the Shopify which determines the sub-domain of the REST service
     * @return 
     */
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody ModelAndView exchangingTempToken(
            @RequestParam("code") String tempToken, 
            @RequestParam("shop") String shopUrl ) {  
        
        LOGGER.debug("OAuth temporary token has been received. Shop: {}", shopUrl);
        
        // exchange temp token to permanent
        final String siteName = getStoreNameFromShopUrl(shopUrl);
        TokenExchangeResult result;
        try{
            result = shopifyRestApiService.exchangeTempTokenToPermanent(siteName, tempToken);
            LOGGER.debug("OAuth permanent token has been received. Result: {}", result);
        }catch(Exception ex){
            // we need to catch every type of exceptions here
            LOGGER.warn("An exception occured during the shopify temp token exchange process. ShopName: " + siteName, ex);
            result = TokenExchangeResult.error(ex.getMessage());
        }
                
        // construct a response
        ModelAndView mav;
        final ModelMap mm = new ModelMap("succeeded", result.succeeded());
        
        // if perm token has been received
        if (result.succeeded()){
            // a brand new store entity need to be created
            final StoreEntity storeEntity = shopifyStoreService.createStore(siteName, result.getPermToken());
        
            mm.addAttribute("storeId", storeEntity.getId());
            mm.addAttribute("storeName", storeEntity.getName());
            mm.addAttribute("storeTimezone", storeEntity.getTimeZone());
            mm.addAttribute("oauthPermToken",  aesUtility.aesDecrypt(storeEntity.getOauthToken()));
            mav = new ModelAndView("shopify/tokenexchange_success", mm);
        } else{
            mm.addAttribute("errorMsg", result.getErrorMsg());
            mm.addAttribute("httpCode", result.getHttpCode());
            mm.addAttribute("responseBody", result.getResponseBody());
            mav = new ModelAndView("shopify/tokenexchange_error", mm);
        }
        
        return mav;
    }
    
    // ----------------------------------------------------------------------
    // ~ Private methods
    // ----------------------------------------------------------------------
    
    
    /**
     * Retrieves the shop name from the shop url.
     * 
     * @param shopUrl
     * @return 
     */
    private String getStoreNameFromShopUrl(final String shopUrl){
        return shopUrl.replace(baseShopifyUrl, "");
    }
    


}
