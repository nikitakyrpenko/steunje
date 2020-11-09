/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.flipbook;

import java.io.File;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public abstract class AbstractFilePagesReader {
	
	public abstract int getPagesCount();
	public abstract void finish();
	public abstract File getPage(File file, int i);
	
	public int getWidth(){
		return 1008;
	}
	
	public int getHeight() {
		return 1332;
	}
}

