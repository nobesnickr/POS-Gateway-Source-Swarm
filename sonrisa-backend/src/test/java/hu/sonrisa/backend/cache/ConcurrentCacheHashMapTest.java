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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author kelemen
 */
public class ConcurrentCacheHashMapTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentCacheHashMapTest.class);

    private final static int THREAD_COUNT = 20;
    //ms szama ami után lekapcsoljuk a multithreaded junit tesztet
    private final static int STOP_IN = 60000;

    private volatile int finished = 0;

    private volatile int wait = 0;
    /**
     * Test of put method, of class ConcurrentCacheHashMap.
     */
    @Test
    public void testCacheSize() {
        ConcurrentCacheHashMap<String, String> map = new ConcurrentCacheHashMap<String, String>();
        testMap(0, map);
        testMap(map.getCacheSize(), map);
        testMap(map.getCacheSize()-1, map);
        testMap(map.getCacheSize()+1, map);
    }

	/**
	 * @TODO: This unit test fails with Java 8!
	 */
    @Test
    public void testConstruction(){
        ConcurrentCacheHashMap<String, String> map = new ConcurrentCacheHashMap<String, String>(10001);
        testMap(0, map);        
        testMap(map.getCacheSize()-1, map);
        testMap(map.getCacheSize()+1, map);
        testMap(map.getCacheSize(), map,true);
        ConcurrentCacheHashMap<String, String> map2 = new ConcurrentCacheHashMap<String, String>(1,5);
        testMap(1, map2,true);
        map.putAll(map2);
        assertEquals(1, map.size());
    }

    @Test
    public void testConcurrentPut() throws InterruptedException{
        final ConcurrentCacheHashMap<String, String> map = new ConcurrentCacheHashMap<String, String>();
        testMap(map.getCacheSize(), map,true);
        Runnable r = new Runnable(){
             @Override
            public void run() {
                wait++;
                String threadid = Thread.currentThread().getName();
                Long begin = System.currentTimeMillis();
                while(true){
                    //megfelelo szamu ms után azert menjen tovabb
                    if(wait == THREAD_COUNT || STOP_IN < System.currentTimeMillis()-begin){
                        map.put(threadid, threadid);
                        break;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                    }
                }
                Long end = System.currentTimeMillis();
                LOGGER.info("Thread " + threadid + " finished after " + (end - begin) + " msec");
                finished++;
            }
        };

         long begin = System.currentTimeMillis();

        for (int i = 0; i < THREAD_COUNT; i++) {
            Thread t = new Thread(r);
            t.setName("Thread" + i);
            t.start();
        }

        while (finished < THREAD_COUNT && STOP_IN > System.currentTimeMillis() - begin) {
            Thread.sleep(100);
        }

        Long end = System.currentTimeMillis();
        LOGGER.info("All threads finished after " + (end - begin) + " msec, and map size is: " + map.size());
        assertEquals(THREAD_COUNT, map.size());

    }

    private void testMap(int size, ConcurrentCacheHashMap<String, String> map){
        testMap(size, map, false);
    }

    private void testMap(int size, ConcurrentCacheHashMap<String, String> map, boolean keepEntries){
        for(int i=0; i < size ;i++){
            map.put("kulcs"+i, "ertek"+i);
        }
        int expectedSize = size > map.getCacheSize() ? 1 : size;
        assertEquals(expectedSize,map.size());
        if(!keepEntries){
            map.clear();
        }
    }
}