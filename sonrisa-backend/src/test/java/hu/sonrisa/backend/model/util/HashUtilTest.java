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

import hu.sonrisa.backend.model.util.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Joe
 */
public class HashUtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HashUtilTest.class);

    /**
     * Test of MD5 hash method, of class HashUtil.
     */
    @Test
    public void testMD5Hash() {
        final String str = "12345";
        final String hashedValue = HashUtil.getInstance(HashUtil.MD5).hash(str);
        LOGGER.info("Hash: " + str + " => " + hashedValue);
        final String expResult = "827ccb0eea8a706c4c34a16891f84e7b";
        assertEquals(expResult, hashedValue);
    }
    
    @Test
    public void testSHAHash() {
        final String str = "12345";
        final String hashedValue = HashUtil.getInstance(HashUtil.SHA).hash(str);
        LOGGER.info("Hash: " + str + " => " + hashedValue);
        final String expResult = "8cb2237d0679ca88db6464eac60da96345513964";
        assertEquals(expResult, hashedValue);
    }
    
    @Test
    public void testHashException() {
        final String str = "12345";
        try {
            HashUtil.getInstance("any").hash(str);
        } catch (Exception e) {
            return;
        }
        fail("No exception occured but should have!");
    }    
}