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

import java.util.List;

import org.junit.Test;

import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.CategoryStage;
import com.sonrisa.swarm.model.staging.CustomerStage;
import com.sonrisa.swarm.model.staging.InvoiceLineStage;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.model.staging.ManufacturerStage;
import com.sonrisa.swarm.model.staging.ProductStage;

/**
 * Test cases for the whole staging loading job.
 *
 * @author joe
 */
public class StagingLoaderTest extends BaseLoaderTest {

    /**
     * Test loading entities from the staging tables to the legacy tables
     */
    @Test
    public void testBestCaseScenario(){
         // creates a store 
        StoreEntity strEntity = MockTestData.mockStoreEntity("myStore");
        storeService.save(strEntity);                 
        final Long storeId = strEntity.getId();
              
        // creates a staging product 
        final Long lsProductId1 = 12l;
        final Long lsProductId2 = 46l;
        final ProductStage stgProd1 = MockTestData.mockProductStage(lsProductId1.toString(), storeId);
        final ProductStage stgProd2 = MockTestData.mockProductStage(lsProductId2.toString(), storeId);
        productStagingService.save(stgProd1);
        productStagingService.save(stgProd2);
        
        // creates a staging customer 
        final Long lsCustomerId = 112l;
        final CustomerStage stgCust = MockTestData.mockCustomerStage(lsCustomerId.toString(), "name", storeId);
        customerStagingService.save(stgCust);        
        
        // creates a staging invoice
        final Long lsInvoiceId = 99l;
        final InvoiceStage stgInvoice = MockTestData.mockInvoice(lsInvoiceId.toString(), lsCustomerId.toString(), storeId, "0.99");
        invoiceStagingService.save(stgInvoice);
        
        // creates staging invoice lines
        InvoiceLineStage stgLine1 = MockTestData.mockInvoiceLine(stgInvoice, stgProd1, "1", storeId);
        InvoiceLineStage stgLine2 = MockTestData.mockInvoiceLine(stgInvoice, stgProd2, "2", storeId);
        invoiceLineStagingService.save(stgLine1);
        invoiceLineStagingService.save(stgLine2);
        
        // create staging category entries
        CategoryStage stgCategory1 = MockTestData.mockCategoryStage("123", storeId);
        CategoryStage stgCategory2 = MockTestData.mockCategoryStage("456", storeId);
        categoryStagingService.save(stgCategory1);
        categoryStagingService.save(stgCategory2); 
        
        // create staging manufactures
        ManufacturerStage stgManufacturer = MockTestData.mockManufacturerStage("678", storeId);
        manufacturerStagingService.save(stgManufacturer);
        
         // executes the whole loader job twice
        launchJob();       
        
        // asserts
        assertProducts(stgProd1, stgProd2);
        assertCustomers(stgCust);        
        final ProductStage[] stgProds = {stgProd1, stgProd2};
        final InvoiceLineStage[] stgLines = {stgLine1, stgLine2};
        assertInvoice(storeId, stgInvoice, stgProds, stgLines); 
        assertCategory(stgCategory1, stgCategory2);
        assertManufacturer(stgManufacturer);
    }
    
    /**
    * This test tests the behavior of the batch insert procedure
    * if duplicate entries are present in the staging tables,
    * and they collide. 
    * 
    * Expected behavior: Customer, Product, etc. entries do not halt
    * with exception, and are inserted in batch size segments, not one by
    * one. 
    */
   @Test
   public void testDuplicateEntriesInTheStagingTables(){
       // creates a store 
       StoreEntity strEntity = MockTestData.mockStoreEntity("myStore");
       storeService.save(strEntity);
       final Long storeId = strEntity.getId();
  

       // creates a staging product and inserts it twice
       final Long lsProductId1 = 12l;
       final ProductStage stgProd1 = MockTestData.mockProductStage(lsProductId1.toString(), storeId);
       final ProductStage stgProd2 = MockTestData.mockProductStage(lsProductId1.toString(), storeId);
       productStagingService.save(stgProd1);
       productStagingService.save(stgProd2);

       // creates a staging customer and inserts it twice
       final Long lsCustomerId = 112l;
       final CustomerStage stgCust1 = MockTestData.mockCustomerStage(lsCustomerId.toString(), "name", storeId);
       final CustomerStage stgCust2 = MockTestData.mockCustomerStage(lsCustomerId.toString(), "name", storeId);
       customerStagingService.save(stgCust1);
       customerStagingService.save(stgCust2);

       // executes the whole loader job
       launchJob();

       // asserts
       assertProducts(stgProd1);
       assertCustomers(stgCust1);

       final List<Long> remainedInCustomerStaging = customerStagingService.findAllIds();
       assertEquals(0, remainedInCustomerStaging.size());
       
       final List<Long> remainedInProductStaging = productStagingService.findAllIds();
       assertEquals(0, remainedInProductStaging.size());
   }
}
