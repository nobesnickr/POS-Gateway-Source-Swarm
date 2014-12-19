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


	private static MockPosDataDescriptor getRealExtractionDescriptor(boolean staging) {
		Map<String, Integer> count = new HashMap<String, Integer>();
		count.put("CategoryDTO", 0);
		count.put("ManufacturerDTO", 0);
		count.put("ProductDTO", staging ? 29 : 26);
		count.put("CustomerDTO", 35);
		count.put("InvoiceDTO", 22);
		count.put("InvoiceLineDTO", 29);
		return new MockPosDataDescriptor(count);
	}
	
	public static MockPosDataDescriptor getExtractionDescriptor() {
		return getRealExtractionDescriptor(true);
	}
	
	public static MockPosDataDescriptor getLegacyExtractionDescriptor() {
		return getRealExtractionDescriptor(false);
	}
}
