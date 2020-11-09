/*
 * @(#)$Id: LocationXmlBuilder.java,v 1.9, 2005-06-06 13:04:45Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.store_locator.generator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.domain.Contact;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.util.Timer;
import com.negeso.module.store_locator.Configuration;
import com.negeso.module.store_locator.domain.Location;
import com.negeso.module.store_locator.domain.City;

/**
 *
 * Location xml building routines
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 10$
 */
public class LocationXmlBuilder {
	private static Logger logger = Logger.getLogger( LocationXmlBuilder.class );
	
	private static final String getLocationsSql = 
		"SELECT * FROM contact " +
		"WHERE type=? ORDER BY city, company_name"
	;
	
	/**
	 * Build locations xml ordered by city-name
	 * 
	 * @param con
	 * @param parent
	 * @param langId
	 * @throws CriticalException
	 */
	public static void 	buildLocationByContact( 
		Connection con, Contact contact, Element parent, int langId) 
		throws CriticalException
	{
		Timer timer = new Timer();
		timer.start();
		
		if ( contact == null ){
			logger.warn("Contact is NULL!!!");
			return;
		}
		Element location = Xbuilder.addEl(parent, "sl-location", null);
		Xbuilder.setAttr(location, "id", contact.getId());
		Xbuilder.setAttr(location, "title", contact.getCompanyName());
		Xbuilder.setAttr(location, "phone", contact.getPhone());
		Xbuilder.setAttr(location, "address-line", contact.getAddressLine());
		Xbuilder.setAttr(location, "email", contact.getEmail());
		Xbuilder.setAttr(location, "web-link", contact.getWebLink());
		Xbuilder.setAttr(location, "city", contact.getCity());
		Xbuilder.setAttr(location, "image-link", contact.getImageLink());
		
		String zipStr = contact.getZipCode();
		if ( zipStr != null ){
			String[] zipParts = zipStr.split(" "); 
			
			Xbuilder.setAttr(location, "zip-code", zipParts[0]);
			if ( zipParts.length > 1 ){
                Xbuilder.setAttr(location, "zip-suffix", zipParts[1]);
			}
		}
		
		Xbuilder.setAttr(location, "image-width", "" + Configuration.get().getMapImageWidth());
		Xbuilder.setAttr(location, "image-height", "" + Configuration.get().getMapImageHeight());
		
		logger.info("contact building:" + timer.stop());
		
		LocationXmlBuilder.buildMatchZipcodes(con, location, langId, contact.getId());
		logger.info("zipcode building:" + timer.stop());
	}

	
	/**
	 * Build zip code for location
	 * 
	 * @param con
	 * @param parent
	 * @param langId
	 * @param locationId
	 * @throws CriticalException
	 */
	public static void buildMatchZipcodes(
		Connection con, Element parent, int langId, Long locationId )
		throws CriticalException
	{
		if (locationId == null){
			logger.warn("locationId is NULL");
			return;
		}
		Element zips = Xbuilder.addEl(parent, "sl-zipcodes", null);

		PreparedStatement stmt = null;
		try{
			stmt = con.prepareStatement(Location.getMatchZipCodesSql);
			stmt.setLong(1, locationId.longValue());
			ResultSet rs = stmt.executeQuery();
			ZipcodeXmlBuilder.buildZipcodeXml(rs, zips, langId);
		}
		catch( SQLException e ){
			logger.error("-error", e);
			throw new CriticalException(e);
		}
		finally{
			DBHelper.close(stmt);
		}
	}
		

	
	/**
	 * Build locations xml ordered by city-name
	 * 
	 * @param con
	 * @param parent
	 * @param langId
	 * @throws CriticalException
	 */
	public static void 	buildLocations( Connection con, Element parent, int langId ) 
		throws CriticalException
	{
		Element locations = Xbuilder.addEl(parent, "sl-locations", null);
		PreparedStatement stmt = null;
		try{
			stmt = con.prepareStatement(LocationXmlBuilder.getLocationsSql);
			stmt.setString(1, Location.STORE_LOCATOR_CONTACT_TYPE);
			ResultSet rs = stmt.executeQuery();
			LocationXmlBuilder.buildLocationXml(rs, locations, langId);
		}
		catch(SQLException e){
			logger.error("-error", e);
			throw new CriticalException(e);
		}
	}

	/**
	 * Location builder (use ResultSet as location source) 
	 * 
	 * @param rs
	 * @param parent
	 * @param langId
	 * @throws CriticalException
	 */
	public static void 	buildLocationXml(ResultSet rs, Element parent, int langId) 
		throws CriticalException
	{
		try{
			while ( rs.next() ){
				Element location = Xbuilder.addEl(parent, "sl-location", null);
				Xbuilder.setAttr(location, "id", "" + rs.getLong("id"));
				Xbuilder.setAttr(location, "title", rs.getString("company_name"));
				Xbuilder.setAttr(location, "phone", rs.getString("phone"));
				Xbuilder.setAttr(location, "address-line", rs.getString("address_line"));
				Xbuilder.setAttr(location, "email", rs.getString("email"));
				Xbuilder.setAttr(location, "web-link",  rs.getString("web_link"));
				Xbuilder.setAttr(location, "city", rs.getString("city"));
				Xbuilder.setAttr(location, "zip-code", rs.getString("zip_code"));
				Xbuilder.setAttr(location, "image-link", rs.getString("image_link"));
			}
		}
		catch(SQLException e){
			logger.error("-error", e);
			throw new CriticalException(e);
		}
	}

    public static void buildCitiesListXml(Element parent, Connection conn)
            throws CriticalException
    {
        try{
            Element sl_cities_list = Xbuilder.addEl(parent, "sl_cities_list", null);
            PreparedStatement stat = conn.prepareStatement(City.getCitiesSql);
            ResultSet res = stat.executeQuery();
            Element sl_cities_list_item = null;
            while( res.next() ){
                sl_cities_list_item =  Xbuilder.addEl(sl_cities_list, "sl_cities_list_item", null);
                sl_cities_list_item.setAttribute("id", res.getString("id"));
                sl_cities_list_item.setAttribute("title", res.getString("title"));
                sl_cities_list_item.setAttribute("min", res.getString("min"));
                sl_cities_list_item.setAttribute("max", res.getString("max"));
            }
        }
        catch(Exception e){
			logger.error("-error", e);
			throw new CriticalException(e);
		}
    }

}
