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
package com.sonrisa.swarm.test.matcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.mockito.ArgumentMatcher;

/**
 * Custom matcher that matches that a map contains certain values
 * @author sonrisa
 *
 * @param <K>
 * @param <V>
 */
public class MapContainsMatcher<K,V> extends ArgumentMatcher<Map<K,V>> {

    private Map<K,V> expectedValues;
    
    /**
     * Matcher expecting nothing yet
     */
    public MapContainsMatcher(){
        this.expectedValues = new HashMap<K,V>();
    }
    
    /**
     * Matcher expecting map with K=>V
     */
    public MapContainsMatcher(K key, V value){
        this();
        andExpects(key, value);
    }
    
    /**
     * Add expected pair to matcher
     * @param key Key (Key=>Value)
     * @param value Value (Key=>Value)
     */
    public MapContainsMatcher<K, V> andExpects(K key, V value){
        expectedValues.put(key, value);
        return this;
    }
    
    /**
     * Match all expected Key=>Value pairs
     */
    @Override
    public boolean matches(Object argument) {
        Map<K,V> map = (Map<K,V>)argument;
        for(Entry<K,V> entry : expectedValues.entrySet()){
            if(!map.containsKey(entry.getKey())){
                return false;
            }
            if(!map.get(entry.getKey()).equals(expectedValues.get(entry.getKey()))){
                return false;
            }
        }
        return true;
    }

}
