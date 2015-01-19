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
package com.sonrisa.swarm.job.mos;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;
import hu.sonrisa.backend.dao.filter.FilterParameter;
import hu.sonrisa.backend.dao.filter.JpaFilter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonrisa.swarm.BaseBatchTest;
import com.sonrisa.swarm.job.ExtractorLauncherWriter;
import com.sonrisa.swarm.legacy.dao.CategoryDao;
import com.sonrisa.swarm.legacy.dao.CustomerDao;
import com.sonrisa.swarm.legacy.dao.InvoiceDao;
import com.sonrisa.swarm.legacy.dao.InvoiceLineDao;
import com.sonrisa.swarm.legacy.dao.ManufacturerDao;
import com.sonrisa.swarm.legacy.dao.ProductDao;
import com.sonrisa.swarm.legacy.service.StoreService;
import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.mock.mos.MockMosData;
import com.sonrisa.swarm.model.BaseSwarmEntity;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.mos.MosAPI;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.staging.service.CategoryStagingService;
import com.sonrisa.swarm.staging.service.CustomerStagingService;
import com.sonrisa.swarm.staging.service.InvoiceLineStagingService;
import com.sonrisa.swarm.staging.service.InvoiceStagingService;
import com.sonrisa.swarm.staging.service.ManufacturerStagingService;
import com.sonrisa.swarm.staging.service.ProductStagingService;
import com.sonrisa.swarm.warehouse.stage.StagingDTOService;

/**
 * Test cases for the whole Erply extraction process.
 * 
 *
 * @author joe
 */
public class MosExtractorFullTest extends BaseBatchTest {
    
    /** Port used by the Mock Erply server in this test class. */
    private static final int WIRE_PORT = 6127;

    @Autowired
    @Qualifier("mosExtractorLauncherTest")
    private JobLauncherTestUtils mosExtractorJobUtil;
    @Autowired
    @Qualifier("loaderJobTestUtil")
    private JobLauncherTestUtils loaderJobUtil;
        
    @Autowired
    private CustomerStagingService customerStgService;
    @Autowired
    private ProductStagingService productStgService;
    @Autowired
    private InvoiceStagingService invoiceStgService;
    @Autowired
    private InvoiceLineStagingService invoiceLineStgService;
    @Autowired
    private CategoryStagingService categoryStgService;
    @Autowired
    private ManufacturerStagingService manufacturerStgService;
    
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private InvoiceDao invoiceDao;
    @Autowired
    private InvoiceLineDao invoiceLineDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private ManufacturerDao manufacturerDao;    
    
    @Autowired
    private StoreService storeService;
    
    /** Service encrypting and decrypting strings from the MySQL database */
    @Autowired
    private AESUtility aesUtility;
    
    /**
     * Invoices before this date should be ignored
     */
    @Autowired
    private @Value("${extractor.ignoreEarlier.invoices}") String ignoreInvoicesProperty = "2000-01-01";
    
    /**
     * Dateformat of the {@link #ignoreInvoicesProperty} string 
     */
    private SimpleDateFormat ignoreDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    @Autowired
    StagingDTOService dtoService;
    
    /**
     * @see http://wiremock.org/getting-started.html
     */
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WIRE_PORT);
    
    /**
     * MosAPI used to access remote JSON contents, but mocked using {@link #wireMockRule}
     */
    @Autowired
    private MosAPI api;
    
    /**
     * Store entry in the stores table with active set to true
     */
    StoreEntity activeStore;

    /**
     * Initial setup sets up the account, passes the authentication information
     * for the ErplyAPI singleton, and reads the json files for the mock
     * responses. For all this we use the utility class
     */
    @Before
    public void setUp() {
        // creation of an active and an inactive store
        final Long mosApiID = jdbcTemplate.queryForObject("select api_id from apis where name like 'merchantos_gw'", Long.class);
        
        activeStore = createMockStore(mosApiID, "54321",44444);
        StoreEntity inactiveStore = createMockStore(mosApiID, "12345",77777);
        inactiveStore.setActive(Boolean.FALSE);
        
        // creates a store
        storeService.save(activeStore);
        storeService.save(inactiveStore);
        
        MosUtility.setUpWiremock();
    }
    
    /**
     * Test case: 
     *  - starts a mock Merchant OS server
     *  - extracts data from them into the staging db
     *  - asserts the number of the records inserted into the staging db
     *  - launches the loader job which moves the records from the staging db to the legacy db
     *  - asserts the number of the records inserted into the legacy db
     */
    @Test
    public void testExecution() {       
        final JobExecution extractionResult = launchJob(mosExtractorJobUtil);
        
        // we expect that 1 store has been fetched because 1 active store exists
        final int numOfExtractedStores = extractionResult.getExecutionContext().getInt(ExtractorLauncherWriter.NUM_OF_STORES_EXTRACTED);
        assertEquals(1, numOfExtractedStores);
        
        // assert staging counts
        Map<String, Integer> correctCount = MockMosData.getCountOfMockJsonItems();
        assertEquals((int) correctCount.get("CategoryDTO"), categoryStgService.findAllIds().size());
        assertEquals((int) correctCount.get("CustomerDTO"), customerStgService.findAllIds().size());
        assertEquals((int) correctCount.get("InvoiceDTO"), invoiceStgService.findAllIds().size());
        assertEquals((int) correctCount.get("InvoiceLineDTO"), invoiceLineStgService.findAllIds().size());
        assertEquals((int) correctCount.get("ProductDTO"), productStgService.findAllIds().size());
        assertEquals((int) correctCount.get("ManufacturerDTO"), manufacturerStgService.findAllIds().size());
        
        // launch the loader job
        launchJob(loaderJobUtil);        
        
        // assert the legacy db
        assertEquals((int) correctCount.get("CategoryDTO"), categoryDao.findAll().size());
        assertEquals((int) correctCount.get("CustomerDTO"), customerDao.findAll().size());
        assertEquals((int) correctCount.get("InvoiceDTO"), invoiceDao.findAll().size());
        assertEquals((int) correctCount.get("InvoiceLineDTO"), invoiceLineDao.findAll().size());
        assertEquals((int) correctCount.get("ProductDTO"), productDao.findAll().size());
        assertEquals((int) correctCount.get("ManufacturerDTO"), manufacturerDao.findAll().size());
        assertEquals(0, categoryStgService.findAllIds().size());
        assertEquals(0, customerStgService.findAllIds().size());
        assertEquals(0, invoiceStgService.findAllIds().size());
        assertEquals(0, invoiceLineStgService.findAllIds().size());
        assertEquals(0, productStgService.findAllIds().size());
        assertEquals(0, manufacturerStgService.findAllIds().size());
    }
    
    /**
     * Test case:
     *  - Using the mock JSON data extract content from the remote system
     *  - Move this data through staging and finally into the legacy tables
     *  - Meanwhile internal caches should be updated
     * Expected:
     *  - Internal timestamp caches are updated 
     * @throws ParseException 
     */
    @Test
    public void testInvoiceLineFiltering() throws ParseException{
    	Date originalIgnoreFilter = ignoreDateFormat.parse(ignoreInvoicesProperty);
    	
    	launchJob(mosExtractorJobUtil);
    	launchJob(mosExtractorJobUtil);
        
    	// Prepare filter for Wiremock
        final String filter = ISO8061DateTimeConverter.dateToMerchantOSURIEncodedString(originalIgnoreFilter);
        
        verify(1, getRequestedFor(urlMatching("/API/Account/[0-9]+/SaleLine.*timeStamp=%3E," + filter + "(.*?)")));
    }
    
    /**
     * Test case: 
     *      There are two Merchant OS sales in the mock json
     *      files, one with completed set to <code>true</code>, and one when where
     *      completed is set to <code>false</code>
     * Expected:
     *      These are appropriately written into the legacy DB.
     */
    @Test
    public void testCompletedFieldIsBeingFilled(){

        final Long completedSaleId = 6L;
        final Long notCompletedSaleId = 4L;

        launchJob(mosExtractorJobUtil);
        
        assertEquals("1", invoiceStgService.findSingle(getFilterForLegacyId(completedSaleId)).getCompleted());
        assertEquals("0", invoiceStgService.findSingle(getFilterForLegacyId(notCompletedSaleId)).getCompleted());
        
        // launch the loader job
        launchJob(loaderJobUtil);     

        assertEquals(true, invoiceDao.findByStoreAndForeignId(activeStore.getId(), completedSaleId).getCompleted());
        assertEquals(false, invoiceDao.findByStoreAndForeignId(activeStore.getId(), notCompletedSaleId).getCompleted());
    }
    
    /**
     * Generate JPA filter for retrieving sales from the staging_invoice table
     * @param saleId ls_invoice_id
     * @return Filter
     */
    private JpaFilter<InvoiceStage> getFilterForLegacyId(final Long saleId){
        return new SimpleFilter<InvoiceStage>(InvoiceStage.class,new FilterParameter("lsInvoiceId", saleId.toString()));
    }

    /**
     * 
     * @param erplyApiId
     * @param apiKey
     * @param username
     * @return 
     */
    private StoreEntity createMockStore(final Long mosApiId, String apiKey, int accountId) {
        final StoreEntity store = MockTestData.mockStoreEntity("myStore");
        store.setApiId(mosApiId);
        store.setApiUrl(("http://localhost:"+WIRE_PORT+"/API/").getBytes());
        store.setAccountNumber(accountId);
        store.setApiKey(aesUtility.aesEncryptToBytes(apiKey));
        return store;
    }
}
