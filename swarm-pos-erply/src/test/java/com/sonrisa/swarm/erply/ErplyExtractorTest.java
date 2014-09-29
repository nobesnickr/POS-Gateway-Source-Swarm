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

package com.sonrisa.swarm.erply;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.swarm.mock.erply.MockErplyData;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalDTOTransformer;
import com.sonrisa.swarm.test.extractor.BaseExtractorTest;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;

/**
 * Class testing the {@link ErplyExtractor} class.
 */
public class ErplyExtractorTest extends BaseExtractorTest<ErplyAccount> {
		
    /**
     * Instance being tested
     */
    private ErplyExtractor extractor;
    
	/**
	 * Account where data is saved
	 */
	private ErplyAccount account;
 
	/**
	 * Initial setup sets up the account, passes the authentication information
	 * for the ErplyAPI singleton, and reads the json files for the mock responses.
	 * For all this we use the utility class
	 * @throws ExternalExtractorException 
	 */
	@Before
	public void setUp() throws ExternalExtractorException{
	    
        addJsonRestService(new ExternalCommandMatcher<ErplyAccount>("verifyUser"), MockErplyData.MOCK_ERPLY_VERIFY_USER);
        addJsonRestService(new ExternalCommandMatcher<ErplyAccount>("getSalesDocuments"), MockErplyData.MOCK_ERPLY_SALES_DOCUMENTS);
        addJsonRestService(new ExternalCommandMatcher<ErplyAccount>("getCustomers"), MockErplyData.MOCK_ERPLY_CUSTOMERS);
        addJsonRestService(new ExternalCommandMatcher<ErplyAccount>("getProducts").andParam("pageNo", "1"), MockErplyData.MOCK_ERPLY_PRODUCTS_PAGE_1);
        addJsonRestService(new ExternalCommandMatcher<ErplyAccount>("getProducts").andParam("pageNo", "2"), MockErplyData.MOCK_ERPLY_PRODUCTS_PAGE_2);
        addJsonRestService(new ExternalCommandMatcher<ErplyAccount>("getProductCategories"), MockErplyData.MOCK_ERPLY_CATEGORIES);
	    
        ErplyAPIReader apiReader = new ErplyAPIReader(api);
        
	    // Setup extractor, and attach it to the utility
	    extractor = new ErplyExtractor(apiReader);
	    extractor.setDtoTransformer(new ExternalDTOTransformer());
	    
	    this.account = new ErplyAccount();
	    account.setTimeZone("US/Pacific");
	}
	
	/**
	 * Test that all kinds of data is written to the data store, and then
	 * count the number of different items saved into the SimpleDataStore,
	 * and compare that to the number of different items in the mock json files 
	 */
	@Test
	public void testDataIsBeingSentToDatastore() throws ExternalExtractorException {
		extractor.fetchData(account, dataStore);
		assertQuantityOfItemsExtracted(MockErplyData.getCountInMockData());
	}
	
	/**
	 * Test common invoice fields at Erply extraction
	 */
	@Test
	public void testCommonInvoiceFields() throws ExternalExtractorException {

        // Act
        extractor.fetchData(account, dataStore);
        
        // Assert
        List<InvoiceDTO> invoices = getDtoFromCaptor(account, invoiceCaptor, InvoiceDTO.class);
        
        InvoiceDTO invoice = invoices.get(0);
        JsonNode invoiceJson = firstMockJson(MockErplyData.MOCK_ERPLY_SALES_DOCUMENTS, "records");

        assertEquals(invoiceJson.get("id").asText(), Long.toString(invoice.getRemoteId()));
        assertEquals(invoiceJson.get("netTotal").asDouble(), invoice.getTotal(), 0.001);
        
        SimpleDateFormat pstDateformat = new SimpleDateFormat("yyyy-MM-dd");
        pstDateformat.setTimeZone(TimeZone.getTimeZone(account.getTimeZone()));
        
        SimpleDateFormat pstTimeFormat = new SimpleDateFormat("HH:mm:ss");
        pstTimeFormat.setTimeZone(TimeZone.getTimeZone(account.getTimeZone()));
        
        assertEquals("Created date's date segment should be modified",
                invoiceJson.get("date").asText(),
                pstDateformat.format(invoice.getInvoiceTimestamp()));
        
        assertEquals("Created date's time segment should be modified",
                invoiceJson.get("time").asText(),
                pstTimeFormat.format(invoice.getInvoiceTimestamp()));
        
        assertEquals("Updated date should have remote value, but in milliseconds",
                invoiceJson.get("lastModified").asText(), 
                Long.toString(invoice.getLastModified().getTime() / 1000));
	}
}
