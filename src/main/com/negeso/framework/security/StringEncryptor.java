/*
 * @(#)$Id: StringEncryptor.java,v 1.4, 2005-06-06 13:05:10Z, Stanislav Demchenko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.log4j.Logger;


import com.negeso.framework.domain.CriticalException;

/**
 *
 * String encoder class. Used for strings encoding - decoding.
 * Note: to decode encoded text secret phrase should be equal to 
 * encoding one. 
 * 
 * @version		$Revision: 5$
 * @author		Olexiy Strashko
 * 
 */
public class StringEncryptor {
    private static Logger logger = Logger.getLogger(StringEncryptor.class);

    private static final String defaultEncoding = "UTF8"; 

    private Cipher ecipher = null;
    private Cipher dcipher = null;
    
    /**
     * @throws CriticalException
     * 
     */
    public StringEncryptor(String secretPhrase) throws CriticalException {
        super();
        // 8-bytes Salt
        byte[] salt = {
            (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
            (byte)0x56, (byte)0x34, (byte)0xE3, (byte)0x03
        };

        // Iteration count
        int iterationCount = 19;

        try {

            KeySpec keySpec = new PBEKeySpec(secretPhrase.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);

            ecipher = Cipher.getInstance(key.getAlgorithm());
            dcipher = Cipher.getInstance(key.getAlgorithm());

            // Prepare the parameters to the cipthers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

        } 
        catch (InvalidAlgorithmParameterException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        } 
        catch (InvalidKeySpecException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        } 
        catch (NoSuchPaddingException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        } 
        catch (NoSuchAlgorithmException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        } 
        catch (InvalidKeyException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        }
    }

    /**
     * Encodes text and returns it.
     * 
     * @param text
     * @return
     */
    public String encrypt(String text) throws CriticalException {
        try {
            // Encode the string into bytes using utf-8
            byte[] utf8 = text.getBytes(StringEncryptor.defaultEncoding);

            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);

            // Encode bytes to base64 to get a string
            return new sun.misc.BASE64Encoder().encode(enc);

        } 
        catch (BadPaddingException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        } 
        catch (IllegalBlockSizeException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        } 
        catch (UnsupportedEncodingException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        } 
    }
    
    /**
     * Decodes text and returns it. 
     * 
     * @param eText
     * @return
     * @throws CriticalException
     */
    public String decrypt(String eText) throws CriticalException{
        try {

            // Decode base64 to get bytes
            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(eText);

            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);

            // Decode using utf-8
            return new String(utf8, StringEncryptor.defaultEncoding);

        } 
        catch (BadPaddingException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        } 
        catch (IllegalBlockSizeException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        } 
        catch (UnsupportedEncodingException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        } 
        catch (IOException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        }
    }
}
