/*
 * @(#)$Id: ManageLocations.java,v 1.11, 2005-06-06 13:04:49Z, Stanislav Demchenko$
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

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.Contact;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.util.Timer;
import com.negeso.module.store_locator.domain.Location;
import com.negeso.module.store_locator.domain.Location2Zip;
import com.negeso.module.store_locator.domain.Zipcode;
import com.negeso.module.store_locator.generator.LocationXmlBuilder;

/**
 *
 * Manage locations command. Perform:
 * -	list locations
 * -	add location
 * -	remove location
 * -	show location
 * -	save location	
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 12$
 */
public class ManageLocations extends AbstractCommand {
	private static Logger logger = Logger.getLogger( ManageLocations.class );
	
	private static final String RESULT_LOCATIONS = "location-list";
	private static final String RESULT_EDIT_LOCATION = "edit-location";
	private static final String INPUT_LOCATION_ID = "locationId";
	private static final String INPUT_ZIPCODE_ID = "zipcodeId";
	private static final String INPUT_BACK_LINK = "back_link";

	
	
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
			Xbuilder.setAttr(
				page, "back-link", request.getNonblankParameter(INPUT_BACK_LINK)
			);
			con = DBHelper.getConnection();
			if ( "add_location".equals(action) ){
				this.doAddLocation(con, request, page, langId);
				response.setResultName(ManageLocations.RESULT_EDIT_LOCATION);
			}
			else if( "remove_location".equals(action) ){
				this.doRemoveLocation(con, request, page, langId);
				response.setResultName(ManageLocations.RESULT_LOCATIONS);
			}
			else if( "show_location".equals(action) ){
				this.doShowLocation(con, request, page, langId);
				response.setResultName(ManageLocations.RESULT_EDIT_LOCATION);
			}
			else if( "save_location".equals(action) ){
				this.doSaveLocation(con, request, page, langId);
				response.setResultName(ManageLocations.RESULT_EDIT_LOCATION);
			}
			else if( "remove_zipcode".equals(action) ){
				this.doRemoveZipcode(con, request, page, langId);
				response.setResultName(ManageLocations.RESULT_EDIT_LOCATION);
			}
			else{
				this.doListLocations(con, request, page, langId);
				response.setResultName(ManageLocations.RESULT_LOCATIONS);
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
	 */
	private void doRemoveZipcode(
		Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		Long locationId = request.getLong( INPUT_LOCATION_ID );
		Long zipcodeId = request.getLong( INPUT_ZIPCODE_ID );
		if ( (locationId == null) || (zipcodeId == null) ){
			logger.warn("LocaitonId or zipcode id is null");
		}
		Location location = new Location(locationId);
		location.removeZipcode( con, zipcodeId );
		
		if ( locationId != null ){
			Contact contact = Contact.findById(con, locationId);
			LocationXmlBuilder.buildLocationByContact(con, contact, page, langId);
            LocationXmlBuilder.buildCitiesListXml(page, con);
		}
	}

	/**
	 * @param con
	 * @param request
	 * @param page
	 * @param langId
	 * @throws CriticalException
	 */ 
	private void doListLocations(
		Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		LocationXmlBuilder.buildLocations(con, page, langId);
	}


	/**
	 * @param con
	 * @param request
	 * @param page
	 * @param langId
	 * @return
	 * @throws CriticalException
	 */
	private void doSaveLocation(
		Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		Long locationId = request.getLong( INPUT_LOCATION_ID );
		// remove
		Contact contact = null;
		if ( locationId != null ){
			contact = Contact.findById(con, locationId);
		}
		if ( contact == null ){
			logger.info("Creating a new contact");
			contact = new Contact();
		}
		
		String tmp = request.getNonblankParameter("titleField");
		contact.setCompanyName(tmp);
		
		tmp = request.getNonblankParameter("addressLineField");
		contact.setAddressLine(tmp);

		tmp = request.getNonblankParameter("phoneField");
		contact.setPhone(tmp);
		
		tmp = request.getNonblankParameter("emailField");
		contact.setEmail(tmp);
		
		tmp = request.getNonblankParameter("weblinkField");
		contact.setWebLink(tmp);

		tmp = request.getNonblankParameter("cityField");
		contact.setCity(tmp);

		if ( request.getNonblankParameter("removeImage") != null ){
			logger.info("remove image");
			contact.setImageLink(null);
		}
		else{
			tmp = request.getNonblankParameter("imageField");
			logger.info("image field:" + tmp);
			contact.setImageLink(tmp);
		}
		
		Long zipCode = request.getLong("zipcodeField");
		String zipSuffix = request.getNonblankParameter("zipcodeSuffix");
		tmp = zipCode.toString();
		if ( zipSuffix != null ){
			tmp += " " + zipSuffix.toUpperCase(); 
		}
		
		contact.setZipCode( tmp );
		
		contact.setType(Location.STORE_LOCATOR_CONTACT_TYPE);
		if ( contact.getId() == null ){
			contact.insert(con);
			
			// append zipcode
			if ( zipCode != null ){
				Zipcode zipcode = Zipcode.findCityRangeByZipcodeId(con, zipCode);
				if (zipcode != null ){
					logger.info("Adding zipcode");
					Location2Zip location = new Location2Zip();
					location.setContactId( contact.getId() );
					location.setZipcodeId( zipcode.getId() );
					location.insert(con);
				}
			}
		}
		else{
			contact.update(con);
		}


		
		LocationXmlBuilder.buildLocationByContact(con, contact, page, langId);
        LocationXmlBuilder.buildCitiesListXml(page, con);
	}


	/**
	 * @param con
	 * @param request
	 * @param page
	 * @param langId
	 * @return
	 * @throws CriticalException
	 */
	private void doShowLocation(Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		Long locationId = request.getLong( INPUT_LOCATION_ID );
		// remove
		if ( locationId != null ){
			Contact contact = Contact.findById(con, locationId);
			LocationXmlBuilder.buildLocationByContact(con, contact, page, langId);
            LocationXmlBuilder.buildCitiesListXml(page, con);
		}
	}


	/**
	 * @param con
	 * @param request
	 * @param page
	 * @param langId
	 * @return
	 * @throws CriticalException
	 */
	private void doRemoveLocation(
		Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		Long locationId = request.getLong( INPUT_LOCATION_ID );
		// remove
		if ( locationId != null ){
			// remove
			Contact contact = Contact.findById(con, locationId);
			if ( contact != null ){
				contact.delete(con);
			}
		}
		this.doListLocations(con, request, page, langId);
	}


	/**
	 * @param con
	 * @param request
	 * @param page
	 * @param langId
	 * @return
	 * @throws CriticalException
	 */
	private void doAddLocation(
		Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		//add
		Contact contact = new Contact();
		LocationXmlBuilder.buildLocationByContact(con, contact, page, langId);
        LocationXmlBuilder.buildCitiesListXml(page, con);
	}

}
