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

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sonrisa
 */
public class MapAdapterTest {

    private MapAdapter<Valami> map = new MapAdapter(new HashMap<String, Valami>());
    private ArrayList<Valami> list = new ArrayList<Valami>();
    private Valami valami = new Valami("3","3");

    private int mapSize = 3;


    public class Valami implements Key {

        String key;
        String valami;

        public Valami(String key, String valami) {
            this.key = key;
            this.valami = valami;
        }

        @Override
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValami() {
            return valami;
        }

        public void setValami(String valami) {
            this.valami = valami;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Valami other = (Valami) obj;
            if ((this.key == null) ? (other.key != null) : !this.key.equals(other.key)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 79 * hash + (this.key != null ? this.key.hashCode() : 0);
            return hash;
        }
    }

    @Before
    public void setUp() {
        list.add(new Valami("1", "1"));
        list.add(new Valami("2", "2"));
        map.addAll(list);
        map.add(valami);
    }

    /**
     * Test of size method, of class MapAdapter.
     */
    @Test
    public void testSize() {
        assertEquals(mapSize, map.size());
        assertFalse("Nem szabadna üresnek lennie a map-nek",map.isEmpty());
        assertTrue("1-es kulcsú teszt elemet tartalmaznia kell a map-nek, most még sincs",map.containsKey("1"));
        assertFalse("4-es kulcsú teszt elem nincs a map-ben... most mégis ",map.containsKey("4"));
        //contains-ben a valami equals metodusaban nem szamit a valami resz
        //ezert ez itt egyenlo lesz!
        assertTrue(map.contains(new Valami("1", "xx")));
    }

    /**
     * Test of isEmpty method, of class MapAdapter.
     */
    @Test
    public void testIsEmpty() {
        assertFalse(map.isEmpty());
        map.clear();
        assertTrue(map.isEmpty());
    }

    /**
     * Test of iterator method, of class MapAdapter.
     */
    @Test
    public void testIterator() {
        Iterator<Valami> it = map.iterator();
        while(it.hasNext()){
            Valami v = it.next();
            assertTrue(map.contains(v));
        }
    }

    /**
     * Test of toArray method, of class MapAdapter.
     */
    @Test
    public void testToArray_0args() {
        Object[] valamis = map.toArray();
        assertEquals(mapSize, valamis.length);
        for(Object o : valamis){
            Valami v = (Valami) o;
            assertTrue(map.contains(v));
        }
    }

    /**
     * Test of toArray method, of class MapAdapter.
     */
    @Test
    public void testToArray_GenericType() {
        Valami[] valamis = map.toArray(new Valami[0]);
        assertEquals(mapSize, valamis.length);
        for(Valami v: valamis){
            assertTrue(map.contains(v));
        }
    }

    /**
     * Test of remove method, of class MapAdapter.
     */
    @Test
    public void testRemove() {
        assertTrue(map.remove(valami));
        assertEquals(list.size(), map.size());
    }

    /**
     * Test of containsAll method, of class MapAdapter.
     */
    @Test
    public void testContainsAll() {
        list.containsAll(map);
        assertTrue(map.containsAll(list));
    }

    /**
     * Test of addAll method, of class MapAdapter.
     */
    @Test
    public void testAddAll() {
        assertEquals(mapSize, map.size());
        List<Valami> list2 = new ArrayList<Valami>();
        list2.add(valami);//ez már elvileg benne van
        list2.add(new Valami("1", "xx"));//ennek a kulcsa már benne van
        list2.add(new Valami("5","5"));//ez még nincs benne
        map.addAll(list2);
        assertEquals(mapSize+1, map.size());//eggyel növekszik a méret
        assertTrue(map.containsKey("5"));
        assertTrue(map.contains(new Valami("5","5")));

    }

    /**
     * Test of removeAll method, of class MapAdapter.
     */
    @Test
    public void testRemoveAll() {
        assertEquals(mapSize, map.size());
        map.removeAll(list);
        assertEquals(mapSize - list.size(), map.size());
    }

    /**
     * Test of retainAll method, of class MapAdapter.
     */
    @Test
    public void testRetainAll() {
        assertEquals(mapSize, map.size());
        map.retainAll(list);
        assertEquals(list.size(), map.size());
        assertTrue(map.containsKey("1"));
        assertFalse(map.containsKey("3"));
    }

    /**
     * Test of get method, of class MapAdapter.
     */
    @Test
    public void testGet() {
        Valami v = map.get("1");
        assertNotNull(v);
        v = map.get("7");
        assertNull(v);
    }

    /**
     * Test of removeByKey method, of class MapAdapter.
     */
    @Test
    public void testRemoveByKey() {
        assertEquals(mapSize, map.size());
        map.removeByKey("5");
        assertEquals(mapSize, map.size());
        map.removeByKey("1");
        assertEquals(mapSize-1, map.size());
    }
}
