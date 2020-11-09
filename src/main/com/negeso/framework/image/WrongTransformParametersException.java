/*
 * @(#)WrongTransformParametersException.java       @version	30.01.2004
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
 * Thown in case of wrong image transformation parameters assignment.
 *
 * @version 	30.01.2004
 * @author 		Yuri Shanoilo
 */
public class WrongTransformParametersException extends ImageTransformException {
	public WrongTransformParametersException() {}
	public WrongTransformParametersException(String msg) {
		super(msg);
		text = msg;
	}
	public String text() {return text;}
	private String text = "";
}
