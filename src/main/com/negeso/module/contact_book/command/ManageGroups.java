/*
 * @(#)$Id: ManageGroups.java,v 1.2, 2005-06-06 13:04:49Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.contact_book.command;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.util.Timer;
import com.negeso.module.contact_book.domain.Group;
import com.negeso.module.contact_book.generators.GroupXmlBuilder;

/**
 *
 * Manage groups
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 3$
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ManageGroups extends AbstractCommand {
	private static Logger logger = Logger.getLogger( ManageGroups.class );
	
	private static final String RESULT_GROUPS = "group-list";
	private static final String RESULT_EDIT_GROUP = "edit-group";

	
	private static final String INPUT_GROUP_ID = "groupId";
	private static final String INPUT_BACK_LINK = "back_link";

	/* (non-Javadoc)
	 * @see com.negeso.framework.command.Command#execute()
	 */
	@ActiveModuleRequired
	public ResponseContext execute() {
		logger.debug("+");
		RequestContext request = this.getRequestContext();
		ResponseContext response = new ResponseContext();
		
		if ( !SecurityGuard.isContributor(request.getSession().getUser()) ){
			response.setResultName( AbstractCommand.RESULT_ACCESS_DENIED );
			return response;
		}
		
		Timer timer = new Timer();
		timer.start();
		
		int langId = request.getSession().getLanguage().getId().intValue();
		Connection con = null;
		String action = request.getNonblankParameter( "action" );
		logger.info("action: " + action);
		Element page = null;
		try{
			page = XmlHelper.createPageElement( request );
			Xbuilder.setAttr(
				page, "back-link", request.getNonblankParameter(INPUT_BACK_LINK)
			);
			con = DBHelper.getConnection();
			if ( "show_group".equals(action) ){
				response.setResultName(ManageGroups.RESULT_EDIT_GROUP);
			}
			else if( "add_group".equals(action) ){
				response.setResultName(ManageGroups.RESULT_EDIT_GROUP);
			}
			else if( "save_group".equals(action) ){
				this.doSaveGroup(con, request, page, langId);
				response.setResultName(ManageGroups.RESULT_EDIT_GROUP);
			}
			else if( "remove_group".equals(action) ){
				this.doRemoveGroup(con, request, page, langId);
				response.setResultName(ManageGroups.RESULT_GROUPS);
			}
			else{
				response.setResultName(ManageGroups.RESULT_GROUPS);
			}
			
			if ( ManageGroups.RESULT_GROUPS.equals(response.getResultName()) ){
				this.renderGroups(con, request, page, langId);
			}
			else{
				this.renderGroup(con, request, page, langId);
			}
		}
		catch( SQLException e ){
			logger.error("-error", e);
			response.setResultName(RESULT_FAILURE);
		}
		catch( CriticalException e ){
			logger.error("-error", e);
			response.setResultName(RESULT_FAILURE);
		}
		finally{
			DBHelper.close(con);
		}
		
		if ( page != null ){
			response.getResultMap().put( OUTPUT_XML, page.getOwnerDocument() );
		}
		logger.info("time:" + timer.stop());
		
		logger.debug("-");
		return response;
	}

	/**
	 * @param con
	 * @param request
	 * @param page
	 * @param langId
	 * @throws CriticalException
	 */
	private void doRemoveGroup(Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		Long groupId = request.getLong(INPUT_GROUP_ID);
		Group group = Group.findById(con, groupId);
		if ( group!= null ){
			group.delete(con);
		}
	}

	/**
	 * @param con
	 * @param request
	 * @param page
	 * @param langId
	 */
	private void doSaveGroup(
		Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		Long groupId = request.getLong( INPUT_GROUP_ID );
		logger.info("groupId:" + groupId);
		// remove
		Group group = null;
		if ( groupId != null ){
			group = Group.findById(con, groupId);
		}
		if ( group == null ){
			logger.info("Creating a new contact");
			group = new Group();
		}
		
		String tmp = request.getNonblankParameter("title");
		group.setTitle(tmp);

		if ( group.getId() == null ){
			group.insert(con);
			request.setParameter(INPUT_GROUP_ID, group.getId().toString());
		}
		else{
			group.update(con);
		}
	}

	/**
	 * 
	 * @param con
	 * @param request
	 * @param page
	 * @param langId
	 * @throws CriticalException
	 */
	private void renderGroups(
		Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		GroupXmlBuilder.buildGroups(con, page);
	}

	/**
	 * 
	 * @param con
	 * @param request
	 * @param page
	 * @param langId
	 * @throws CriticalException
	 */
	private void renderGroup(
		Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		Long groupId = request.getLong(INPUT_GROUP_ID);
		GroupXmlBuilder.buildGroup(con, page, groupId);
	}

	@Override
	public String getModuleName() {
		return ModuleConstants.CONTACT_BOOK_MODULE;
	}
}
