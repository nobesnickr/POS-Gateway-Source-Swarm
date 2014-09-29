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

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sonrisa.swarm.job.loader.helper.LoaderTestHelper;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.InvoiceLineEntity;
import com.sonrisa.swarm.model.legacy.ProductEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.CustomerStage;
import com.sonrisa.swarm.model.staging.InvoiceLineStage;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.model.staging.ProductStage;
import com.sonrisa.swarm.model.staging.retailpro.enums.RpReceiptType;

/**
 * Test loading invoices from the staging table to the legacy DB
 */
public class InvoiceLoaderTest extends BaseLoaderTest {
        
    @Autowired 
    private LoaderTestHelper testHelper;
    
    /** Store id for the StoreEntity the invoices use */
    private Long storeId;
    
    /**
     * Setup test by creating the store entity
     */
    @Before
    public void setupByInsertingStore(){
        // creates a store 
       StoreEntity strEntity = MockTestData.mockStoreEntity("myStore");
       storeService.save(strEntity);                 
       this.storeId = strEntity.getId();
    }
    
    /**
     * Test case:
     *  Invoice in stage has the same legacy id and store as
     *  an other entity in the legacy tables;
     *  
     * Expected:
     *  Its updated
     */
    @Test
    public void testUpdatingInvoice(){

        // Insert invoice once
        Long lsInvoiceId = 1234567L;
        InvoiceStage stgInvoice = MockTestData.mockInvoice(lsInvoiceId.toString(), "8888", storeId, "24.99");
        invoiceStagingService.save(stgInvoice);
        launchJob();

        // Insert invoice again, with different total
        BigDecimal newTotalValue = new BigDecimal("1999.49");
        stgInvoice.setTotal(newTotalValue.toPlainString());
        stgInvoice.setCompleted("1");
        invoiceStagingService.save(stgInvoice);

        launchJob();
        
        // Assert
        InvoiceEntity entity = invoiceDao.findByStoreAndForeignId(storeId, lsInvoiceId);
        assertNotNull(entity);
        assertEquals(newTotalValue, entity.getTotal());
        assertEquals(Boolean.TRUE, entity.getCompleted());
    }
    
    /**
     * Test case:
     *  There is an {@link InvoiceEntity} in the legacy with <code>completed<code> set to 1
     *  A new invoice arrives with Receipt Type CHECK_IN
     *  
     * Expected:
     *  The completed field of the invoice is update to 0
     */
    @Test
    public void testUpdatingCompletedForRetailPro(){

        // Insert invoice once
        Long lsInvoiceId = 1234567L;
        InvoiceStage stgInvoice = MockTestData.mockInvoice(lsInvoiceId.toString(), "8888", storeId, "24.99");
        stgInvoice.setCompleted("1"); 
        invoiceStagingService.save(stgInvoice);
        launchJob();
        
        // Insert invoice again
        stgInvoice.setCompleted(null);
        stgInvoice.setReceiptType(Integer.toString(RpReceiptType.CHECK_IN.getLsReceiptType()));
        invoiceStagingService.save(stgInvoice);
        launchJob();
        
        // Assert
        InvoiceEntity entity = invoiceDao.findByStoreAndForeignId(storeId, lsInvoiceId);
        assertNotNull(entity);
        assertEquals(Boolean.FALSE, entity.getCompleted());
    }
    
    /**
     * Test case:
     *  InvoiceLine in stage has the same legacy id and store as
     *  an other entity in the legacy tables
     *  
     * Expected:
     *  It's updated
     */
    @Test
    public void testUpdatingInvoiceLines(){

        // Insert invoice and line once
        InvoiceStage stgInvoice = MockTestData.mockInvoice("3", "8888", storeId, "24.99");
        invoiceStagingService.save(stgInvoice);
        
        InvoiceLineStage stgInvoiceLine = MockTestData.mockInvoiceLine(stgInvoice, "89273293", "499.99", "15156167", storeId);
        invoiceLineStagingService.save(stgInvoiceLine);
        launchJob();

        // Insert the same line again
        Integer newQuantity = 988;
        stgInvoiceLine.setQuantity(newQuantity.toString());
        invoiceLineStagingService.save(stgInvoiceLine);
        launchJob();
        
        // Assert
        List<InvoiceLineEntity> legacyLines = invoiceLineDao.findAll();
        assertEquals("Invoice line shouldn't be duplicated", 1, legacyLines.size());
        assertEquals(newQuantity, legacyLines.get(0).getQuantity());
    }
    
    /**
     * Test case: 
     *  Test inserting two invoices with missing customer stage record.
     *  
     * Expected:
     *  No dummy entity is created
     */
    @Test
    public void testInsertingInvoicesWithEmptyCustomer(){
        
        InvoiceStage stgInvoice = MockTestData.mockInvoice("12345678", "8888", storeId, "24.99");
        InvoiceStage stgInvoice2 = MockTestData.mockInvoice("987654321", "8888", storeId, "259.00");
        invoiceStagingService.save(stgInvoice);
        invoiceStagingService.save(stgInvoice2);
        
        launchJob();
        
        // Test if all invoices were moved to legacy database
        assertEquals(2, invoiceDao.findAll().size());
        
        // Test if all invoices were removed from staging tables
        assertEquals(0, invoiceStagingService.findAllIds().size());
        
        // Tests that no dummy entity was created
        assertEquals(0, customerDao.findAll().size());
    }
    
    /**
     * Test case:
     *  Test inserting 1 invoice with 1 invoice line into
     *  a database with no products
     * 
     * Expected:
     *  Invoice line is moved to the legacy tables
     *  No dummy product is created
     */
    @Test
    public void testInsertingInvoicesWithInvoiceLines(){
        
        InvoiceStage stgInvoice = MockTestData.mockInvoice("12345678", "8888", storeId, "24.99");
        invoiceStagingService.save(stgInvoice);
       
        InvoiceLineStage stgInvoiceLine = MockTestData.mockInvoiceLine(stgInvoice, "89273293", "499.99", "15156167", storeId);
        invoiceLineStagingService.save(stgInvoiceLine);
        
        launchJob();
        
        // Test if all invoices were moved to legacy database
        assertEquals(1, invoiceLineDao.findAll().size());
        // Test if all invoices were removed from staging tables
        assertEquals(0, invoiceLineStagingService.findAllIds().size());
        
        // Test that no dummy entity was created
        assertEquals(0, productDao.findAll().size());
    }
    
    /**
     * Test case: 
     * Test inserting only the invoice line, so the invoice entity
     * will not be found
     * 
     * Expected:
     * Job doesn't modify the tables, the invoice line stays in the staging area
     */
    @Test
    public void testInsertingInvoiceLineAloneFails(){
        
        // Not inserted into the staging table!
        InvoiceStage stgInvoice = MockTestData.mockInvoice("12345678", "8888", storeId, "24.99");
        
        InvoiceLineStage stgInvoiceLine = MockTestData.mockInvoiceLine(stgInvoice, "89273293", "499.99", "15156167", storeId);
        invoiceLineStagingService.save(stgInvoiceLine);
        
        launchJob();

        // Test if all invoices were moved to legacy database
        assertEquals(0, invoiceLineDao.findAll().size());
        // Test if all invoices were removed from staging tables
        assertEquals(1, invoiceLineStagingService.findAllIds().size());
    }
    
    /**
     * Test case:
     * Creates a staging invoice with two lines.
     * One of the lines has a valid lsProductId the other has an invalid.
     * 
     * Expected:
     * All staging entities should be moved.
     */
    @Test
    public void testLinesWithInvalidOrEmptyProduct() {

        // creates a staging product 
        final Long lsProductId1 = 12L;
        final Long lsProductId2 = 46L;
        final ProductStage stgProd1 = MockTestData.mockProductStage(lsProductId1.toString(), storeId);
        final ProductStage stgProd2 = MockTestData.mockProductStage(lsProductId2.toString(), storeId);
        productStagingService.save(stgProd1);
        productStagingService.save(stgProd2);

        // creates a staging customer 
        final Long lsCustomerId = 112L;
        final CustomerStage stgCust = MockTestData.mockCustomerStage(lsCustomerId.toString(), "name", storeId);
        customerStagingService.save(stgCust);

        // creates a staging invoice
        final Long lsInvoiceId = 99L;
        final InvoiceStage stgInvoice = MockTestData.mockInvoice(lsInvoiceId.toString(), lsCustomerId.toString(), storeId, "0.99");
        invoiceStagingService.save(stgInvoice);

        // creates a staging invoice lines
        InvoiceLineStage stgLine1 = MockTestData.mockInvoiceLine(stgInvoice, stgProd1, "1", storeId);
        invoiceLineStagingService.save(stgLine1);
        
        // creates a staging invoice lines with invalid productId
        InvoiceLineStage stgLine2 = MockTestData.mockInvoiceLine(stgInvoice, stgProd2, "2", storeId);
        stgLine2.setLsProductId(Long.toString(Long.MAX_VALUE));
        invoiceLineStagingService.save(stgLine2);

        // creates a staging invoice lines with invalid productId
        InvoiceLineStage stgLine3 = MockTestData.mockInvoiceLine(stgInvoice, stgProd2, "3", storeId);
        stgLine3.setLsProductId("");
        invoiceLineStagingService.save(stgLine3);
        
        // executes the whole loader job
        launchJob();

        // asserts
        assertProducts(stgProd1, stgProd2);
        assertCustomers(stgCust);
        
        // both lines should've been moved
        final ProductStage[] stgProds = {stgProd1};
        final InvoiceLineStage[] stgLines = {
            stgLine1, // correct line
            stgLine2,  // line with incorrect productId
            stgLine3 // line with empty product id
        };
        InvoiceEntity invoiceInLegacyDb = assertInvoice(storeId, stgInvoice, null, null);
        
        // asserts that the line with the invalid productId has moved as well
        final List<Long> remainedInStaging = invoiceLineStagingService.findAllIds();
        assertEquals("Not all invoice lines have been moved!", 0, remainedInStaging.size());        
    }
    
    /**
     * Test case:
     * Creates two staging invoices, one with a valid customerId and an other with invalid customerId.
     * 
     * Expected:
     * All staging entities should be moved.
     */
    @Test
    public void testInvoicesWithInvalidCustomer() {

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

        // creates a valid staging invoice
        final Long lsInvoiceId = 99l;
        final InvoiceStage stgInvoice = MockTestData.mockInvoice(lsInvoiceId.toString(), lsCustomerId.toString(), storeId, "0.99");
        invoiceStagingService.save(stgInvoice);
        
        // creates a staging invoice with invalid customerID
        final Long lsInvoiceId2 = 88l;
        final Long invalidCustomerId = 678442617167364L;
        final InvoiceStage stgInvoice2 = MockTestData.mockInvoice(lsInvoiceId2.toString(), invalidCustomerId.toString(), storeId, "0.99");
        invoiceStagingService.save(stgInvoice2);

        // executes the whole loader job
        launchJob();

        // asserts
        assertProducts(stgProd1, stgProd2);
        assertInvoice(storeId, stgInvoice, null, null);
        
        assertEquals("Only valid customers should be moved to the customers table", 1, customerDao.findAll().size());
        assertSingleCustomer(stgCust);
        assertNull("Entity for invalid customer shouldn't be created", customerDao.findByStoreAndForeignId(storeId, invalidCustomerId));
        
        // asserts that the invoice with the invalid customerId has been moved as well
        final List<Long> remainedInStaging = invoiceStagingService.findAllIds();
        assertEquals(0, remainedInStaging.size());        
    }   
    
    /**
     * Test case: 
     *  Attempting to convert a sales invoice with a negative total
     *  
     * Expected:
     *  The resulting legacy entity has a negative total
     * 
     */
    @Test
    public void testInvoicesWithReceiptType() {
        final Double totalForSale = 444.50;
        final Double totalForPositiveReturn = 225.19;
        final Double totalForNegativeReturn = -9950.49;
        
        InvoiceStage inputForSale = new InvoiceStage();
        Long inputForSaleId = 987654321L;
        inputForSale.setLsInvoiceId(inputForSaleId.toString());
        inputForSale.setTotal(totalForSale.toString());
        inputForSale.setReceiptType(Integer.toString(RpReceiptType.SALES.getLsReceiptType()));
        inputForSale.setStoreId(storeId);
        invoiceStagingService.save(inputForSale);
        
        InvoiceStage inputForPositiveReturn = new InvoiceStage();
        Long inputForPositiveReturnId = 987654322L;
        inputForPositiveReturn.setLsInvoiceId(inputForPositiveReturnId.toString());
        inputForPositiveReturn.setTotal(totalForPositiveReturn.toString());
        inputForPositiveReturn.setReceiptType(Integer.toString(RpReceiptType.RETURN.getLsReceiptType()));
        inputForPositiveReturn.setStoreId(storeId);
        invoiceStagingService.save(inputForPositiveReturn);

        InvoiceStage inputForNegativeReturn = new InvoiceStage();
        Long inputForNegativeReturnId = 987654323L;
        inputForNegativeReturn.setLsInvoiceId(inputForNegativeReturnId.toString());
        inputForNegativeReturn.setTotal(totalForNegativeReturn.toString());
        inputForNegativeReturn.setReceiptType(Integer.toString(RpReceiptType.RETURN.getLsReceiptType()));
        inputForNegativeReturn.setStoreId(storeId);
        invoiceStagingService.save(inputForNegativeReturn);

        // executes the whole loader job
        launchJob();
        
        List<InvoiceEntity> ids = invoiceDao.findAll();
        
        // Assert
        InvoiceEntity resultForSale = invoiceDao.findByStoreAndForeignId(storeId, inputForSaleId);
        InvoiceEntity resultForPositiveReturn = invoiceDao.findByStoreAndForeignId(storeId, inputForPositiveReturnId);
        InvoiceEntity resultForNegativeReturn = invoiceDao.findByStoreAndForeignId(storeId, inputForNegativeReturnId);
        
        assertNotNull(resultForSale);
        assertNotNull(resultForPositiveReturn);
        assertNotNull(resultForSale);
        assertEquals(totalForSale.doubleValue(), resultForSale.getTotal().doubleValue(),0.01);
        assertEquals(-totalForPositiveReturn.doubleValue(), resultForPositiveReturn.getTotal().doubleValue(),0.01);
        assertEquals(totalForNegativeReturn.doubleValue(), resultForNegativeReturn.getTotal().doubleValue(),0.01);
    }

    /**
     * Test case:
     *  Saving invoice lines into the legacy database with its 
     *  price missing. 
     * 
     * Expected:
     *  Its price is copied from the product, and {@link InvoiceLineEntity}
     *  also gets description, class (category_name) and 
     *  family (manufacturer_name) from the {@link ProductEntity}
     */
    @Test
    public void testCopyingProductProperties() {

        // creates a staging product, to source from where the fields should be copied
        final Long lsProductId1 = 12L;
        final ProductStage stgProd = MockTestData.mockProductStage(lsProductId1.toString(), storeId);
        productStagingService.save(stgProd);

        // creates a staging invoice, so invoice line will be movable to legacy tables
        final Long lsInvoiceId = 99L;
        final InvoiceStage stgInvoice = MockTestData.mockInvoice(lsInvoiceId.toString(), "112", storeId, "0.99");
        invoiceStagingService.save(stgInvoice);

        // creates a staging invoice lines
        InvoiceLineStage stgLine1 = MockTestData.mockInvoiceLine(stgInvoice, stgProd, "1", storeId);
        
        // Force price field of the mock to be missing,
        // we want to test exactly this scenario
        stgLine1.setPrice(null);
        invoiceLineStagingService.save(stgLine1);
        
        // executes the whole loader job
        launchJob();

        // asserts
        assertProducts(stgProd);
        
        List<InvoiceLineEntity> legacyLines = invoiceLineDao.findAll();
        assertEquals("Single invoice line wasn't moved", 1, legacyLines.size());
        
        InvoiceLineEntity legacyLine = legacyLines.get(0);

        assertEquals(legacyLine.getPrice(), new BigDecimal(stgProd.getPrice()));
        assertEquals(legacyLine.getDescription(), stgProd.getDescription());
        assertEquals(legacyLine.getFamily(), stgProd.getManufacturer());
        assertEquals(legacyLine.getClazz(), stgProd.getCategory());
    }
    
    /**
     * Test case:
     *  Saving invoice lines into the legacy database with a product
     *  for which the category and manufacturer fields are empty,
     *  but the ls_category_id and ls_manfuacturer_id fields are not.
     * 
     * Expected:
     *  If these ids reference valid entities then their names are
     *  copied to the invoice line.
     *  
     */
    @Test
    public void testCopyingReferencedProductProperties() {

        // Insert category and manufacturer and products
        final String categoryName = "Enchanted Blades";
        final String manufacturerName = "Magical Kingdom Co.";
        
        List<ProductStage> stgProds = testHelper.setupStageWithProductsCategoriesAndManufacturers(storeId, categoryName, manufacturerName);

        // creates a staging invoice, so invoice line will be movable to legacy tables
        final Long lsInvoiceId = 99L;
        final InvoiceStage stgInvoice = MockTestData.mockInvoice(lsInvoiceId.toString(), "112", storeId, "0.99");
        invoiceStagingService.save(stgInvoice);

        // creates a staging invoice lines
        InvoiceLineStage stgLineWithValid = MockTestData.mockInvoiceLine(stgInvoice, stgProds.get(0), "1", storeId);
        InvoiceLineStage stgLineWithMissing = MockTestData.mockInvoiceLine(stgInvoice, stgProds.get(1), "2", storeId);

        invoiceLineStagingService.save(stgLineWithValid);
        invoiceLineStagingService.save(stgLineWithMissing);
        
        // executes the whole loader job
        launchJob();

        // asserts
        assertProducts((ProductStage[])stgProds.toArray());
        
        List<InvoiceLineEntity> legacyLines = invoiceLineDao.findAll();
        assertEquals(2, legacyLines.size());
        testHelper.sortLinesByLegacyId(legacyLines);
        
        assertEquals("Referenced category name not copied", categoryName, legacyLines.get(0).getClazz());
        assertEquals("Referenced manufacturer name not copied", manufacturerName, legacyLines.get(0).getFamily());
        assertNull(legacyLines.get(1).getClazz());
        assertNull(legacyLines.get(1).getFamily());
    }
    
    /**
     * Test case:
     *  Saving invoice into legacy database.
     * 
     * Expected:
     *  The email, name and mainphone fields are copied from the
     *  <code>customers</code> table
     */
    @Test
    public void testCopyingCustomerProperties() {

        // creates a staging customer 
        final Long lsCustomerId = 112L;
        final CustomerStage stgCust = MockTestData.mockCustomerStage(lsCustomerId.toString(), "name", storeId);
        customerStagingService.save(stgCust);

        Long invoiceWithCustomerId = 132465L;
        InvoiceStage stgInvoiceWithCustomer = MockTestData.mockInvoice(invoiceWithCustomerId.toString(), stgCust.getLsCustomerId(), storeId, "24.99");
        Long invoiceWithoutCustomerId = 847534L;
        InvoiceStage stgInvoiceWithNoCustomer = MockTestData.mockInvoice(invoiceWithoutCustomerId.toString(), "9999999", storeId, "259.00");
        invoiceStagingService.save(stgInvoiceWithCustomer);
        invoiceStagingService.save(stgInvoiceWithNoCustomer);
        
        launchJob();
        
        // Test if all invoices were removed from staging tables
        assertEquals(0, invoiceStagingService.findAllIds().size());
        
        // Tests that no dummy entity was created
        assertEquals("Only one customer should be created", 1, customerDao.findAll().size());
        
        // Assert invoice fields
        InvoiceEntity invoiceWithCustomer = invoiceDao.findByStoreAndForeignId(storeId, invoiceWithCustomerId);
        assertNotNull(invoiceWithCustomer);
        assertEquals(stgCust.getName(), invoiceWithCustomer.getCustomerName());
        assertEquals(stgCust.getEmail(), invoiceWithCustomer.getCustomerEmail());
        assertEquals(stgCust.getPhone(), invoiceWithCustomer.getCustomerPhone());
        
        InvoiceEntity invoiceWithoutCustomer = invoiceDao.findByStoreAndForeignId(storeId, invoiceWithoutCustomerId);
        assertNotNull(invoiceWithoutCustomer);
        assertNull(invoiceWithoutCustomer.getCustomerName());
        assertNull(invoiceWithoutCustomer.getCustomerEmail());
        assertNull(invoiceWithoutCustomer.getCustomerPhone());
    }
    
    /**
     * This test is because we must convert negative Customer.ls_cutomer_id to be positive.
     * Unfortunately Swarm  does not use foreign key for Invoice.customer_id so we must care about its integrity
     * 
     * Test case:
     * insert a customer to the legacy table with negative {@code ls_cutomer_id}. 
     * insert an invoice to the legacy table that has reference to this customer.
     * 
     * Expected result:
     * we can insert the invoice
     * we can query this invoice's customer
     */
    @Test
    public void testNegativeCustomer() {
    	Long customrLsId = Long.valueOf(-562);
    	final CustomerStage stgCust = MockTestData.mockCustomerStage(customrLsId.toString(), "testNegativeCustomer", storeId);
        customerStagingService.save(stgCust);
        
        Long invoiceLsId = Long.valueOf(1563);
        InvoiceStage stgInvoiceWithCustomer = MockTestData.mockInvoice(invoiceLsId.toString(), stgCust.getLsCustomerId(), storeId, "24.99");
        invoiceStagingService.save(stgInvoiceWithCustomer);
        
        launchJob();
        
        //assert the customerId is positive
        InvoiceEntity invoice = invoiceDao.findByStoreAndForeignId(storeId, invoiceLsId);
        long readLsInvoiceId = invoice.getLsCustomerId().longValue();
        assertEquals(Math.abs(customrLsId), readLsInvoiceId);
        
        // can we find the customer based on invoice data?
        assertNotNull(customerDao.findByStoreAndForeignId(storeId, readLsInvoiceId));
    }
}
