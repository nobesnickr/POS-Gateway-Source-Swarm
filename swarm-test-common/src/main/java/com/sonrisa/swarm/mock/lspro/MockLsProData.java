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
package com.sonrisa.swarm.mock.lspro;

import java.util.HashMap;
import java.util.Map;

import com.sonrisa.swarm.mock.MockPosDataDescriptor;

/**
 * Utility class for generating mock data from Lightspeed Pro testing
 */
public class MockLsProData {
    
    /**
     * Sample json for invoices
     */
    public static final String MOCK_LSPRO_SAMPLE = "36_mock_lspro_sample.json";
    
    /**
     * Json received from sending request with invalid password
     */
    public static final String MOCK_LSPRO_UNAUTHORIZED = "37_mock_lspro_unauthorized_error.json";
    
    /**
     * Mock invoices response's first page containing <i>50</i> invoices
     */
    public static final String MOCK_LSPRO_INVOICES_PAGE_1 = "38_mock_lspro_invoices_1.json";
    
    /**
     * Mock invoices response's second page containing <i>13</i> invoices, as its the last page
     */
    public static final String MOCK_LSPRO_INVOICES_PAGE_2 = "39_mock_lspro_invoices_2.json";

    /**
     * Mock invoices response's second page containing <i>13</i> invoices, as its the last page
     */
    public static final String MOCK_LSPRO_LINE_ITEMS = "40_mock_lspro_line_items.json";
    
    /**
     * Mock invoices response's second page containing <i>13</i> invoices, as its the last page
     */
    public static final String MOCK_LSPRO_CUSTOMERS = "41_mock_lspro_customers.json";
    
    /**
     * Mock invoices response's second page containing <i>13</i> invoices, as its the last page
     */
    public static final String MOCK_LSPRO_PRODUCTS = "42_mock_lspro_products.json";
    
    /**
     * Mock invoice response with no invoices
     */
    public static final String MOCK_LSPRO_EMPTY_INVOICES = "44_mock_lspro_empty_invoices.json";
    
    /**
     * Mock invoice response with <code>"LocationName":"LS-Store2"</code>
     */
    public static final String MOCK_LSPRO_DIFFERENT_LOCATION = "45_mock_lspro_location_2.json";
    
    /**
     * Get number of items in the resource json files when testing
     * @return Map, with keys like "Category", and values like 4
     */
    public static MockPosDataDescriptor getLsProMockDescriptor (){
        Map<String,Integer> count = new HashMap<String,Integer>();
        count.put("CategoryDTO", 0);
        count.put("ManufacturerDTO", 0);
        count.put("ProductDTO", 2);
        count.put("CustomerDTO",7);
        count.put("InvoiceDTO",63);
        count.put("InvoiceLineDTO",2);
        return new MockPosDataDescriptor(count);
    }
    
    /**
     * Number of invoices on page 1 and page 2
     */
    public static final int NUMBER_OF_MOCK_INVOICES = 50 + 13;
}
