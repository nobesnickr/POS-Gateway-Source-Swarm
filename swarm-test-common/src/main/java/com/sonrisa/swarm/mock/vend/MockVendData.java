package com.sonrisa.swarm.mock.vend;

import java.util.HashMap;
import java.util.Map;

import com.sonrisa.swarm.mock.MockPosDataDescriptor;

/**
 * Utility class to access mock content for Vend
 */
public class MockVendData {

    /**
     * Mock response for refreshing access tokens
     */
    public static final String MOCK_VEND_ACCESS_TOKEN = "44_mock_vend_access_token.json";
    
    /**
     * Mock response when token expires
     */
    public static final String MOCK_VEND_UNAUTHORIZED = "45_mock_vend_unauthorized_token.json";
    
    /**
     * Mock response when Vend resource is not found
     */
    public static final String MOCK_VEND_NOT_FOUND = "46_mock_vend_not_found_error.json";
    
    /**
     * Mock response when Vend is queried
     */
    public static final String MOCK_VEND_ORDER_BATCH 	 = 	"47_mock_vend_batch_orders.json";
    
	public static final String MOCK_VEND_CUSTOMER_BATCH  = 	"48_mock_vend_batch_customers.json";

	public static final String MOCK_VEND_PRODUCTS_BATCH  = 	"49_mock_vend_batch_products.json";

	public static final String MOCK_VEND_OUTLETS_BATCH 	 = 	"50_mock_vend_batch_outlets.json";

	public static final String MOCK_VEND_REGISTERS_BATCH = 	"51_mock_vend_batch_registers.json";

    /**
     * Get number of items in the batch json files
     */
    public static MockPosDataDescriptor getVendBatchMockDescriptor (){
        Map<String,Integer> count = new HashMap<String,Integer>();
        count.put("CustomerDTO",50);
        count.put("InvoiceDTO",50);
        count.put("InvoiceLineDTO",81);
        count.put("ProductDTO",50);
        count.put("OutletDTO",7);
        count.put("RegisterDTO",12);
        return new MockPosDataDescriptor(count);
    }
    
    /**
     * Get number of items in the detailed json files when testing
     */
    public static MockPosDataDescriptor getVendMockDescriptor (){
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
    public static MockPosDataDescriptor getVendMockProcessedBatchDescriptor (){
        Map<String,Integer> count = new HashMap<String,Integer>();
        count.put("InvoiceDTO",3);
        count.put("CustomerDTO",3);
        count.put("InvoiceLineDTO",6);
        count.put("ProductDTO",6);
        return new MockPosDataDescriptor(count);
    }
}
