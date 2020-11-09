/*
 * @(#)$Id: ManageCategories.java,v 1.3, 2007-01-09 18:46:02Z, Anatoliy Pererva$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.glossary.command;

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
import com.negeso.module.glossary.domain.Category;
import com.negeso.module.glossary.generators.GlossaryXmlGenerator;

/**
 *
 * Manage groups
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 4$ 
 */

@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ManageCategories extends AbstractCommand {
	private static Logger logger = Logger.getLogger( ManageCategories.class );
	
	private static final String RESULT_GROUPS = "category-list";
	private static final String RESULT_EDIT_GROUP = "edit-category";
	private static final String INPUT_CATEGORY_ID = "categoryId";
	
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
		logger.debug("action: " + action);
		Element page = null;
		try{
			page = XmlHelper.createPageElement( request );
			Xbuilder.setAttr(page, "action", action);
			con = DBHelper.getConnection();
			if( "save_category".equals(action) ){
				this.doSaveCategory(con, request, page, langId);
				this.renderCategories(con, request, page, langId);
				Xbuilder.setAttr(page, "view", "category_list");
			}
			else if( "show_category".equals(action) ){
				this.renderCategory(con, request, page, langId);
				Xbuilder.setAttr(page, "view", "edit_category");
			}
			else if( "add_category".equals(action) ){
				this.renderCategory(con, request, page, langId);
				Xbuilder.setAttr(page, "view", "add_category");
			}
			else if( "remove_category".equals(action) ){
				this.doRemoveCategory(con, request, page, langId);
				this.renderCategories(con, request, page, langId);
				Xbuilder.setAttr(page, "view", "category_list");
			}
			else{
				Xbuilder.setAttr(page, "view", "category_list");
				this.renderCategories(con, request, page, langId);
			}
			response.setResultName(RESULT_SUCCESS);
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
	private void doRemoveCategory(Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		Long categoryId = request.getLong(INPUT_CATEGORY_ID);
		Category group = Category.findById(con, categoryId);
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
	private void doSaveCategory(
		Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		Long catId = request.getLong( INPUT_CATEGORY_ID );
		logger.info("catId:" + catId);
		// remove
		Category category = null;
		if ( catId != null ){
			category = Category.findById(con, catId);
		}
		if ( category == null ){
			logger.info("Creating a new category");
			category = new Category();
		}
		
		String tmp = request.getNonblankParameter("title");
		category.setName(tmp);

		if ( category.getId() == null ){
			category.insert(con);
			request.setParameter(INPUT_CATEGORY_ID, category.getId().toString());
		}
		else{
			category.update(con);
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
	private void renderCategories(
		Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		GlossaryXmlGenerator.createCategoryXml(con, page, null);
	}

	/**
	 * 
	 * @param con
	 * @param request
	 * @param page
	 * @param langId
	 * @throws CriticalException
	 */
	private void renderCategory(
		Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		Element catEl = Xbuilder.addEl(page, "glossary_category", null);
		Long catId = request.getLong(INPUT_CATEGORY_ID);
		if ( catId == null ){
		}
		else{
			Category cat = Category.findById(con, catId);
			if ( cat == null ){
				logger.warn("Requested category not found by id:" + catId);
			}
			else{
				Xbuilder.setAttr(catEl, "id", cat.getId());
				Xbuilder.setAttr(catEl, "name", cat.getName());
			}
		}
	}

	@Override
	public String getModuleName() {
		return ModuleConstants.GLOSSARY_MODULE;
	}
}
