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
package com.sonrisa.swarm.kounta.extractor.processor;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.swarm.kounta.KountaAccount;
import com.sonrisa.swarm.kounta.KountaUriBuilder;
import com.sonrisa.swarm.kounta.api.util.KountaItemAPIReader;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.kounta.MockKountaData;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalDTOTransformer;
import com.sonrisa.swarm.test.extractor.BaseWarehouseTest;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;
/**
 * Unit tests for the {@link KountaInvoiceProcessor}
 * @author Barnabas
 */
public class KountaInvoiceProcessorTest extends BaseWarehouseTest {

    /**
     * Mocked API, data source
     */
    @Mock
    private ExternalAPI<KountaAccount> api;
    
    /**
     * Dummy account
     */
    private KountaAccount account;
    
    /**
     * Target being tested
     */
    private KountaInvoiceProcessor target;
    
    /**
     * Invoice id of the mock invoice in the JSON
     */
    private static final Long INVOICE_ID = 12267739L;
    
    /**
     * Mock work item of processing
     */
    private InvoiceEntity mockWorkItem;
    
    /**
     * Set up JSON service and target
     */
    @Before
    public void setUp() throws Exception {
        
        account = new KountaAccount(7L);
        account.setSite("8");
        account.setCompany("15");
        
        // Setup response for detailed page
        when(api.sendRequest(
                argThat(new ExternalCommandMatcher<KountaAccount>(KountaUriBuilder.getCompanyUri(account, "orders/" + INVOICE_ID + ".json")).andAccount(account))))
            .thenReturn(MockDataUtil.getResourceAsExternalResponse(MockKountaData.MOCK_KOUNTA_DETAILED_ORDER));
        
        // Mock invoice input
        mockWorkItem = mock(InvoiceEntity.class);
        when(mockWorkItem.getLsInvoiceId()).thenReturn(INVOICE_ID);
        when(mockWorkItem.getTotal()).thenReturn(new BigDecimal("500.00"));

        // Setup datasource using mock API
        ExternalAPIReader<KountaAccount> apiReader = new KountaItemAPIReader(api);
        target = new KountaInvoiceProcessor(apiReader);

        // DTO transformer used
        target.setDtoTransformer(new ExternalDTOTransformer());
    }
    
    /**
     * Test case: 
     *  There is work-item to be processed by the processor
     *  
     * Expected:
     *  It access the detailed page by id and extracts customers, products, etc.
     * @throws ExternalExtractorException 
     */
    @Test
    public void testQuantityOfItemsExtracted() throws ExternalExtractorException {
        
        // Act
        target.processEntity(account, mockWorkItem, dataStore);
        
        // Assert
        assertQuantityOfItemsExtracted(MockKountaData.getKountaMockDescriptor());
    }
    
    /**
     * Test case: 
     *  Processing a high-level invoice
     * 
     * Expected:
     *  A detailed invoice was inserted with detailed information including referenced customer
     */
    @Test
    public void testCommonInvoiceFields() throws ExternalExtractorException {

        // Act
        target.processEntity(account, mockWorkItem, dataStore);

        List<InvoiceDTO> invoices = getDtoFromCaptor(account, invoiceCaptor, InvoiceDTO.class);
        InvoiceDTO invoice = invoices.get(0);
        JsonNode invoiceJson = MockDataUtil.getResourceAsJson(MockKountaData.MOCK_KOUNTA_ORDER_BATCH).get(0);
        
        assertEquals(invoiceJson.get("id").asText(), Long.toString(invoice.getRemoteId()));
        assertEquals("Total should be the work item's", mockWorkItem.getTotal().doubleValue(), invoice.getTotal(), 0.001);
        assertEquals(invoiceJson.get("sale_number").asText(), invoice.getInvoiceNumber());
        assertNotNull("Details have customer", invoice.getCustomerId());
        assertNull("Kounta doesn't have completed logic", invoice.getCompleted());
        assertNotNull("Invoice should have timestamp", invoice.getLastModified());
        assertNotNull("Invoice should have timestamp", invoice.getInvoiceTimestamp());
        assertEquals("Kounta processor should produce finished DTOs", 1, invoice.getLinesProcessed().intValue());
    }

    /**
     * Test case: 
     *  Processing a high-level invoice
     * 
     * Expected:
     *  A detailed customer was inserted with detailed information including referenced customer
     */
    @Test
    public void testCommonCustomerFields() throws ExternalExtractorException {

        // Act
        target.processEntity(account, mockWorkItem, dataStore);

        List<CustomerDTO> customers = getDtoFromCaptor(account, customerCaptor, CustomerDTO.class);
        CustomerDTO customer = customers.get(0);
        JsonNode customerJson = MockDataUtil.getResourceAsJson(MockKountaData.MOCK_KOUNTA_DETAILED_ORDER).get("customer");
        
        assertEquals(customerJson.get("id").asText(), Long.toString(customer.getRemoteId()));
        assertEquals(customerJson.get("first_name").asText(), customer.getFirstName());
        assertEquals(customerJson.get("last_name").asText(), customer.getLastName());
        assertEquals("Lucy Lavender", customer.getName());
        assertEquals(customerJson.get("email").asText(), customer.getEmail());
        assertNotNull("Custoemr should inherit timestamp", customer.getLastModified());
    }
    

    /**
     * Test case: 
     *  Processing a high-level invoice
     * 
     * Expected:
     *  A detailed products were inserted with detailed information including inherited price
     */
    @Test
    public void testCommonProductFields() throws ExternalExtractorException {

        // Act
        target.processEntity(account, mockWorkItem, dataStore);

        List<ProductDTO> products = getDtoFromCaptor(account, productCaptor, ProductDTO.class);
        ProductDTO product = products.get(0);
        
        JsonNode invoiceLineJson = MockDataUtil.getResourceAsJson(MockKountaData.MOCK_KOUNTA_DETAILED_ORDER).get("lines").get(0);
        JsonNode productJson = invoiceLineJson.get("product");
        
        assertEquals(productJson.get("id").asText(), Long.toString(product.getRemoteId()));
        assertEquals(productJson.get("name").asText(), product.getDescription());
        assertEquals("Product should inherit line price", invoiceLineJson.get("unit_price").asDouble(), product.getPrice(), 0.001);
        assertNotNull("Product should inherit timestamp", product.getLastModified());
    }
    

    /**
     * Test case: 
     *  Processing a high-level invoice
     * 
     * Expected:
     *  A detailed invoice lines were inserted with detailed information including referenced invoice and product
     */
    @Test
    public void testCommonInvoiceLineFields() throws ExternalExtractorException {

        // Act
        target.processEntity(account, mockWorkItem, dataStore);

        List<InvoiceLineDTO> invoiceLines = getDtoFromCaptor(account, invoiceLineCaptor, InvoiceLineDTO.class);
        InvoiceLineDTO invoiceLine = invoiceLines.get(0);
        
        JsonNode invoiceJson = MockDataUtil.getResourceAsJson(MockKountaData.MOCK_KOUNTA_DETAILED_ORDER);
        JsonNode invoiceLineJson = invoiceJson.get("lines").get(0);
        JsonNode productJson = invoiceLineJson.get("product");
        
        assertEquals("Invoice ID should be inherited", invoiceJson.get("id").asText(), Long.toString(invoiceLine.getInvoiceId()));
        assertEquals(productJson.get("id").asText(), Long.toString(invoiceLine.getProductId()));
        assertEquals(invoiceLineJson.get("unit_price").asDouble(), invoiceLine.getPrice(), 0.001);
        assertEquals(invoiceLineJson.get("unit_tax").asDouble(), invoiceLine.getTax(), 0.001);
        assertEquals(invoiceLineJson.get("quantity").asInt(), invoiceLine.getQuantity());
        assertNotNull("Line should inherit timestamp", invoiceLine.getLastModified());
    }
}
