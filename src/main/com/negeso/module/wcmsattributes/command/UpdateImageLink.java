/*
 * @(#)$Id: $
 *
 * Copyright (c) 2009 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.wcmsattributes.command;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.wcmsattributes.domain.Image;

/**
 * 
 * @TODO
 * 
 * @author		Ekaterina Dzhentemirova
 * @version		$Revision: $
 *
 */
public class UpdateImageLink extends AbstractCommand {
	private static Logger logger = Logger.getLogger(UpdateImageLink.class);

	// for tree list support
	public static final String INPUT_IDIMG = "idImg";
	public static final String INPUT_ALT = "alt";
	public static final String INPUT_LINK = "link";
	public static final String INPUT_TARGET = "target";
	public static final String INPUT_ACATION = "action";
	
	Long id = null;
	String action = null;
	String alt = null;
	String link = null;
	String target = null;
	
	/* (non-Javadoc)
	 * @see com.negeso.framework.command.Command#execute()
	 */
	public ResponseContext execute() {
		logger.debug("+");
	    
		RequestContext request = getRequestContext();
		ResponseContext response = new ResponseContext();

		// pass security check
		if (!SecurityGuard.isContributor(request.getSession().getUser())) {
			response.setResultName(RESULT_ACCESS_DENIED);
			logger.debug("-");
			return response;
		}
		id = request.getLong(INPUT_IDIMG);
		action=request.getString(INPUT_ACATION, null);
		alt=request.getString(INPUT_ALT, null);
		link=request.getString(INPUT_LINK, null);
		target=request.getString(INPUT_TARGET, null);
		Element page = null;
		Image image =null;
		Connection con = null;
		try{
			con = DBHelper.getConnection();
			image = Image.findById(con, id);
			if("update".equalsIgnoreCase(action)){
				image.setAlt(alt);
				image.setLink(link);
				image.setTarget(target);
				image.update(con);				
			}
			page = buildResultXml(request, image);
		}
		catch(SQLException e){
			logger.error("Object: " + UpdateImageLink.INPUT_IDIMG +
                    " not found by id: " + id, e);
		}
		catch (CriticalException e) {
			response.setResultName(RESULT_FAILURE);
			return response;
        }
		finally{
			DBHelper.close(con);
		}

		response.setResultName(RESULT_SUCCESS);
		response.getResultMap().put(OUTPUT_XML, page.getOwnerDocument());

		logger.debug("-");
		return response;
	}


	private Element buildResultXml(RequestContext request, Image image) {
		Element page;
		page = XmlHelper.createPageElement(request);
		Element elementPage = Xbuilder.addEl(page, "image", null);			
		Xbuilder.setAttr(elementPage,"img-id",image.getId());
		if (image.getAlt()!=null)
			Xbuilder.setAttr(elementPage,"title",image.getAlt());
		if (image.getLink()!=null){
			Xbuilder.setAttr(elementPage,"link",image.getLink());
			Xbuilder.setAttr(elementPage,"target",image.getTarget()+"");
		}
		return page;
	}
	
	
    /**
     * @return Returns the link.
     */
    public String getLink() {
        return link;
    }
    /**
     * @param mode The mode to link.
     */
    public void setLink(String link) {
        this.link = link;
    }

	/**
	 * @return Returns the target.
	 */
	public String getTarget() {
		return target;
	}
	/**
	 * @param isStrict The isStrict to set.
	 */
	public void setTarget(String target) {
		this.target = target;
	}
}
