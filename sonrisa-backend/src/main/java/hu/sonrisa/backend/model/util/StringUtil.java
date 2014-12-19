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
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * String manipulációs segéd metódusok gyűjteménye.
 *
 * @author Joe
 */
public final class StringUtil {

    public static final String UTF8 = "UTF-8";

    public static final String WIN1252 = "windows-1252";

    public static final String ISO88592 = "ISO-8859-2";

    public static final String ISO88591 = "ISO-8859-1";

    public static final String SEMICOLON = ";";
    
    public static final String TAB = "\t";
    
    public static final String SPACE = " ";
    
    public static final String NEWLINE = "\n";
    
    public static final Charset UTF8_CHARSET = Charset.forName(UTF8);
    
    private static final byte[] UTF8_BOM_BYTES = new byte[]{(byte)0xef,(byte)0xbb,(byte)0xbf};
    
    private static final Charset[] CHARSETS = {
        Charset.defaultCharset(), UTF8_CHARSET,
        Charset.forName(ISO88591), Charset.forName(ISO88592),
        Charset.forName(WIN1252)
    };

    /**
     * Azért private a default construktor,
     * mert ez egy Util oszály, nem kell példányosítani.
     */
    private StringUtil() {}
    
    /**
     * Ékezetes karakterek cseréje ékezet nélkülire.
     * <p/>
     *
     * Az ékezet nélküli karaktereken nem változtat.
     * Null bemeneti paraméter esetén null-t ad vissza.
     *
     * @param text
     * @return
     */
    public static String removeAccents(final String text) {
        String result = null;
        if (text != null){
            result = Normalizer.normalize(text, Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        }
        return result;
    }

    /**
     * Visszaadja az elérhető karakterkódok neveinek listáját.
     * 
     * @return 
     */
    public static List<String> getAvailableCharsetCodes(){
         List<Charset> charsets = getAvailableCharsets();
         
         List<String> ret = new ArrayList<String>();
         for (Charset charset : charsets){
             ret.add(charset.displayName());
         }
         return ret;
    }

    /**
     * Visszaadja az elérhető karakterkódok listáját.
     * 
     * @return 
     */
    public static List<Charset> getAvailableCharsets(){
        List<Charset> ret = Arrays.asList(CHARSETS);
        Charset defaulltc = Charset.defaultCharset();
        if (!ret.contains(defaulltc)) {
            ret = new ArrayList<Charset>(ret);
            ret.add(defaulltc);
            Collections.sort(ret);
        }
        return ret;
    }

    /**
     * Ha az átadott sor BOM-mal kezdődik, akkor 
     * a visszaadott string-ből eltávoítja azt.
     * 
     * @param line
     * @return 
     */
    public static String removeIfUtf8Bom(final String line) {
        String utf8Bom = new String(getUTFbyteOrderMark(), UTF8_CHARSET);
        if (line.startsWith(utf8Bom)) {
            return line.substring(utf8Bom.length());
        }
        return line;
    }
    
    /**
     * Visszaadja a BOM-ot byte tömb formájában UTF8 kódoláshoz.
     * 
     * @return 
     */
    public static byte[] getUTFbyteOrderMark(){
        return Arrays.copyOf(UTF8_BOM_BYTES, UTF8_BOM_BYTES.length);
    }
    
    /**
     * Kicseréli a kapott string-ben a slash karaktereket
     * üres String-re.
     */
    public static String removeSlash(final String str){
        return str == null ? null : str.replaceAll("/", "");
    }
    
    /**
     * Kicseréli a kapott string-ben a backslash karaktereket
     * üres String-re.
     * 
     * @param str
     * @return 
     */
    public static String removeBackslash(final String str){
        // a négy darab backslash fog egyet jelenteni
        // mert a java egyszer felezi a számukat
        // után a regexp kifejezeés megint
        return str == null ? null : str.replaceAll("\\\\", "");
    }
    
    /**
     * Trim-meli a kapott string-et, ha az nem null.
     * Ha null, akkor változtatás nélkül visszaadja.
     * 
     * @param str
     * @return 
     */
    public static String safeTrim(final String str){
        return str == null ? null : str.trim();
    }
    
    /**
     * Kornyezet fuggetlen uj sor karaktert ad vissza.
     * 
     * @return 
     */
    public static String newLine(){
        return System.getProperty("line.separator");
    }
    
  /**
     * Megnézi, hogy a két string egyezik-e. 
     * Null biztos, azaz akkor is true-t ad vissza, ha
     * mindkét string null.
     * 
     * @param str1
     * @param str2
     * @return 
     */
    public static boolean safeEquals(final String str1, final String str2){
        return str1 == null && str2 == null 
            || str1 != null && str1.equals(str2);
    }    
}