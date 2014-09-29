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
package com.sonrisa.swarm.integration;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.MockRetailProData;
import com.sonrisa.swarm.legacy.dao.InvoiceDao;
import com.sonrisa.swarm.legacy.dao.InvoiceLineDao;
import com.sonrisa.swarm.legacy.dao.ProductDao;
import com.sonrisa.swarm.legacy.service.InvoiceService;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.InvoiceLineEntity;
import com.sonrisa.swarm.model.legacy.ProductEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.CustomerStage;
import com.sonrisa.swarm.model.staging.InvoiceLineStage;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.model.staging.ProductStage;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;
import com.sonrisa.swarm.staging.dao.InvoiceLineStageDao;
import com.sonrisa.swarm.staging.dao.InvoiceStageDao;
import com.sonrisa.swarm.staging.service.CustomerStagingService;
import com.sonrisa.swarm.staging.service.InvoiceLineStagingService;
import com.sonrisa.swarm.staging.service.ProductStagingService;

/**
 *
 * @author joe 
*/
public class RetailProIntegrationTest extends BaseIntegrationTest {
    
    private static final String BASE_URI = "/items/invoice";
    public static final String SWARM_ID = "someId";

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private InvoiceLineDao invoiceLineDao;   
    @Autowired
    private InvoiceLineStageDao invoiceLineStageDao;    
    @Autowired
    private InvoiceLineStagingService invoiceLineStagingService;
    
    @Autowired
    private InvoiceDao invoiceDao;
    @Autowired
    private InvoiceStageDao invoiceStageDao; 
    
    @Autowired
    private ProductStagingService productStagingService;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private CustomerStagingService customerStagingService;
    @Autowired
    private InvoiceService invoiceService;
        
    @Autowired
    @Qualifier(value = "loaderJob")
    private Job loaderJob;
    
    /**
     * Creates a mock store and RetailPro store entity in the DB.
     * Returns the storeId
     */
    public Long createMockStores(final String storeName, final String sbsNo, String storeNo){         
         final StoreEntity store = MockTestData.mockStoreEntity(storeName);
         storeService.save(store);
         
         // the value of the sbsNo and storeNo equal with the values in the mock json request file
         final RpStoreEntity rpStore = MockRetailProData.mockRpStoreEntity(SWARM_ID, sbsNo, storeNo);
         rpStore.setTimeZone(TimeZone.getDefault().getID());
         rpStore.setStoreId(store.getId());
         rpStoreService.save(rpStore);
         
         return store.getId();
    }

    /**
     * Test case: a few valid invoices are received from the RetailPro.
     * Expected result: They can be saved to the staging database.
     * 
     * @throws JobExecutionAlreadyRunningException
     * @throws JobRestartException
     * @throws JobInstanceAlreadyCompleteException
     * @throws JobParametersInvalidException
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     * @throws Exception 
     */
    @Test
    public void testValidInvoice() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException, JsonParseException, JsonMappingException, IOException {
        createMockStores("myStore", "sbs1", "123456");
        
        InputStream jsonStream = MockDataUtil.getResourceAsStream(MockTestData.MOCK_REQUEST);
        Map<String, Object> content = objectMapper.readValue(jsonStream, Map.class);                
        String requestBody = objectMapper.writeValueAsString(content);       
        performRequest(requestBody);
        
        final int numOfInvoices = 2;
        final int numOfLines = 2;
        final int numOfProducts = 2;
        final int numOfCustomers = 1;
        
        assertStagingTables(numOfInvoices, numOfLines, numOfProducts, numOfCustomers);     
        assertEquals(SWARM_ID, jdbcTemplate.queryForList("select swarm_id from staging_invoices", String.class).get(0));

        assertLineSbsAndStoreNo();
    }
    
    /**
     * Test case: a few valid invoices are received from the RetailPro and than the same invoices again.
     * Expected result: At the first time they can be saved to the staging database but at the second time
     * they will be deleted from the staging table.
     * 
     * @throws Exception
     */
    @Test
    public void sameInvoiceReceivedTwice() throws Exception {
        createMockStores("myStore", "sbs1", "123456");
        
        InputStream jsonStream = MockDataUtil.getResourceAsStream(MockTestData.MOCK_REQUEST);
        Map<String, Object> content = objectMapper.readValue(jsonStream, Map.class);                
        String requestBody = objectMapper.writeValueAsString(content);       
        performRequest(requestBody);
        
        final int numOfInvoices = 2;
        final int numOfLines = 2;
        final int numOfProducts = 2;
        final int numOfCustomers = 1;
        
        assertStagingTables(numOfInvoices, numOfLines, numOfProducts, numOfCustomers);
        runStagingLoaderJob();  
     
        // staging tables should be empty
        assertStagingTables(0, 0, 0, 0);
        // everything should be moved to the legacy DB
        assertLegacyTables(numOfInvoices, numOfLines, numOfProducts, numOfCustomers);
        
        // we receive the same request again
        performRequest(requestBody);
        // the we'll be in the staging tables again
        assertStagingTables(numOfInvoices, numOfLines, numOfProducts, numOfCustomers);
        runStagingLoaderJob();
        
        // staging tables should be empty
        assertStagingTables(0, 0, 0, 0);
        // the legacy tables remains untouched
        assertLegacyTables(numOfInvoices, numOfLines, numOfProducts, numOfCustomers);
    }
    

    /**
     * Test case: a few valid invoices are received multiple times in the same batch
     * Expected: they are only inserted once into the legacy tables
     */
    @Test
    public void sameInvoiceTwiceInSameBatch() throws Exception {
        final Long storeId = createMockStores("myStore", "sbs1", "123456");
        
        String requestBody = MockDataUtil.getResourceAsString(MockTestData.TEST_RP_WITH_DUPLICATE_INVOICES);
        performRequest(requestBody);
        
        // Act
        assertStagingTables(3, 0, 0, 0);
        
        // Move entities from staging to legacy area
        runStagingLoaderJob();  
     
        // Assert
        assertStagingTables(0, 0, 0, 0);
        assertLegacyTables(2, 0, 0, 0);
        
        // Invoice with InvoiceSid 2 was duplicated, first time
        // with InvoiceNo 1234, second time with 5555
        InvoiceEntity invoice = invoiceDao.findByStoreAndForeignId(storeId, 2L);
        assertEquals("Invoice content wasn't updated", "5555", invoice.getInvoiceNo());
    }
    
    /**
     * Test case: an invoice is received from the RetailPro but its Total value
     * is too much to insert it to the legacy DB.
     * 
     * Expected result: 
     * After the execution the invoice remains in the staging DB, and it can not be
     * moved to the legacy DB.
     */
    @Test
    public void testTooMuchTotalValue() throws Exception{
        final Long storeId = createMockStores("myStore", "0", "SMF");       
         
        // creates a staging product 
        final Long lsProductId1 = 12l;
        final Long lsProductId2 = 46l;
        final ProductStage stgProd1 = MockTestData.mockProductStage(lsProductId1.toString(), storeId);
        final ProductStage stgProd2 = MockTestData.mockProductStage(lsProductId2.toString(), storeId);
        productStagingService.save(stgProd1);
        productStagingService.save(stgProd2);
        
        // creates a staging customer 
        final Long lsCustomerId = 1l;
        final CustomerStage stgCust = MockTestData.mockCustomerStage(lsCustomerId.toString(), "name", storeId);
        customerStagingService.save(stgCust);     
        
        InputStream jsonStream = MockDataUtil.getResourceAsStream(MockTestData.TEST_RP_INVOICE);
        Map<String, Object> content = objectMapper.readValue(jsonStream, Map.class);                
        String requestBody = objectMapper.writeValueAsString(content);       
        performRequest(requestBody);
        assertEquals("1", jdbcTemplate.queryForObject("select count(*) from staging_invoices", Integer.class).toString());
        runStagingLoaderJob();  
        // the invoice should be deleted forever because it can be processed
        assertEquals("0", jdbcTemplate.queryForObject("select count(*) from staging_invoices", Integer.class).toString());
        assertEquals("0", jdbcTemplate.queryForObject("select count(*) from invoices", Integer.class).toString());
    }
    
    /**
     * Test case:  
     *  Retail Pro data extract is received with the 
     *  product's price and invoice line's price:
     *   <code>8.86479290759E11</code>
     * 
     * Expected result: 
     *  These lines and products are deleted from staging,
     *  because they are unprocessable.
     */
    @Test
    public void testTooMuchProductPrice() throws Exception{
        createMockStores("myStore", "0", "GWF");       
         
        String requestBody = MockDataUtil.getResourceAsString(MockTestData.TEST_RP_WITH_PRODUCT_PRICE_TOO_MUCH);       
        performRequest(requestBody);
        
        // Both should be moved to the staging tables
        assertEquals("1", jdbcTemplate.queryForObject("select count(*) from staging_products", Integer.class).toString());
        assertEquals("1", jdbcTemplate.queryForObject("select count(*) from staging_invoice_lines", Integer.class).toString());
        runStagingLoaderJob();  
        
        // Both should've been deleted
        assertEquals("0", jdbcTemplate.queryForObject("select count(*) from staging_products", Integer.class).toString());
        assertEquals("0", jdbcTemplate.queryForObject("select count(*) from staging_invoice_lines", Integer.class).toString());
        assertEquals("0", jdbcTemplate.queryForObject("select count(*) from products", Integer.class).toString());
        assertEquals("0", jdbcTemplate.queryForObject("select count(*) from invoice_lines", Integer.class).toString());
    }
    
     /**
     * Test case:  
     *  Retail Pro data extract is received with the 
     *  invoice total being 23 or 24 character long:
     *  
     *  Triming these:
     *   <code>-7.1054273576010019E-15</code>
     *   <code>-7.10542735760100195E-15</code>
     *   <code>-7.105427357601001955E-15</code>
     *  
     *  To 20 chars would be:
     *  <code>-7.1054273576010019E-</code>
     *  <code>-7.10542735760100195E</code>
     *  <code>-7.105427357601001955</code>
     * 
     * Expected result: 
     *  These invoices are deleted from the staging tables
     */
    @Test
    public void testTooLongInvoicePrice() throws Exception{
        createMockStores("myStore", "0", "GWF");       
         
        String requestBody = MockDataUtil.getResourceAsString(MockTestData.TEST_RP_WITH_INVOICE_PRICE_TOO_LONG);       
        performRequest(requestBody);
        
        // Should be moved to the staging tables
        assertEquals("Invoice should be moved to the staging tables", "3", jdbcTemplate.queryForObject("select count(*) from staging_invoices", Integer.class).toString());
        runStagingLoaderJob();  
        
        // Should've been deleted
        assertEquals("Staging invoices should've been deleted", "0", jdbcTemplate.queryForObject("select count(*) from staging_invoices", Integer.class).toString());
        assertEquals("Invoices shouldnt have been moved", "0", jdbcTemplate.queryForObject("select count(*) from invoices", Integer.class).toString());
    }
    
    /**
     * Test case:  
     *  Retail Pro data extract is received with the 
     *  date: <code>"1601-06-15T07:07:41"</code>
     *  
     * Expected:
     *  It's not moved to legacy, but it doesn't interrupt staging loading,
     *  others are moved.
     */
    @Test
    public void testInvoiceDateCenturiesAgo() throws Exception {
        createMockStores("myStore", "0", "SMF");

        String requestBody = MockDataUtil.getResourceAsString(MockTestData.TEST_RP_INVOICES_WITH_DATE_CENTURIES_AGO);
        performRequest(requestBody);

        // Should be moved to the staging tables
        assertEquals("Invoice should be moved to the staging tables", "2",jdbcTemplate.queryForObject("select count(*) from staging_invoices", Integer.class).toString());
        runStagingLoaderJob();

        // Should've been deleted
        assertEquals("Staging invoices should be empty", "0",jdbcTemplate.queryForObject("select count(*) from staging_invoices", Integer.class).toString());
        assertEquals("A single invoice should have been moved", "1",jdbcTemplate.queryForObject("select count(*) from invoices", Integer.class).toString());
    }
    
     /**
     * Test case:  
     *  Retail Pro data extract is received with the 
     *  lines having tax "", null or none
     * 
     * Expected result: 
     *  These lines are moved to the legacy tables
     */
    @Test
    public void testNullTax() throws Exception{
        createMockStores("myStore", "0", "SMF");       
         
        String requestBody = MockDataUtil.getResourceAsString(MockTestData.TEST_RP_INVOICE_LINES_WITH_NULL_TAX);       
        performRequest(requestBody);
        
        // Should be moved to the staging tables
        assertEquals("Invoice lines should be moved to the staging tables", "3", jdbcTemplate.queryForObject("select count(*) from staging_invoice_lines", Integer.class).toString());
        runStagingLoaderJob();  
        
        // Should've been deleted
        assertEquals("Staging invoice lines should've been deleted", "0", jdbcTemplate.queryForObject("select count(*) from staging_invoice_lines", Integer.class).toString());
        assertEquals("Invoice lines shouldnt have been moved", "3", jdbcTemplate.queryForObject("select count(*) from invoice_lines", Integer.class).toString());
    }
    
    /**
     * Test case: a few valid and an invalid invoices are received from the RetailPro.
     * Expected result: The valid invoices will be moved to the legacy DB, but the invalid
     * one will be deleted.
     * 
     * @throws IOException
     * @throws Exception 
     */
    @Test
    public void testInvoicesWithAnInvalid() throws IOException, Exception{
        final Long storeId = createMockStores("myStore", "0", "SMF");
         
        // creates a staging product 
        final Long lsProductId1 = 12l;
        final Long lsProductId2 = 46l;
        final ProductStage stgProd1 = MockTestData.mockProductStage(lsProductId1.toString(), storeId);
        final ProductStage stgProd2 = MockTestData.mockProductStage(lsProductId2.toString(), storeId);
        productStagingService.save(stgProd1);
        productStagingService.save(stgProd2);
        
        // creates a staging customer 
        final Long lsCustomerId = 1l;
        final CustomerStage stgCust = MockTestData.mockCustomerStage(lsCustomerId.toString(), "name", storeId);
        customerStagingService.save(stgCust);     
        
        InputStream jsonStream = MockDataUtil.getResourceAsStream(MockTestData.TEST_RP_INVOICES_WITH_AN_INVALID);
        Map<String, Object> content = objectMapper.readValue(jsonStream, Map.class);                
        String requestBody = objectMapper.writeValueAsString(content);       
        performRequest(requestBody);
        
        assertEquals("4", jdbcTemplate.queryForObject("select count(*) from staging_invoices", Integer.class).toString());
        runStagingLoaderJob();                        
        assertEquals("The staging table should be empty because all of the invoices had been processed.", 
                "0", jdbcTemplate.queryForObject("select count(*) from staging_invoices", Integer.class).toString());
        assertEquals("The invalid invoice should be missing from here, but the other 3 has to be moved to the legacy DB.", 
                "3", jdbcTemplate.queryForObject("select count(*) from invoices", Integer.class).toString());
      }    
    
    /**
     * Test case: a few valid and an invalid invoice item are received from the RetailPro.
     * Expected result: The valid ones will be moved to the legacy DB, but the invalid
     * one will be deleted.
     * 
     * @throws IOException
     * @throws Exception 
     */
    @Test
    public void testInvoicesWithInvalidItems() throws IOException, Exception{
        final Long storeId = createMockStores("myStore", "0", "SMF");
         
        // Create staging products with price set to null,
        // so there's no chance the price value is copied onto the 
        // invoice line
        final Long lsProductId1 = 12l;
        final Long lsProductId2 = 46l;
        final ProductStage stgProd1 = MockTestData.mockProductStage(lsProductId1.toString(), storeId);
        stgProd1.setPrice(null);
        final ProductStage stgProd2 = MockTestData.mockProductStage(lsProductId2.toString(), storeId);
        stgProd2.setPrice(null);
        productStagingService.save(stgProd1);
        productStagingService.save(stgProd2);
        
        // creates a staging customer 
        final Long lsCustomerId = 1l;
        final CustomerStage stgCust = MockTestData.mockCustomerStage(lsCustomerId.toString(), "name", storeId);
        customerStagingService.save(stgCust);     
        
        InputStream jsonStream = MockDataUtil.getResourceAsStream(MockTestData.TEST_RP_INVOICES_WITH_INVALID_ITEMS);
        Map<String, Object> content = objectMapper.readValue(jsonStream, Map.class);                
        String requestBody = objectMapper.writeValueAsString(content);       
        performRequest(requestBody);
        assertEquals("1", jdbcTemplate.queryForObject("select count(*) from staging_invoices", Integer.class).toString());
        assertEquals("6", jdbcTemplate.queryForObject("select count(*) from staging_invoice_lines", Integer.class).toString());
        runStagingLoaderJob();                        
        assertEquals("The staging table should be empty because all of the invoices had been processed.", 
                "0", jdbcTemplate.queryForObject("select count(*) from staging_invoices", Integer.class).toString());
        assertEquals("The invoice should be moved to the legacy DB.", 
                "1", jdbcTemplate.queryForObject("select count(*) from invoices", Integer.class).toString());
        
        assertEquals("All the items should be processed.", 
                "0", jdbcTemplate.queryForObject("select count(*) from staging_invoice_lines", Integer.class).toString());
        assertEquals("The invalid lines should be missing from here, but the other 4 has been moved to the legacy DB.", 
                "4", jdbcTemplate.queryForObject("select count(*) from invoice_lines", Integer.class).toString());
    }        
          
    /**     
     * 
     * Test case: 
     * Two invoice have been received through the RetailPro API,
     * one with a valid invoice number an other with invalid (null) number.
     * 
     * Expected result:
     * With the default configuration the second should be skipped during
     * the moving of staging entities. It should be left in the legacy DB.
     */
    @Test
    public void testNullInvoiceNumber() throws Exception {
        
        // creates a store suitable for the invoices in the json file
        createMockStores("myStore", "sbs1", "123456");
        
        // loads the json file
        final InputStream jsonStream = MockDataUtil.getResourceAsStream(MockTestData.TEST_RP_INVOICES_WITH_NULL_NUMBER);
        final Map<String, Object> content = objectMapper.readValue(jsonStream, Map.class);
        final String requestBody = objectMapper.writeValueAsString(content);
        
        // performs a RetailPro request and starts the processing jobs 
        performRequest(requestBody);
        runStagingLoaderJob();
        
        // asserts the result
        final List<InvoiceEntity> invoices = invoiceDao.findAll();
        assertEquals("Both invoices should be moved to the legacy DB.",  2, invoices.size());

        InvoiceEntity invalidInvoice = null;
        InvoiceEntity validInvoice = null;
        
        if("0".equals(invoices.get(0).getInvoiceNo())){
            validInvoice = invoices.get(1);
            invalidInvoice = invoices.get(0);
        } else {
            validInvoice = invoices.get(0);
            invalidInvoice = invoices.get(1);
        }
        
        assertFalse("This invoice has got a valid number so it should has been moved to the legacy DB.", 
                "0".equals(validInvoice.getInvoiceNo()));
        
        assertTrue("This valid invoice should have been moved to the legacy DB as well as completed", 
                Boolean.TRUE.equals(validInvoice.getCompleted()));
        
        assertTrue("Lines processed should be TRUE at all times", 
                Boolean.TRUE.equals(validInvoice.getLinesProcessed()));
        
        assertTrue("This invalid invoice should have been moved to the legacy DB as well", 
                "0".equals(invalidInvoice.getInvoiceNo()));
        
        assertTrue("This invalid invoice should have been moved to the legacy DB as well as incomplete", 
                Boolean.FALSE.equals(invalidInvoice.getCompleted()));
        
        assertTrue("Lines processed should be TRUE at all times", 
                Boolean.TRUE.equals(invalidInvoice.getLinesProcessed()));
        
        final List<InvoiceStage> stagingInvoices = invoiceStageDao.findAll();
        assertEquals("The staging table should be empty because all of the invoices had been processed.", 
                0, stagingInvoices.size());
    }
    
    
    /**
     * 
     * Test case: An invoice has been received through the RetailPro API, one
     * with a Total field where the decimal point is a ","
     * 
     * Expected result: Should be processed correctly
     */
    @Test
    public void testInvoiceWithHungarianNumberFormat() throws Exception {

        // creates a store suitable for the invoices in the json file
        createMockStores("myStore", "sbs1", "123456");

        // loads the json file
        final InputStream jsonStream = MockDataUtil
                .getResourceAsStream(MockTestData.TEST_RP_INVOICES_WITH_HUNGARIAN_NUMBERS);
        final Map<String, Object> content = objectMapper.readValue(jsonStream, Map.class);
        final String requestBody = objectMapper.writeValueAsString(content);

        // performs a RetailPro request and starts the processing jobs
        performRequest(requestBody);
        runStagingLoaderJob();

        // asserts the result
        final List<InvoiceEntity> invoices = invoiceDao.findAll();
        assertEquals("The invoice should be moved to the legacy DB.", 1,invoices.size());
        
        InvoiceEntity entity = invoices.get(0);
        assertEquals((Double)222.22, (Double)entity.getTotal().doubleValue());
    }
    
    /**
     * 
     * Test case: An invoice has been received through the RetailPro API, with CreatedDate
     * and CreateTime parameters sent to be used for timeStamp
     * 
     * Expected result: Should be processed correctly
     */
    @Test
    public void testInvoiceWithDocTimeSent() throws Exception {

        // creates a store suitable for the invoices in the json file
        createMockStores("myStore", "sbs1", "123456");

        // loads the json file
        final InputStream jsonStream = MockDataUtil
                .getResourceAsStream(MockTestData.TEST_RP_INVOICES_WITH_DOCTIME);
        final Map<String, Object> content = objectMapper.readValue(jsonStream, Map.class);
        final String requestBody = objectMapper.writeValueAsString(content);

        // performs a RetailPro request and starts the processing jobs
        performRequest(requestBody);
        runStagingLoaderJob();

        // asserts the result
        final List<InvoiceEntity> invoices = invoiceDao.findAll();
        assertEquals("The invoice should be moved to the legacy DB.", 1,invoices.size());
        
        InvoiceEntity entity = invoices.get(0);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertEquals("2013-09-07 23:30:51", sdf.format(entity.getTs()));
    }
    
   /** 
    * Test case: An invoice has been received through the RetailPro API, 
    * but the CustomerSID JSON field is an empty string
    * 
    * Expected result: Should be processed correctly, customer should be null
    * 
    */
   @Test
   public void testInvoiceWithEmptyCustomer() throws Exception {

       // creates a store suitable for the invoices in the json file
       createMockStores("myStore", "sbs1", "123456");

       // loads the json file
       final InputStream jsonStream = MockDataUtil.getResourceAsStream(MockTestData.TEST_RP_INVOICES_WITH_EMPTY_CUSTOMER_FIELD);
       final Map<String, Object> content = objectMapper.readValue(jsonStream, Map.class);
       final String requestBody = objectMapper.writeValueAsString(content);

       // performs a RetailPro request and starts the processing jobs
       performRequest(requestBody);
       runStagingLoaderJob();

       // asserts the result
       final List<InvoiceEntity> invoices = invoiceDao.findAll();
       assertEquals("The invoice should be moved to the legacy DB.", 1,invoices.size());
       
       InvoiceEntity entity = invoices.get(0);
       
       assertEquals((Long)0L, entity.getLsCustomerId());
   }
    
    /**
     * 
     * Test case: The Sonrisa Retail Pro instance (192.168.10.126) sends 
     * all data, but with most fields empty or missing on the invoice,
     * most importantly the InvoiceLineItem's TaxAmt and Price
     * 
     * Expected result: Should be processed correctly
     */
    @Test
    public void testInitialImportWithInvoiceItemsMissingPrice()
            throws JsonParseException, JsonMappingException, IOException,
            JobParametersInvalidException, JobInstanceAlreadyCompleteException,
            JobRestartException, JobExecutionAlreadyRunningException {

        // creates a store suitable for the invoices in the json file
        createMockStores("Plugin Test", "0", "001");

        // loads the json file
        final InputStream jsonStream = MockDataUtil.getResourceAsStream(MockTestData.TEST_RP_INVOICES_WITH_MISSING_PRICE_FIELD);
        final Map<String, Object> content = objectMapper.readValue(jsonStream, Map.class);
        final String requestBody = objectMapper.writeValueAsString(content);

        // performs a RetailPro request and starts the processing jobs
        performRequest(requestBody);
        runStagingLoaderJob();

        // asserts the result
        final List<InvoiceEntity> invoices = invoiceDao.findAll();
        assertEquals("The invoices shouldve be moved to the legacy DB.", 1, invoices.size());

        // asserts the result
        final List<InvoiceLineEntity> invoiceLines = invoiceLineDao.findAll();
        assertEquals("The invoices lines shouldve be moved to the legacy DB.", 1, invoiceLines.size());
        
        // asserts the price field on the lines
        for (InvoiceLineEntity line : invoiceLines){
            assertNotNull("The price of this line must not be null: "+ line, line.getPrice());
            
            final ProductEntity prod = productDao.findByStoreAndForeignId(line.getStore().getId(), line.getLsProductId());
            assertNotNull("Product cannot found for this line: " + line, prod);
            assertEquals(prod.getPrice(), line.getPrice());
        }
    }
    
    /**
     * 
     * Test case: An invoice has been received through the RetailPro API, with 
     * ReceiptType, ReceiptStatus and Tender specified 
     * 
     * Expected result: Should be processed correctly and moved to the staging tables
     * 
     */
    @Test
    public void testInvoiceStageWithReceiptAttributes() throws Exception {

        // creates a store suitable for the invoices in the json file
        createMockStores("myStore", "sbs1", "123456");

        // loads the json file
        final String requestBody = MockDataUtil.getResourceAsString(MockTestData.TEST_RP_WITH_RECEIPT_ATTRIBUTES);

        // performs a RetailPro request and starts the processing jobs
        performRequest(requestBody);
        
        final List<InvoiceStage> stagingInvoices = invoiceStageDao.findAll();
        
        assertEquals(1, stagingInvoices.size());
        
        final InvoiceStage stagingInvoice = stagingInvoices.get(0);
        
        assertEquals("0", stagingInvoice.getReceiptType());
        assertEquals("2", stagingInvoice.getReceiptStatus());
        assertEquals("1", stagingInvoice.getTender());
    }
    
    /**
     * Asserts whether the sbsNo and storeNo fields are filled up on the staging invoice lines,
     * and the store can be found.
     */
    private void assertLineSbsAndStoreNo() {
        final List<InvoiceLineStage> lines = invoiceLineStageDao.findAll();
        assertFalse(lines.isEmpty());
        
        for (InvoiceLineStage line : lines) {
            assertNotNull(line.getLsSbsNo());
            assertNotNull(line.getLsStoreNo());
            
            final StoreEntity store = invoiceLineStagingService.findStore(line);
            assertNotNull(store);
        }
    }

    private void performRequest(String requestBody) throws RuntimeException {
        // calls the REST service with a new invoice object
        MockHttpServletRequestBuilder request = put(BASE_URI)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .header("SwarmId", SWARM_ID);
        final ResultActions postResultAction;
        try {
            postResultAction = mockMvc.perform(request);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        assertCreatedStatus(postResultAction);
    }

    private void runStagingLoaderJob() throws JobParametersInvalidException, JobInstanceAlreadyCompleteException, JobRestartException, JobExecutionAlreadyRunningException {
        JobExecution jobExecution2 = jobLauncher.run(loaderJob, createJobParams());
        assertEquals("COMPLETED", jobExecution2.getExitStatus().getExitCode());
    }
    
    private void assertStagingTables(int expInvoiceNum, int expLineNum, int expProdNum, int expCustNum){
        assertEquals(expInvoiceNum, jdbcTemplate.queryForObject("select count(*) from staging_invoices", Integer.class).intValue());
        assertEquals(expLineNum, jdbcTemplate.queryForObject("select count(*) from staging_invoice_lines", Integer.class).intValue());
        assertEquals(expProdNum, jdbcTemplate.queryForObject("select count(*) from staging_products", Integer.class).intValue());
        assertEquals(expCustNum, jdbcTemplate.queryForObject("select count(*) from staging_customers", Integer.class).intValue());
    }

    private void assertLegacyTables(final int numOfInvoices, final int numOfLines, final int numOfProducts, final int numOfCustomers) throws DataAccessException {       
        assertEquals(numOfInvoices, jdbcTemplate.queryForObject("select count(*) from invoices", Integer.class).intValue());
        assertEquals(numOfLines, jdbcTemplate.queryForObject("select count(*) from invoice_lines", Integer.class).intValue());
        assertEquals(numOfProducts, jdbcTemplate.queryForObject("select count(*) from products", Integer.class).intValue());
        assertEquals(numOfCustomers, jdbcTemplate.queryForObject("select count(*) from customers", Integer.class).intValue());
    }
}
