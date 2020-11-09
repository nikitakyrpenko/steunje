/*
 * @(#)$Id: ImageInformation.java,v 1.0, 2004-07-23 11:53:56Z, Olexiy Strashko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog.domain;

/**
 *
 * ImageInfo storing class. Used to store image width and height.
 * 
 * @version		$Revision: 1$
 * @author		Olexiy Strashko
 * 
 */
public class ImageInformation {
    
    private Long width = null; 
    private Long height = null; 
    
    /**
     * 
     */
    public ImageInformation(long width, long height) {
        super();
        this.width = new Long(width);
        this.height = new Long(height);
    }

    /**
     * @return Returns the height.
     */
    public Long getHeight() {
        return height;
    }
    /**
     * @param height The height to set.
     */
    public void setHeight(Long height) {
        this.height = height;
    }
    /**
     * @return Returns the width.
     */
    public Long getWidth() {
        return width;
    }
    /**
     * @param width The width to set.
     */
    public void setWidth(Long width) {
        this.width = width;
    }
}
