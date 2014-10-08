package hu.sonrisa.backend.model.util;

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
import hu.sonrisa.backend.exception.BackendException;
import java.io.Reader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Dombi Gergely
 */
public class InputStreamUtilTest {

    private static final String testString = "teszt szoveg";
    private InputStream is;

    @Test
    public void testGetBytes() {
        byte[] buf = testString.getBytes();
        is = new ByteArrayInputStream(buf);
        byte[] bytes = InputStreamUtil.getBytes(is);
        assertEquals(buf.length, bytes.length);
        assertEquals(testString, new String(bytes));
    }

    @Test
    public void testGetBytesWrong() throws IOException{
        is = InputStreamUtilTest.class.getResourceAsStream("InputStreamUtilTest.class");
        //most valami hiba van
        is.close();
        byte[] bytes = null;
        try{
            bytes = InputStreamUtil.getBytes(is);
        }catch(BackendException bad){
            return;
        }
        int length = bytes != null ? bytes.length : -1;
        fail("Nem jött a várt BackendException! " + length);
    }

    @Test
    public void testGetString(){
        Reader r = new StringReader(testString);
        String res = InputStreamUtil.getString(r).toString();
        assertEquals(testString, res);
    }
}