/*
 * @(#)$Id: ResetCmsCache.java,v 1.4, 2007-04-12 08:57:20Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.command;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.cache.CacheFacade;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.security.SecurityUtils;
import com.negeso.framework.util.Timer;

/**
 * 
 * Force reset all WebCMS cache. This include: 
 * - core (site, resource maps, xsl's, mappings)
 * - modules;
 * 
 * Usage: user to provide hot update of xsl's, properties, ects. 
 * 
 * @author		Olexiy V. Strashko
 * @version		$Revision: 5$
 *
 */
public class ResetCmsCache extends AbstractCommand {
	private static Logger logger = Logger.getLogger( ResetCmsCache.class );


	public ResponseContext execute() {
		ResponseContext response = new ResponseContext();
		
		SecurityUtils.transparentLogon(this.getRequestContext());
		
		if (!SecurityGuard.isContributor(this.getRequestContext().getSession().getUser())) {
			response.setResultName(AbstractCommand.RESULT_ACCESS_DENIED);
		}
		
		
		Timer timer = new Timer();
		timer.start();
		
		Element root = XmlHelper.createPageElement(this.getRequestContext());
		response.getResultMap();
		
		try{
			CacheFacade.getInstance().cleanUpCache();
		} catch (Exception e) {
            logger.error("Throwable", e);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(bos));
            Xbuilder.addEl(root, "error", bos.toString());
		}
        Xbuilder.setAttr(root, "success", "Reset time: " + timer.toString());
		
		response.getResultMap().put(AbstractCommand.OUTPUT_XML, root.getOwnerDocument());
		response.setResultName(AbstractCommand.RESULT_SUCCESS);
		
		return response;
	}
}
