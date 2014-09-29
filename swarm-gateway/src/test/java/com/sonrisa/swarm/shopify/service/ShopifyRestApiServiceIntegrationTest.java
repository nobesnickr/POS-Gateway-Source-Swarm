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
package com.sonrisa.swarm.shopify.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.shopify.service.ShopifyRestApiService;
import com.sonrisa.swarm.BaseIntegrationTest;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author joe
 */
public class ShopifyRestApiServiceIntegrationTest extends BaseIntegrationTest{
    
    @Autowired
    private ShopifyRestApiService restApiService;

    // TODO implement
    @Test
    @Ignore
    public void testSomeMethod() {
        final JsonNode storeInfo = restApiService.getStoreInfo("sondev2", "a173d948c294baaac5f1a96694d1022f");
        assertNotNull(storeInfo);
        System.out.println("storeInfo = " + storeInfo);
    }
}