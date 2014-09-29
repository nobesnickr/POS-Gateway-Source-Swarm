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

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * Class providing a service to encrypt and decrypt the AES encrypted
 * content
 */
@Service
public class AESUtility {
    private static final Logger LOGGER = LoggerFactory.getLogger(AESUtility.class);
    
    private @Value("${encryption.aes.key}") String strKey = "NOKEY";

    /**
     * Function that generates a MySQL compatible key for Java Cypher's AES encoding
     */
    public static SecretKeySpec generateMySQLAESKey(final String key, final String encoding) throws UnsupportedEncodingException{
        final byte[] finalKey = new byte[16];
        int i = 0;
        for(byte b : key.getBytes(encoding)){
            finalKey[i++%16] ^= b;          
        }
        return new SecretKeySpec(finalKey, "AES");
    }
    
    /**
     * Encrypt byte[] using AES encryption
     * http://info.michael-simons.eu/2011/07/18/mysql-compatible-aes-encryption-decryption-in-java/
     * @param secret String to be encrypted
     * @return The encrypted content
     */
    public byte[] aesEncryptToBytes(byte[] secret)  {
        try {
            final Cipher encryptCipher = Cipher.getInstance("AES");                         
            encryptCipher.init(Cipher.ENCRYPT_MODE, generateMySQLAESKey(strKey, "UTF-8"));
            return encryptCipher.doFinal(secret);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn("AES encryption failed", e);
        } catch (NoSuchPaddingException e) {
            LOGGER.warn("AES encryption failed", e);
        } catch (InvalidKeyException e) {
            LOGGER.warn("AES encryption failed", e);
        }catch (IllegalBlockSizeException e) {
            LOGGER.warn("AES encryption failed", e);
        } catch (BadPaddingException e) {
            LOGGER.warn("AES encryption failed", e);
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("AES encryption failed", e);
        }
        return null;
    }
    
    /**
     * Encrypt String using AES encryption
     * http://info.michael-simons.eu/2011/07/18/mysql-compatible-aes-encryption-decryption-in-java/
     * @param secret String to be encrypted
     * @return The encrypted content
     */
    public byte[] aesEncryptToBytes(String secret)  {
        return aesEncryptToBytes(secret.getBytes());
    }

    /**
     * Encrypt byte[] using AES encryption
     * http://info.michael-simons.eu/2011/07/18/mysql-compatible-aes-encryption-decryption-in-java/
     * @param secret String to be encrypted
     * @return The encrypted content
     */
    public String aesEncrypt(byte[] secret)  {
        return new String(Hex.encodeHex(aesEncryptToBytes(secret)));
    }    
    
    /**
     * Encrypt String using AES encryption
     * @param secret String to be encrypted
     * @return The encrypted content
     */
    public String aesEncrypt(String secret){
        return this.aesEncrypt(secret.getBytes());
    }
    
    /**
     * Decrypt byte[] using AES decryption
     * @param encryptedSecret String to be decrypted
     * @return The decrypted content
     */
    public String aesDecrypt(byte[] encryptedBytes) {
        try {
            final Cipher cipher = Cipher.getInstance("AES");    
            cipher.init(Cipher.DECRYPT_MODE, generateMySQLAESKey(strKey, "UTF-8"));
            return new String(cipher.doFinal(encryptedBytes));
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn("AES decryption failed", e);
        } catch (NoSuchPaddingException e) {
            LOGGER.warn("AES decryption failed", e);
        } catch (InvalidKeyException e) {
            LOGGER.warn("AES decryption failed", e);
        }catch (IllegalBlockSizeException e) {
            LOGGER.warn("AES decryption failed", e);
        } catch (BadPaddingException e) {
            LOGGER.warn("AES decryption failed", e);
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("AES decryption failed", e);
        }
        return null;
    }  
    
    public String getEncryptionKey() {
        return strKey;
    }

    public void setEncryptionKey(String strKey) {
        this.strKey = strKey;
    }
}
