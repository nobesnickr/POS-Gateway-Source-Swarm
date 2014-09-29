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

package com.sonrisa.swarm.mock;

import java.util.HashMap;
import java.util.Map;

import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;

/**
 * Interface for static classes describing how to mock POS systems
 */
public class MockPosDataDescriptor {
    
    /**
     * Number of items in the resource JSON files when testing
     */
    private Map<String,Integer> countOfMockJsonItems = new HashMap<String,Integer>();

    /**
     * Create using number of items in the resource JSON files when testing
     * @param countOfMockJsonItems
     */
    public MockPosDataDescriptor(Map<String, Integer> countOfMockJsonItems) {
        super();
        this.countOfMockJsonItems = countOfMockJsonItems;
    }

    /**
     * Get number of items in the resource JSON files when testing
     * @return Map, with keys like "Category", and values like 4
     */
     public Map<String,Integer> getCountOfMockJsonItems(){
         return this.countOfMockJsonItems;
     }
     
     /**
      * Get number of mock items for the a clazz
      * @param clazz
      * @return
      */
     public int getCountForDTOClass(Class<? extends DWTransferable> clazz){
         final String key = clazz.getSimpleName();
         if(this.countOfMockJsonItems.containsKey(key)){
             return this.countOfMockJsonItems.get(key);
         } else {
             return 0;
         }
     }
    
}
