/*
 * @(#)$Id: UploadEnv.java,v 1.6, 2005-10-19 14:44:44Z, Olexiy Strashko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.updownload.domain;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.UploadProfile;
import com.negeso.module.updownload.ImageStudio;

/**
 *
 * ImageUploadParameters incapsulation
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 7$
 */
public class UploadEnv {
    private static Logger logger = Logger.getLogger( UploadEnv.class );


	public static int DEFAULT_FILE_AMOUNT = 5; 
	
	private static final String INPUT_IS_STRICT = "is_strict";
	private static final String INPUT_HEIGHT = "height";
	private static final String INPUT_WIDTH = "width";
	private static final String INPUT_FILE_SET_AMOUNT = "file_set_amount";
	private static final String INPUT_RESIZE_BY = "resize_by";
	private static final String INPUT_RESIZE_METHOD = "resize_method";
	private static final String INPUT_WORKING_FOLDER = "working_folder";
	private static final String INPUT_WORKING_FOLDER_ID = "working_folder_id";
	private static final String INPUT_UPLOAD_PARAMS_ID = "upload_params_id"; 

	private int width = 0;
	private int height = 0;
	private String resizeMethod;
	private String resizeBy;
	private boolean isStrict;
	private String type;
	private String workingFolder;
	private int fileSetAmount;

	
	private UploadProfile uploadProfile = null;


	public static UploadEnv buildFromRequest(
		RequestContext request, String type, Collection errors)
	{
		UploadEnv form = new UploadEnv();
		
		// type
		form.setType(type);
		
		// width
		Long longTmp = request.getLong(INPUT_WIDTH);
		if ( longTmp != null ){
			form.setWidth(longTmp.intValue());
		}
		
		// heigth
		longTmp = request.getLong(INPUT_HEIGHT);
		if ( longTmp != null ){
			form.setHeight(longTmp.intValue());
		}
		
		// is_strict
		if ( "true".equals(request.getNonblankParameter(INPUT_IS_STRICT)) ){
			form.setStrict( true );
		}
		else{
			form.setStrict( false );
		}

		// resize_method
		String strTmp = request.getNonblankParameter(INPUT_RESIZE_METHOD);
		if ( strTmp != null ){
			form.setResizeMethod(strTmp);
		}
		else{
			form.setResizeMethod(ImageStudio.RESIZE_METHOD_PROPORTIONAL);
		}
		
		// amount of images
		longTmp = request.getLong(INPUT_FILE_SET_AMOUNT);
		if ( longTmp != null ){
			form.setFileSetAmount(longTmp.intValue());
		}
		else{
			form.setFileSetAmount(ImageStudio.DEFAULT_FILE_SET_AMOUNT);
		}

		// resilze_by
		strTmp = request.getNonblankParameter(INPUT_RESIZE_BY);
		if ( strTmp != null ){
			form.setResizeBy(strTmp);
		}
		else{
			form.setResizeBy(ImageStudio.RESIZE_BY_WIDTH);
		}

		// working_folder
		strTmp = request.getNonblankParameter(INPUT_WORKING_FOLDER);
		if ( strTmp != null ){
			form.setWorkingFolder(strTmp);
		}
		else{
			Long wfId = request.getLong(INPUT_WORKING_FOLDER_ID);
			if ( wfId != null ){
				try{
					form.setWorkingFolder(
						Repository.get().getFolder(wfId).getCatalogPath()
					);
				}
				catch (CriticalException e){
					logger.error("-error", e);
				}
			}
		}
		if ( form.getWorkingFolder() == null ){
			// setup root working folder on error
			form.setWorkingFolder(
				Repository.get().getRootFolder().getCatalogPath()
			);
		}

		// Update from UploadParameters
		strTmp = request.getNonblankParameter(INPUT_UPLOAD_PARAMS_ID);
		logger.info("found parameters, preparing UploadForm: " + strTmp);
		if ( strTmp != null ){
			UploadProfile uploadParams = Repository.get().getUploadProfile(strTmp);
			if ( uploadParams != null ){
				logger.info("found parameters, preparing UploadForm: " + strTmp);
				form.setResizeBy(uploadParams.getResizeBy());
				form.setStrict(uploadParams.isStrict());
				form.setWidth(uploadParams.getWidth());
				form.setHeight(uploadParams.getHeight());
				form.setFileSetAmount(uploadParams.getFileSetAmount());
				
				//if ( uploadParams.getWorkingFolder() != null ){
				//	form.setWorkingFolder(uploadParams.getWorkingFolder());
				//}

				form.setUploadParameters(uploadParams);
			}
			else{
				logger.error("!!! Error: Upload parameters not found by Id: " + strTmp);
			}
		}
		return form;
	}
	
	public Element buildElement(Element parent){
		Element formEl = Xbuilder.addEl(parent, "uplad-form", null);
		Xbuilder.setAttr(formEl, "type", this.getType());
		Xbuilder.setAttr(formEl, "width", this.getWidth());
		Xbuilder.setAttr(formEl, "height", this.getHeight());
		Xbuilder.setAttr(formEl, "file-set-amount", this.getFileSetAmount());
		Xbuilder.setAttr(formEl, "resize-method", this.getResizeMethod());
		Xbuilder.setAttr(formEl, "resize-by",  this.getResizeBy());
		Xbuilder.setAttr(formEl, "is-strict",  this.isStrict());
		Xbuilder.setAttr(formEl, "working-folder",  this.getWorkingFolder());
		Xbuilder.setAttr(formEl, "upload-params-id",  this.getUploadProfile().getId());
		return formEl;
	}

	
	public boolean canDeleteSource(){
		if ( "thumbnail".equals(this.getType()) ){
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 */
	public UploadEnv() {
		super();
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
	 * @return Returns the resizeMethod.
	 */
	public String getResizeMethod() {
		return resizeMethod;
	}
	
	/**
	 * @param resizeMethod The resizeMethod to set.
	 */
	public void setResizeMethod(String resizeMethod) {
		this.resizeMethod = resizeMethod;
	}
	
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
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

	
	public long getMaxFileSize() {
		return this.getUploadProfile().getMaxFileSize();
	}

	
	public long getMaxImageWidth() {
		return this.getUploadProfile().getMaxImageWidth();
	}

	
	public long getMaxImageHeight() {
		return this.getUploadProfile().getMaxImageHeight();
	}

	
	/**
	 * @return Returns the uploadParams.
	 */
	public UploadProfile getUploadProfile() {
		if ( this.uploadProfile == null ){
			this.uploadProfile = Repository.get().createUploadProfile();
		}
		return this.uploadProfile;
	}
	
	/**
	 * @param uploadParams The uploadParams to set.
	 */
	public void setUploadParameters(UploadProfile uploadParams) {
		this.uploadProfile = uploadParams;
	}

	public int getFileSetAmount() {
		return this.fileSetAmount;
	}
	

	public void setFileSetAmount(int fileSetAmount) {
		this.fileSetAmount = fileSetAmount;
	}
}
