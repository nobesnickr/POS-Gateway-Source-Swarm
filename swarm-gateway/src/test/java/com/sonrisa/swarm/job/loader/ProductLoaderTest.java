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

import static org.junit.Assert.*;
import hu.sonrisa.backend.dao.filter.SimpleFilter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sonrisa.swarm.MockRetailProData;
import com.sonrisa.swarm.job.loader.helper.LoaderTestHelper;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.model.legacy.ProductEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.ProductStage;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;

/**
 * Integration test for loading {@link ProductStage} entities.
 *
 * @author joe, Barna
 */
public class ProductLoaderTest extends BaseLoaderTest {

    @Autowired 
    private LoaderTestHelper testHelper;
    
    /**
     * Number of products the test uses for testing the loader.
     */
    private static final int NUM_OF_TEST_DATA = 102;
     
    /**
     * Name of the product loading step 
     */
    private static final String PRODUCT_STG_LOADING_STEP = "stageProductProcessing";
    
    /**
     * Test case:
     * It creates a bunch of staging products and stores for them. Then executes the loader step
     * and asserts that all of them has been moved to the data warehouse and checks the properties of one of them.
     */
    @Test
    public void testStepWithNewProducts() {
        final List<ProductStage> stgProds = createTestData();
        
        // executes the loader job
        stepExecute(PRODUCT_STG_LOADING_STEP);

        // assert
        final List<ProductEntity> prods = productService.find(
                new SimpleFilter<ProductEntity>(ProductEntity.class), 0, 0);

        assertNotNull(prods);
        assertEquals(NUM_OF_TEST_DATA, prods.size());

        // asserts the first product
        // we assume the rest behaves the same way
        final ProductStage stgProd = stgProds.get(0);
        // finds the RetailPro store for this staging product
        final RpStoreEntity rpStore = rpStoreService.findBySbsNoAndStoreNoAndSwarmId(stgProd.getLsSbsNo(), stgProd.getLsStoreNo(), stgProd.getSwarmId());
        assertNotNull(rpStore);
        // finds the store by the RetailPro store
        final StoreEntity store = storeService.find(rpStore.getStoreId());
        assertNotNull(store);
        // find the product in the data warehouse
        final ProductEntity prod = productDao.findByStoreAndForeignId(store.getId(), Long.parseLong(stgProd.getLsProductId()));
        assertNotNull(prod);

        // asserts whether the two properties of the two products equal.
        assertProductEquals(stgProd, prod);
        
        // asserts that the staging products have been removed
        assertTrue(productStagingService.findAllIds().isEmpty());

    }
    
 /**
     * Test case:
     * A staging product modifies an existing product.
     * 
     */
    @Test
    public void testStepWithExistingProduct() {
        final String swarmId = "swarmId";
        final String sbs = "sbs1";
        final String storeNo = "store1";
        final Long lsProductId = 12l;
        
        // creates a store 
        StoreEntity strEntity = MockTestData.mockStoreEntity("myStore");
        storeService.save(strEntity);        
        // creates a product
        final ProductEntity prod = MockTestData.mockProduct(lsProductId, strEntity);       
        productService.save(prod);
        
        assertEquals("One and only one product has to exist.", 1, productDao.findAll().size());
        
        // creates a RetailPro store
        RpStoreEntity rpStore = MockRetailProData.mockRpStoreEntity(swarmId, sbs, storeNo);
        rpStore.setStoreId(strEntity.getId());
        rpStoreService.save(rpStore);
                                
        // creates a staging product 
        final String modifiedDesc = "modified desc";
        final String modifiedSku = "modified sku";
        final ProductStage stgProd = MockTestData.mockProductStage(swarmId, sbs, storeNo, lsProductId.toString());
        stgProd.setDescription(modifiedDesc);
        stgProd.setSku(modifiedSku);
        productStagingService.save(stgProd);
        
        // executes the loader job        
        stepExecute(PRODUCT_STG_LOADING_STEP);
        
        // asserts
        // we expect that the job modifies the existing product
        assertEquals("One and only one product has to exist.", 1, productDao.findAll().size());
        final ProductEntity modifiedProd = productService.find(prod.getId());
        assertProductEquals(stgProd, modifiedProd);        
        
        // asserts that the staging products have been removed
        assertTrue(productStagingService.findAllIds().isEmpty());
    }    
    
    /**
     * Test case:
     * Moving a staging product, who has a storeId to the data warehouse.
     * 
     * Expected result:
     * The loader will be able to identify the store for the product by its storeId
     * and the product can be moved to the data warehouse.
     * 
     */
    @Test
    public void testStepWithStoreId() {
        assertEquals("No product should exist at the beginning.", 0, productDao.findAll().size());
        
        // creates a store 
        StoreEntity strEntity = MockTestData.mockStoreEntity("myStore");
        storeService.save(strEntity);                 
              
        // creates a staging product 
        final Long lsProductId = 12l;
        final ProductStage stgProd = MockTestData.mockProductStage(null, null, null, lsProductId.toString());
        stgProd.setStoreId(strEntity.getId());
        productStagingService.save(stgProd);
        
        // executes the loader job        
        stepExecute(PRODUCT_STG_LOADING_STEP);
        
        // asserts
        // we expect that the job moved the product to the data warehouse
        final List<ProductEntity> prods = productDao.findAll();
        assertEquals("One and only one product has to exist.", 1, prods.size());
        final ProductEntity movedCustomer = productService.find(prods.get(0).getId());
        assertProductEquals(stgProd, movedCustomer);        
        
        // asserts that the staging product has been removed
        assertTrue(productStagingService.findAllIds().isEmpty());
    }
    
    /**
     * Test case:
     *  No category or manufacturer value for product, but it references
     *  entities. 
     * 
     * Expected result:
     *  The loader will be able to find these entities in the legacy tables
     *  and copy their names onto the product entities.
     * 
     * @see {@link InvoiceLoaderTest#testCopyingReferencedProductProperties()}
     */
    @Test
    public void testProductReferencesEntities() {

        // creates a store 
        StoreEntity strEntity = MockTestData.mockStoreEntity("myStore");
        storeService.save(strEntity);    

        // Insert category and manufacturer and products
        final String categoryName = "Enchanted Blades";
        final String manufacturerName = "Magical Kingdom Co.";
        
        List<ProductStage> stgProds = testHelper.setupStageWithProductsCategoriesAndManufacturers(strEntity.getId(), categoryName, manufacturerName);
        
        // executes the whole loader job
        launchJob();

        // asserts
        assertProducts((ProductStage[])stgProds.toArray());
        
        List<ProductEntity> legacyProducts = productDao.findAll();
        assertEquals(2, legacyProducts.size());
        testHelper.sortProductsByLegacyId(legacyProducts);
        
        assertEquals("Referenced category name not copied", categoryName, legacyProducts.get(0).getCategory());
        assertEquals("Referenced manufacturer name not copied", manufacturerName, legacyProducts.get(0).getManufacturer());
        assertNull(legacyProducts.get(1).getCategory());
        assertNull(legacyProducts.get(1).getManufacturer());
    }
    
    /**
     * Test case:
     * Moving a staging product that has a storeId to the data warehouse
     * and creating another one that has an invalid storeId.
     * 
     * Expected result:
     * The loader will be able to identify the store for the valid product by its storeId
     * and the product can be moved to the data warehouse. The invalid product remains 
     * in the staging DB.
     * 
     */
    @Test
    public void testStepWithInvalidStoreId() {
        assertEquals("No product should exist at the beginning.", 0, productDao.findAll().size());
        
        // creates a store 
        StoreEntity strEntity = MockTestData.mockStoreEntity("myStore");
        storeService.save(strEntity);                 
              
        // creates a staging product 
        final Long lsProductId = 12l;
        final ProductStage validStgProd = MockTestData.mockProductStage(lsProductId.toString(), strEntity.getId());        
        productStagingService.save(validStgProd);
        
        // creates a staging product with invalid storeID
        final Long lsProductId2 = 122l;
        final ProductStage invalidStgProd = MockTestData.mockProductStage(lsProductId2.toString(), Long.MAX_VALUE);        
        final Long invalidProductId = productStagingService.save(invalidStgProd);
        
        // executes the loader job        
        stepExecute(PRODUCT_STG_LOADING_STEP);
        
        // asserts
        // we expect that the job moved the product to the data warehouse
        final List<ProductEntity> prods = productDao.findAll();
        assertEquals("One and only one product has to exist.", 1, prods.size());
        final ProductEntity movedCustomer = productService.find(prods.get(0).getId());
        assertProductEquals(validStgProd, movedCustomer);        
        
        // asserts that the product with the invalid storeId has been skipped and he remained in the staging DB
        final List<Long> remainedInStaging = productStagingService.findAllIds();
        assertEquals("The skipped product has been deleted as well!", 1, remainedInStaging.size());        
        assertEquals(invalidProductId, remainedInStaging.get(0));
    }    
    
     /**
     * 
     *
     * @return
     */
    private List<ProductStage> createTestData() {
        final List<ProductStage> result = new ArrayList<ProductStage>();

        final String swarmId = "swarmId";
        final String sbs = "sbs1";
        final Set<String> storeNums = new HashSet();

        // staging products
        for (int i = 0; i < NUM_OF_TEST_DATA; i++) {
            final String storeNo = "store" + new Double(Math.floor(i / 100)).intValue();
            storeNums.add(storeNo);
            final ProductStage prod = MockTestData.mockProductStage(swarmId, sbs, storeNo, Integer.toString(i));
            result.add(prod);

            productStagingService.save(prod);
        }

        // stores
        createMockStores(storeNums, swarmId, sbs);

        return result;
    }
}
