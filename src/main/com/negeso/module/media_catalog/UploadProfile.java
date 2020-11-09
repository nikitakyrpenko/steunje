/*
 * @(#)$Id: UploadProfile.java,v 1.0, 2005-03-04 11:54:13Z, Olexiy Strashko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog;

import com.negeso.module.updownload.ImageStudio;


/**
 *
 * Upload parameters
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 1$
 */
public class UploadProfile {

	private String id = null;
	private int width = 0;
	private int height = 0;
	private int fileSetAmount = 5;
	private boolean isStrict = false;
	private String workingFolder = null;
	private String resizeBy = ImageStudio.RESIZE_BY_WIDTH;
	private long maxFileSize = 5;
	private long maxImageWidth = 5;
	private long maxImageHeight = 5;


	/**
	 * 
	 */
	public UploadProfile() {
		super();
	}

	/**
	 * @return Returns the fileSetAmount.
	 */
	public int getFileSetAmount() {
		return fileSetAmount;
	}
	
	/**
	 * @param fileSetAmount The fileSetAmount to set.
	 */
	public void setFileSetAmount(int fileSetAmount) {
		this.fileSetAmount = fileSetAmount;
	}
	
	/**
	 * @return Returns the height.
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * @param height The height to set.
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
	/**
	 * @return Returns the maxFileSize.
	 */
	public long getMaxFileSize() {
		return maxFileSize;
	}
	
	/**
	 * @param maxFileSize The maxFileSize to set.
	 */
	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
	
	/**
	 * @return Returns the maxImageHeight.
	 */
	public long getMaxImageHeight() {
		return maxImageHeight;
	}
	
	/**
	 * @param maxImageHeight The maxImageHeight to set.
	 */
	public void setMaxImageHeight(long maxImageHeight) {
		this.maxImageHeight = maxImageHeight;
	}
	
	/**
	 * @return Returns the maxImageWidth.
	 */
	public long getMaxImageWidth() {
		return maxImageWidth;
	}
	
	/**
	 * @param maxImageWidth The maxImageWidth to set.
	 */
	public void setMaxImageWidth(long maxImageWidth) {
		this.maxImageWidth = maxImageWidth;
	}
	
	/**
	 * @return Returns the width.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width The width to set.
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	
	/**
	 * @return Returns the workingFolder.
	 */
	public String getWorkingFolder() {
		return workingFolder;
	}
	
	/**
	 * @param workingFolder The workingFolder to set.
	 */
	public void setWorkingFolder(String workingFolder) {
		this.workingFolder = workingFolder;
	}

	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return Returns the isStrict.
	 */
	public boolean isStrict() {
		return isStrict;
	}

	/**
	 * @param isStrict The isStrict to set.
	 */
	public void setStrict(boolean isStrict) {
		this.isStrict = isStrict;
	}

	/**
	 * @return Returns the resizeBy.
	 */
	public String getResizeBy() {
		return resizeBy;
	}

	/**
	 * @param resizeBy The resizeBy to set.
	 */
	public void setResizeBy(String resizeBy) {
		this.resizeBy = resizeBy;
	}
	
	/**
	 * 
	 * @param size
	 */
	public void setMaxFileSizeKb( long size ) {
		this.setMaxFileSize( 1024 * size );
	}
}
