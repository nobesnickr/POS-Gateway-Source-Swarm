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

import hu.sonrisa.backend.exception.BackendException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Inputstream-ek kezelése
 * 
 * @author Dombi Gergely
 */
public final class InputStreamUtil {
    
    private static final int BUFFER_SIZE = 16384;

    private InputStreamUtil() {
    }
    /**
     * Az átadott inputstream tartalmát adja vissza byte array-ként
     * @param is
     * @return
     */
    public static byte[] getBytes(InputStream is) {
        byte[] result = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int c;
            while ((c = is.read()) != -1) {
                baos.write(c);
            }
            result = baos.toByteArray();
        } catch (IOException ex) {
            throw new BackendException(ex.getMessage(), ex);
        }
        return result;
    }
    /**
     * Vissza ad egy karakterláncot az átadott reader alapján
     * @param reader
     * @return
     */
    public static CharSequence getString(final Reader reader){
        StringBuilder result = new StringBuilder(1000);
        BufferedReader br = new BufferedReader(reader, BUFFER_SIZE);
        try {
            int c;
            while ((c = br.read()) != -1) {
                result.append((char) c);
            }
        } catch (IOException ex) {
            throw new BackendException(ex.getMessage(), ex);
        }
        return result;
    }
}
