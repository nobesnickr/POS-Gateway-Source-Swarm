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

package hu.sonrisa.backend.csv;

import hu.sonrisa.backend.exception.BackendExceptionConstants;
import hu.sonrisa.backend.model.util.StringUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.zip.ZipInputStream;

/**
 * Az importer osztályok közös segéd osztálya.
 *
 * @author Joe
 */
public final class ImporterHelper {

    private ImporterHelper() {
    }
    /**
     * Oszlopnévből setter metódusnevet előállító metódus
     *
     * @param header
     * @return
     */
    public static String toMethodName(final String header) {
        StringBuilder b = new StringBuilder();
        b.append(Character.toUpperCase(header.charAt(0)));
        for (int i = 1; i < header.length(); i++) {
            char c = header.charAt(i);
            if (c == '_') {
                i++;
                c = header.charAt(i);
            } else {
                c = Character.toLowerCase(c);
            }
            b.append(c);
        }
        return b.toString();
    }

    /**
     * ZipInputStream-mé alakítja a sima inputStream-et.
     *
     * @param inputStream
     * @return
     */
    public static ZipInputStream convertToZipStream(InputStream inputStream) {
        if (!(inputStream instanceof ZipInputStream)) {
            return new ZipInputStream(inputStream);
        } else {
            return (ZipInputStream) inputStream;
        }
    }

    /**
     * Megpróbálja kitalálni az inputStream-ből jövő fájl karakter kódolását.
     * <p/>
     *
     * Egyszerű implementáció, csak azt tudja eldönteni, hogy UTF-8-e vagy sem.
     * Ha UTF-8, akkor azt adja vissza, különben a default kódolást.
     * <p/>
     *
     * <strong>Fontos:</strong> az inputStream-et továbbiakban nem lehet
     * használni. Amit a metódus kap, azt lezárja, miután végzett.
     * Még a hívás előtt kell gondoskodni róla, hogy legyen belőle egy másolat.
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static Charset guessEncoding(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            String elsoSor = reader.readLine();
            if(elsoSor != null && !elsoSor.trim().isEmpty()){
                if (new String(StringUtil.getUTFbyteOrderMark()).equals(elsoSor.substring(0,3))) {
                    return Charset.forName(StringUtil.UTF8);
                } else {
                    return Charset.defaultCharset();
                }
            }else{
                throw new IOException(BackendExceptionConstants.BEND_00003);
            }
        } finally {
            reader.close();
            in.close();
        }
    }

    /**
     * Lsd: {@link #guessEncoding(java.io.InputStream)}
     * <p/>
     *
     * A kapott ZIP stream első bejegyzésével dolgozik.
     *
     * @param zis
     * @return
     * @throws IOException
     */
    public static Charset guessEncodingZip(ZipInputStream zis) throws IOException {
        if (zis.getNextEntry() != null){
            return guessEncoding(zis);
        }else{
            return Charset.defaultCharset();
        }                
    }
}
