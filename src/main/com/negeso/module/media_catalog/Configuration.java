/*
 * @(#)$Id: Configuration.java,v 1.13, 2007-01-15 17:38:11Z, Svetlana Bondar$
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;


import com.negeso.framework.Env;


/**
 * Media catalog configuration
 *
 * @version 	$Revision: 14$
 * @author 		Olexiy.Strashko
 */
public class Configuration {
    
    private static Logger logger = Logger.getLogger(Configuration.class);

    public static final String I18N_DICTIONARY_FILE_NAME = "modules/media_catalog/dict_commons.xsl";

	public static String[] allExtensions = new String[0];

	public static String[] imageTypeExtensions = {
		"gif", "jpg", "jpeg", "png"
	};

	public static String[] iconTypeExtensions = {
		"ico"
	};
	
	public static String[] textTypeExtensions = {
		"txt", "log"
	};

	public static String[] htmlTypeExtensions = {
		"html", "htm"
	};

	public static String[] documentTypeExtensions 	= {
		"doc", "docx", "pdf", "rtf", "xls", "xlsx", "ppt", "pptx", "mpeg", "mpg", "avi", "swf","ico"
	};

	public static String[] flashTypeExtensions 	= {
		"swf"
	};

	public static String DATE_FORMAT_PATTERN = "dd.MM.yyyy HH:mm:ss";
	
	public static long DEFAULT_REPOSITORY_SIZE_MB = 40; 				// 40 MB
	public static long DEFAULT_MAX_FILE_SIZE_KB = 1500; 				// 1500 KB
	private static final int DEFAULT_MAX_IMAGE_WIDTH = 1200;			// pix
	private static final int DEFAULT_MAX_IMAGE_HEIGHT = 1200;			// pix
	public static long MB_SIZE_SCALER = 1024 * 1024;					// Byte To MB 
	public static long KB_SIZE_SCALER = 1024;							// Byte To KB 
	private static final int DEFAULT_MASS_UPLOAD_AMOUNT = 5;			// Files admount in Mass Uploader

	/* PRIVATE MEMEBERS */
	private static final String REPOSITORY_SIZE_ENTRY = "media.catalog.size";
	private static final String MAX_FILE_SIZE_ENTRY = "media.catalog.maxFileSize";
	
	public static final String EMAIL_TO = "media.catalog.emailTo";
	public static final String EMAIL_FROM = "media.catalog.emailFrom";
	public static final String EMAIL_SUBJECT = "media.catalog.emailSubject";
	public static final String FIRST_EMAIL_BODY = "media.catalog.firstEmailBody";
	public static final String SECOND_EMAIL_BODY = "media.catalog.secondEmailBody";	
	public static final String USED_FREE_SPACE = "media.catalog.usedFreeSpace";
	public static final String SEND_EMAIL = "media.catalog.isSendEmail";
	
	private SimpleDateFormat dateFormat = null;
	
	private long repositorySize = DEFAULT_REPOSITORY_SIZE_MB * MB_SIZE_SCALER;
	private long maxFileSize = DEFAULT_MAX_FILE_SIZE_KB * KB_SIZE_SCALER;

	/**
	* Holds singleton instance
	*/
	private static Configuration instance = null;


	/**
	 * 
	 */
	private Configuration() {
		super();
	}
	
	/**
	* Returns the singleton instance.
	* @return	the singleton instance
	*/
	static public Configuration get() {
		if (instance == null) {
			instance = new Configuration();
		}
		return instance;
	}

	public DateFormat getDateFormat(){
		if (this.dateFormat == null){
			this.dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
		}
		return this.dateFormat; 
	}

	/**
	 * Get Repository size
	 * @return
	 */
	public long getRepositorySize(){
	    try{
			this.repositorySize = Long.parseLong(
			    Env.getProperty( REPOSITORY_SIZE_ENTRY )
			) * MB_SIZE_SCALER;
	    }
	    catch(Exception e){
	        logger.error("Error reading config: media.catalog.maxFileSize");
	    }
		return this.repositorySize; 
	}

	/**
	 * Get Maximum file size. 
	 * 
	 * @return
	 */
	public long getMaxFileSize(){
	    try{
			this.maxFileSize = Long.parseLong(Env.getProperty(
				MAX_FILE_SIZE_ENTRY
			)) * KB_SIZE_SCALER;
	    }
	    catch(Exception e){
	        logger.error("Error reading config: media.catalog.maxFileSize");
	    }
		return this.maxFileSize; 
	}
	
	
	public int getPopupWidthAppendix(){
	    return Env.getIntProperty("media.catalog.popupWidth.appendix", 0);
	}
	
	public int getPopupHeightAppendix(){
	    return Env.getIntProperty("media.catalog.popupHeight.appendix", 0);
	}
	
	public boolean isXmlCached(){
		return "true".equals( Env.getProperty("media.catalog.cache.xsl", "false") );
	}
	
	public int getMaxImageWidth(){
	    return Env.getIntProperty("media.catalog.maxImageWidth", DEFAULT_MAX_IMAGE_WIDTH);
	}

	public int getMaxImageHeight(){
	    return Env.getIntProperty("media.catalog.maxImageHeight", DEFAULT_MAX_IMAGE_HEIGHT);
	}

	public int getMassUploadAmount(){
	    return Env.getIntProperty("media.catalog.massUploadAmount", DEFAULT_MASS_UPLOAD_AMOUNT);
	}

	public String[] getImageTypeExtensions() {
		return imageTypeExtensions;
	}
}
