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

package com.sonrisa.swarm.job.loader.helper;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.legacy.dao.CategoryDao;
import com.sonrisa.swarm.legacy.dao.CustomerDao;
import com.sonrisa.swarm.legacy.dao.InvoiceDao;
import com.sonrisa.swarm.legacy.dao.InvoiceLineDao;
import com.sonrisa.swarm.legacy.dao.ManufacturerDao;
import com.sonrisa.swarm.legacy.dao.ProductDao;
import com.sonrisa.swarm.legacy.service.CategoryService;
import com.sonrisa.swarm.legacy.service.CustomerService;
import com.sonrisa.swarm.legacy.service.InvoiceLineService;
import com.sonrisa.swarm.legacy.service.InvoiceService;
import com.sonrisa.swarm.legacy.service.ManufacturerService;
import com.sonrisa.swarm.legacy.service.ProductService;
import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.model.legacy.InvoiceLineEntity;
import com.sonrisa.swarm.model.legacy.ProductEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.CategoryStage;
import com.sonrisa.swarm.model.staging.ManufacturerStage;
import com.sonrisa.swarm.model.staging.ProductStage;
import com.sonrisa.swarm.staging.service.CategoryStagingService;
import com.sonrisa.swarm.staging.service.CustomerStagingService;
import com.sonrisa.swarm.staging.service.InvoiceLineStagingService;
import com.sonrisa.swarm.staging.service.InvoiceStagingService;
import com.sonrisa.swarm.staging.service.ManufacturerStagingService;
import com.sonrisa.swarm.staging.service.ProductStagingService;

/**
 * Helper class for loader tests, which sets up certain test scenarios. 
 * 
 * @author Barnabas Szirmay <szirmayb@sonrisa.hu>
 */
@Component
public class LoaderTestHelper {

    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private InvoiceLineService invoiceLineService;
    @Autowired
    private InvoiceStagingService invoiceStagingService;
    @Autowired
    private InvoiceLineStagingService invoiceLineStagingService;
    @Autowired
    private InvoiceDao invoiceDao;
    @Autowired
    private InvoiceLineDao invoiceLineDao;
    
    @Autowired
    private CustomerStagingService customerStagingService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerDao customerDao;
    
    @Autowired
    private ProductStagingService productStagingService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductDao productDao;
    
    @Autowired
    private CategoryStagingService categoryStagingService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryDao categoryDao;
    
    @Autowired
    private ManufacturerStagingService manufacturerStagingService;
    @Autowired
    private ManufacturerService manufacturerService;
    @Autowired
    private ManufacturerDao manufacturerDao;

    @Autowired
    private StoreService storeService;
    
    /**
     * Sets up staging tables with:
     * <ul>
     *  <li>1 store</li>
     *  <li>1 category &amp; 1 manufacturer</li>
     *  <li>1 product referencing valid category and manufacturer entities</li>
     *  <li>1 product referencing non-existing category and manufacturer entities</li>
     * </ul>
     * 
     * @returns Products set up and inserted
     */
    public List<ProductStage> setupStageWithProductsCategoriesAndManufacturers(final Long storeId, final String categoryName, final String manufacturerName){
        
        // Create category
        final Long lsCategoryId = 56789L;
        CategoryStage stgCategory = MockTestData.mockCategoryStage(lsCategoryId.toString(), storeId);
        stgCategory.setName(categoryName);
        categoryStagingService.save(stgCategory);
        
        // Create manufacturer
        final Long lsManufacturerId = 1177L;
        ManufacturerStage stgManufacturer = MockTestData.mockManufacturerStage(lsManufacturerId.toString(), storeId);
        stgManufacturer.setManufacturerName(manufacturerName);
        manufacturerStagingService.save(stgManufacturer);

        // creates a staging product, to source from where the fields should be copied
        final Long lsProductIdForValid = 12L;
        final Long lsProductIdForMissing = 15L;
        final ProductStage stgProdWithValid = MockTestData.mockProductStage(lsProductIdForValid.toString(), storeId);
        final ProductStage stgProdWithMissing = MockTestData.mockProductStage(lsProductIdForMissing.toString(), storeId);
        
        // Force values to be empty
        stgProdWithValid.setCategory(null);
        stgProdWithValid.setManufacturer(null);
        stgProdWithMissing.setCategory(null);
        stgProdWithMissing.setManufacturer(null);
        
        // But set referenced entities
        stgProdWithValid.setLsManufacturerId(lsManufacturerId.toString());
        stgProdWithValid.setLsCategoryId(lsCategoryId.toString());
        stgProdWithMissing.setLsManufacturerId("112233445566"); // no such manufacturer
        stgProdWithMissing.setLsCategoryId("665544332211"); // no such category
        
        productStagingService.save(stgProdWithValid);
        productStagingService.save(stgProdWithMissing);
        
        return Arrays.asList(stgProdWithValid, stgProdWithMissing);
    }

    /**
     * Sorts a list of {@link InvoiceLineEntity} by their legacy id
     * @param legacyLines
     */
    public void sortLinesByLegacyId(List<InvoiceLineEntity> legacyLines){
        Collections.sort(legacyLines, new Comparator<InvoiceLineEntity>(){
            @Override
            public int compare(InvoiceLineEntity arg0, InvoiceLineEntity arg1) {
                return (int)(arg0.getLsLineId() - arg1.getLsLineId());  
            }
        });
    }

    /**
     * Sorts a list of {@link ProductEntity} by their legacy id
     * @param legacyProducts
     */
    public void sortProductsByLegacyId(List<ProductEntity> legacyProducts) {
        Collections.sort(legacyProducts, new Comparator<ProductEntity>(){
            @Override
            public int compare(ProductEntity arg0, ProductEntity arg1) {
                return (int)(arg0.getLsProductId() - arg1.getLsProductId());  
            }
        });
    }
}
