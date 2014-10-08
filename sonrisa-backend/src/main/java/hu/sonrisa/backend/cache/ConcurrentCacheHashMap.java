/*
 *  Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Sonrisa Informatikai Kft. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sonrisa.
 *
 * SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package hu.sonrisa.backend.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache-re hasznalhato ConcurrentHashMap, amely kikuszoboli azt, hogy memory
 * leak keletkezzen a programunkban, mindezt azzal, hogyha a cache eler egy
 * bizonyos meretet, akkor uriti a tartalmat.
 *
 * @param <K>
 * @param <V>
 * @author Palesz
 */
public class ConcurrentCacheHashMap<K, V> extends ConcurrentHashMap<K, V> {

    private static final int MAX_CACHE_SIZE = 10000;
    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     *
     * @param initialCapacity
     * @param loadFactor
     * @param concurrencyLevel
     */
    public ConcurrentCacheHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
    }

    /**
     * {@inheritDoc}
     *
     * @param initialCapacity
     * @param loadFactor
     */
    public ConcurrentCacheHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * {@inheritDoc}
     *
     * @param initialCapacity
     */
    public ConcurrentCacheHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     *
     */
    public ConcurrentCacheHashMap() {
    }

    /**
     *
     * @param m
     */
    public ConcurrentCacheHashMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    @Override
    public V put(K key, V value) {
        checkCapacity();
        return super.put(key, value);
    }

    private synchronized void checkCapacity() {
        if (size() >= getCacheSize()) {
            clear();
        }
    }

    /**
     *
     * @return
     */
    protected int getCacheSize() {
        return MAX_CACHE_SIZE;
    }
}
