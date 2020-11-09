/*
 * @(#)$Id: EncriptedAuthenticator.java,v 1.1, 2007-01-09 18:35:43Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.security;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.Security;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.generators.Xbuilder;

/**
 * 
 * EncriptedAuthenticator - class to make secure authentication.
 * 
 * 
 * @author		Andrey V. Morskoy
 * @version		$Revision: 2$
 *
 */
public class EncriptedAuthenticator {
	private static Logger logger = Logger.getLogger(EncriptedAuthenticator.class);
	
	/*singlenton to store user's key pairs in next way:
	 * hash key: hex representation of public key (bieng put/get into/from request)
	 * hash value: private key as object, implemented PrivateKey (from key pair)
	 */ 
	private static HashMap<String, RSAPrivateCrtKeyParameters> keyMap = new HashMap<String,RSAPrivateCrtKeyParameters>();

	/**
	 * Processes requeast and response to make public key push or password decoding
	 * @param request
	 * @param response
	 * @return decoded password.
	 */
	public static String processPassword(RequestContext request, ResponseContext response, Element loginElement)
	throws Exception
	{
		 logger.debug("+");
		 String result = null;
	
		 Security.addProvider(new BouncyCastleProvider());
	     
	     String reqKey = request.getParameter("pkey");
	     String reqExp = request.getParameter("pexp");
	     
	     if( reqKey==null || reqKey.equals("") ){

	    	 HashMap<RSAKeyParameters, RSAPrivateCrtKeyParameters> keys = generateRSAKeyPair();
			
	    	 RSAKeyParameters rsaPublic = keys.keySet().iterator().next();
			 RSAPrivateCrtKeyParameters rsaPrivate = keys.get(rsaPublic);

		     HashMap<String, RSAPrivateCrtKeyParameters> map = getKeyMap();
		     map.put(
		    		 new String( Hex.encode(rsaPublic.getModulus().toByteArray()) ), 
		    		 rsaPrivate);
        	 Xbuilder.setAttr(
        			 loginElement, 
        			 "publicKey",
        			 new String( Hex.encode(rsaPublic.getModulus().toByteArray()) ));
        	 
        	 Xbuilder.setAttr(
        			 loginElement, 
        			 "publicExp",
        			 new String( Hex.encode(rsaPublic.getExponent().toByteArray()) ));
	     }
	     else{
		     String password = request.getParameter("password");
		     HashMap<String, RSAPrivateCrtKeyParameters> map = getKeyMap();
		     RSAPrivateCrtKeyParameters prvk = map.get(reqKey);
	    	 Xbuilder.setAttr(loginElement, "publicKey", reqKey);
	    	 Xbuilder.setAttr(loginElement, "publicExp", reqExp);
		     if(prvk!=null){
		    	 byte[] decBytes = RSADecrypt(Hex.decode(password),prvk);
//		    	 map.remove(reqKey);
		    	 result = new String(decBytes);		    	 
		     }
	     }
	     logger.debug("-");
	     return result;
	}
	
	
	
	
	
	/** This is simple demo of how enription looks like with bouncycastle
	 * 
	 */
	public static  void test(){
	
		String theStringBeforeEncryption = "y7wt26";
		String theStringAfterDecryption = null;
		byte[] theEncryptedBytes;
		byte[] hexBytes;
		byte[] unhexBytes;
		try {
			HashMap<RSAKeyParameters, RSAPrivateCrtKeyParameters> keys = generateRSAKeyPair();

			RSAKeyParameters _RSAPublicKey = keys.keySet().iterator().next();
			RSAPrivateCrtKeyParameters _RSAPrivateKey = keys.get(_RSAPublicKey);
			
			logger.error("BEFORE ENC: "+theStringBeforeEncryption);
			theEncryptedBytes = RSAEncrypt(theStringBeforeEncryption.getBytes(),_RSAPublicKey);
			logger.error("AFTER ENC: "+new String(theEncryptedBytes));
			
			hexBytes = Hex.encode(theEncryptedBytes);
			logger.error("AFTER HEX: "+new String(hexBytes));
			

			unhexBytes = Hex.decode(hexBytes);
			logger.error("AFTER UNHEX: "+new String(unhexBytes));
			
			theStringAfterDecryption = new String(RSADecrypt(unhexBytes,_RSAPrivateKey));
			logger.error("AFTER DEC: "+theStringAfterDecryption);
		} catch (Exception e) {
			logger.error("TEST EXCEPTION!!: ",e);
		}
		
	}
	
	
	//------------------Bouncycastle variant
	
	private static HashMap<RSAKeyParameters, RSAPrivateCrtKeyParameters> generateRSAKeyPair () 
	throws Exception 
	{
		SecureRandom theSecureRandom = new SecureRandom();
		BigInteger thePublicExponent = new BigInteger("010001", 16);
		
		RSAKeyGenerationParameters theRSAKeyGenParam =
			new RSAKeyGenerationParameters(thePublicExponent, theSecureRandom, 256, 80);
		RSAKeyPairGenerator theRSAKeyPairGen = new RSAKeyPairGenerator();
		theRSAKeyPairGen.init(theRSAKeyGenParam);
		AsymmetricCipherKeyPair theKeyPair = theRSAKeyPairGen.generateKeyPair();
		
		RSAPrivateCrtKeyParameters _RSAPrivateKey = (RSAPrivateCrtKeyParameters) theKeyPair.getPrivate();
		RSAKeyParameters _RSAPublicKey = (RSAKeyParameters) theKeyPair.getPublic();
		
		HashMap<RSAKeyParameters, RSAPrivateCrtKeyParameters> result = 
			new HashMap<RSAKeyParameters, RSAPrivateCrtKeyParameters> ();
		
		result.put(_RSAPublicKey, _RSAPrivateKey);
		return result;
	}
	
	
	
	
	private static byte [] RSAEncrypt (byte [] toEncrypt,RSAKeyParameters _RSAPublicKey) 
	throws Exception 
	{
		if (_RSAPublicKey == null) {
			throw new Exception("Please generate RSA keys first in order to work");
		}
		AsymmetricBlockCipher theEngine = new RSAEngine();
		theEngine = new PKCS1Encoding(theEngine);
		theEngine.init(true, _RSAPublicKey);
		return theEngine.processBlock(toEncrypt, 0, toEncrypt.length);
	}
	
	
	
	
	private static byte [] RSADecrypt (byte [] toDecrypt, RSAPrivateCrtKeyParameters _RSAPrivateKey) 
	throws Exception 
	{
		if (_RSAPrivateKey == null) {
			throw new Exception("Please generate RSA keys first in order to work");
		}
		
		AsymmetricBlockCipher theEngine = new RSAEngine();
		theEngine = new PKCS1Encoding(theEngine);
		theEngine.init(false, _RSAPrivateKey);
		return theEngine.processBlock(toDecrypt, 0, toDecrypt.length);
	}
	

	
	

	
	
	
	
	private static HashMap<String, RSAPrivateCrtKeyParameters> getKeyMap(){
		if( keyMap==null ){
			keyMap = new HashMap<String, RSAPrivateCrtKeyParameters>();
		}
		return keyMap;
	}

	public static void removeKey(RequestContext request){
		String reqKey = request.getParameter("pkey");
		if( reqKey != null && !reqKey.equals("") && keyMap != null && !keyMap.isEmpty() && keyMap.containsKey(reqKey) ){
			keyMap.remove(reqKey);
		}
	}
	
	
}
