/*
 *   Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.
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
package com.sonrisa.swarm.posintegration.extractor.security;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.DecoderException;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Before;
import org.junit.Test;

/**
 * Class testing the AESUtility class 
 */
public class AESUtilityTest {

    /**
     * AES encyption key to used while testing 
     */
    private static String encyptionKey = "ABC123";
    
    /**
     * Utility class being tested
     */
    private AESUtility utility;
    
    /**
     * Setup tests by setting encryption key
     * @throws DecoderException 
     */
    @Before
    public void setupTests() throws DecoderException{
        utility = new AESUtility();
        utility.setEncryptionKey(encyptionKey);
    }
    
    /**
     * Test encypting a dummy string
     */
    @Test
    public void testEncryption(){
        String encryptedString = utility.aesEncrypt("almakorte123");

        // Expected value calculated using MySQL: SELECT AES_ENCRYPT(  'almakorte123',  encyptionKey )
        assertEquals("3e9f599c1c3b309948589df5a3967f59", encryptedString);
    }

    /**
     * Test decyption of the dummy string
     * @throws UnsupportedEncodingException 
     */
    @Test
    public void testDecryption() {
        // Encoded string calculated using MySQL: SELECT SELECT AES_ENCRYPT(  'alma',  'ABC123' )
        String decyptedString;
        decyptedString = utility.aesDecrypt(Hex.decode("2cf7c4f41448c3e4caa1e937f0127d50"));
        assertEquals("alma", decyptedString);
    }
}
