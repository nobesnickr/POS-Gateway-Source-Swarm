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
package com.sonrisa.swarm.mock.kounta;

import java.util.HashMap;
import java.util.Map;

import com.sonrisa.swarm.mock.MockPosDataDescriptor;

/**
 * Utility class to access mock content for Kounta
 */
public class MockKountaData {

    /**
     * Mock response for refreshing access tokens
     */
    public static final String MOCK_KOUNTA_ACCESS_TOKEN = "44_mock_kounta_access_token.json";
    
    /**
     * Mock response when token expires
     */
    public static final String MOCK_KOUNTA_UNAUTHORIZED = "45_mock_kounta_unauthorized_token.json";
    
    /**
     * Mock response when Kounta resource is not found
     */
    public static final String MOCK_KOUNTA_NOT_FOUND = "46_mock_kounta_not_found_error.json";
    
    /**
     * Mock response when Kounta is queried for orders
     */
    public static final String MOCK_KOUNTA_ORDER_BATCH = "47_mock_kounta_batch_orders.json";
    
    /**
     * Mock response when Kounta is queried for orders by order id
     */
    public static final String MOCK_KOUNTA_DETAILED_ORDER = "48_mock_kounta_high_level_order.json";

    /**
     * Mock response exchanging temporary token for permanent
     */
    public static final String MOCK_KOUNTA_REFRESH_TOKEN = "49_mock_kounta_refresh_token.json";

    /**
     * Mock response for fetching companies (like Merchant OS accounts)
     */
    public static final String MOCK_KOUNTA_COMPANY = "50_mock_kounta_company.json";
    
    /**
     * Mock response for fetching sites (like LsPro locations)
     */
    public static final String MOCK_KOUNTA_SITES = "51_mock_kounta_sites.json";

    /**
     * Get number of items in the batch json files
     */
    public static MockPosDataDescriptor getKountaBatchMockDescriptor (){
        Map<String,Integer> count = new HashMap<String,Integer>();
        count.put("InvoiceDTO",3);
        return new MockPosDataDescriptor(count);
    }
    
    /**
     * Get number of items in the detailed json files when testing
     */
    public static MockPosDataDescriptor getKountaMockDescriptor (){
        Map<String,Integer> count = new HashMap<String,Integer>();
        count.put("InvoiceDTO",1);
        count.put("CustomerDTO",1);
        count.put("InvoiceLineDTO",2);
        count.put("ProductDTO",2);
        return new MockPosDataDescriptor(count);
    }
    
    /**
     * Get number of items expected when processing the whole batch
     */
    public static MockPosDataDescriptor getKountaMockProcessedBatchDescriptor (){
        Map<String,Integer> count = new HashMap<String,Integer>();
        count.put("InvoiceDTO",3);
        count.put("CustomerDTO",3);
        count.put("InvoiceLineDTO",6);
        count.put("ProductDTO",6);
        return new MockPosDataDescriptor(count);
    }
}
