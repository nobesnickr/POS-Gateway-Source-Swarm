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

package com.sonrisa.swarm.mock.rics;

import java.util.HashMap;
import java.util.Map;

import com.sonrisa.swarm.mock.MockPosDataDescriptor;

public class MockRicsData {

	/**
	 * Sample json response: skip:0 take:50
	 */
	public static final String MOCK_SALES_FIRST_PAGE = "43_mock_rics_sales_first_page.json";

	/**
	 * Sample json response: skip:50 take:50
	 */
	public static final String MOCK_SALES_SECOND_PAGE = "44_mock_rics_sales_second_page.json";

	/**
	 * Successful token response
	 */
	public static final String MOCK_TOKEN = "47_mock_rics_authenticated.json";
	/**
	 * Sample json response is response is something went wrong
	 */
	public static final String MOCK_UNSUCCESS = "45_mock_rics_unsuccess.json";

	/**
	 * Sample response customeers
	 */
	public static final String MOCK_CUSTOMERS = "46_mock_rics_customers.json";

	/**
	 * Sample one-page response for invoices 
	 */
	public static final String MOCK_INVOICES_ONE_PAGE_RESPONSE = "48_mock_rics_one_page_invoice.json";
	
	/**
	 * Empty (there no data) response
	 */
	public static final String MOCK_EMPTY_RESPONSE = "49_mock_empty_page_received.json";

	/**
	 * @param staging true if the descriptor should not contain duplicates (staging tables can contain duplicates while legacy table can not)
	 * @return an instance of {@link MockPosDataDescriptor} that contains the number of items should be extracted during an extraction
	 */
	public static MockPosDataDescriptor getExtractionDescriptor(boolean staging) {
		Map<String, Integer> count = new HashMap<String, Integer>();
		if (staging) {
			count.put("CategoryDTO", 0);
			count.put("ManufacturerDTO", 0);
			count.put("ProductDTO", 9);
			count.put("CustomerDTO", 35);
			count.put("InvoiceDTO", 6);
			count.put("InvoiceLineDTO", 10);
		} else {
			count.put("CategoryDTO", 0);
			count.put("ManufacturerDTO", 0);
			count.put("ProductDTO", 3);
			count.put("CustomerDTO", 35);
			count.put("InvoiceDTO", 6);
			count.put("InvoiceLineDTO", 10);
		}
		return new MockPosDataDescriptor(count);
	}

	// we should use another descriptor for RicsExtractor (why????)
	public static MockPosDataDescriptor getForQuantity() {
		Map<String, Integer> count = new HashMap<String, Integer>();
		count.put("RicsCategoryDTO", 0);
		count.put("RicsManufacturerDTO", 0);
		count.put("RicsProductDTO", 3);
		count.put("RicsCustomerDTO", 35);
		count.put("RicsInvoiceDTO", 6);
		count.put("RicsInvoiceLineDTO", 10);
		return new MockPosDataDescriptor(count);
	}
}
