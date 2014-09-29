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
package com.sonrisa.swarm.mock.mos;

import java.util.HashMap;
import java.util.Map;

public class MockMosData {
    
    public static final String MOCK_MOS_CUSTOMERS = "17_mock_mos_Customers.json";
    public static final String MOCK_MOS_PRODUCTS = "18_mock_mos_Items.json";
    public static final String MOCK_MOS_EMPTY = "19_mock_mos_Empty.json";
    public static final String MOCK_MOS_MANUFACTURER = "20_mock_mos_Manufacturer.json";
    public static final String MOCK_MOS_SALE = "21_mock_mos_Sale.json";
    public static final String MOCK_MOS_SALE_LINE = "22_mock_mos_SaleLine.json";
    public static final String MOCK_MOS_CATEGORIES = "23_mock_mos_Categories.json";
    
    /**
     * Get number of items in the resource json files when testing
     * @return Map, with keys like "Category", and values like 4
     */
    public static Map<String,Integer> getCountOfMockJsonItems(){
        Map<String,Integer> count = new HashMap<String,Integer>();
        count.put("CategoryDTO", 4);
        count.put("ManufacturerDTO", 2);
        count.put("ProductDTO", 3);
        count.put("CustomerDTO",2);
        count.put("InvoiceDTO",2);
        count.put("InvoiceLineDTO",3);
        return count;
    }
}
