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

package com.sonrisa.swarm.rics.extractor;

import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sonrisa.swarm.mock.rics.MockRicsData;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalDTOTransformer;
import com.sonrisa.swarm.rics.RicsAccount;
import com.sonrisa.swarm.rics.RicsApiReader;
import com.sonrisa.swarm.rics.constants.RicsUri;
import com.sonrisa.swarm.test.extractor.BaseExtractorTest;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;

public class RicsExtractorTest extends BaseExtractorTest<RicsAccount> {
	private RicsExtractor extractor;
	private RicsAccount account;

	@Before
	public void setUp() throws Exception {
		extractor = new RicsExtractor(new RicsApiReader(api));
		extractor.setDtoTransformer(new ExternalDTOTransformer());

		addJsonRestService(new ExternalCommandMatcher<RicsAccount>(RicsUri.INVOICES.uri), MockRicsData.MOCK_INVOICES_ONE_PAGE_RESPONSE);
		addJsonRestService(new ExternalCommandMatcher<RicsAccount>(RicsUri.CUSTOMERS.uri), MockRicsData.MOCK_CUSTOMERS);
		RicsApiReader reader = new RicsApiReader(api);
		reader.setPageSize(50);

		account = new RicsAccount(1L);
	}

	/**
	 * Test case:
	 * Fetch mock sales and customer data.
	 * There is an invoice line in the sample data that does not contains product data
	 * 
	 * Expected:
	 * all invoice, invoice line, product and customer appears in stage
	 * the missing product should be handled silently (skipped)
	 * @throws ExternalExtractorException 
	 */
	@Test
	public void testQuantity() throws ExternalExtractorException {
	    extractor.fetchData(account, dataStore);
	    assertQuantityOfItemsExtracted(MockRicsData.getForQuantity());
	}
	
	/**
	 * Test case:
	 * extract some invoice lines and check the sum(total) of them
	 * 
	 *  Expected:
	 *  if we sum the inserted invoices total we get the correct number (we know the test data, so we exactly know the expected result )
	 * @throws ExternalExtractorException 
	 */
	@Test
	public void testTotal() throws ExternalExtractorException {
		extractor.fetchData(account, dataStore);
		List<InvoiceDTO> invoices = getDtoFromCaptor(account, invoiceCaptor, InvoiceDTO.class);
		BigDecimal sum = new BigDecimal("0.0");
		for (InvoiceDTO invoice : invoices) {
			sum = sum.add(new BigDecimal(invoice.getTotal()));
		}
		assertEquals(551d, sum.doubleValue(), 0.001);
	}
}
