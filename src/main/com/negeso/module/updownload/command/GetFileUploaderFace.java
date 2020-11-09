/*
 * @(#)$Id: GetFileUploaderFace.java,v 1.29, 2007-02-09 12:08:05Z, Vyacheslav Zapadnyuk$
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.updownload.command;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.util.RequestParametersException;
import com.negeso.module.media_catalog.Configuration;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.UploadProfile;

/**
 * Get all available resources from media catalog and
 * build xml document model. Used to choose resource from 
 * catalog in linking resource process. 
 *
 * @version 	$Revision: 30$
 * @author 		Olexiy.Strashko
 */
public class GetFileUploaderFace extends AbstractCommand {
	private static Logger logger = Logger.getLogger(GetFileUploaderFace.class);

	
	// FILE INPUT MODES
	public static final String DEFAULT_MODE = "file";  
	public static final String IMAGE_MODE = "image";
	public static final String THUMBNAIL_MODE = "thumbnail";
	public static final String DOC_MODE = "doc";  
	public static final String FLASH_MODE = "flash";  
	public static final String BANNER_MODE = "banner";  
	
	// FILE INPUT SOURCES
	public static final String INPUT_SOURCE_UPLOADED = "disk";  
	public static final String INPUT_SOURCE_EXISTENT = "catalog";
	
	
	public static final String INPUT_WORKING_FOLDER = "workfolder";
	public static final String INPUT_MODE = "mode";

	// IMAGE UPLOADING CONSTS
    public static final String INPUT_FORCE_RESIZE_MODE = "force_resize_mode";
    public static final String INPUT_IMAGE_UPLOAD_TYPE = "type";

    public static final String INPUT_IMAGE_WIDTH = "width";
    public static final String INPUT_IMAGE_HEIGHT = "height";

    public static final String INPUT_PROFILE_ID = "profile_id";
    
    public static final String IMAGE_UPLOAD_IS_STRICT = "is_strict";
    public static final String IMAGE_UPLOAD_TYPE_STRICT = "strict";
    public static final String IMAGE_UPLOAD_TYPE_BY_WIDTH = "by_width";
    public static final String IMAGE_UPLOAD_TYPE_BY_HEIGHT = "by_height";
    public static final String DEFAULT_IMAGE_UPLOAD_TYPE = IMAGE_UPLOAD_TYPE_STRICT;
//    public static final String FLASH_QUANTITY = "flash_quantity";
	/**
	 * 
	 */
	public GetFileUploaderFace() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.negeso.framework.command.Command#execute()
	 */
	public ResponseContext execute() {

		RequestContext request = getRequestContext();
		ResponseContext response = new ResponseContext();
		
		logger.debug("+");
		// pass security check
		if (!SecurityGuard.isContributor(request.getSession().getUser())) {
			response.setResultName(RESULT_ACCESS_DENIED);
			logger.debug("-");
			return response;
		}

		Element page = null;
		// Default, file mode 
		String title = "Upload file"; 
		String[] extensions = Configuration.allExtensions; 

		try{
			// read mode			
			String mode = request.getString("mode", DEFAULT_MODE);
			
            String workingFolder = null;
            if ( request.getNonblankParameter(INPUT_PROFILE_ID) != null ){
                UploadProfile profile = Repository.get().getUploadProfile(
                    request.getNonblankParameter(INPUT_PROFILE_ID)
                );
                if ( profile != null ){
                    workingFolder = profile.getWorkingFolder();
                }
            }
            
            // read working folder			
            if ( workingFolder == null ){
    			workingFolder = request.getString(
			        INPUT_WORKING_FOLDER,
			        Repository.get().getRootPath()
    			);
            }

			page = XmlHelper.createPageElement(request);
			Element option = Repository.get().getXBuilder().getFileUploadOption(
			        page,
			        workingFolder,
			        mode 
			); 
			
			// Image/Thumbnail upload mode 
			if ( IMAGE_MODE.equalsIgnoreCase(mode) ||  
			     THUMBNAIL_MODE.equalsIgnoreCase(mode))	
			{
				if ( THUMBNAIL_MODE.equalsIgnoreCase(mode) ){
					title = "Upload thumbnail image";
				}
				else {
					title = "Upload image";
				}
				GetFileUploaderFace.checkImageParameters(request, option);				
				extensions = Configuration.imageTypeExtensions;

			}
			// Document upload mode
			else if ( mode.equalsIgnoreCase(DOC_MODE) )
			{
				title = "Upload document";		
				extensions = Configuration.documentTypeExtensions;
			}
			
			
			// Flash upload mode
			else if ( mode.equalsIgnoreCase(FLASH_MODE) )
			{
				title = "Upload flash";		
				extensions = Configuration.flashTypeExtensions;
				GetFileUploaderFace.checkImageParameters(request, option);				
			} else if (mode.equals(BANNER_MODE)) {
				checkBannerParameters(request, option);				
			}

			Xbuilder.setAttr(page, "title", title);		
			Repository.get().getXBuilder().getAvailableFilesElement(
					page,
					request.getSession().getUser(),
					extensions,
					true
			);
			
			Repository.get().getXBuilder().getAvailableFoldersElement(
			    page,
			    request.getSession().getUser()
			);
			
			String errorMessage = request.getString("error_message", null);
			if ( errorMessage != null ){
			    Repository.get().getXBuilder().getErrorMessage(
			        page,
			        errorMessage
			    );
			}
			

		}
		catch (RequestParametersException e){
		    Repository.get().getXBuilder().getErrorMessage( page, e.getMessage() );
		} 
		catch (CriticalException e) {
			response.setResultName(RESULT_FAILURE);
			return response;
        }

		response.setResultName(RESULT_SUCCESS);
		response.getResultMap().put(OUTPUT_XML, page.getOwnerDocument());

		logger.debug("-");
		return response;
	}

	/**
	 * Check image uploading parameters, according to image processing type
	 * 
	 * @param type
	 * @param width
	 * @param height
	 * @throws RequestParametersException
	 */
	public static void checkImageParameters(RequestContext request, Element option) 
		throws RequestParametersException
	{
		//check parameters
		Long width = request.getLong(INPUT_IMAGE_WIDTH);
		Long height = request.getLong(INPUT_IMAGE_HEIGHT);
		String type = request.getString(
		        INPUT_IMAGE_UPLOAD_TYPE , 
		        DEFAULT_IMAGE_UPLOAD_TYPE
		);
		
	    if (IMAGE_UPLOAD_TYPE_STRICT.equalsIgnoreCase(type)){
	        if ( width == null ){
	    		throw new RequestParametersException( "Unable to proceed. Image width is missing" );
	        }
	        if ( height == null ){
	    		throw new RequestParametersException( "Unable to proceed. Image height is missing" );
	        }
	    }
	   if (IMAGE_UPLOAD_TYPE_BY_WIDTH.equalsIgnoreCase(type)){
	        if ( width == null ){
	    		throw new RequestParametersException( "Unable to proceed. Image width is missing" );
	        }
	    }
	    if (IMAGE_UPLOAD_TYPE_BY_HEIGHT.equalsIgnoreCase(type)){
	        if ( height == null ){
	    		throw new RequestParametersException( "Unable to proceed. Image height is missing" );
	        }
	    }

	    Xbuilder.setAttr(option, "type", type);
		Xbuilder.setAttr(option, "width", width);
		Xbuilder.setAttr(option, "height", height);

		String forceResizeMode = request.getNonblankParameter(INPUT_FORCE_RESIZE_MODE);
		if ( forceResizeMode != null ){
            Xbuilder.setAttr(option, "force-resize-mode", forceResizeMode);
		}

		String isStrict = request.getNonblankParameter(IMAGE_UPLOAD_IS_STRICT);
		if ( isStrict != null ){
            Xbuilder.setAttr(option, "is-strict", isStrict);
		}
	    
	}

	public static void checkBannerParameters(RequestContext request, Element option) 
	throws RequestParametersException
	{
		checkImageParameters(request, option);
	}
}
