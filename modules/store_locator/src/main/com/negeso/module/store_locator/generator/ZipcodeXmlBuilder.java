/*
 * @(#)$Id: ZipcodeXmlBuilder.java,v 1.4, 2005-06-06 13:05:20Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.store_locator.generator;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.Xbuilder;

/**
 *
 * Zip code xml buider
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 5$
 */
public class ZipcodeXmlBuilder {
	private static Logger logger = Logger.getLogger( ZipcodeXmlBuilder.class );

	
	/**
	 * Zipcode builder (use ResultSet as location source) 
	 * 
	 * @param rs
	 * @param parent
	 * @param langId
	 * @throws CriticalException
	 */
	public static void 	buildZipcodeXml(ResultSet rs, Element parent, int langId) 
		throws CriticalException
	{
		try{
			while ( rs.next() ){
				Element zipcode = Xbuilder.addEl(parent, "sl-zipcode", null);
				Xbuilder.setAttr(zipcode, "id", "" + rs.getLong("id"));

				String zipRange = " " + rs.getLong("min");
				if ( rs.getLong("max") != 0 ){
					zipRange = zipRange + "-" +  rs.getLong("max"); 
				}
				Xbuilder.setAttr(zipcode, "zip-range", zipRange);
				
				Xbuilder.setAttr(zipcode, "city", rs.getString("city_name"));
			}
		}
		catch(SQLException e){
			logger.error("-error:" + e.getErrorCode(), e);
			throw new CriticalException(e);
		}
	}

}
