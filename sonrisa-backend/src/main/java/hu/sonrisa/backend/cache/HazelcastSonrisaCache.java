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

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import java.util.Collection;

/**
 * Hazelcast based distributed implementation of a SonrisaCache
 *
 * @param <T>
 * @author János Cserép <cserepj@sonrisa.hu>
 */
public class HazelcastSonrisaCache<T> implements SonrisaCache<String, T> {

    private HazelcastInstance instance;
    private IMap<String, T> map;

    public HazelcastSonrisaCache(HazelcastInstance instance, IMap<String, T> map) {
        this.instance = instance;
        this.map = map;
    }

    public HazelcastInstance getInstance() {
        return instance;
    }

    public IMap<String, T> getMap() {
        return map;
    }

    @Override
    public T get(String key) {
        return getMap().get(key);
    }

    @Override
    public T remove(String key) {
        return getMap().remove(key);
    }

    @Override
    public void put(String key, T value) {
        getMap().put(key, value);
    }

    @Override
    public void lock(String key) {
        getMap().lock(key);
    }

    @Override
    public void unlock(String key) {
        getMap().unlock(key);
    }

    @Override
    public boolean containsKey(String cacheKey) {
        return getMap().containsKey(cacheKey);
    }

    @Override
    public Collection<T> values() {
        return getMap().values();
    }

    @Override
    public void clear() {
        getMap().clear();
    }
}
