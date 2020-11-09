/*
 * @(#)$Id: Configuration.java,v 1.0, 2005-01-21 17:35:37Z, Olexiy Strashko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.store_locator;

import com.negeso.framework.Env;

/**
 *
 * Store locator configuration
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 1$
 */
public class Configuration {
	private int mapImageWidth = -1;
	private int mapImageHeight = -1;
	
	private static Configuration instance = null;
	
	private Configuration(){};
	
	public static Configuration get(){
		if ( Configuration.instance == null ){
			Configuration.instance = new Configuration();
		}
		return Configuration.instance;
	}

	public int getMapImageWidth(){
		if ( this.mapImageWidth == -1 ){
			this.mapImageWidth = Env.getIntProperty("store-locator.mapImage.width", 0);
		}
		return this.mapImageWidth;
	}

	public int getMapImageHeight(){
		if ( this.mapImageHeight == -1 ){
			this.mapImageHeight = Env.getIntProperty("store-locator.mapImage.height", 0);
		}
		return this.mapImageHeight;
	}
}
