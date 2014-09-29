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
package com.sonrisa.swarm;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.model.legacy.CategoryEntity;
import com.sonrisa.swarm.model.legacy.CustomerEntity;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.InvoiceLineEntity;
import com.sonrisa.swarm.model.legacy.ManufacturerEntity;
import com.sonrisa.swarm.model.legacy.ProductEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.CategoryStage;
import com.sonrisa.swarm.model.staging.CustomerStage;
import com.sonrisa.swarm.model.staging.InvoiceLineStage;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.model.staging.ManufacturerStage;
import com.sonrisa.swarm.model.staging.ProductStage;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;
import com.sonrisa.swarm.posintegration.service.ApiService;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;
import com.sonrisa.swarm.retailpro.service.RpStoreService;

/**
 * Common base class of the integration tests.
 *
 * @author joe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/appServlet/swarm-applicationContext.xml")
@ActiveProfiles("inmemory-test-db")
public abstract class BaseIntegrationTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseIntegrationTest.class);

    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    protected WebApplicationContext webContext;
    protected MockMvc mockMvc;
    
    @Autowired
    protected ObjectMapper objectMapper;
    
    @Autowired
    protected StoreService storeService;
    @Autowired
    protected RpStoreService rpStoreService;
    @Autowired
    protected ApiService apiService;
    
    @Autowired
    protected AESUtility aesUtility;   

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webContext).build();
    }
    
    @After
    public void truncateAll() {
        // FIXME the transaction should be rolled back automatically instead of this, but i haven't got enough time to configure this properly - joe
        jdbcTemplate.execute("TRUNCATE TABLE staging_invoices;");
        jdbcTemplate.execute("TRUNCATE TABLE staging_invoice_lines;");
        jdbcTemplate.execute("TRUNCATE TABLE staging_products;");
        jdbcTemplate.execute("TRUNCATE TABLE staging_customers;");
        jdbcTemplate.execute("TRUNCATE TABLE staging_categories;");
        jdbcTemplate.execute("TRUNCATE TABLE staging_manufacturers;");
        jdbcTemplate.execute("TRUNCATE TABLE stores_rp;");
        jdbcTemplate.execute("TRUNCATE TABLE invoice_lines;");
        jdbcTemplate.execute("DELETE FROM stores;");
        jdbcTemplate.execute("TRUNCATE TABLE categories;");
        jdbcTemplate.execute("TRUNCATE TABLE manufacturers;");
        jdbcTemplate.execute("TRUNCATE TABLE customers;");
        jdbcTemplate.execute("TRUNCATE TABLE products;");
        jdbcTemplate.execute("TRUNCATE TABLE updates;");
        
        jdbcTemplate.execute("TRUNCATE TABLE retailpro_client;");
        jdbcTemplate.execute("TRUNCATE TABLE retailpro_plugin;");
        
        jdbcTemplate.execute("TRUNCATE TABLE retailpro_configuration;");
    }

    // ------------------------------------------------------------------------
    // ~ Protected methods
    // ------------------------------------------------------------------------
    
    protected void assertResult(String msg, ResultActions result, ResultMatcher matcher){
        try {
            result.andExpect(matcher);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(msg);
        }
    }
    
    /**
     * Performs the received HTTP request.
     * 
     * @param request
     * @return 
     */
    protected ResultActions perfom(RequestBuilder request){
        ResultActions result = null;
        try {
            result = mockMvc.perform(request);
        } catch (Exception ex) {
            throw new RuntimeException("An exception has been occured during request.", ex);
        }
        return result;
    }
    
    /**
     * Asserts the HTTP status and the content type of the result action.
     * <p/>
     * 
     * The expected values are:
     * <ul>
     *  <li>status: 200 (OK)</li>
     *  <li>content type: JSON and charset UTF-8</li>
     * </ul>
     * 
     * @param result 
     */
    protected void assertStatusAndContentType(ResultActions result) {
        assertJsonContentType(result);
        assertCreatedStatus(result);
    }
    
    /**
     * Asserts the HTTP status of the result action.
     * <p/>
     * 
     * The expected value: 200 (OK)
     * @param result 
     */    
    protected void assertOkStatus(ResultActions result){
        try {
            result.andExpect(status().isOk());
        } catch (Exception ex) {
           throw new RuntimeException("An exception has been occured during the OK status assertation.", ex);
        }
    }
    
    /**
     * Asserts the HTTP status of the result action.
     * <p/>
     * 
     * The expected value: 201 (CREATED)
     * @param result 
     */    
    protected void assertCreatedStatus(ResultActions result){
        try {
            result.andExpect(status().isCreated());
        } catch (Exception ex) {
           throw new RuntimeException("An exception has been occured during the CREATED status assertation.", ex);
        }
    }
    
    /**
     * Asserts the HTTP status of the result action.
     * <p/>
     * 
     * The expected value: 400 (BAD REQUEST)
     * @param result 
     */    
	protected void assertBadRequestStatus(ResultActions result) {
		try {
			result.andExpect(status().isBadRequest());
		} catch (Exception ex) {
			throw new RuntimeException("An exception has been occured during the BAD REQUEST status assertation.", ex);
		}
	}

    /**
     * Asserts the content type of the result action.
     * <p/>
     *
     * The expected value: JSON and charset UTF-8
     *
     * @param result
     */
    protected void assertJsonContentType(ResultActions result) throws RuntimeException {
        try {
            result.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"));
        } catch (Exception ex) {
            throw new RuntimeException("An exception has been occured during the content type assertation.", ex);
        }
    }
    
    /**
     * It creates a spring job execution parameter ojbect with a unique identifier.
     * 
     * @return 
     */
    protected JobParameters createJobParams(){
        final JobParametersBuilder jb = new JobParametersBuilder();
        jb.addString("runId", UUID.randomUUID().toString());
        return jb.toJobParameters();
    }
    
    /**
     * Check that the invoice exists in the DB.
     * 
     * @param invoiceNumber - the expected number
     */
    protected void checkInvoiceExists(Integer invoiceNumber){
    	assertEquals(invoiceNumber, jdbcTemplate.queryForObject("select count(id) from staging_invoices", Integer.class));
		
		Map<String, Object> result = jdbcTemplate.queryForMap("select ls_customer_id, invoice_no from staging_invoices where ls_customer_id=?", 123456);
		
		assertEquals("123456", result.get("ls_customer_id"));
    }
    
    /**
     *  Check that the customer exists in the DB.
     *  
     * @param customerNumber - the expected number
     */
    protected void checkCustomerExists(Integer customerNumber){
    	assertEquals(customerNumber, jdbcTemplate.queryForObject("select count(id) from staging_customers", Integer.class));
		
		Map<String, Object> result = jdbcTemplate.queryForMap("select firstname, lastname, email from staging_customers where ls_customer_id=?", 123456);
		
		assertEquals("Foo", result.get("firstname"));
		assertEquals("Bar", result.get("lastname"));
		assertEquals("foo@bar.com", result.get("email"));
    }
    
    /**
     * Asserts that the basic properties of the staging and the other customer are equal.
     * 
     * @param stgCust
     * @param cust 
     */
    protected void assertCustomerEquals(final CustomerStage stgCust, final CustomerEntity cust) {
        assertEquals(stgCust.getAddress1(), cust.getAddress1());
        assertEquals(stgCust.getAddress2(), cust.getAddress2());
        assertEquals(stgCust.getCity(), cust.getCity());
        assertEquals(stgCust.getEmail(), cust.getEmail());
        assertEquals(stgCust.getFirstname(), cust.getFirstname());
        assertEquals(stgCust.getLastname(), cust.getLastname());
        assertEquals(stgCust.getName(), cust.getName());
        assertEquals(stgCust.getNotes(), cust.getNotes());
        assertEquals(stgCust.getPhone(), cust.getPhone());
        assertEquals(stgCust.getState(), cust.getState());
        assertEquals(stgCust.getLsCustomerId(), cust.getLsCustomerId().toString());
        assertTimestamp(stgCust.getLastModified(), cust.getLastModified());
    }
    
    private void assertTimestamp(String ts, Date date){
        assertEquals(ts, Long.toString(date.getTime()));    
    }
    
    /**
     * Asserts that the basic properties of the staging and the other proudct are equal.
     * 
     * @param stgProd
     * @param prod 
     */
    protected void assertProductEquals(final ProductStage stgProd, final ProductEntity prod) {
        assertEquals(stgProd.getDescription(), prod.getDescription());
        assertEquals(stgProd.getEan(), prod.getEan());
        assertEquals(stgProd.getLsProductId(), prod.getLsProductId().toString());
        assertEquals(stgProd.getSku(), prod.getSku());
        assertEquals(stgProd.getUpc(), prod.getUpc());
        assertEquals(Double.parseDouble(stgProd.getPrice()), prod.getPrice().doubleValue(), 0); 
        assertTimestamp(stgProd.getModifiedAt(), prod.getLastModified());
    }
    
    protected void assertInvoiceEquals(final InvoiceStage stgInv, InvoiceEntity inv){
        assertEquals(stgInv.getInvoiceNo(), inv.getInvoiceNo());
        assertEquals(stgInv.getLsCustomerId(), inv.getLsCustomerId().toString());
        assertEquals(stgInv.getStoreId(), inv.getStore().getId());
        final double doubleValue = inv.getTotal() != null ? inv.getTotal().doubleValue() : 0;
        assertEquals(Double.valueOf(stgInv.getTotal()), doubleValue, 0);
        assertTimestamp(stgInv.getTs(),inv.getTs());
    }
    
    protected void assertInvoiceLineEqual(final InvoiceLineStage stgLine, final ProductStage stgProd, InvoiceLineEntity line){
        assertEquals(stgLine.getLsInvoiceId(), line.getInvoice().getLsInvoiceId().toString());
        assertEquals(stgLine.getLsLineId(), line.getLsLineId().toString());
        assertEquals(stgLine.getLsProductId(), line.getLsProductId().toString());
        assertEquals(stgLine.getPrice(), line.getPrice().toString());
        assertEquals(stgLine.getQuantity(), line.getQuantity().toString());
        assertEquals(Double.valueOf(stgLine.getTax()), line.getTax().doubleValue(), 0.0);
        
        assertEquals(stgProd.getCategory(), line.getClazz());
        assertEquals(stgProd.getDescription(), line.getDescription());
        assertEquals(stgProd.getManufacturer(), line.getFamily());
    }
    
    protected void assertCategoryEqual(final CategoryStage expectedCategory, final CategoryEntity actualCategory){
        assertEquals(expectedCategory.getLsCategoryId(), nullOrToString(actualCategory.getLsCategoryId()));
        assertEquals(expectedCategory.getLsParentCategoryId(), nullOrToString(actualCategory.getLsParentCategoryId()));
        assertEquals(expectedCategory.getLsLeftCategoryId(), nullOrToString(actualCategory.getLsLeftCategoryId()));
        assertEquals(expectedCategory.getLsRightCategoryId(), nullOrToString(actualCategory.getLsRightCategoryId()));
        assertEquals(expectedCategory.getLastModified(), nullOrToString(actualCategory.getLastModified().getTime()));
    }
    
    protected void assertManufacturerEqual(final ManufacturerStage expectedManufacturer, final ManufacturerEntity actualManufacturer){
        assertEquals(expectedManufacturer.getManufacturerId(), Long.toString(actualManufacturer.getManufacturerId()));
        assertEquals(expectedManufacturer.getManufacturerName(), actualManufacturer.getManufacturerName());
        assertEquals(expectedManufacturer.getLastModified(), Long.toString(actualManufacturer.getLastModified().getTime()));
    }
   
    protected void createMockStores(Set<String> storeNums, final String swarmId, final String sbs) {
        for (String storeNo : storeNums) {
            final StoreEntity store = MockTestData.mockStoreEntity("Test store " + storeNo);
            storeService.save(store);
          
            final RpStoreEntity rpStore = MockRetailProData.mockRpStoreEntity(swarmId, sbs, storeNo);
            rpStore.setStoreId(store.getId());
            rpStoreService.save(rpStore);
        }
    }
    
    private static final String nullOrToString(Long longValue){
        return longValue == null ? null : longValue.toString();
    }
}
