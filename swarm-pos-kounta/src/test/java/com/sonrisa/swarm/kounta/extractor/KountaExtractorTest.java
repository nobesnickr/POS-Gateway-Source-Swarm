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
package com.sonrisa.swarm.kounta.extractor;

import static org.junit.Assert.*;

import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.swarm.kounta.KountaAccount;
import com.sonrisa.swarm.kounta.KountaUriBuilder;
import com.sonrisa.swarm.kounta.api.util.KountaAPIReader;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.kounta.MockKountaData;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalDTOTransformer;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.test.extractor.BaseExtractorTest;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;

/**
 * Unit tests for the {@link KountaExtractor} class.
 * @author Barnabas
 */
public class KountaExtractorTest extends BaseExtractorTest<KountaAccount> {

    /** Tested class */
    private KountaExtractor extractor;
    
    /** Account used during mocking */
    private KountaAccount account;
    
    /** Timezone for Kounta's mock content */
    private static final TimeZone TIMEZONE = TimeZone.getTimeZone("Australia/Sydney");
    
    /**
     * Set up extractor and account
     */
    @Before
    public void setUp() throws ExternalExtractorException{

         account = new KountaAccount(5L);
         account.setSite("5");
         account.setCompany("100");
        
         addJsonRestService(
                 new ExternalCommandMatcher<KountaAccount>(KountaUriBuilder.getSiteUri(account, "orders/complete.json")), 
                 MockKountaData.MOCK_KOUNTA_ORDER_BATCH);
         
         KountaAPIReader apiReader = new KountaAPIReader(api);
         this.extractor = new KountaExtractor(apiReader);
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
        
        assertQuantityOfItemsExtracted(MockKountaData.getKountaBatchMockDescriptor());
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
        assertContainsParams("created_gt", expectedFilter, KountaUriBuilder.getSiteUri(account, "orders/completed.json"));
    }
    
    /**
     * Test case: There is 1 invoice in Kounta
     * 
     * Expected: These are retrieved from Kounta and mapped appropriately
     *  
     * @throws ExternalExtractorException
     */
    @Test
    public void testCommonInvoiceFields() throws ExternalExtractorException {

        extractor.fetchData(account, dataStore);

        List<InvoiceDTO> invoices = getDtoFromCaptor(account, invoiceCaptor, InvoiceDTO.class);
        InvoiceDTO invoice = invoices.get(0);
        JsonNode invoiceJson = MockDataUtil.getResourceAsJson(MockKountaData.MOCK_KOUNTA_ORDER_BATCH).get(0);
        
        assertEquals(invoiceJson.get("id").asText(), Long.toString(invoice.getRemoteId()));
        assertEquals(invoiceJson.get("updated_at").asText(), getKountaDate(invoice.getLastModified(), TIMEZONE));
        assertEquals(invoiceJson.get("created_at").asText(), getKountaDate(invoice.getLastModified(), TIMEZONE));
        assertEquals(invoiceJson.get("total").asDouble(), invoice.getTotal(), 0.001);
        assertEquals(invoiceJson.get("sale_number").asText(), invoice.getInvoiceNumber());
        assertEquals("Kounta should produce unfinished DTOs", 0, invoice.getLinesProcessed().intValue());
        assertNull("Kounta doesn't have completed logic", invoice.getCompleted());
    }
}
