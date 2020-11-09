/*
 * @(#)$Id: GetImageUploaderFace.java,v 1.8, 2005-06-06 13:04:34Z, Stanislav Demchenko$
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
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DOMWriter;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Resource;
import com.negeso.module.updownload.UpdownloadXmlBuilder;

/**
 *
 * Get image uploader face view
 * 
 * @version		$Revision: 9$
 * @author		Olexiy Strashko
 * 
 */
public class GetImageUploaderFace extends AbstractCommand {
	private static Logger logger = Logger.getLogger(GetImageUploaderFace.class);
	
	/**  */
	public static final String RESULT_SUCCESS = "success";
	
	/**  */ 
	public static final String RESULT_FAILURE = "failure";
	
	/** Object of class File */
	public static final String OUTPUT_XML = "xml";

    /* (non-Javadoc)
     * @see com.negeso.framework.command.Command#execute()
     */
    public ResponseContext execute() {
		RequestContext request = getRequestContext();
		ResponseContext response = new ResponseContext();

		// pass security check
		if ( !request.getSessionData().isAuthorizedUser() ) {
			response.setResultName(RESULT_ACCESS_DENIED);
			logger.debug("-");
			return response;
		}
		
		Element page = UpdownloadXmlBuilder.get().getPageDom4j("Image uploader");
		page.addAttribute("type", "face");
			
		page.add(this.getUploaderImageOption(request));
		page.add(this.getUploaderThumbnailOption(request));

		page.add( Repository.get().getAvailableFoldersElement(
		        request.getSession().getUser()
		) );

		String errorMessage = request.getNonblankParameter("error_message");
		if ( errorMessage != null ){
			page.add(
				UpdownloadXmlBuilder.get().getErrorMessageDom4j(errorMessage)
			);
		}
		
		response.setResultName(RESULT_SUCCESS);
		
		
		
		
		Document doc = DocumentHelper.createDocument(page);
		org.w3c.dom.Document domDoc = null;
		try{
		    DOMWriter writer = new DOMWriter();
		    domDoc = writer.write(doc);
		}
		catch(DocumentException e){
			response.setResultName(RESULT_FAILURE);
			logger.error("-", e);
			return response;
		}
		response.getResultMap().put(OUTPUT_XML, domDoc);

        return response;
    }

	/**
	 * @param thumbWidth
	 * @param thumbHeight
	 * @param workingFolder
	 */
	private Element getUploaderImageOption(RequestContext request) 
	{
		Element imageOption = UpdownloadXmlBuilder.get().createElement("image-option");
		Element imageChooser = Repository.get().getAvailableFilesElement(
		        request.getSession().getUser(),
		        Resource.IMAGE_TYPE
		);
		imageChooser.addAttribute("type", "existentFile");
		imageOption.add( imageChooser );
		return imageOption;
	}

	/**
	 * @param thumbWidth
	 * @param thumbHeight
	 * @param workingFolder
	 */
	public Element getUploaderThumbnailOption(RequestContext request) 
	{
		Element thumbnailOption = UpdownloadXmlBuilder.get().createElement("thumbnail-option"); 

		Element large = Repository.get().getAvailableFilesElement(
		        request.getSession().getUser(),
		        Resource.IMAGE_TYPE
		);
		large.addAttribute("type", "existentLarge");
		thumbnailOption.add(large);
		
		Element small = large.createCopy();
		
		small.addAttribute("type", "existentSmall");
		thumbnailOption.add(small);

		return thumbnailOption;
	}
}
