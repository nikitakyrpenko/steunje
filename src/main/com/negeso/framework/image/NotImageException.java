/*
 * @(#)NotImageException.java       @version	30.01.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.image;

/**
 * Thown in the case then passed to tramsformation file - is not an image .
 *
 * @version 	30.01.2004
 * @author 	    Yuri Shanoilo
 */

public class NotImageException extends ImageTransformException {
	public NotImageException() {}
	public NotImageException(String msg) {
		super(msg);
		text = msg;
	}
	public String text() {return text;}
	private String text = "";
}
