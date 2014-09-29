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
package com.sonrisa.swarm.mock.erply;

import java.util.HashMap;
import java.util.Map;

import com.sonrisa.swarm.mock.MockPosDataDescriptor;

/**
 * Class containing standard JSON files for testing Erply
 * @author sonrisa
 *
 */
public class MockErplyData {

    public static final String USERNAME = "mock";
    public static final String PASSWORD = "mySecretPassword";
    public static final String CLIENT_CODE = "98765";
    public static final String SESSION_KEY = "qwertyuiopasdfghjk";
    
    /** The WireMock server will be opened in this port*/
    public static final int PORT = 8089;
    
    public static final String MOCK_ERPLY_VERIFY_USER = "09_mock_erply_verifyUser.json";
    public static final String MOCK_ERPLY_SALES_DOCUMENTS = "10_mock_erply_getSalesDocuments.json";
    public static final String MOCK_ERPLY_SALES_DOCUMENTS_ERROR = "15_mock_erply_getSalesDocument_error.json";    
    public static final String MOCK_ERPLY_CUSTOMERS = "11_mock_erply_getCustomers.json";
    public static final String MOCK_ERPLY_PRODUCTS_PAGE_1 = "12_mock_erply_getProducts_page1.json";
    public static final String MOCK_ERPLY_PRODUCTS_PAGE_2 = "13_mock_erply_getProducts_page2.json";
    public static final String MOCK_ERPLY_CATEGORIES = "14_mock_erply_getProductCategories.json";
    public static final String MOCK_ERPLY_INVALID_RESOURCE = "55_mock_erply_invalid_resource.json";
    public static final String MOCK_ERPLY_CONF_PARAMETERS = "56_mock_erply_conf_parameters.json";
    public static final String MOCK_ERPLY_COMPANY_INFO = "57_mock_erply_company_info.json";    

    /** MockErplyData is a utility class, its constructor is hidden */
    private MockErplyData(){
    }
    
    /**
     * Get number of items in the resource json files when testing
     * @return Map, with keys like "Category", and values like 4
     */
    public static MockPosDataDescriptor getCountInMockData (){
        Map<String,Integer> count = new HashMap<String,Integer>(){{
            put("CustomerDTO",2);
            put("InvoiceDTO",4);
            put("ProductDTO",152);
            put("CategoryDTO",6);
            put("InvoiceLineDTO",5);
        }};
        return new MockPosDataDescriptor(count);
    }
}
