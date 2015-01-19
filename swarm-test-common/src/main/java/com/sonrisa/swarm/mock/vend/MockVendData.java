package com.sonrisa.swarm.mock.vend;

import java.util.HashMap;
import java.util.Map;

import com.sonrisa.swarm.mock.MockPosDataDescriptor;

/**
 * Utility class to access mock content for Vend
 */
public class MockVendData {

    private static final String OUTLET_DTO = "OutletDTO";

	private static final String PRODUCT_DTO = "ProductDTO";

	private static final String INVOICE_LINE_DTO = "InvoiceLineDTO";

	private static final String CUSTOMER_DTO = "CustomerDTO";

	private static final String INVOICE_DTO = "InvoiceDTO";

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
        count.put(CUSTOMER_DTO,48);
        count.put(INVOICE_DTO,48);
        count.put(INVOICE_LINE_DTO,78);
        count.put(PRODUCT_DTO,48);
        count.put(OUTLET_DTO,7);
        count.put("RegisterDTO",12);
        return new MockPosDataDescriptor(count);
    }
    
    /**
     * Get number of items in the resource json files when testing
     * @return Map, with keys like "Category", and values like 4
     */
	public static Map<String, Integer> getCountOfMockJsonItems() {
		Map<String, Integer> count = new HashMap<String, Integer>();
        count.put(CUSTOMER_DTO,48);
        count.put(INVOICE_DTO,48);
        count.put(INVOICE_LINE_DTO,78);
        count.put(PRODUCT_DTO,48);
        count.put(OUTLET_DTO,7);
        count.put("RegisterDTO",12);
		return count;
	}
    
    /**
     * Get number of items in the detailed json files when testing
     */
    public static MockPosDataDescriptor getVendMockDescriptor (){
        Map<String,Integer> count = new HashMap<String,Integer>();
        count.put(INVOICE_DTO,1);
        count.put(CUSTOMER_DTO,1);
        count.put(INVOICE_LINE_DTO,2);
        count.put(PRODUCT_DTO,2);
        return new MockPosDataDescriptor(count);
    }
    
    /**
     * Get number of items expected when processing the whole batch
     */
    public static MockPosDataDescriptor getVendMockProcessedBatchDescriptor (){
        Map<String,Integer> count = new HashMap<String,Integer>();
        count.put(INVOICE_DTO,3);
        count.put(CUSTOMER_DTO,3);
        count.put(INVOICE_LINE_DTO,6);
        count.put(PRODUCT_DTO,6);
        return new MockPosDataDescriptor(count);
    }
}
