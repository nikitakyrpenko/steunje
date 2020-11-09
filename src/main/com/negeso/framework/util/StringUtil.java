/*
 * @(#)$Id: StringUtil.java,v 1.17, 2005-10-23 18:27:02Z, Olexiy Strashko$
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.util;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import sun.misc.BASE64Encoder;

/**
 * String processing utilities and tools.
 *
 * @version 		$Revision: 18$
 * @author 			Olexiy.Strashko
 */
public class StringUtil {
	public static final String STANDARD_BREAFNAME = "file";
	public static final int MAX_STANDARD_BREAFNAME_LENGTH = 25;
	public static int MAXIMUM_FRACTION_DIGITS = 2;

	public static NumberFormat KB_SIZE_NUMBER_FORMAT = null;
	public static final String KB_SIZE_PATTERN = "###,###,###,###.### Kb";
	
	private static final String DEFAULT_JOIN_SEPARATOR = ", ";

	public static NumberFormat PERCENT_NUMBER_FORMAT = null;
	public static final String PERCENT_PATTERN = "###.##%";

	/**
	 * Tests if string array contain tested string element.
	 * 
	 * @param stringArray  Contain available varialnts for test. 
	 * @param tested String wich is compared with every element in string array.
	 * @return true if tested exists in string array, false - not exists.
	 */
	public static boolean isInStringArray(String[] stringArray,  String tested){
		/* tests is string array contain tested string element */
		//Arrays.binarySearch((Object[])stringArray, (Object)tested, (Comparable)tested);
		tested = tested.trim();
		for (int i = 0; i < stringArray.length; i++ ){
			if (stringArray[i].trim().equalsIgnoreCase(tested)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Join elements of string arrray into one string, elements are divided by separator.
	 * 
	 * @param stringArray  			The string array to join. 
	 * @param separator 			The separator between array elements.
	 * @return <code>String</code>	The joined string. 
	 */
	public static String join(String[] stringArray,  String separator){
		StringBuffer buffer = new StringBuffer();
		if ( separator == null ){
			return separator = DEFAULT_JOIN_SEPARATOR;
		}
		if ( stringArray == null ){
			return buffer.toString(); 
		}
		for (int i = 0; i < stringArray.length; i++ ){
			if (i > 0){
				buffer.append(separator);
			}
			if ( stringArray[i] != null ){
				buffer.append(stringArray[i]);
			}
		}
		return buffer.toString();
	}

	public static String join(Iterator objectIterator,  String separator){
		StringBuffer buffer = new StringBuffer();
		if ( separator == null ){
			return separator = DEFAULT_JOIN_SEPARATOR;
		}
		if ( objectIterator == null ){
			return buffer.toString(); 
		}
		
		Object cur = null;
		boolean isFirst = true;
		for (; objectIterator.hasNext(); ){
			if ( isFirst ){
				isFirst = false;
			}
			else{
				buffer.append(separator);
			}
			cur = objectIterator.next();
			if ( cur != null ){
				buffer.append( cur.toString() );
			}
		}
		return buffer.toString();
	}

	
	/**
	 * Create unique file name. 
	 * FileName = breafName_YYYY_MM_DD_HH_MM_MS.extension.
	 * 
	 * @param extension  			The string extension(extension of filename) 
	 * @param breafName 			The string breaf name of the new file(prefix to filename)
	 * @return <code>String</code>	Unique filename. 
	 */
	public static String createUniqueFileName(String extension, String breafName){
		/* check input params */
		if (breafName == null){
			breafName = StringUtil.STANDARD_BREAFNAME;
		}
		if (extension == null){
			extension = "";
		}
		
		/* generate name, using current time */
		Calendar calendar = Calendar.getInstance();
		StringBuffer fileName = new StringBuffer();
		
		/* create file name prefix( if breafName is too long - it is trimed)
		 * all spaces replaced by underscore. 
		 * */ 
		String prefix = breafName.replaceAll(" ", "_");
		
		if (prefix.length() > MAX_STANDARD_BREAFNAME_LENGTH){
			prefix = prefix.substring(0, MAX_STANDARD_BREAFNAME_LENGTH);
		}
		fileName.append(prefix);
		/* uppend date and time to guarantee uniqueness */ 
		fileName.append("_");
		fileName.append(calendar.get(Calendar.YEAR));
		fileName.append("_");
		fileName.append(calendar.get(Calendar.MONTH)); 
		fileName.append("_");
		fileName.append(calendar.get(Calendar.DAY_OF_MONTH)); 
		fileName.append("_");
		fileName.append(calendar.get(Calendar.HOUR_OF_DAY));
		fileName.append("_");
		fileName.append(calendar.get(Calendar.MINUTE)); 
		fileName.append("_");
		fileName.append(calendar.get(Calendar.MILLISECOND));
		/* uppend extension (if it is not empty) */ 
		if (!extension.equalsIgnoreCase("")){
			fileName.append(".");
			fileName.append(extension);
		}

		return fileName.toString();
	}
	
	/**
	 * Convert java.util.Map into viewable string presentation.
	 * Format: <map.key1> :<map.value1>, <map.key2> :<map.value2>   
	 * 
	 * @param java.util.Map
	 * @return String 			The string presentation of the Map
	 */
	public static String mapToString(Map map){
		StringBuffer buffer = new StringBuffer();
		buffer.append("Size: " + map.size() + ", ");
		String currKey = null;
		for (Iterator i = map.keySet().iterator(); i.hasNext();) {
			currKey = (String) i.next();
			buffer.append(" " + currKey + " :" + map.get(currKey).toString());
		}
		return buffer.toString();
	}


	/**
	 * Convert java.util.Enumeration into viewable string presentation.
	 * Format: elem1, elem2   
	 * 
	 * @param java.util.Enumeration
	 * @return String 			The string presentation of the Enumeration
	 */
	public static String enumerationToString(Enumeration map){
		StringBuffer buffer = new StringBuffer();
		String currKey = null;
		while (map.hasMoreElements()){
			currKey = (String) map.nextElement();
			buffer.append(" " + currKey + " ,");
		}
		return buffer.toString();
	}
    
    /**
     * Create plain text from HTML
     * 
     * @param s
     * @return
     */
    public static String stripTags(String s){
        if(s == null) return "";
        return s.replaceAll("\\<.*?\\>"," ");
    }
    
    /**
     * Create plain text from html. Including formatting tags like br, &nbsp;, 
     * etc 
     * 
     * @param s
     * @return
     */
   public static String stripTagsEx(String s){
        if(s == null) return "";
        s = s.replaceAll("<BR>", "\n");
        s = s.replaceAll("<br>", "\n");
        s = s.replaceAll("<Br>", "\n");
        s = s.replaceAll("<bR>", "\n");
        s = s.replaceAll("&nbsp;", " ");
        return stripTags(s);
    }
   
   	/**
    * Create plain text from html. Including formatting tags like br, &nbsp;, 
    * etc Replaces br and &nbsp to spaces
    * 
    * @param s
    * @return
    */
   	public static String stripTagsEx2(String s){
       if(s == null) return "";
       s = s.replaceAll("<BR>", " ");
       s = s.replaceAll("<br>", " ");
       s = s.replaceAll("<Br>", " ");
       s = s.replaceAll("<bR>", " ");
       s = s.replaceAll("&nbsp;", " ");
       return stripTags(s);
	}
    
    /**
     * Replace all chars from input char array to replacement char.  
     * 
     * @param s
     * @param chars
     * @param replacement
     * @return
     */
    public static String replaceChars(String s, char[] chars, char replacement){
    	if ( (chars == null) || (s == null) ){
    		return s;
    	}
    	for ( int i = 0; i < chars.length; i++ ){
    		s = s.replace(chars[i], replacement);
    	}
    	return s; 
    }
    
    /**
     * Replace all chars out of input char array to replacement char.  
     * 
     * @param s
     * @param chars
     * @param replacement
     * @return
     */
    public static String replaceNotAllowedChars(String s, char[] chars, char replacement){
    	if ( (chars == null) || (s == null) ){
    		return s;
    	}    	
    	
    	String allowedSymbols = new String(chars);
    	char[] filenameChars = s.toCharArray();
    	
    	for ( int i = 0; i < filenameChars.length; i++ ){    		 
    		if (allowedSymbols.indexOf(filenameChars[i]) == -1){
    			s = s.replace(filenameChars[i], replacement);
        	}    		
    	}   	
    	
    	return s; 
    }
    
    /**
     * Create number format for size formatting in KB
     * 
     * @return
     */
	private static NumberFormat getKbSizeNumberFormat(){
	    if ( StringUtil.KB_SIZE_NUMBER_FORMAT == null ){
		    StringUtil.KB_SIZE_NUMBER_FORMAT = new DecimalFormat(
		    	StringUtil.KB_SIZE_PATTERN
			);
		    StringUtil.KB_SIZE_NUMBER_FORMAT.setGroupingUsed(true);
		    StringUtil.KB_SIZE_NUMBER_FORMAT.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
	    }
		return StringUtil.KB_SIZE_NUMBER_FORMAT; 
	}
    
    /**
     * 
     * @param size
     * @return
     */
    public static String formatSizeInKb(long size){
   		double tmpSize = (double)size / (double)1024;
   		return StringUtil.getKbSizeNumberFormat().format(tmpSize);
    }
    
    /**
     * Convert array of strings to Set of strings. 
     * 
     * @param size
     * @return
     */
    public static Set stringArrayToSet(String[] strArray){
        Set set = new HashSet();
        if (strArray == null){
            return set;
        }
        
        for ( int i = 0; i < strArray.length; i++ ){
            set.add( strArray[i] );
        }
        return set;
    }
    
	/**
	 * Validate email
	 * 
	 * @param email
	 * @return
	 */
	public static boolean validateEmail( String email ){
		String regex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*$";
	    return email.matches( regex );
	}
	
	
    /**
     * Create number format for size formatting in KB
     * 
     * @return
     */
	private static NumberFormat getPercentNumberFormat(){
	    if ( StringUtil.PERCENT_NUMBER_FORMAT == null ){
		    StringUtil.PERCENT_NUMBER_FORMAT = new DecimalFormat(
		    	StringUtil.PERCENT_PATTERN
		    );
		    StringUtil.PERCENT_NUMBER_FORMAT.setGroupingUsed(true);
		    StringUtil.PERCENT_NUMBER_FORMAT.setMaximumFractionDigits(2);
	    }
		return StringUtil.PERCENT_NUMBER_FORMAT; 
	}
	
	/**
	 * Format double as percents
	 * 
	 * @param value
	 * @return
	 */
	public static String formatPercent(double value){
		return StringUtil.getPercentNumberFormat().format(value);
	}
	
	/**
	 * Format BigDecimal as percents
	 * 
	 * @param value
	 * @return
	 */
	public static String formatPercent(BigDecimal value){
		return StringUtil.getPercentNumberFormat().format(value);
	}
	
	/**
	 * Generate 7 letters password from srtingKey (email for example)
	 * 
	 * @param email
	 * @return
	 */
	public static String generatePassword(String stringKey) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(stringKey.getBytes("UTF-8"));
			return (new BASE64Encoder()).encode(md.digest()).substring(0, 6);
		} catch(Exception e) {
		    throw new RuntimeException(e);
		}
	}
	
	public static long[] castToArrayOfLong(String[] ids) {
		long[] longIds = new long[ids.length];
		for (int i = 0; i < longIds.length; i++) {
			longIds[i] = Long.parseLong(ids[i]);
		}
		return longIds;
	}

}
