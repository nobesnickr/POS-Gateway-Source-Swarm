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
package com.sonrisa.swarm.job.loader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sonrisa.swarm.BaseBatchTest;
import com.sonrisa.swarm.MockRetailProData;
import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.retailpro.dao.impl.RpStoreDaoImpl;
import com.sonrisa.swarm.retailpro.loader.store.RpStoreProcessor;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;

/**
 * Unit test for the {@link RpStoreProcessor} loader job responsible
 * for loading stores from the <code>stores_rp</code> to <code>stores</code>
 * 
 * @author joe
 */
public class StoreCheckTest extends BaseBatchTest {
    
    @Autowired
    @Qualifier("storeJobTestUtil")    
    private JobLauncherTestUtils jobLauncherTestUtils;
    
    @Autowired
    private RpStoreDaoImpl rpStoreDao;
    
    @Autowired
    private StoreDao storeDao;

    private static final String API_NAME_RETAIL_PRO8 = "retailpro8";
    
    private static final String API_NAME_RETAIL_PRO9 = "retailpro9";
    
    private Long rpApiId;
    
    private Long rpApiId9;
    
    @Before
    public void initRpApiId(){
        // init the retailPro v8 API id  to use it in assertations 
        rpApiId = apiService.findByName(API_NAME_RETAIL_PRO8).getApiId();
        rpApiId9 = apiService.findByName(API_NAME_RETAIL_PRO9).getApiId();
        assertNotNull(rpApiId);
        assertNotNull(rpApiId9);
    }
    
    /**
     * Test case: 
     *  There are three Retail Pro stores, one with empty, one with retailpro8
     *  and one with retail9 as pos software.
     *  
     * Expected:
     *  - Empty and retailpro8 will be retailpro8
     */
    @Test
    public void testApiIdByPosSoftware(){
        final String emptySwarmId = "legacy-client";
        final String rp8SwarmId = "retailpro8-client";
        final String rp9SwarmId = "retailpro9-client";
        
        final String sbs = "sbs05";
        final String storeNo = "11";
        
        final String nullString = null;        
        rpStoreService.save(emptySwarmId, nullString,
                MockRetailProData.mockRpJsonStore("legacy", sbs, storeNo));
        
        rpStoreService.save(rp8SwarmId, API_NAME_RETAIL_PRO8,
                MockRetailProData.mockRpJsonStore("store8", sbs, storeNo));
        
        rpStoreService.save(rp9SwarmId, API_NAME_RETAIL_PRO9,
                MockRetailProData.mockRpJsonStore("store9", sbs, storeNo));
        
        // Act
        executeStep();
        
        // Assert
        assertEquals(rpApiId, getStoreBySbsNoAndStoreNoAndSwarmId(sbs, storeNo, emptySwarmId).getApiId());
        assertEquals(rpApiId, getStoreBySbsNoAndStoreNoAndSwarmId(sbs, storeNo, rp8SwarmId).getApiId());
        assertEquals(rpApiId9, getStoreBySbsNoAndStoreNoAndSwarmId(sbs, storeNo, rp9SwarmId).getApiId());
    }
    
      
    /**
     * Test case:
     * Creates a store entity in the analytics (aka legacy) DB, and a few RetailPro stores.
     * It creates a relation between one of the RetailPro stores and the store in the legacy DB.
     * 
     * Then launches the "storeCheck" step which contains the {@link RpStoreProcessor} processor.
     * 
     * Expected result:
     * After the step execution the RetailPro stores have got their store_ids
     * and new legacy store entities have been created in the analytics area.
     * 
     * 
     * @throws JobExecutionAlreadyRunningException
     * @throws JobRestartException
     * @throws JobInstanceAlreadyCompleteException
     * @throws JobParametersInvalidException 
     */
    @Test
    public void stepTest() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {                
        
        // creates a legacy store entity in the analytics area
        final String existingStoreName = "store1";
        StoreEntity existingStore = MockTestData.mockStoreEntity(existingStoreName);
        existingStore.setApiId(rpApiId);
        Long existingStoreId = storeService.save(existingStore);
        assertNotNull(existingStoreId);
        
        // creates a few RetailPro store entity
        final String swarmId = "swarmId";
        final String sbs01 = "01";
        final String sbs02 = "09";
        final String storeNo1 = "01";
        final String storeNo2 = "02";
        rpStoreService.save(swarmId,
                MockRetailProData.mockRpJsonStore(existingStoreName, sbs01, storeNo1),
                MockRetailProData.mockRpJsonStore(generateMockStoreName(sbs01, storeNo2), sbs01, storeNo2),
                MockRetailProData.mockRpJsonStore(generateMockStoreName(sbs02, storeNo1), sbs02, storeNo1));
        
        // connects one of the RetailPro store entities with the legacy store 
        RpStoreEntity existingRpStore = rpStoreService.findBySbsNoAndStoreNoAndSwarmId(sbs01, storeNo1, swarmId);
        assertNotNull(existingRpStore);        
        existingRpStore.setStoreId(existingStoreId);
        rpStoreService.save(existingRpStore);

        // executes the loader job
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("storeCheck");
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        
        // asserts whether the RetailPro stores have got their store_ids 
        // and the new legacy store entities have been created in the analytics area        
        for (RpStoreEntity rpStore : rpStoreDao.findAll()){
            final Long storeId = rpStore.getStoreId();
            assertNotNull("The retailPro store have not got its store_id.", storeId);
            
            final StoreEntity store = storeDao.findById(storeId);
            assertNotNull(store);
            
            // this store is a new and it has been created from a RpStore 
            if (!existingStoreId.equals(storeId)){
                final String expectedStoreName = generateMockStoreName(
                        rpStore.getSbsNumber(), rpStore.getStoreNumber())
                        + " (StoreNo: "
                        + rpStore.getStoreNumber()
                        + ", SBS: "
                        + rpStore.getSbsNumber() + ")";
                        
                assertEquals(expectedStoreName, store.getName());        
                assertEquals("Created by RetailPro store processor job.", store.getNotes());
            }else{
                // this is the existing store
                assertEquals(existingStore.getName(), store.getName());        
            }
            assertEquals("ApiID is missing from the store record.", rpApiId, store.getApiId());
        }
    }
    
    @Test    
    public void moreThanOneSubStore(){
        
        final String swarmId = "swarmId";
        final String sbs01 = "sbs01";
        final String storeNo1 = "11";
        rpStoreService.save(swarmId,
                MockRetailProData.mockRpJsonStore("store1", sbs01, storeNo1),
                MockRetailProData.mockRpJsonStore("store2", sbs01, "12"),
                MockRetailProData.mockRpJsonStore("store3", sbs01, "13"),
                MockRetailProData.mockRpJsonStore("store4", sbs01, "14"),
                MockRetailProData.mockRpJsonStore("store5", sbs01, "15"),
                MockRetailProData.mockRpJsonStore("store6", "sbs2", "21"),
                MockRetailProData.mockRpJsonStore("store7", "sbs2", "22"),
                MockRetailProData.mockRpJsonStore("store8", "sbs3", "31"),
                MockRetailProData.mockRpJsonStore("store9", "sbs3", "32"));
        
        // executes the loader job
        executeStep();   
        
        final List<StoreEntity> stores = storeDao.findAll();
        assertEquals(9, stores.size());
        for(StoreEntity strEntity : stores){
            assertEquals("ApiID is missing from the store record.", rpApiId, strEntity.getApiId());        
        }    
    }
    
    /**
     * Generates mock store name for a store based on sbsNo and storeNo
     * @param sbsNo
     * @param storeNo
     * @return <code>Store [sbsNo] [storeNo]</code>
     */
    private String generateMockStoreName(final String sbsNo, final String storeNo){
        return "Store " + storeNo + "/" + sbsNo;
    }
    
    /**
     * Reads the {@link StoreEntity} associated with the swarmId
     * @param swarmId
     * @return
     */
    private StoreEntity getStoreBySbsNoAndStoreNoAndSwarmId(final String sbsNo, final String storeNo, final String swarmId){
        RpStoreEntity rpStore = rpStoreService.findBySbsNoAndStoreNoAndSwarmId(sbsNo, storeNo, swarmId);
        return storeDao.findById(rpStore.getStoreId());
    }
    
    private void executeStep(){
        // executes the loader job
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("storeCheck");
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());     
    }
}