/*
 * @(#)$Id: AddZipcode.java,v 1.5, 2005-06-06 13:04:05Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.store_locator.command;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

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
import com.negeso.framework.util.Timer;
import com.negeso.module.store_locator.domain.Location;
import com.negeso.module.store_locator.generator.CityXmlBuilder;

/**
 *
 * AddZipcode command
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 6$
 */
public class AddZipcode extends AbstractCommand {
	private static Logger logger = Logger.getLogger( AddZipcode.class );

	private static final String INPUT_LOCATION_ID = "locationId";
	private static final String INPUT_ZIPCODE_STRING = "zipcode_string";
	
	/* (non-Javadoc)
	 * @see com.negeso.framework.command.Command#execute()
	 */
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
			con = DBHelper.getConnection();
			con.setAutoCommit(false);
			if ( "submit".equals(action) ){
				this.doSubmit(con, request, page, langId);

				if ( this.hasErrors() ){
					this.doShowDialog(con, request, page, langId);
				}
				else{
					this.doShowResult(con, request, page, langId);
				}
			}
			else{
				this.doShowDialog(con, request, page, langId);
			}
			response.setResultName(AddZipcode.RESULT_SUCCESS);
			if ( this.hasErrors() ){
				con.rollback();
			}
			else{
				con.commit();
			}
		}
		catch( SQLException e ){
			logger.error("-error", e);
			DBHelper.rollback(con);
			response.setResultName(RESULT_FAILURE);
		}
		catch( CriticalException e ){
			logger.error("-error", e);
			DBHelper.rollback(con);
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
	private void doShowDialog(
		Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		Element dialog = Xbuilder.addEl( page, "sl-addzipcode-dialog", null );
		
		Long locationId = request.getLong( INPUT_LOCATION_ID );
		Xbuilder.setAttr( dialog, "location-id", locationId );

		String zipsString = request.getNonblankParameter( INPUT_ZIPCODE_STRING );
		Xbuilder.setAttr( dialog, "zipcode-string", zipsString );
		
		if ( this.hasErrors() ){
			page.appendChild(
				XmlHelper.createErrorsElement(page.getOwnerDocument(), this.getErrors())
			);
		}
		CityXmlBuilder.buildCities(con, dialog, langId, null);
	}

	/**
	 * 
	 * @param con
	 * @param request
	 * @param page
	 * @param langId
	 * @throws CriticalException
	 */
	private void doShowResult(
		Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		Element dialog = Xbuilder.addEl(page, "sl-dialog-result", null);
	}

	/**
	 * @param con
	 * @param request
	 * @param page
	 * @param langId
	 * @throws CriticalException
	 */
	private void doSubmit(Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		Long locationId = request.getLong( INPUT_LOCATION_ID );
		if ( locationId == null ){
			this.getErrors().add( "Location id is null" );
		}
		
		String zipsString = request.getNonblankParameter( INPUT_ZIPCODE_STRING );
		
		Location location = new Location(locationId);
		Collection errors = location.addZipsByBatchString(con, zipsString);
		
		if ( errors != null ){
			this.getErrors().addAll(errors);
		}
	}
	
	
}
