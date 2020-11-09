/*
 * @(#)GetModuleInfoCommand.java       @version	04.02.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.updownload.command;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.module.Module;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.media_catalog.CatalogXmlBuilder;

/**
 * Get all available resources from media catalog and
 * build xml document model. Used to choose resource from 
 * catalog in linking resource process. 
 *
 * @version 	1.0
 * @author 		Olexiy.Strashko
 */
public class GetModuleInfoCommand extends AbstractCommand {

	private static final int UDOWNLOAD_ID = 5;

	private static final Logger logger = Logger.getLogger(GetModuleInfoCommand.class);
	
	/**  */
	public static final String RESULT_SUCCESS = "success";
	
	/**  */ 
	public static final String RESULT_FAILURE = "failure";
	
	/** Object of class File */
	public static final String OUTPUT_XML = "xml";


	/**
	 * 
	 */
	public GetModuleInfoCommand() {
		super();
	}

    /* Note: dom4j must be changed to w3c xml building in this class,
     * because the module class has no dom4j methods
     */
    private Element getModuleDom4jElement(Module module) throws CriticalException {
        try {
            org.w3c.dom.Document doc = Env.createDom();
            doc.appendChild(module.getElement(doc));
            org.dom4j.io.DOMReader xmlReader = new org.dom4j.io.DOMReader();
            Document doc4j = xmlReader.read(doc);
            return doc4j.getRootElement();
        }
        catch (Exception ex) {
            logger.error("-", ex);
            throw new CriticalException(ex);
        }
    }

	/* (non-Javadoc)
	 * @see com.negeso.framework.command.Command#execute()
	 */
	public ResponseContext execute() {
		logger.debug("+");

		RequestContext request = getRequestContext();
		ResponseContext response = new ResponseContext();
		Connection conn = null;
		try{
			if (!SecurityGuard.isUserAutorized(request)) {
				response.setResultName(RESULT_ACCESS_DENIED);
				logger.debug("-");
				return response;
			}
            conn = DBHelper.getConnection();
			Module module = Module.findById(conn, new Long (UDOWNLOAD_ID));
			
			Element page = CatalogXmlBuilder.get().getPageDom4j("Up & Download module info");
		
			page.add(getModuleDom4jElement(module));
			page.add(CatalogXmlBuilder.get().getRepositoryDom4j());
		
			Document doc = DocumentHelper.createDocument(page);
			response.setResultName(RESULT_SUCCESS);
			response.getResultMap().put(OUTPUT_XML, doc);

			logger.debug("-");
			return response;
		}
		catch(CriticalException e){
			response.setResultName(RESULT_FAILURE);
			logger.error("-", e);
			return response;
		}
		catch(Exception e){
			response.setResultName(RESULT_FAILURE);
			logger.error("-", e);
			return response;
		}
        finally {
            DBHelper.close(conn);
        }
	}
}