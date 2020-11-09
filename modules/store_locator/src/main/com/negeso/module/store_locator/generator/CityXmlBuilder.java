/*
 * @(#)$Id: CityXmlBuilder.java,v 1.6, 2005-06-06 13:04:14Z, Stanislav Demchenko$
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

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.store_locator.domain.City;

/**
 *
 * City xml builder
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 7$
 */
public class CityXmlBuilder {
	private static Logger logger = Logger.getLogger( CityXmlBuilder.class );
	
	
	
	public static void 	buildCities( Connection con, Element parent, int langId, String currentCity ) 
		throws CriticalException
	{
		Element cities = Xbuilder.addEl(parent, "sl-cities", null);
		PreparedStatement stmt = null;
		try{
			stmt = con.prepareStatement(City.getCitiesSql);
			ResultSet rs = stmt.executeQuery();
			CityXmlBuilder.buildCityXml(rs, cities, langId, currentCity);
		}
		catch(SQLException e){
			logger.error("-error", e);
			throw new CriticalException(e);
		}
	}
	
	/**
	 * City builder (use ResultSet as location source) 
	 * 
	 * @param rs
	 * @param parent
	 * @param langId
	 * @throws CriticalException
	 */
	public static void 	buildCityXml(ResultSet rs, Element parent, int langId, String currentCity) 
		throws CriticalException
	{
		try{
			while ( rs.next() ){
				Element city = Xbuilder.addEl(parent, "sl-city", null);
				Xbuilder.setAttr(city, "id", "" + rs.getLong("id"));
				String title = rs.getString("title");
				Xbuilder.setAttr(city, "title", title);
				if ( currentCity != null ){
					if ( currentCity.equalsIgnoreCase(title) ){
                        Xbuilder.setAttr(city, "current", "true");
					}
				}
				
				String zipRange = " " + rs.getLong("min");
				if ( rs.getLong("max") != 0 ){
					zipRange = zipRange + "-" +  rs.getLong("max"); 
				}
				Xbuilder.setAttr(city, "zip-range", zipRange);
			}
		}
		catch(SQLException e){
			logger.error("-error", e);
			throw new CriticalException(e);
		}
	}
}
