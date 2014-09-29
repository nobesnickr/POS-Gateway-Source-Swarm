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
package com.sonrisa.swarm.mock.revel;

import java.util.HashMap;
import java.util.Map;

import com.sonrisa.swarm.mock.MockPosDataDescriptor;
import com.sonrisa.swarm.mock.shopify.MockShopifyData;

/**
 * Data used for mocking the Revel system
 */
public class MockRevelData {
    
    public static final String MOCK_PRODUCT_CATEGORIES = "26_revel_mock_product_categories.json";
    public static final String MOCK_CUSTOMERS = "27_revel_mock_customers.json";
    public static final String MOCK_ORDERS = "28_revel_mock_orders.json";
    public static final String MOCK_ORDER_ITEMS = "29_revel_mock_orderitem.json";
    public static final String MOCK_PRODUCTS = "30_revel_mock_products.json";
    public static final String MOCK_ESTABLISHMENTS = "34_revel_mock_establishments.json";

    /** MockRevelData is a utility class, its constructor is hidden */
    private MockRevelData(){
    }
    
    /**
     * Retrieve the number of instances each type of data is present in the mock json files
     */
    public static final Map<String,Integer> getCountInMockData(){
        return new HashMap<String,Integer>(){{
            put("CustomerDTO",2);
            put("InvoiceDTO",20);
            put("ProductDTO",20);
            put("CategoryDTO",20);
            put("InvoiceLineDTO",20);
        }};
    }
    
    /**
     * Returns the content of {@link MockShopifyData#getCountOfMockJsonItems()}
     * in a descriptor entity
     */
    public static MockPosDataDescriptor getRevelMockDescriptor(){
        return new MockPosDataDescriptor(getCountInMockData());
    }
}
