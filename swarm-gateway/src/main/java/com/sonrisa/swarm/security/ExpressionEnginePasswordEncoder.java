/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sonrisa.swarm.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;

/**
 *
 * @author Péter Brindzik <brindzik.peter@openminds.hu>
 */
public class ExpressionEnginePasswordEncoder implements PasswordEncoder {

    /**
     * Takes a previously encoded password and compares it with a rawpassword
     * after mixing in the salt and encoding that value
     *
     * @param encPass previously encoded password
     * @param rawPass plain text password
     * @param salt salt to mix into password
     * @return true or false
     */
    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        String pass1 = "" + encPass;
        String pass2 = encodePassword(getMessageDigest(encPass), rawPass, salt);
        return pass1.equals(pass2);
    }

    @Override
    public String encodePassword(String rawPass, Object salt) {
        return encodePassword(getMessageDigest(null), rawPass, salt);
    }

    /**
     * Encodes the rawPass using a MessageDigest. If a salt is specified it will
     * be merged with the password before encoding.
     *
     * @param messageDigest The message digest to use for encoding.
     * @param rawPass The plain text password
     * @param salt The salt to sprinkle
     * @return Hex string of password digest (or base64 encoded string if
     * encodeHashAsBase64 is enabled.
     */
    protected String encodePassword(MessageDigest messageDigest, String rawPass, Object salt) {
        String saltedPass = (salt == null ? "" : salt) + rawPass;
        byte[] digest = messageDigest.digest(Utf8.encode(saltedPass));
        return new String(Hex.encode(digest));
    }

    protected MessageDigest getMessageDigest(String encPass) {
        try {
            if (encPass == null) {
                return MessageDigest.getInstance("SHA-512");
            }
            MessageDigest messageDigest;
            switch (encPass.length()) {
                case 32:
                    messageDigest = MessageDigest.getInstance("MD5");
                    break;
                case 40:
                    messageDigest = MessageDigest.getInstance("SHA-1");
                    break;
                case 64:
                    messageDigest = MessageDigest.getInstance("SHA-256");
                    break;
                case 128:
                    messageDigest = MessageDigest.getInstance("SHA-512");
                    break;
                default:
                    throw new RuntimeException("Invalid password hash length: " + encPass.length());
            }
            return messageDigest;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
