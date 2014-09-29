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
package com.sonrisa.swarm.mock.shopify;

import java.util.HashMap;
import java.util.Map;

import com.sonrisa.swarm.mock.MockPosDataDescriptor;

/**
 * Tools and enums for testing the Shopify POS system
 * @author sonrisa
 */
public class MockShopifyData {

    public static final String MOCK_SHOPIFY_PRODUCTS = "25_shopify_products.json";
    public static final String MOCK_SHOPIFY_ORDERS = "31_shopify_orders.json";
    public static final String MOCK_SHOPIFY_CUSTOMERS = "32_shopify_customers.json";

    /** MockShopifyData is a utility class, its constructor is hidden */
    private MockShopifyData(){
    }
    
    /**
     * Get number of items in the resource json files when testing
     * @return Map, with keys like "Category", and values like 4
     */
	public static Map<String, Integer> getCountOfMockJsonItems() {
		Map<String, Integer> count = new HashMap<String, Integer>();
		count.put("CategoryDTO", 0);
		count.put("ManufacturerDTO", 0);
		count.put("ProductDTO", 5);
		count.put("CustomerDTO", 1);
		count.put("InvoiceDTO", 2);
		count.put("InvoiceLineDTO", 6);
		return count;
	}

    /**
     * Returns the content of {@link MockShopifyData#getCountOfMockJsonItems()}
     * in a descriptor entity
     */
    public static MockPosDataDescriptor getShopifyMockDescriptor(){
        return new MockPosDataDescriptor(getCountOfMockJsonItems());
    }
}

