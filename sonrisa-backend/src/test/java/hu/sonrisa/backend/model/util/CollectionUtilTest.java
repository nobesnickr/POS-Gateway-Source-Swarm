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

package hu.sonrisa.backend.model.util;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 * @author kelemen
 */
public class CollectionUtilTest {

    /**
     * Test of sortByProperty method, of class CollectionUtil.
     */
    @Test
    public void testNullList(){
        List<Object> objects = null;
        try{
            CollectionUtil.sortByProperty(objects, "", true);
        } catch(Exception ex){
            assertTrue(ex instanceof NullPointerException);
            return;
        }
        fail("Nem jött nullpe! Ez baj!");
    }

    @Test
    public void testEmptyList(){
        List<Object> objects = new ArrayList<Object>();
        CollectionUtil.sortByProperty(objects, null, true);
        CollectionUtil.sortByProperty(objects, "", true);
    }

    @Test
    public void testList(){
        List<Object> objects = new ArrayList<Object>();
        DummyObject do3 = new DummyObject("c", 2);
        objects.add(do3);
        DummyObject do1 = new DummyObject("a", 3);
        objects.add(do1);
        DummyObject do2 = new DummyObject("b", 1);
        objects.add(do2);
        CollectionUtil.sortByProperty(objects, "name", true);
        assertEquals(do1, objects.get(0));
        CollectionUtil.sortByProperty(objects, "name", false);
        assertEquals(do3, objects.get(0));
        CollectionUtil.sortByProperty(objects, "id", true);
        assertEquals(do2, objects.get(0));
        CollectionUtil.sortByProperty(objects, "id", false);
        assertEquals(do1, objects.get(0));
    }

    private static final class DummyObject{
        private String name;
        private Integer id;

        private DummyObject(String name, Integer id) {
            this.name = name;
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}