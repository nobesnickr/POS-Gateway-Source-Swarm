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

import java.io.Serializable;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author joe
 */
public class ObjectUtilTest {
    
    
    
    /**
     * Klónozást teszteli.     
     * Az értékeknek egyezniük kell, de a referenciáknak különbözniük.
     * Megnézi, hogy tényleg deep copy történik-e, azaz a tartalmazott 
     * osztályok is klónozódnak-e.
     * 
     */
    @Test
    public void testClone() {
        B orig = new B();        
        B result = ObjectUtil.clone(orig);                
        
        // értékek egyeznek
        assertEquals(orig.getBar(), result.getBar());
        assertEquals(orig.getA().getFoo(), result.getA().getFoo());
        
        // referenciák különböznek
        assertFalse(orig.equals(result));
        assertFalse(orig.getA().equals(result.getA()));
    }
    
    /**
     * Teszteset, amikor a klónozni kívánt osztály nem sorosítható.
     */
    @Test(expected=RuntimeException.class)
    public void testCloneNotSerializable() {
        NotSerializableWrapper orig = new NotSerializableWrapper();        
        ObjectUtil.clone(orig);                                
    }
    
        
    
    /**
     * Teszt során használt példa osztály, klónozást teszteljük vele.
     */
    public static class A implements Serializable{
        private String foo;

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public String getFoo() {
            return foo;
        }
        
        
    }
    
    /**
     * Teszt során használt példa osztály, klónozást teszteljük vele.
     * Tartalmaz egy A-t.
     */
    public static class B implements Serializable{
        private String bar;
        private A a;

        public B() {
            bar = "bar";
            a = new A();
            a.setFoo("foo");
        }
                
        public A getA() {
            return a;
        }

        public void setA(A a) {
            this.a = a;
        }

        public String getBar() {
            return bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }
        
        
    }
    
    /**
     * Teszt során használt példa osztály, klónozást teszteljük vele.
     * Nem serializálható.
     */
    public static class NotSerializable{
        private int a = 10;
    }
    
    /**
     * Csomagoló osztály, hogy elrentse a nem sorosítható fieldet.
     */
    public static class NotSerializableWrapper implements Serializable{
        private NotSerializable ns = new NotSerializable();
    }
}
