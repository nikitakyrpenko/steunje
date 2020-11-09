package com.negeso.module.search;

import org.apache.lucene.document.Document;

public interface DocumentIterator {
	
	public static final String TYPE_LAW 					= "0";
    public static final String TYPE_LEGISLATION 			= "1";
    public static final String TYPE_PROCEDURAL_DOCUMENT 	= "2";
    public static final String TYPE_JURISPRUD 				= "3";
    public static final String TYPE_MUNICIPAL 				= "4";
    public static final String TYPE_COMMENTS 				= "5";
    public static final String TYPE_TOELICHTING				= "6";
    public static final String TYPE_WORKPROCESSES			= "7";
    public static final String TYPE_NORMEN					= "8";
	   
	/**
	 * @return 	org.apache.lucene.document.Document; null if the last
	 * 			iterable Document is reached
	 */
	public Document next ();

}