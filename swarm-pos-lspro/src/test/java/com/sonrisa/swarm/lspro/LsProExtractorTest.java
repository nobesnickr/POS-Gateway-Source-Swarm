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

package com.sonrisa.swarm.lspro;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.swarm.lspro.extractor.LsProExtractor;
import com.sonrisa.swarm.mock.lspro.MockLsProData;
import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalDTOTransformer;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.test.extractor.BaseExtractorTest;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;

/**
 * Class for testing the {@link LsProExtractor}
 */
public class LsProExtractorTest extends BaseExtractorTest<LsProAccount> {

   /** Tested class */
   private LsProExtractor extractor;
   
   /** Account used during mocking */
   private LsProAccount account;
   
   @Before
   public void setUp() throws ExternalExtractorException{
       
        addJsonRestService(new ExternalCommandMatcher<LsProAccount>("Products"), MockLsProData.MOCK_LSPRO_PRODUCTS);
        addJsonRestService(new ExternalCommandMatcher<LsProAccount>("Customers"), MockLsProData.MOCK_LSPRO_CUSTOMERS);
        addJsonRestService(new ExternalCommandMatcher<LsProAccount>("LineItems"), MockLsProData.MOCK_LSPRO_LINE_ITEMS);

        addJsonRestService(
                new ExternalCommandMatcher<LsProAccount>("Invoices")
                .andParam("$expand", "Customer")
                .andParam("$skip", 0), 
                MockLsProData.MOCK_LSPRO_INVOICES_PAGE_1);

        addJsonRestService(
                new ExternalCommandMatcher<LsProAccount>("Invoices")
                .andParam("$expand", "Customer")
                .andParam("$skip", 50), 
                MockLsProData.MOCK_LSPRO_INVOICES_PAGE_2);

        // Setup test context
        LsProAPIReader apiReader = new LsProAPIReader(api);
        apiReader.setFetchSize(50); // Force fetch size 50, as mocks contain
                                    // maximum 50 items

        this.extractor = new LsProExtractor(apiReader);
        this.extractor.setDtoTransformer(new ExternalDTOTransformer());

        // Setup account
        account = new LsProAccount();
   }
   
   
   /**
    * Test that the number of extracted DTO items
    * matched the number in the mock JSON files
    * @throws ExternalExtractorException
    */
   @Test
   public void testQuantityOfItemsExtracted() throws ExternalExtractorException {

       extractor.fetchData(account, dataStore);
       
       assertQuantityOfItemsExtracted(MockLsProData.getLsProMockDescriptor());
   }
   
   /**
    * Test that timestamp URL parameter is properly added
    * @throws ExternalExtractorException
    */
   @Test
   public void testTimestampSent() throws ExternalExtractorException {

       extractor.fetchData(account, dataStore);
       
       /*
        * We want timestamps like this (the $filter field)
        * "Products?$filter=(DateModified)gt(DateTime'2014-01-01')&$orderby=(DateModified)asc")));
        */
       final String expectedModifiedFilter = "(DateModified)gt(DateTime'" + ISO8061DateTimeConverter.dateToOdataString(filter.getTimestamp()) + "')";
       final String expectedCreatedFilter = "(DateCreatedUtc)gt(DateTime'" + ISO8061DateTimeConverter.dateToOdataString(filter.getTimestamp()) + "')";
       assertContainsParams("$filter", expectedModifiedFilter, "Customers", "Products");
       assertContainsParams("$filter", expectedCreatedFilter, "Invoices", "LineItems");
   }
   

    /**
     * Test that if account has location information its added to the query
     * @throws ExternalExtractorException
     */
    @Test
    public void testLocationNameFiltering() throws ExternalExtractorException {

        LsProAccount account = new LsProAccount();
        account.setStoreFilter("Swarm Mobile");

        extractor.fetchData(account, dataStore);

        final String expectedModifiedFilter = "(DateModified)gt(DateTime'"
                + ISO8061DateTimeConverter.dateToOdataString(filter.getTimestamp()) + "')"
                + String.format("and(LocationName)eq('%s')", account.getStoreFilter());

        final String expectedCreatedFilter = "(DateCreatedUtc)gt(DateTime'"
                + ISO8061DateTimeConverter.dateToOdataString(filter.getTimestamp()) + "')"
                + String.format("and(LocationName)eq('%s')", account.getStoreFilter());

        assertContainsParams("$filter", expectedModifiedFilter, "Customers", "Products");
        assertContainsParams("$filter", expectedCreatedFilter, "Invoices", "LineItems");
    }
   
   /**
    * Test that timestamp URL parameter is properly added
    * @throws ExternalExtractorException
    */
   @Test
   public void testOrderKeySent() throws ExternalExtractorException {

       extractor.fetchData(account, dataStore);

       /*
        * We want order key like this (the $orderby field)
        * "Products?$filter=(DateModified)gt(DateTime'2014-01-01')&$orderby=(DateModified)asc")));
        */
       assertContainsParams("$orderby", "(DateModified)asc", "Customers", "Products");
       assertContainsParams("$orderby", "(DateCreatedUtc)asc", "Invoices", "LineItems");
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
       
       // Assert
       List<CustomerDTO> customers = getDtoFromCaptor(account, customerCaptor, CustomerDTO.class);
       CustomerDTO customer = customers.get(0);
       JsonNode customerJson = firstMockJson(MockLsProData.MOCK_LSPRO_CUSTOMERS, "value");

       assertEquals(customerJson.get("Id").asText(), Long.toString(customer.getRemoteId()));
       assertEquals(customerJson.get("DateModified").asText(), getLsProDate(customer.getLastModified()));
       assertEquals(customerJson.get("Name").asText(), customer.getName());
       assertEquals(customerJson.get("BillingStreet").asText(), customer.getAddress());
       assertEquals(customerJson.get("BillingStreet").asText(), customer.getAddress());
   }
   
   /**
    * Test case: There is 5 product variants in Shopify
    * 
    * Expected: These are retrieved from Shopify and mapped appropriately
    *  
    * @throws ExternalExtractorException
    */
   @Test
   public void testCommonProductVariantFields() throws ExternalExtractorException {

       extractor.fetchData(account, dataStore);
       
       // Assert
       List<ProductDTO> products = getDtoFromCaptor(account, productCaptor, ProductDTO.class);
       ProductDTO product = products.get(0);
       JsonNode productJson = firstMockJson(MockLsProData.MOCK_LSPRO_PRODUCTS, "value");
       
       assertEquals(productJson.get("Id").asText(), Long.toString(product.getRemoteId()));
       assertEquals(productJson.get("DateModified").asText(), getLsProDate(product.getLastModified()));
       assertEquals(productJson.get("SellPrice").asDouble(), product.getPrice(), 0.001);
       assertEquals(productJson.get("Description").asText(), product.getDescription());
   }
   
   /**
    * Test case: There is 5 product variants in Shopify
    * 
    * Expected: These are retrieved from Shopify and mapped appropriately
    *  
    * @throws ExternalExtractorException
    */
   @Test
   public void testCommonInvoiceFields() throws ExternalExtractorException {

       extractor.fetchData(account, dataStore);

       List<InvoiceDTO> invoices = getDtoFromCaptor(account, invoiceCaptor, InvoiceDTO.class);
       InvoiceDTO invoice = invoices.get(0);
       JsonNode invoiceJson = firstMockJson(MockLsProData.MOCK_LSPRO_INVOICES_PAGE_1, "value");

       assertEquals(invoiceJson.get("Id").asText(), Long.toString(invoice.getRemoteId()));
       assertEquals(invoiceJson.get("DateModifiedUtc").asText(), getLsProDate(invoice.getLastModified()));
       assertEquals(invoiceJson.get("DateCreatedUtc").asText(), getLsProDate(invoice.getInvoiceTimestamp()));
       assertEquals(invoiceJson.get("Total").asDouble(), invoice.getTotal(), 0.001);
       
   }
   
   @Test
   public void testCommonLineItemFields() throws ExternalExtractorException {

       extractor.fetchData(account, dataStore);

       List<InvoiceLineDTO> invoiceLines = getDtoFromCaptor(account, invoiceLineCaptor, InvoiceLineDTO.class);
       final InvoiceLineDTO line = invoiceLines.get(0);
       JsonNode invoiceLineJson = firstMockJson(MockLsProData.MOCK_LSPRO_LINE_ITEMS, "value");
       
       assertEquals(invoiceLineJson.get("Id").asText(), Long.toString(line.getRemoteId()));
       assertEquals(invoiceLineJson.get("DateCreatedUtc").asText(), dateToAssertionString(line.getLastModified(), "yyyy-MM-dd'T'HH:mm:ss"));
       assertEquals(invoiceLineJson.get("InvoiceId").asText(), Long.toString(line.getInvoiceId()));
       assertEquals(invoiceLineJson.get("ProductId").asText(), Long.toString(line.getProductId()));
   }
}
