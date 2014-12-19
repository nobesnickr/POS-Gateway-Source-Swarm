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

import java.nio.charset.Charset;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 *
 * @author Joe
 */
public class StringUtilTest {


    /**
     * Teszt ékezetes karakterekkel.
     * 
     * Elvárt működés: az ékezetes karaktereket,
     * ékezet nélküli megfelelőjükre cseréli.
     */
    @Test
    public void testRemoveAccents1() {

        // kisbetűkre
        String result = StringUtil.removeAccents("árvíztűrő tükörfúrógép ä");
        assertEquals("arvizturo tukorfurogep a", result);

        // nagybetűkre
        result = StringUtil.removeAccents("ÁRVÍztŰrŐ tÜkÖrfÚrÓgÉp");
        assertEquals("ARVIztUrO tUkOrfUrOgEp", result);
    }

    /**
     * Teszt ékezet nélküli karakterekkel.
     *
     * Elvárt működés: nem változtat a bemeneti string-en.
     */
    @Test
    public void testRemoveAccents2() {

        final String str = "qwertzuioplkjhgfdsa YXCVBNM,.1234567890 _ ><'!%/=()#&@{}$[]";
        String result = StringUtil.removeAccents(str);
        assertEquals(str, result);
    }
    
    @Test
    public void testSlash() {
        final String str = "/alma/korte // szilva";
        String result = StringUtil.removeSlash(str);
        assertEquals("almakorte  szilva", result);
    }    
    
    @Test
    public void testSlashWithNull() {        
        assertNull(StringUtil.removeSlash(null));
    }
    
    @Test
    public void testBackSlashWithNull() {        
        assertNull(StringUtil.removeBackslash(null));
    }
        
    @Test
    public void testBackSlash() {
        final String str = "\\alma\\\\korte \\\\\\\\ szilva";
        String result = StringUtil.removeBackslash(str);
        assertEquals("almakorte  szilva", result);
    }

    /**
     * Teszt null inputra.
     *
     * Elvárt működés: null-t ad vissza.
     */
    @Test
    public void testRemoveAccents3() {
        String result = StringUtil.removeAccents(null);
        assertNull(result);
    }
    
    @Test
    public void testSafeTrim(){
        // teszt valid inputra
        final String expectedResult = "a bc";        
        assertEquals(expectedResult, StringUtil.safeTrim(" a bc"));
        assertEquals(expectedResult, StringUtil.safeTrim("a bc "));
        assertEquals(expectedResult, StringUtil.safeTrim(" a bc "));
        
        // teszt null inputra
        assertNull(StringUtil.safeTrim(null));
    }
    
    @Test
    public void testGetAvailableCharsets(){
        final Charset[] expected = {
            Charset.defaultCharset(), 
            Charset.forName(StringUtil.UTF8),
            Charset.forName(StringUtil.ISO88591), 
            Charset.forName(StringUtil.ISO88592),
            Charset.forName(StringUtil.WIN1252)
        };
        
        List<Charset> result = StringUtil.getAvailableCharsets();
        assertNotNull(result);
        
        for (Charset ch : expected){
            assertTrue(result.contains(ch));
        }
    }
    
    @Test
    public void testGetAvailableCharsetCodes(){
        final String[] expected = {            
            StringUtil.UTF8,
            StringUtil.ISO88591, 
            StringUtil.ISO88592,
            StringUtil.WIN1252
        };
        
        List<String> result = StringUtil.getAvailableCharsetCodes();
        assertNotNull(result);
        
        for (String chCode : expected){
            assertTrue(result.contains(chCode));
        }                            
    }    
    
    /**
     *  Egyezik-e a kódok és a charset-ek száma
     */
    @Test
    public void testCharsetCnt(){
        List<String> codes = StringUtil.getAvailableCharsetCodes();
        List<Charset> charsets = StringUtil.getAvailableCharsets();
        
        assertEquals("Ugyanannyi charset-et varunk, ahany kod is van.", charsets.size(), codes.size());
    }
    
    @Test
    public void testRemoveIfUtf8Bom(){
        final String bom = new String(StringUtil.getUTFbyteOrderMark(), StringUtil.UTF8_CHARSET);        
        final String str = "árvíztűrő tükörfúrógép";        
                
        final String result = StringUtil.removeIfUtf8Bom(bom+str);
        assertEquals(str, result);        
    }
    
    @Test
    public void testSafeEquals(){
        assertFalse(StringUtil.safeEquals("aa", "ab"));
        assertFalse(StringUtil.safeEquals("aa", null));
        assertFalse(StringUtil.safeEquals(null, "aa"));
        
        assertTrue(StringUtil.safeEquals("aa", "aa"));
        assertTrue(StringUtil.safeEquals(null, null));
    }

}