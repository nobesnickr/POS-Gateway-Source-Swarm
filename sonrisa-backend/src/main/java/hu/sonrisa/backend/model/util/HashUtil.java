/*
 *  Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
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

import hu.sonrisa.backend.exception.BackendExceptionConstants;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * String-ek hash értékének számításához használható segéd osztály.
 * A java.security.MessageDigest osztály wrappere, bonyolultabb esetekben azt érdemes használni
 * @author Joe
 */
public final class HashUtil {

    /** Logger */
    //private static final Logger LOGGER = LoggerFactory.getLogger(HashUtil.class);

    private static final int HEXA = 16;

    /** A hash érték számításánal használt MD5 algoritmus. */
    public static final String MD5 = "MD5";
    
    /** A hash érték számításánal használt SHA algoritmus. */
    public static final String SHA = "SHA";
    
    private String hashAlgCode = MD5;

    private HashUtil() {
    }
    
    public static HashUtil getInstance(final String hashAlgCode){
        HashUtil hu = new HashUtil();
        hu.hashAlgCode = hashAlgCode;
        return hu;
    }
    
    /**
     * Egy string hash értékét adja vissza, a beállított {@link #HASH_ALG algoritmussal} számolva.
     *
     * @param str
     * @return a kapott string-ből számított hash érték
     */
    public String hash(final String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance(hashAlgCode);
        } catch (NoSuchAlgorithmException ex) {
            //LOGGER.error("Unknown hashing algorithm: " + HASH_ALG);
            throw new IllegalArgumentException(BackendExceptionConstants.BEND_00017 + hashAlgCode, ex);
        }
        messageDigest.update(str.getBytes(), 0, str.length());
        return new BigInteger(1, messageDigest.digest()).toString(HEXA);
    }
}