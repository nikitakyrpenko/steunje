package com.negeso.framework.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class CryptUtil {
	public static String SHA1(String toCrypt) throws NoSuchAlgorithmException{
		byte[] textInBytes = toCrypt.getBytes();
		MessageDigest md = MessageDigest.getInstance( "SHA" );
		md.reset();
		byte[] result = md.digest(textInBytes);
		
		String hexString = new String(Hex.encodeHex(result));
		return hexString.toString();
	}

}
