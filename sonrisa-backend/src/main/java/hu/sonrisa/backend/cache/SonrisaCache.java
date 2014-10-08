/*
 *   Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
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
package hu.sonrisa.backend.cache;

import java.util.Collection;

/**
 *
 * Interface that hides a cache and executor service
 * 
 * @param <K> 
 * @param <V> 
 * @author János Cserép <cserepj@sonrisa.hu>
 */
public interface SonrisaCache<K, V> {
    
    /**
     * Get value from cache by key
     * @param key
     * @return
     */
    V get(K key);

    /**
     * Returns and removes value by key
     * @param key
     * @return
     */
    V remove(K key);

    /**
     * Puts value to key
     * @param key
     * @param value
     */
    void put(K key, V value);
    
    /**
     * Locks based on key
     * @param key
     */
    void lock(K key);
    
    /**
     * Unlocks based on key
     * @param key
     */
    void unlock(K key);
    
    /**
     * Returns true if cache contains value for key
     * @param key
     * @return
     */
    boolean containsKey(String key);
    
    /**
     * Return all values stored in cache
     * @return
     */
    Collection<V> values();
    
    /**
     * Clear cache
     */
    void clear();
}