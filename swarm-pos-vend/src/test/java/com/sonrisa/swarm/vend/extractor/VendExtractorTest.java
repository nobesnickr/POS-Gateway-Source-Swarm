package com.sonrisa.swarm.vend.extractor;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.vend.MockVendData;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalDTOTransformer;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.test.extractor.BaseExtractorTest;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;
import com.sonrisa.swarm.vend.VendAccount;
import com.sonrisa.swarm.vend.api.util.VendAPIReader;

/**
 * Unit tests for the {@link VendExtractor} class.
 */
public class VendExtractorTest extends BaseExtractorTest<VendAccount> {

    /** Tested class */
    private VendExtractor extractor;
    
    /** Account used during mocking */
    private VendAccount account;
    
    /** Timezone for Vend's mock content */
    private static final TimeZone TIMEZONE = TimeZone.getTimeZone("Australia/Sydney");
    
    /**
     * Set up extractor and account
     */
    @Before
    public void setUp() throws ExternalExtractorException{
         account = new VendAccount(5L);
         account.setSite("5");
         account.setCompany("100");
        
         addJsonRestService(
                 new ExternalCommandMatcher<VendAccount>("register_sales").andUri("register_sales")
                 .andParam("page", 1),
                 MockVendData.MOCK_VEND_ORDER_BATCH);
         
		 addJsonRestService(
                new ExternalCommandMatcher<VendAccount>("customers").andUri("customers")
                .andParam("page", 1), 
                MockVendData.MOCK_VEND_CUSTOMER_BATCH);
         
         
		 addJsonRestService(
                new ExternalCommandMatcher<VendAccount>("products").andUri("products")
                .andParam("page", 1), 
                MockVendData.MOCK_VEND_PRODUCTS_BATCH);
		 
		 
		 addJsonRestService(
                new ExternalCommandMatcher<VendAccount>("outlets").andUri("outlets")
                .andParam("page", 1), 
                MockVendData.MOCK_VEND_OUTLETS_BATCH);
		 
		 addJsonRestService(
                new ExternalCommandMatcher<VendAccount>("registers").andUri("registers")
                .andParam("page", 1), 
                MockVendData.MOCK_VEND_REGISTERS_BATCH);
		 
         VendAPIReader apiReader = new VendAPIReader(api);
         this.extractor = new VendExtractor(apiReader);
         this.extractor.setDtoTransformer(new ExternalDTOTransformer());
    }
    
    
    /**
     * Test that the number of extracted DTO items
     * matched the number in the mock JSON files
     * @throws ExternalExtractorException
     */
    @Test
    public void testQuantityOfItemsExtracted() throws ExternalExtractorException {

        extractor.fetchData(account, dataStore);
        
        assertQuantityOfItemsExtracted(MockVendData.getVendBatchMockDescriptor());
    }
    

    /**
     * Test that timestamp URL parameter is properly added
     * @throws ExternalExtractorException
     */
    @Test
    public void testTimestampSent() throws ExternalExtractorException {

        extractor.fetchData(account, dataStore);
        
        /*
         * We want timestamps like this 
         * <code>/v1/companies/5678/orders.json?created_gte=2013-06-01</code>
         */
        final String expectedFilter = ISO8061DateTimeConverter.dateToString(filter.getTimestamp(), "yyyy-MM-dd hh:mm:ss");
        assertContainsParams("since", expectedFilter, "");
    }
    
    /**
     * Test case: There is 1 invoice in Vend
     * 
     * Expected: These are retrieved from Vend and mapped appropriately
     *  
     * @throws ExternalExtractorException
     */
    @Test
    public void testCommonInvoiceFields() throws ExternalExtractorException {

        extractor.fetchData(account, dataStore);

        List<InvoiceDTO> invoices = getDtoFromCaptor(account, invoiceCaptor, InvoiceDTO.class);
        InvoiceDTO invoice = invoices.get(0);
        JsonNode invoiceJson = MockDataUtil.getResourceAsJson(MockVendData.MOCK_VEND_ORDER_BATCH).get("register_sales").get(0);
        
        assertEquals(invoiceJson.get("sale_date").asText(), getVendDate(invoice.getLastModified(), TIMEZONE));
        assertEquals(invoiceJson.get("totals").get("total_payment").asDouble(), invoice.getTotal(), 0.001);
        assertEquals("Vend should produce unfinished DTOs", 0, invoice.getLinesProcessed().intValue());
        assertEquals(new Integer(1), invoice.getCompleted());
    }
}
