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

package com.sonrisa.swarm;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonrisa.swarm.legacy.dao.CategoryDao;
import com.sonrisa.swarm.legacy.dao.CustomerDao;
import com.sonrisa.swarm.legacy.dao.InvoiceDao;
import com.sonrisa.swarm.legacy.dao.InvoiceLineDao;
import com.sonrisa.swarm.legacy.dao.ManufacturerDao;
import com.sonrisa.swarm.legacy.dao.ProductDao;
import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.mock.MockPosDataDescriptor;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.BaseStageEntity;
import com.sonrisa.swarm.posintegration.dto.CategoryDTO;
import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.dto.ManufacturerDTO;
import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalExtractor;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;
import com.sonrisa.swarm.staging.service.BaseStagingService;
import com.sonrisa.swarm.staging.service.CategoryStagingService;
import com.sonrisa.swarm.staging.service.CustomerStagingService;
import com.sonrisa.swarm.staging.service.InvoiceLineStagingService;
import com.sonrisa.swarm.staging.service.InvoiceStagingService;
import com.sonrisa.swarm.staging.service.ManufacturerStagingService;
import com.sonrisa.swarm.staging.service.ProductStagingService;

/**
 * Base class for testing {@link ExternalExtractor} implementations
 */
public abstract class BaseExtractionIntegrationTest extends BaseBatchTest {
    /** Port used by the Mock Shopify server in this test class. */
    public static final int WIREMOCK_PORT = 5555;

    @Autowired
    protected CategoryStagingService categoryStgService;
    @Autowired
    protected ManufacturerStagingService manufacturerStgService;
    @Autowired
    protected CustomerStagingService customerStgService;
    @Autowired
    protected ProductStagingService productStgService;
    @Autowired
    protected InvoiceStagingService invoiceStgService;
    @Autowired
    protected InvoiceLineStagingService invoiceLineStgService;
    
    @Autowired
    protected CategoryDao categoryDao;
    @Autowired
    protected ManufacturerDao manufacturerDao;
    @Autowired
    protected CustomerDao customerDao;
    @Autowired
    protected ProductDao productDao;
    @Autowired
    protected InvoiceDao invoiceDao;
    @Autowired
    protected InvoiceLineDao invoiceLineDao;
        
    @Autowired
    protected StoreService storeService;
            
    /** Service encrypting and decrypting strings from the MySQL database */
    @Autowired
    protected AESUtility aesUtility;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WIREMOCK_PORT);
    
    /**
     * Assert that the number staging entities matches the number in the mock entities
     * @param dataDescriptor
     */
    protected void assertStagingCount(MockPosDataDescriptor dataDescriptor){
        assertStagingEntityCount(dataDescriptor.getCountForDTOClass(CategoryDTO.class), categoryStgService);
        assertStagingEntityCount(dataDescriptor.getCountForDTOClass(ManufacturerDTO.class), manufacturerStgService);
        assertStagingEntityCount(dataDescriptor.getCountForDTOClass(CustomerDTO.class), customerStgService);
        assertStagingEntityCount(dataDescriptor.getCountForDTOClass(InvoiceDTO.class), invoiceStgService);
        assertStagingEntityCount(dataDescriptor.getCountForDTOClass(InvoiceLineDTO.class), invoiceLineStgService);
        assertStagingEntityCount(dataDescriptor.getCountForDTOClass(ProductDTO.class), productStgService);
    }
    
    /**
     * Helper method for {@link BaseExtractionIntegrationTest#assertStagingCount(MockPosDataDescriptor)}
     */
    private <T extends BaseStageEntity>void assertStagingEntityCount(int expectedCount, BaseStagingService<T> stgService){
    	List<T> stgEntities = stgService.findByIds(stgService.findAllIds());
    	assertEquals(stgEntities.toString(), expectedCount, stgEntities.size());
    }
    
    /**
     * Assert that the number staging entities matches the number in the mock entities
     * @param dataDescriptor
     */
    protected void assertStagingIsEmpty(){
        assertEquals(0, categoryStgService.findAllIds().size());
        assertEquals(0, manufacturerStgService.findAllIds().size());
        assertEquals(0, customerStgService.findAllIds().size());
        assertEquals(0, invoiceStgService.findAllIds().size());
        assertEquals(0, invoiceLineStgService.findAllIds().size());
        assertEquals(0, productStgService.findAllIds().size());      
    }
    
    /**
     * Assert that the number legacy entities matches the number in the mock entities, ignoring dummy entities
     * @param dataDescriptor
     */
    protected void assertNonDummyLegacyCount(MockPosDataDescriptor dataDescriptor){
        // Potential dummy entities
        assertEquals(dataDescriptor.getCountForDTOClass(CategoryDTO.class), categoryDao.findAll().size());
        assertEquals(dataDescriptor.getCountForDTOClass(ManufacturerDTO.class), manufacturerDao.findAll().size());
        assertEquals(dataDescriptor.getCountForDTOClass(CustomerDTO.class), customerDao.findAll().size());
        
        // No dummy entities for invoices, invoice lines and products
        assertEquals(dataDescriptor.getCountForDTOClass(InvoiceDTO.class), invoiceDao.findAll().size());
        assertEquals(dataDescriptor.getCountForDTOClass(InvoiceLineDTO.class), invoiceLineDao.findAll().size());
        assertEquals(dataDescriptor.getCountForDTOClass(ProductDTO.class), productDao.findAll().size());  
    }
    
    /**
     * Creates an active and a inactive store in the database
     * @param apiName
     * @returns The ID of the active store
     */
    protected Long saveMockStoreEntities(String apiName){
    	saveSingleMockStoreEntity(apiName, "54321", false);
    	return saveSingleMockStoreEntity(apiName, "12345", true);
    }
    
    /**
     * Creates a store entity in the <code>stores</code> table
     * @param apiName
     * @returns The ID of the active store
     */
    protected Long saveSingleMockStoreEntity(String apiName, String storeName, boolean active){
        final Long apiId = apiService.findByName(apiName).getApiId();
        StoreEntity store = createMockStore(apiId, storeName);
        store.setActive(active);
        
        final Long storeId = storeService.save(store);
        storeService.save(store);
        return storeId;
    }
    
    /**
     * Get mock store
     * @return 
     */
    protected StoreEntity createMockStore(final Long apiId, String storeName) {
        final StoreEntity store = MockTestData.mockStoreEntity("myStore");
        store.setApiId(apiId);
        store.setApiUrl(aesUtility.aesEncryptToBytes("http://localhost:"+WIREMOCK_PORT+"/"));
        store.setApiKey(aesUtility.aesEncryptToBytes("API_KEY"));
        store.setUsername(aesUtility.aesEncryptToBytes(storeName));
        store.setPassword(aesUtility.aesEncryptToBytes("password"));
        store.setOauthToken(aesUtility.aesEncryptToBytes("abcd1234"));
        store.setAccountNumber(12345);
        store.setStoreFilter("1");        
        return store;
    }
}
