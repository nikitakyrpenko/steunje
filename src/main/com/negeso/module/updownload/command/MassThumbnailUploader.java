/*
 * @(#)$Id: MassThumbnailUploader.java,v 1.6, 2005-10-19 14:44:01Z, Olexiy Strashko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.updownload.command;

import java.io.File;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.util.RequestParametersException;
import com.negeso.framework.util.StringUtil;
import com.negeso.module.media_catalog.Configuration;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.RepositoryException;
import com.negeso.module.media_catalog.domain.ImageResource;
import com.negeso.module.updownload.ImageStudio;
import com.negeso.module.updownload.domain.UploadEnv;

/**
 *
 * Mass thumbnail uploader
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 7$
 */
public class MassThumbnailUploader extends AbstractCommand {
    private static Logger logger = Logger.getLogger( MassThumbnailUploader.class );
	
	private static String INPUT_ACTION = "action";
	
	/* (non-Javadoc)
	 * @see com.negeso.framework.command.Command#execute()
	 */
	public ResponseContext execute() {
		RequestContext request = this.getRequestContext();
		ResponseContext response = new ResponseContext();
		
		// pass security check
//		if ( !SecurityGuard.isContributor(request.getSession().getUser()) ){
//			response.setResultName( AbstractCommand.RESULT_ACCESS_DENIED );
//			return response;
//		}

		String action = request.getNonblankParameter(INPUT_ACTION);
		Element page = null;
		try{
			page = XmlHelper.createPageElement(request);	
			UploadEnv form = UploadEnv.buildFromRequest(request, "thumbnail", null);
			if ("upload".equals(action)){
				this.doUpload(request, form, page);
				Element formEl = (Element)page.getLastChild();
				Xbuilder.setAttr(formEl, "render-mode", "result");
			}
			else{
				this.renderForm(form, page);
				Element formEl = (Element)page.getLastChild();
				Xbuilder.setAttr(formEl, "render-mode", "show-form");
				
			}
			
			response.getResultMap().put(OUTPUT_XML, page.getOwnerDocument());
			response.setResultName(RESULT_SUCCESS);
		}
		catch( AccessDeniedException e ){
			logger.warn("-access denied", e);
			this.getErrors().add(e.getMessage());
		} 
		catch (CriticalException e) {
			response.setResultName( AbstractCommand.RESULT_FAILURE );
		}
		
		return response;
	}

	/**
	 * 
	 * @param form
	 * @param page
	 */
	private void renderForm(UploadEnv form, Element page) {
		Element params = form.buildElement(page);
		
		Element fileSet = Xbuilder.addEl(params, "file-set", null);
		Element fileEl = null;
		for ( int i = 0; i < form.getFileSetAmount(); i++ ){
			fileEl = Xbuilder.addEl(fileSet, "file", null);
			Xbuilder.setAttr(fileEl, "id", "" + i);
		}
	}

	/**
	 * 
	 * @param request
	 * @throws RequestParametersException
	 * @throws AccessDeniedException
	 */
	private void doUpload(RequestContext request, UploadEnv form, Element parent) 
		throws AccessDeniedException 
	{
		Element params = form.buildElement(parent);

		Element fileSet = Xbuilder.addEl(params, "file-set", null);
		Element fileEl = null;

		File curFile = null;
		File thumbnailFile = null;
		FileItem curFileItem = null;
		for ( int i = 0; i < form.getFileSetAmount(); i++ ){
			//logger.info("File: " + i);
			curFileItem = request.getFile( "file" + "_" + i );
			if (curFileItem == null) {
				continue;
			}
			if ( curFileItem.getSize() == 0 ){
				//empty file
				continue;
			}
			
			fileEl = Xbuilder.addEl(fileSet, "file", null);
			Xbuilder.setAttr(fileEl, "id", "" + i);

			try{
				curFile = Repository.get().saveFileItemSafe(
				    request.getSession().getUser(),    
					curFileItem, 
					form.getWorkingFolder(),
					Configuration.imageTypeExtensions,
					true,
					new Long(form.getMaxFileSize()) 
				);
				
				if ( curFile == null ) {
					// empty file
					continue;
				}
				
				logger.info("file: " + curFile.getName());

				ImageStudio studio = new ImageStudio(); 
				thumbnailFile = studio.processStrictByWidthImage(
					curFile, form 
				);
				
				if ( studio.hasErrors() ){
					Xbuilder.setAttr(fileEl, "result", "error");
					Xbuilder.setAttr(fileEl, "image-name", curFileItem.getName());
					fileEl.appendChild(
						XmlHelper.createErrorsElement(
							fileEl.getOwnerDocument(), studio.getErrors() 
						)
					);
					curFileItem.delete();
				}
				else{
					if ( thumbnailFile != null ){
						this.buildImageResult(fileEl, curFile, thumbnailFile);
					}
					else{
						logger.error("!!!! Thumbnail is null!!!");
					}
				}
			}
			catch( RequestParametersException e ){
				logger.info("Exception -" + e.getMessage() + ".file(" + i + ") " + curFileItem.getName());
				this.getErrors().add( e.getMessage() ); 
				Xbuilder.setAttr(fileEl, "result", "error");
				Xbuilder.setAttr(fileEl, "error", e.getMessage());
				Xbuilder.setAttr(fileEl, "image-name", curFileItem.getName());
			}
		}
	}
	
	/**
	 * 
	 * @param parent
	 * @param image
	 * @param thumbnail
	 * @return
	 */
	private Element buildImageResult(Element parent, File image, File thumbnail){
		try{
			Xbuilder.setAttr(parent, "result", "OK");

			ImageResource imageRes = Repository.get().getImageResource(image);
			Xbuilder.setAttr(parent, "image-name", imageRes.getName());
			Xbuilder.setAttr(parent, "image-path", imageRes.getCatalogPath());
			Xbuilder.setAttr(parent, "image-width", imageRes.getWidth());
			Xbuilder.setAttr(parent, "image-height", imageRes.getHeight());
			Xbuilder.setAttr(parent, "image-size", StringUtil.formatSizeInKb(imageRes.getSize()) );
			
			imageRes = Repository.get().getImageResource(thumbnail);
			Xbuilder.setAttr(parent, "th-name", imageRes.getName());
			Xbuilder.setAttr(parent, "th-path", imageRes.getCatalogPath());
			Xbuilder.setAttr(parent, "th-width", imageRes.getWidth());
			Xbuilder.setAttr(parent, "th-height", imageRes.getHeight());
			Xbuilder.setAttr(parent, "th-size", StringUtil.formatSizeInKb(imageRes.getSize()) );
		}
		catch(RepositoryException e){
			logger.error("-error", e);
		}
		return parent;
	}
}
