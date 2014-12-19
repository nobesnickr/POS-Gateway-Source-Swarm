/*
 *  *  Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
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
package hu.sonrisa.backend.model.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Joe
 */
public class ReflectionUtilTest {

    /**
     * A teszteléshez használt dummy class.
     */
    public class DummyClass{

        public static final String PROPERTY_NAME = "member";

        private String member;

        public String getMember() {
            return member;
        }

        public void setMember(String member) {
            this.member = member;
        }

        public boolean isLogikai(){
            return false;
        }
    }

    // ------------------------------------------------------------------------
    // ~ Test cases
    // ------------------------------------------------------------------------

    @Test
    public void testGetHelyesErtek(){
        // dummy objektum összeállítása
        DummyClass obj = new DummyClass();
        final String PROP_VALUE = "value";
        obj.setMember(PROP_VALUE);

        Object resultObj = ReflectionUtil.get(obj, DummyClass.PROPERTY_NAME);
        
        assertNotNull(resultObj);
        assertEquals(PROP_VALUE, (String)resultObj);
    }

    @Test
    public void testBooleanMember(){
        // dummy objektum összeállítása
        DummyClass obj = new DummyClass();
        
        Object resultObj = ReflectionUtil.get(obj, "logikai");

        // isXxxx() metódussal nem működik
        assertNull(resultObj);        
    }

    @Test
    public void testGetNemLetezoMember(){
        // dummy objektum összeállítása
        DummyClass obj = new DummyClass();

        Object resultObj = ReflectionUtil.get(obj, "nincsIlyenMember");

        assertNull(resultObj);
    }

    @Test
    public void testSetMember(){
        // dummy objektum összeállítása
        DummyClass obj = new DummyClass();

        final String PROPERTY_VALUE = "ezt fogja besettelni";
        ReflectionUtil.set(obj, DummyClass.PROPERTY_NAME, PROPERTY_VALUE, PROPERTY_VALUE.getClass());

        assertEquals(PROPERTY_VALUE, obj.getMember());
    }

    @Test
    public void testSetNemLetezoMember(){
        // dummy objektum összeállítása
        DummyClass obj = new DummyClass();

        final String PROPERTY_VALUE = "ezt fogja besettelni";
        ReflectionUtil.set(obj, "ilyenNincs", PROPERTY_VALUE, PROPERTY_VALUE.getClass());

        assertNull(obj.getMember());
    }


    @Test
    public void testSetRosszClass(){
        // dummy objektum összeállítása
        DummyClass obj = new DummyClass();

        final String PROPERTY_VALUE = "ezt fogja besettelni";
        ReflectionUtil.set(obj, DummyClass.PROPERTY_NAME, PROPERTY_VALUE, Integer.class);

        assertNull(obj.getMember());
    }

    @Test
    public void testWithNullObj(){
        // dummy objektum összeállítása
        DummyClass obj = null;

        //getter
        Object result = ReflectionUtil.get(obj, DummyClass.PROPERTY_NAME);
        assertNull(result);

        //setter
        final String PROPERTY_VALUE = "ezt fogja besettelni";
        ReflectionUtil.set(obj, DummyClass.PROPERTY_NAME, PROPERTY_VALUE, PROPERTY_VALUE.getClass());
        assertNull(obj);       
    }
}