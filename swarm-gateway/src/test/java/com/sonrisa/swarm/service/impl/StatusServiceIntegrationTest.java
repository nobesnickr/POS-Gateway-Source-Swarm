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

package com.sonrisa.swarm.service.impl;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.admin.model.StatusEntity;
import com.sonrisa.swarm.admin.model.query.StatusQueryEntity;
import com.sonrisa.swarm.admin.service.StatusService;
import com.sonrisa.swarm.admin.service.exception.InvalidStatusRequestException;
import com.sonrisa.swarm.admin.service.impl.StatusServiceImpl;
import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.model.legacy.StoreEntity;

/**
 * Integration test for the {@link StatusServiceImpl} class.
 * 
 * @author Barnabas
 */
@Transactional
public class StatusServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private StoreDao storeDao;

    /**
     * Target to be tested
     */
    @Autowired
    private StatusService target;
    
    /**
     * Date format used to create dates
     */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Test case: 
     *  There are some stores in the store table.
     *  
     * Expected: 
     *  Using the {@link StoreAdminQueryConfig} we can configure the query,
     *  and the service returns the appropriate subset from the stores table
     * @throws InvalidStatusRequestException 
     */
    @Test
    public void testStoreQueryConfigFiltering() throws ParseException, InvalidStatusRequestException{
                
        final Long erplyApiId = apiService.findByName("erply").getApiId();
        final Long shopifyApiId = apiService.findByName("shopify").getApiId();
        
        final StoreEntity oldStore = createAndSaveMockStore("Old store", erplyApiId, dateFormat.parse("2010-01-01"), true);
        final StoreEntity newStore = createAndSaveMockStore("New store", erplyApiId, dateFormat.parse("2014-04-01"), true);
        createAndSaveMockStore("Inactive store", erplyApiId, dateFormat.parse("2014-01-01"), false);
        createAndSaveMockStore("Shopify store", shopifyApiId, dateFormat.parse("2014-01-01"), false);
        
        StatusQueryEntity activeErplyConfig = new StatusQueryEntity();
        activeErplyConfig.setActive(true);
        activeErplyConfig.setApi(new HashSet<String>(Arrays.asList(new String[] {"erply"})));

        // Act
        List<StatusEntity> activeErplyStores = target.getStoreStatuses(activeErplyConfig);
        
        // Assert
        assertEquals(2, activeErplyStores.size());
        
        final StatusEntity mostRecentStore = activeErplyStores.get(0);
        assertEquals(newStore.getId(), mostRecentStore.getStoreId());
        assertEquals(newStore.getName(), mostRecentStore.getName());

        final StatusEntity oldestStore = activeErplyStores.get(1);
        assertEquals(oldStore.getId(), oldestStore.getStoreId());
        assertEquals(oldStore.getName(), oldestStore.getName());
    }
    
    /**
     * Creates mock store entity and saves it to database
     * @return
     */
    private StoreEntity createAndSaveMockStore(String name, Long apiId, Date created, Boolean active){
        StoreEntity store = new StoreEntity();
        store.setName(name);
        store.setApiId(apiId);
        store.setCreated(created); 
        store.setActive(active);
        storeDao.persist(store);
        return store;
    }
}
