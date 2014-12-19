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
package hu.sonrisa.backend.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Egy Collection elemeiből implicite Map-et építő adapter
 * 
 * @param <T> A Key interfészt implementáló osztály
 * @author cserepj
 */
public final class MapAdapter<T extends Key> implements Collection<T>, Serializable {

    private Map<String, T> adatMap;

    /**
     * jaxb használja
     */
    public MapAdapter() {
        adatMap = new HashMap<String, T>();
    }

    /**
     * 
     * @param adatMap
     */
    public MapAdapter(Map<String, T> adatMap) {
        this.adatMap = adatMap;
    }

    @Override
    public int size() {
        return adatMap.size();
    }

    @Override
    public boolean isEmpty() {
        return adatMap.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return adatMap.containsValue(o);
    }

    /**
     * 
     * @param key
     * @return
     */
    public boolean containsKey(String key) {
        return adatMap.containsKey(key);
    }

    @Override
    public Iterator<T> iterator() {
        return adatMap.values().iterator();
    }

    @Override
    public Object[] toArray() {
        return adatMap.values().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return adatMap.values().toArray(a);
    }

    @Override
    public boolean add(T e) {
        adatMap.put(e.getKey(), e);
        return true;
    }
    
    @Override
    public boolean remove(Object o) {
        if (o instanceof Key) {
            T a = (T) o;
            a = adatMap.remove(a.getKey());
            return a != null;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return adatMap.values().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (T a : c) {
            add(a);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for (Object a : c) {
            remove(a);
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
	Iterator<T> e = iterator();
	while (e.hasNext()) {
	    if (!c.contains(e.next())) {
		e.remove();
		modified = true;
	    }
	}
	return modified;
    }

    @Override
    public void clear() {
        adatMap.clear();
    }

    /**
     * 
     * @param key
     * @return
     */
    public T get(String key) {
        return adatMap.get(key);
    }

    @Override
    public String toString() {
        return "MapAdapter{" + "adatMap=" + adatMap + "}\n";
    }

    /**
     * 
     * @param kulcs
     */
    public void removeByKey(String kulcs) {
        adatMap.remove(kulcs);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MapAdapter<T> other = (MapAdapter<T>) obj;
        if (this.adatMap != other.adatMap && (this.adatMap == null || !this.adatMap.equals(other.adatMap))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.adatMap != null ? this.adatMap.hashCode() : 0);
        return hash;
    } 
}