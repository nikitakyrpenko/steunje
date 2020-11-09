/*
 * @(#)$Id: TestCommand.java,v 1.4, 2005-06-06 13:05:12Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog.command;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;

/**
 *
 * MC Test command
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 5$
 */
public class TestCommand extends AbstractCommand {
	private static Logger logger = Logger.getLogger( TestCommand.class );

	/* (non-Javadoc)
	 * @see com.negeso.framework.command.Command#execute()
	 */
	public ResponseContext execute() {

		RequestContext request = getRequestContext();
		ResponseContext response = new ResponseContext();

		if ( !SecurityGuard.isContributor( request.getSession().getUser()) )	{
			response.setResultName(RESULT_ACCESS_DENIED);
			logger.debug("-");
			return response;
		}

		try{
			StringBuffer buffer = new StringBuffer();
			
			Folder root = Repository.get().getRootFolder();
			Folder folder = Repository.get().createFolder(root, "mc_test_folder", false);
			buffer.append(" Folder: " + folder.getCatalogPath() + "\n" );
			folder = Repository.get().createFolder(root, "m!c te%st prijscategorieën", false);
			buffer.append(" Folder: " + folder.getCatalogPath() + "\n"  );
			
			logger.info(" Test folder security" + "\n" );
			Folder newFolder = Repository.get().createFolder(root, "mc test secure", new Long(1));
			logger.info(
				" Created folder: path: " + folder.getCatalogPath() +
				" id: " + folder.getId() + 
				" containerId: " + folder.getContainerId() +
				"\n" 
			);
			
			folder.setContainerId(new Long(2));
			logger.info(
				" New cid folder: path: " + folder.getCatalogPath() +
				" id: " + folder.getId() + 
				" containerId: " + folder.getContainerId() +
				"\n" 
			);
		
			
			folder = Repository.get().getFolder( newFolder.getId() );
			logger.info(
				" Test folder: path: " + folder.getCatalogPath() +
				" id: " + folder.getId() + 
				" containerId: " + folder.getContainerId() + 
				"\n" 
			);
			folder = Repository.get().getFolder( newFolder.getId() );
			logger.info(
				" Test folder: path: " + folder.getCatalogPath() +
				" id: " + folder.getId() + 
				" containerId: " + folder.getContainerId() +
				"\n" 
			);
			
			folder = Repository.get().getFolder( new Long(6) );
			logger.info(
				" Test folder: path: " + folder.getCatalogPath() +
				" id: " + folder.getId() + 
				" containerId: " + folder.getContainerId() +
				"\n" 
			);


            Repository rep = Repository.get();
            logger.info(
                " Root path: " + rep.getRootPath() +
                " Real path for root path: " + rep.getRealPath( rep.getRootPath() ) + 
                " Real path for '': " + rep.getRealPath("") + 
                " Root catalog path: " + rep.getRootFolder().getCatalogPath() + 
                "\n" 
            );

            Element page = XmlHelper.createPageElement(request);
			Xbuilder.addText( page, buffer.toString() );
			response.setResultName( RESULT_SUCCESS );
			response.getResultMap().put( OUTPUT_XML, page.getOwnerDocument() );
		}
		catch(CriticalException e){
			logger.error("-e", e);
		}
		return response;
	}
}
