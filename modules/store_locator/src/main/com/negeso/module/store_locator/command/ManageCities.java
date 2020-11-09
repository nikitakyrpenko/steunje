/*
 * @(#)$Id: ManageCities.java,v 1.6, 2005-06-06 13:04:47Z, Stanislav Demchenko$
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
import com.negeso.module.store_locator.domain.City;
import com.negeso.module.store_locator.generator.CityXmlBuilder;

/**
 *
 * Manage cities command, actions:
 * 	- show cities for select city dialog
 * 
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 7$
 */
public class ManageCities extends AbstractCommand {
	private static Logger logger = Logger.getLogger( ManageCities.class );
	
	private static String RESULT_SELECT_CITY = "select_city";
    private static String RESULT_BROWSE_CITIES = "browse_cities";

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
		
		int langId = request.getSession().getLanguage().getId().intValue();
		Connection con = null;
		String action = request.getNonblankParameter( "action" );
		if ( logger.isInfoEnabled() ){
			logger.info("action: " + action);
		}
		Element page = null;
		try{
			page = XmlHelper.createPageElement( request ); 
			con = DBHelper.getConnection();
			con.setAutoCommit(false);
			if ( "show_select_dialog".equals(action) ){
				this.doShowDialog(con, request, page, langId);
				response.setResultName(ManageCities.RESULT_SELECT_CITY);
			}
            else if ( "browse_cities".equals(action) ){
				renderBrowseCities(con, request, page);
				response.setResultName(ManageCities.RESULT_BROWSE_CITIES);
			}
			else{
				response.setResultName(ManageCities.RESULT_SUCCESS);
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
		Element dialog = Xbuilder.addEl( page, "sl-select-city-dialog", null );
		logger.info("asdasd");
		CityXmlBuilder.buildCities(con, dialog, langId, null);
	}

	/**
	 * Render city browsers
	 * 
	 * @param conn
	 * @param request
	 * @param page
	 * @throws CriticalException
	 */
    private void renderBrowseCities(
		Connection conn, RequestContext request, Element page) 
    	throws CriticalException
	{
    	Timer timer = new Timer();

		Element browse_city = Xbuilder.addEl(page, "browse_city", null);

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
	        stmt = conn.prepareStatement( City.getCitiesWithLocaionsSql );
	        rs = stmt.executeQuery();
	        long cityId = -1;
	        long tmpId = 0;
	        Element city = null;
	        Element location = null;
	        long locationId = -1;
	        while (rs.next()){
	        	tmpId = rs.getLong("city_id");
	        	if ( tmpId != cityId ) {
	        		cityId = tmpId;
	                city = Xbuilder.addEl(browse_city, "browse_city_item", null);
	                Xbuilder.setAttr(city, "title", rs.getString("title"));
	                Xbuilder.setAttr(
	                	city, 
						"range", 
						rs.getLong("min") + "-" + rs.getLong("max")
	                );
	        	}
	            locationId = rs.getLong("location_id"); 
	            if ( locationId > 0 ) {
		           location = Xbuilder.addEl(city, "sl_store", null);
                   Xbuilder.setAttr(location, "name", rs.getString("location_title"));
                   Xbuilder.setAttr(location, "id", "" + locationId);
                }
	        }
        }
        catch(SQLException e){
        	logger.error("-error", e);
        	throw new CriticalException(e);
        }
        finally{
        	DBHelper.close(rs);
        	DBHelper.close(stmt);
        }
        logger.info("Timer:" + timer.stop());
	}
}
