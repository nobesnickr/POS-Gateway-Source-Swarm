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
package com.sonrisa.swarm.datastore.stage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.mock.MockDTOUtil;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.CustomerStage;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.model.staging.ProductStage;
import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.warehouse.DWFilter;
import com.sonrisa.swarm.shopify.dto.ShopifyCustomerDTO;
import com.sonrisa.swarm.shopify.dto.ShopifyInvoiceDTO;
import com.sonrisa.swarm.shopify.dto.ShopifyInvoiceLineDTO;
import com.sonrisa.swarm.staging.service.CustomerStagingService;
import com.sonrisa.swarm.staging.service.InvoiceLineStagingService;
import com.sonrisa.swarm.staging.service.InvoiceStagingService;
import com.sonrisa.swarm.staging.service.ProductStagingService;
import com.sonrisa.swarm.warehouse.stage.StagingDTOService;

/**
 * This class test the behaviour of the StagingDataStore
 * class and the functionality of all the DAOs and services
 * it depends on. It creates dummy DTO objects and checks
 * if they are correctly inserted into the staging tables. 
 */
@Transactional
public class StagingDTOServiceTest extends BaseIntegrationTest {
	
	/** The staging data save service that saves data into the staging tables	 */
	@Autowired
	private StagingDTOService dataStore;
	@Autowired
	private CustomerStagingService customerStgService;
	@Autowired
	private ProductStagingService productStgService;
	@Autowired
	private InvoiceStagingService invoiceStgService;
	@Autowired
	private InvoiceLineStagingService invoiceLineStgService;
	
    @Autowired
    private @Value("${extractor.ignoreEarlier.invoices}") String ignoreInvoicesProperty = "2000-01-01";
	
	/** To modify the legacy databases's updates table we need the jdbc template */
    @Autowired
    protected JdbcTemplate jdbcTemplate;
		
	/** Test that a particular CustomerDTO is correctly inserted into the staging table */
	@Test
	public void testCustomerInsert () {
		// Some random data of a customer
		final long storeId = 123;
		CustomerDTO customer = MockDTOUtil.mockCustomerDTO(456L);
		
		// create a list as the SwarmDataStore interface expects a List<CustomerDTO>
		List<CustomerDTO> list = new ArrayList<CustomerDTO>();
		list.add(customer);
		
		// create a SwarmStore
		SwarmStore store = MockDTOUtil.mockStore(storeId);
		
		// Save data into the data store
		dataStore.saveToStage(store, list, CustomerDTO.class);
		
		assertEquals("Exactly one row should be in the stage table", 1, customerStgService.findAllIds().size());
		
		// Read back the inserted line
		CustomerStage stageCustomer = customerStgService.find(customerStgService.findAllIds().get(0));
		assertNotNull(stageCustomer);
		assertEquals(Long.toString(customer.getRemoteId()), stageCustomer.getLsCustomerId());
		assertEquals(customer.getFirstName(), stageCustomer.getFirstname());
		assertEquals(customer.getLastName(), stageCustomer.getLastname());
		assertEquals(customer.getEmail(), stageCustomer.getEmail());
		assertEquals(customer.getPhoneNumber(), stageCustomer.getPhone());
		assertEquals(customer.getAddress(), stageCustomer.getAddress1());
		assertEquals(customer.getAddress2(), stageCustomer.getAddress2());
		assertEquals(customer.getCity(), stageCustomer.getCity());
		assertEquals(customer.getState(), stageCustomer.getState());
		assertEquals(customer.getNotes(), stageCustomer.getNotes());
		assertEquals(null, stageCustomer.getLsSbsNo());
	}
	
	/** 
	 * Test that the local swarm partner's id appears in the stage table if passed using DTOs 
	 */
	@Test 
	public void testStoreIdIsSaved () {
		// Some random data of a customer
		final long storeId = 123;
		CustomerDTO customer = MockDTOUtil.mockCustomerDTO(456L);
		
		// create a list as the SwarmDataStore interface expects a List<CustomerDTO>
		List<CustomerDTO> list = new ArrayList<CustomerDTO>();
		list.add(customer);
		
		// create a SwarmStore
		SwarmStore store = MockDTOUtil.mockStore(storeId);
		dataStore.saveToStage(store, list, CustomerDTO.class);
		
		//Test the inserted store id
		CustomerStage stageCustomer = customerStgService.find(customerStgService.findAllIds().get(0));
		assertEquals((Long)storeId, stageCustomer.getStoreId());
	}
	
	/**
	 * Test that products are correctly inserted into the staging tables
	 */
	@Test
	public void testProductInsert(){
		final long storeId = 456;
		ProductDTO product = MockDTOUtil.mockProductDTO();

		// create a SwarmStore
		SwarmStore store = MockDTOUtil.mockStore(storeId);
		// Save data into the data store
		List<ProductDTO> list = new ArrayList<ProductDTO>();
		list.add(product);
		dataStore.saveToStage(store, list, ProductDTO.class);
		
		//One row was inserted
		assertEquals("Exactly one row should be in the stage table", 1, productStgService.findAllIds().size());
		
		ProductStage stageProduct = productStgService.find(productStgService.findAllIds().get(0));
		assertNotNull(stageProduct);
		assertEquals(Long.toString(product.getRemoteId()), stageProduct.getLsProductId());
		assertEquals(null, stageProduct.getLsCategoryId());
		assertEquals(product.getCategoryName(), stageProduct.getCategory());
		assertEquals(null, stageProduct.getLsManufacturerId());
		assertEquals(product.getManufacturerName(), stageProduct.getManufacturer());
		assertEquals(product.getDescription(), stageProduct.getDescription());
		assertEquals(product.getPrice(), Double.parseDouble(stageProduct.getPrice()), 0.1);
		assertEquals(product.getEan(), stageProduct.getEan());
		assertEquals(product.getUpc(), stageProduct.getUpc());
		assertEquals(product.getStoreSku(), stageProduct.getStoreSku());
		assertEquals(product.getLastModified().getTime(), Long.parseLong(stageProduct.getModifiedAt()));
		assertEquals((Long)storeId, stageProduct.getStoreId());
	}
	
	/**
	 * Test saving invoices into the stage tables using a data store
	 */
	@Test
	public void testInvoiceInsert(){
		final long storeId = 789;
		InvoiceDTO invoice = MockDTOUtil.mockInvoiceDTO(1L);
		
		// create a SwarmStore
		SwarmStore store = MockDTOUtil.mockStore(storeId);
		// Save data into the data store
		List<InvoiceDTO> list = new ArrayList<InvoiceDTO>();
		list.add(invoice);
		dataStore.saveToStage(store, list, InvoiceDTO.class);
		
		// one into invoice
		assertEquals("Exactly one row should be in the stage table", 1, invoiceStgService.findAllIds().size());
		// non into invoice line
		assertEquals("Inserting invoices shouldn't insert invoice lines", 0, invoiceLineStgService.findAllIds().size());
		
		InvoiceStage stageInvoice = invoiceStgService.find(invoiceStgService.findAllIds().get(0));
		assertNotNull(stageInvoice);
		assertEquals(Long.toString(invoice.getRemoteId()), stageInvoice.getLsInvoiceId());
		assertEquals(Long.toString(invoice.getCustomerId()), stageInvoice.getLsCustomerId());
		assertEquals(invoice.getInvoiceNumber(),stageInvoice.getInvoiceNo());
		assertEquals(invoice.getTotal(),Double.parseDouble(stageInvoice.getTotal()),0.1);
		assertEquals(invoice.getInvoiceTimestamp().getTime(),Long.parseLong(stageInvoice.getTs()));
		assertEquals(invoice.getLastModified().getTime(),Long.parseLong(stageInvoice.getLastModified()));
		assertEquals((Long)storeId,stageInvoice.getStoreId());
	}
	
	/**
	 * Test that passing an empty list doesn't fail, and doesn't affect
	 * the staging tables
	 */
	@Test
	public void testSavingEmptyList(){
		final long storeId = 789;
		SwarmStore store = MockDTOUtil.mockStore(storeId);
		
		List<InvoiceLineDTO> list = new ArrayList<InvoiceLineDTO>();
		dataStore.saveToStage(store, list, InvoiceLineDTO.class);		

		assertEquals(0, invoiceLineStgService.findAllIds().size());
	}

	/**
	 * Test that an initial import is starting if no update
	 * has yet taken place for the given shop
	 */
	@Test
	public void testTimestampIsZeroIfNoData(){
		final long storeId = 789;
		final SwarmStore store = MockDTOUtil.mockStore(storeId);

		long dataStoreFilter = dataStore.getFilter(store, ProductDTO.class).getTimestamp().getTime();
		assertEquals(0, dataStoreFilter);
	}
	
	
	/**
	 * Test that timeStamp is correctly retrieved from the last_modified column
	 */
	@Test
	public void testTimestampRetrievedFromLastModified(){
		final long storeId = 789;
		final SwarmStore store = MockDTOUtil.mockStore(storeId);
		
		final long mockTimeStamp = 1375863224191L;
		final long timeInterval = 3600L;
		
		//insert 10 customers each with different time stamp, but the most recent
		//time stamp is the mockTimeStamp
		for(int i = 9; i >= 0; i--){
			jdbcTemplate.update("INSERT INTO customers (ls_customer_id, store_id, name, last_modified) VALUES (?,?,?,?);",
								i + 10, storeId, String.format("Sonrisa %d", i+1), new Timestamp(mockTimeStamp - i * timeInterval));
		}
		
		// Read back the timestamp for the given store
		DWFilter dataStoreFilter = dataStore.getFilter(store, CustomerDTO.class);
		assertEquals(mockTimeStamp, dataStoreFilter.getTimestamp().getTime());
		assertEquals(19, dataStoreFilter.getId());
		
		//insert 10 new customers some with more recent time stamps, but with different 
		//store ids
		final long otherStoreId = 1000;
        for(int i = 6; i >= -3; i--){
            jdbcTemplate.update("INSERT INTO customers (ls_customer_id, store_id, name, last_modified) VALUES (?,?,?,?);",
                                i + 10, otherStoreId, String.format("Sonrisa %d", i+1), new Timestamp(mockTimeStamp - i * timeInterval));
        }
        
        dataStoreFilter = dataStore.getFilter(store, CustomerDTO.class);
        assertEquals(mockTimeStamp, dataStoreFilter.getTimestamp().getTime());
        assertEquals(19, dataStoreFilter.getId());
	}
	

    /**
     * Test that timeStamp is correctly retrieved from the last_modified column
     */
    @Test
    public void testTimestampRetrievedFromLastModifiedForInvoice(){
        StoreEntity storeEntity = MockTestData.mockStoreEntity("store");
        storeService.save(storeEntity);
        
        final SwarmStore store = MockDTOUtil.mockStore(storeEntity.getId());
        
        final long mockTimeStamp = 1375863224191L;
        final long timeInterval = 3600L;
        
        //insert 10 customers each with different time stamp, but the most recent
        //time stamp is the mockTimeStamp
        for(int i = 9; i >= 0; i--){
            jdbcTemplate.update("INSERT INTO invoices (ls_invoice_id, store_id, ts, last_modified) VALUES (?,?,?,?);",
                                i + 10, storeEntity.getId(), 
                                new Timestamp(mockTimeStamp - 5000 - i * timeInterval), // ts (!= last_modified)
                                new Timestamp(mockTimeStamp - i * timeInterval)); // last modified
        }
        
        // Read back the timestamp for the given store
        DWFilter dataStoreFilter = dataStore.getFilter(store, InvoiceDTO.class);
        assertEquals(mockTimeStamp, dataStoreFilter.getTimestamp().getTime());
        assertEquals(19, dataStoreFilter.getId());
    }
	
	/**
	 * Test that the CachingAndIgnoringDataStore, the abstract base class
	 * of the StagingDataStore returns the ignoreEarlierInvoices property
	 * of the swarm.properties file for the getLastTimestamp method, if 
	 * prompted for invoices
	 */
	@Test
	public void testIgnoreFilterApplied() throws ParseException{
	    final long storeId = 789;
        final SwarmStore store = MockDTOUtil.mockStore(storeId);
        
        Timestamp initialCustomerTimestamp = dataStore.getFilter(store, CustomerDTO.class).getTimestamp();
        Timestamp initialInvoiceTimestamp = dataStore.getFilter(store, InvoiceDTO.class).getTimestamp();
        Timestamp initialInvoiceLineTimestamp = dataStore.getFilter(store, InvoiceLineDTO.class).getTimestamp();
        
        long expected = new SimpleDateFormat("yyyy-MM-dd").parse(this.ignoreInvoicesProperty).getTime();
        
        assertEquals(0L, initialCustomerTimestamp.getTime());
        assertEquals(expected, initialInvoiceTimestamp.getTime());
        assertEquals(expected, initialInvoiceLineTimestamp.getTime());
	}
	
	/**
	 * Test inserting a customer entity affects the cache's ID value
	 */
	@Test
	public void testIdFiltering() {
	    ShopifyCustomerDTO customer = new ShopifyCustomerDTO();
	    customer.setCustomerId(12345);
	    
        final long storeId = 789;
        final SwarmStore store = MockDTOUtil.mockStore(storeId);	    
	    dataStore.saveToStage(store, Arrays.asList(customer), CustomerDTO.class);
	    
	    assertEquals(customer.getRemoteId(), dataStore.getFilter(store, CustomerDTO.class).getId());
	}
	
	/**
	 * Test filtering invoices and invoice lines
	 */
	@Test
	public void testInvoiceAndInvoiceLineFiltering(){
	    ShopifyInvoiceDTO invoice = new ShopifyInvoiceDTO();
	    invoice.setInvoiceId(45678);
	    
        ShopifyInvoiceLineDTO invoiceLine = new ShopifyInvoiceLineDTO();
        invoiceLine.setLineId(9876);
        invoiceLine.setInvoiceId(invoice.getRemoteId());
        
        final long storeId = 789;
        final SwarmStore store = MockDTOUtil.mockStore(storeId);    
        
        final long secondStoreId = 987;
        final SwarmStore secondStore = MockDTOUtil.mockStore(secondStoreId);
        
        // Save an invoice to the first
        dataStore.saveToStage(store, Arrays.asList(invoice), InvoiceDTO.class);
        // And an invoice line to the second store
        dataStore.saveToStage(secondStore, Arrays.asList(invoiceLine), InvoiceLineDTO.class);
        
        assertEquals("Invoice should be cached", 
                invoice.getRemoteId(), dataStore.getFilter(store, InvoiceDTO.class).getId());
        
        assertEquals("Invoice line should not be cached and InvoiceDTO should not be found in legacy DB", 
                0L, dataStore.getFilter(store, InvoiceLineDTO.class).getId());
        
        assertEquals("Invoice should not be cached", 
                0L, dataStore.getFilter(secondStore, InvoiceDTO.class).getId());
        
        assertEquals("Invoice line should be cache as InvoiceLineDTO", 
                invoiceLine.getRemoteId(), dataStore.getFilter(secondStore, InvoiceLineDTO.class).getId());
	}
	
	@After
	public void clearCache(){
	    dataStore.clearCache();
	}
}
