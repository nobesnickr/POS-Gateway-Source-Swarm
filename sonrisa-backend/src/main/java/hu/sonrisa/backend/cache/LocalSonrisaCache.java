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
import java.util.Map;

/**
 * Intendend to be used on a single node, mainly for testing purposes
 * 
 * @param <T> 
 * @author János Cserép <cserepj@sonrisa.hu>
 */
public class LocalSonrisaCache<T> implements SonrisaCache<String, T> {
    private  static final int MAX_SIZE = 100;

    private Map<String, T> map = new ConcurrentCacheHashMap<String, T>(){
        
        @Override
        protected int getCacheSize() {
            return getMaxSize();
        }        
    };

    @Override
    public T get(String key) {
        return map.get(key);
    }

    @Override
    public void lock(String key) {
    }

    @Override
    public void put(String key, T value) {
        map.put(key, value);
    }

    @Override
    public T remove(String key) {
        return map.remove(key);
    }

    @Override
    public boolean containsKey(String cacheKey) {
        return map.containsKey(cacheKey);
    }

    @Override
    public Collection<T> values() {
        return map.values();
    }

    @Override
    public void unlock(String key) {
    }
    
    @Override
    public void clear() {
        map.clear();
    }
    
    /**
     * Ekkora lesz a cache merete.
     * 
     * @return 
     */
    protected int getMaxSize(){
        return MAX_SIZE;
    }
}
