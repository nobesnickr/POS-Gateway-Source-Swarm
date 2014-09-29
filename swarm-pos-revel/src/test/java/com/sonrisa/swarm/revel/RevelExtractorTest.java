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
package com.sonrisa.swarm.revel;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.swarm.mock.revel.MockRevelData;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalDTOTransformer;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.test.extractor.BaseExtractorTest;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;

/**
 * Testing the {@link RevelExtractor} class.
 */
public class RevelExtractorTest extends BaseExtractorTest<RevelAccount>{

    /** Class being tested */
    private RevelExtractor extractor;
    
    /** Account for the API */
    private RevelAccount account;
    
    private RevelAPIReader apiReader;
    
    private static final String TIMEZONE = "US/Pacific";
    
    /**
     * Initial setup of the mock service
     */
    @Before
    public void setUp() throws ExternalExtractorException{

        addJsonRestService(new ExternalCommandMatcher<RevelAccount>("products/ProductCategory"), MockRevelData.MOCK_PRODUCT_CATEGORIES);
        addJsonRestService(new ExternalCommandMatcher<RevelAccount>("resources/Product"), MockRevelData.MOCK_PRODUCTS);
        addJsonRestService(new ExternalCommandMatcher<RevelAccount>("resources/Customer"), MockRevelData.MOCK_CUSTOMERS);
        addJsonRestService(new ExternalCommandMatcher<RevelAccount>("resources/Order"), MockRevelData.MOCK_ORDERS);
        addJsonRestService(new ExternalCommandMatcher<RevelAccount>("resources/OrderItem"), MockRevelData.MOCK_ORDER_ITEMS);
        
        apiReader = new RevelAPIReader(api);
         
        // Setup test context
        this.extractor = new RevelExtractor(apiReader);
        this.extractor.setDtoTransformer(new ExternalDTOTransformer());
        
        // Setup account
        account = new RevelAccount();
        account.setUsername("revel-extractor-test");
        account.setStoreFilter("1");
        account.setTimeZone(TIMEZONE);
    }
    
    /**
     * Test that the number of extracted DTO items
     * matched the number in the mock JSON files
     * @throws ExternalExtractorException
     */
    @Test
    public void testQuantityOfItemsExtracted() throws ExternalExtractorException {

        extractor.fetchData(account, dataStore);

        assertQuantityOfItemsExtracted(MockRevelData.getRevelMockDescriptor());
    }
    
    /** 
      * Test case: Extractor is launched
      * 
      * Expected: Timestamp is requested for each class
      * @throws ExternalExtractorException 
      */
    @Test
    public void testWarehouseIsPromptedForTimestamp() throws ExternalExtractorException{
        
        // Act
        extractor.fetchData(account, dataStore);
        
        final String timeStamp = ISO8061DateTimeConverter.dateToMysqlString(filter.getTimestamp());
        
        assertContainsParams("updated_date__gte", timeStamp, 
                    "products/ProductCategory", 
                    "resources/Product", 
                    "resources/Customer",
                    "resources/Order",
                    "resources/OrderItem");
    }
    
    /**
     * Test case:
     *  Extractor is launched
     *  
     * Expected:
     *  Fields of the InvoiceDTO match the expected values
     */
    @Test
    public void testCommonInvoiceFields() throws ExternalExtractorException {

        // Act
        extractor.fetchData(account, dataStore);
        
        // Assert
        List<InvoiceDTO> invoices = getDtoFromCaptor(account, invoiceCaptor, InvoiceDTO.class);
        
        InvoiceDTO invoice = invoices.get(0);
        JsonNode invoiceJson = firstMockJson(MockRevelData.MOCK_ORDERS, "objects");

        assertEquals(invoiceJson.get("id").asText(), Long.toString(invoice.getRemoteId()));
        assertEquals(invoiceJson.get("final_total").asDouble(), invoice.getTotal(), 0.001);
        
        SimpleDateFormat pstDateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        pstDateformat.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
        
        assertEquals("Created date should be modified",
                invoiceJson.get("created_date").asText(),
                pstDateformat.format(invoice.getInvoiceTimestamp()));
        
        assertEquals("Updated date should have remote value",
                invoiceJson.get("updated_date").asText(), 
                dateToAssertionString(invoice.getLastModified(), "yyyy-MM-dd'T'HH:mm:ss"));
    }
    
    /** 
     * Test case: Extractor is launched
     * 
     * Expected: Establishment based filtering is executed
     */
   @Test
   public void testEstablishmentBasedFiltering() throws ExternalExtractorException{
       extractor.fetchData(account, dataStore);
       
       assertContainsParams("establishment", account.getStoreFilter(), 
                   "products/ProductCategory", 
                   "resources/Product", 
                   "resources/Customer",
                   "resources/Order",
                   "resources/OrderItem");
   }
    
    /**
     * Test case:
     *  - Two {@link RevelExtractor} instances are running, extracting
     *    sales data from two different stores
     *    
     * Expected:
     *  - Each instance is extracting data from its dedicated {@link RevelAccount}
     * @throws ExternalExtractorException 
     */
    @Test
    public void testParallelRevelExtraction() throws ExternalExtractorException{
        
        List<RevelAccount> accountList = new ArrayList<RevelAccount>();
        accountList.add(mockRevelAccount(111L));
        accountList.add(mockRevelAccount(222L));
                
        RevelExtractor extractor = new RevelExtractor(apiReader);
        extractor.setDtoTransformer(new ExternalDTOTransformer());
        
        RevelExtractor extractor2 = new RevelExtractor(apiReader);
        extractor2.setDtoTransformer(new ExternalDTOTransformer());
        
        // Act
        extractor2.fetchData(accountList.get(0), dataStore);
        extractor.fetchData(accountList.get(1), dataStore);
        
        // Assert
        verify(api).sendRequest(argThat(new ExternalCommandMatcher<RevelAccount>("resources/Order").andAccount(accountList.get(0))));
        verify(api).sendRequest(argThat(new ExternalCommandMatcher<RevelAccount>("resources/Order").andAccount(accountList.get(1))));
    }    
    
    /**
     * Creates mock Revel account
     */
    private RevelAccount mockRevelAccount(Long storeId){
        AESUtility aesUtility = new AESUtility();
        
        StoreEntity store = new StoreEntity();
        store.setId(storeId);
        store.setUsername(aesUtility.aesEncryptToBytes("username-" + storeId));
        
        return new RevelAccount(store, aesUtility);
    }
}

