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
package com.sonrisa.swarm.shopify;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.shopify.ShopifyAPIReader;
import com.sonrisa.shopify.ShopifyAccount;
import com.sonrisa.shopify.ShopifyExtractor;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.shopify.MockShopifyData;
import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalDTOTransformer;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.test.extractor.BaseExtractorTest;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joe
 */
@RunWith(MockitoJUnitRunner.class)
public class ShopifyExtractorTest extends BaseExtractorTest<ShopifyAccount>{
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopifyExtractorTest.class);
    
     /** Mock token used for authentication */
    private static final String oauthToken = "abc1a1cc351f3aa31ec2921951e1acee3073b4789770ea6a374432d150fbdc73";
    
    /** Tested class */
    private ShopifyExtractor extractor;
    
    /** Account used during mocking */
    private ShopifyAccount account;
        
    /**
     * Initial setup of the mock service
     * @throws ExternalExtractorException 
     */
    @Before
    public void setUp() throws ExternalExtractorException{
        addJsonRestService(new ExternalCommandMatcher<ShopifyAccount>("products.json"), MockShopifyData.MOCK_SHOPIFY_PRODUCTS);
        addJsonRestService(new ExternalCommandMatcher<ShopifyAccount>("customers.json"), MockShopifyData.MOCK_SHOPIFY_CUSTOMERS);
        addJsonRestService(new ExternalCommandMatcher<ShopifyAccount>("orders.json"), MockShopifyData.MOCK_SHOPIFY_ORDERS);

        // Setup test context
        ShopifyAPIReader apiReader = new ShopifyAPIReader(api);
        
        // Setup test context
        this.extractor = new ShopifyExtractor(apiReader);
        this.extractor.setDtoTransformer(new ExternalDTOTransformer());
        
        // Setup account
        account = new ShopifyAccount();
    }    
    
    /**
     * Test that the number of extracted DTO items
     * matched the number in the mock JSON files
     * @throws ExternalExtractorException
     */
    @Test
    public void testQuantityOfItemsExtracted() throws ExternalExtractorException {

        extractor.fetchData(account, dataStore);
        
        assertQuantityOfItemsExtracted(MockShopifyData.getShopifyMockDescriptor());
    }
    
    /**
     * Test that timestamp URL parameter is properly added
     * @throws ExternalExtractorException
     */
    @Test
    public void testTimestampSent() throws ExternalExtractorException {
        
        extractor.fetchData(account, dataStore);
        
        final String expectedDateFilter = ISO8061DateTimeConverter.dateToMysqlString(filter.getTimestamp());
        final String expectedIdFilter = Long.toString(filter.getId());
        
        assertContainsParams("since_id", expectedIdFilter, "customers.json");
        assertContainsParams("created_at_min", expectedDateFilter, "orders.json");
        assertContainsParams("updated_at_min", expectedDateFilter, "products.json");
    }
    
    /**
     * Test case:
     *  Sending request the the Shopify REST service
     *  
     * Expected behavior: 
     *  When fetching orders, the <code>status</code> parameter
     *  is set to <code>any</code>
     */
    @Test
    public void testStatusParamProvided() throws ExternalExtractorException {
        
        extractor.fetchData(account, dataStore);
        
        assertContainsParams("status", "any", "orders.json");
    }
    
    /**
     * Test case: There is 1 Customer in Shopify
     * 
     * Expected: This is retrieved from Shopify and mapped appropriately
     *  
     * @throws ExternalExtractorException
     */
    @Test
    public void testCommonCustomerFields() throws ExternalExtractorException {

        extractor.fetchData(account, dataStore);
        List<CustomerDTO> customers = getDtoFromCaptor(account, customerCaptor, CustomerDTO.class);
        
        CustomerDTO customer = customers.get(0);

        JsonNode customerJson = firstMockJson(MockShopifyData.MOCK_SHOPIFY_CUSTOMERS, "customers");
        
        assertEquals(customerJson.get("updated_at").asText(), dateToAssertionString(customer.getLastModified()));
        assertEquals(customerJson.get("default_address").get("address1").asText(), customer.getAddress());
        assertEquals(customerJson.get("default_address").get("province_code").asText(), customer.getState());
        assertEquals(customerJson.get("default_address").get("country_code").asText(), customer.getCountry());
    }
    
    /**
     * Test case: There are 5 product variants in Shopify
     * 
     * Expected: These are retrieved from Shopify and mapped appropriately
     *  
     * @throws ExternalExtractorException
     */
    @Test
    public void testCommonProductVariantFields() throws ExternalExtractorException {

        extractor.fetchData(account, dataStore);
        List<ProductDTO> products = getDtoFromCaptor(account, productCaptor, ProductDTO.class);
        
        ProductDTO product = products.get(0);

        JsonNode productJson = firstMockJson(MockShopifyData.MOCK_SHOPIFY_PRODUCTS, "products").get("variants").get(0);
        
        // Original timestamp is 13:48 in -05:00 Timezone, so 19:00 is expected
        assertEquals(productJson.get("id").asText(), Long.toString(product.getRemoteId()));
        assertEquals(productJson.get("updated_at").asText(), dateToAssertionString(product.getLastModified()));
        assertEquals(productJson.get("price").asDouble(), product.getPrice(), 0.001);
        assertTrue(product.getDescription().contains(productJson.get("title").asText()));
    }
    
    /**
     * Test case: There are 2 orders in Shopify
     * 
     * Expected: These are retrieved from Shopify and mapped appropriately
     *  
     * @throws ExternalExtractorException
     */
    @Test
    public void testCommonInvoiceAndInvoiceLineFields() throws ExternalExtractorException {

        extractor.fetchData(account, dataStore);
        List<InvoiceDTO> invoices = getDtoFromCaptor(account, invoiceCaptor, InvoiceDTO.class);
        List<InvoiceLineDTO> invoiceLines = getDtoFromCaptor(account, invoiceLineCaptor, InvoiceLineDTO.class);
        
        InvoiceDTO invoice = invoices.get(0);
        JsonNode invoiceJson = firstMockJson(MockShopifyData.MOCK_SHOPIFY_ORDERS, "orders");

        assertEquals(invoiceJson.get("id").asText(), Long.toString(invoice.getRemoteId()));
        assertEquals(invoiceJson.get("total_price").asDouble(), invoice.getTotal(), 0.001);
        assertEquals(invoiceJson.get("created_at").asText(), dateToAssertionString(invoice.getInvoiceTimestamp()));
        assertEquals(invoiceJson.get("updated_at").asText(), dateToAssertionString(invoice.getLastModified()));
        
        // Calculate to sum of invoice line prices and taxes
        double sumOfItemsPrice = 0.0;
        double sumOfItemsTax = 0.0;
        for(InvoiceLineDTO line : invoiceLines){
            if(line.getInvoiceId() == invoice.getRemoteId()){
                sumOfItemsPrice += line.getPrice() * line.getQuantity();
                sumOfItemsTax += line.getTax() * line.getQuantity();
            }
        }
        
        assertEquals(invoiceJson.get("total_price").asDouble(), sumOfItemsPrice, 0.001);
        assertEquals(invoiceJson.get("total_tax").asDouble(), sumOfItemsTax, 0.001);
        
        
        InvoiceLineDTO line = invoiceLines.get(0);
        JsonNode invoiceLineJson = invoiceJson.get("line_items").get(0);
        
        assertEquals(invoiceLineJson.get("id").asText(), Long.toString(line.getRemoteId()));
        assertEquals(invoiceLineJson.get("variant_id").asText(), Long.toString(line.getProductId()));
    }
    
    /**
     * Test case: There are 2 orders in Shopify, one is paid on isn't
     * 
     * Expected: If it's paid it's completed, otherwise not
     *  
     * @throws ExternalExtractorException
     */
    @Test
    public void testInvoiceIsCompleted() throws ExternalExtractorException {
        // Create map with id => source_name based on mock json
        Map<Long,String> financialStatuses = new HashMap<Long,String>();
        for(JsonNode order : MockDataUtil.getResourceAsJson(MockShopifyData.MOCK_SHOPIFY_ORDERS).get("orders")){
            financialStatuses.put(order.get("id").asLong(), order.get("financial_status").asText());
        }
        
        // Act
        extractor.fetchData(account, dataStore);
        List<InvoiceDTO> invoices = getDtoFromCaptor(account, invoiceCaptor, InvoiceDTO.class);
       
        // Assert
        for(int i = 0; i < invoices.size(); i++){
        	assertEquals(financialStatuses.get(invoices.get(i).getRemoteId()).equals("paid"), invoices.get(i).getCompleted() == 1);
        }  
    }
    
    private String dateToAssertionString(Timestamp timestamp){
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        sdf.setTimeZone(TimeZone.getTimeZone("EST"));
        String retval = sdf.format(new Date(timestamp.getTime()));
        final int length = retval.length();
        return retval.substring(0, length-2) + ":" + retval.substring(length-2, length);
    }
}
