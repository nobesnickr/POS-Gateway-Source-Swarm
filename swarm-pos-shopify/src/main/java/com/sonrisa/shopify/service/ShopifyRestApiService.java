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
package com.sonrisa.shopify.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.shopify.model.TokenExchangeResult;

/**
 * This service encapsulates various REST operations
 * that cat be executed through the Shopify REST API.
 *
 * @author joe
 */
public interface ShopifyRestApiService {
    
    /**
     * Exchange the temporary token for a permanent access token using the following request:
     * 
     * POST https://SHOP_NAME.myshopify.com/admin/oauth/access_token
     * with the following parameters:
     * 
     * client_id (required): The API key for swarm app.
     * client_secret (required): The shared secret for the app.
     * code (required): The token we received
     * 
     * The response will contain your access token.
     * 
     * @param siteName
     * @param tempToken
     * @return 
     */
    TokenExchangeResult exchangeTempTokenToPermanent(final String siteName, final String tempToken);
    
    /**
     * Retrieves every properties of a Shopify Store.
     * 
     * See: http://docs.shopify.com/api/shop
     * 
     * @param siteName
     * @param token
     * @return 
     */
    JsonNode getStoreInfo(final String siteName, final String token);
    
    
    
}
