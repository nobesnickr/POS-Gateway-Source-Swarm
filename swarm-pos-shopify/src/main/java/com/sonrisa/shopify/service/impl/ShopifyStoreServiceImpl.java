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
package com.sonrisa.shopify.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.shopify.ShopifyAPI;
import com.sonrisa.shopify.service.ShopifyRestApiService;
import com.sonrisa.shopify.service.ShopifyStoreService;
import com.sonrisa.swarm.common.util.JsonUtil;
import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;
import com.sonrisa.swarm.posintegration.service.ApiService;

import hu.sonrisa.backend.dao.filter.SimpleFilter;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ShopifyStoreService} which
 * creates and updates Shopify stores.
 * 
 * @author joe
 */
@Service
public class ShopifyStoreServiceImpl implements ShopifyStoreService{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopifyRestApiServiceImpl.class);
    
    @Autowired
    private ShopifyRestApiService restApiService;

    /** Legacy store service. */
    @Autowired
    private ApiService apiService;
    
    /** Legacy store service. */
    @Autowired
    private StoreService storeService;
    
    @Autowired
    private AESUtility aesUtility;

    /**
     * {@inheritDoc }
     * 
     * @param siteName
     * @param token
     * @return 
     */
    @Override
    public StoreEntity createStore(String siteName, String token) {
        
        // retrieves store info from Shopify 
        final JsonNode storeInfo = restApiService.getStoreInfo(siteName, token);
        // Shopidy API id in the legacy Swarm DB
        final Long shopifyApiId = getShopifyApiId();
        
        StoreEntity entity = findStoreByName(shopifyApiId, siteName);
        if(entity == null){                
            LOGGER.debug("A new Shopify store has been created with this name: {},", siteName);
            
            entity = new StoreEntity();
            
            // stores are created with an inactive status by default
            entity.setActive(Boolean.FALSE);
            entity.setCreated(new Date());
            entity.setApiId(shopifyApiId);
            entity.setUsername(aesUtility.aesEncryptToBytes(siteName));
        }else{
            LOGGER.debug("A Shopify store already exists with this name: {}, "
                    + "Oauth token and some other fields will be updated.", siteName);
        }
        
        // sets or updates the Oauth token and some other fields
        entity.setOauthToken(aesUtility.aesEncryptToBytes(token));
        entity.setName(getHumanReadableStoreName(storeInfo));
        entity.setTimeZone(getShopTimezone(storeInfo));
        
        storeService.save(entity);
        
        return entity;
    }
    
    // ------------------------------------------------------------------------
    // ~ Private methods
    // ------------------------------------------------------------------------
    
    /**
     * Checks whether a shopify store with this siteName (aka userName) already exists in the legacy DB.
     * 
     * @param siteName
     * @return 
     */
    private StoreEntity findStoreByName(final Long apiId, final String siteName){
        SimpleFilter<StoreEntity> filter = SimpleFilter.of(StoreEntity.class)
                .addParameter(StoreEntity.FIELD_API_ID, apiId)
                .addParameter(StoreEntity.FIELD_USERNAME, aesUtility.aesEncryptToBytes(siteName));
        final List<StoreEntity> storeFromDb = storeService.find(filter, 0, 0);
        
        if (storeFromDb.size() > 1){
            throw new RuntimeException("Maximum one Shopify (apiId: "+apiId
                    +") store should exists with this userName(siteName): "+siteName);
        }
        
        
        return storeFromDb.isEmpty() ? null : storeFromDb.get(0);
    }
    
    /**
     * Returns the ID of the Shopify API from the legacy DB.
     * 
     * @return 
     */
    private Long getShopifyApiId(){
        return apiService.findByName(ShopifyAPI.SHOPIFY_API_NAME).getApiId();
    }

    /**
     * Returns the timezone of the shop from the JSON.
     * 
     * @param storeInfo
     * @return 
     */
    private String getShopTimezone(final JsonNode storeInfo) {
        // TODO is time zone name conversion required?
        return JsonUtil.getJsonField(storeInfo, ShopifyAPI.API_FIELD_SHOP_TIMEZONE);
    }
    
    /**
     * Returns the human readable name of the store from the JSON.
     * 
     * @param storeInfo
     * @return 
     */
    private String getHumanReadableStoreName(final JsonNode storeInfo){
        return JsonUtil.getJsonField(storeInfo, ShopifyAPI.API_FIELD_SHOP_NAME);
    }
    

    
}
