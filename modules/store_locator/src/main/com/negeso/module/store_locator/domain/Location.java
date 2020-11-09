/*
 * @(#)$Id: Location.java,v 1.3, 2005-06-06 13:04:44Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.store_locator.domain;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;


import com.negeso.framework.domain.CriticalException;


/**
 *
 * Fake location domain 
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 4$
 */
public class Location {
	private static Logger logger = Logger.getLogger( Location.class );

	public static final String STORE_LOCATOR_CONTACT_TYPE = "store_locator";
	
	public static final String getMatchZipCodesSql = 
		"SELECT sl_zipcode.id, sl_zipcode.min, sl_zipcode.max, sl_city.title AS city_name " +
		"FROM sl_zipcode " +
		"INNER JOIN sl_location2zip " +
		"ON (sl_zipcode.id = sl_location2zip.zipcode_id AND sl_location2zip.contact_id = ?) " +
		"LEFT JOIN sl_city " +
		"ON (sl_city.id = sl_zipcode.city_id) " +
		"ORDER BY sl_zipcode.min, city_name"
	;
	
	private Long id = null;
	
	public Location(Long id){
		this.id = id;
	}
	
	/**
	 * Return errors collection if was errors
	 * 
	 * @param con
	 * @param zipsString
	 * @return
	 * @throws CriticalException
	 */
	public Collection addZipsByBatchString(Connection con, String zipsString) throws CriticalException {
		if ( zipsString == null ) {
			logger.warn("zipcode string is NULL!!!");
			return null;
		}
		
		Collection<String> errors = new ArrayList<String>();
			
		String[] zips = zipsString.split(",");
		
		for ( int i = 0; i < zips.length; i++ ){

			String[] zipItems = zips[i].split("-");
			
			if ( zipItems.length == 1){
				Long zipMinValue = Zipcode.validate( zipItems[0], errors);
				if ( zipMinValue != null ){ 
					this.addZip(con, zipMinValue, null);
				}
			}
			else if ( zipItems.length == 2){
				Long zipMinValue = Zipcode.validate( zipItems[0], errors );
				Long zipMaxValue = Zipcode.validate( zipItems[1], errors );
				
				if ( (zipMinValue!= null) && (zipMaxValue!= null) ){
					this.addZip(con, zipMinValue, zipMaxValue);
				}
			}
			else{
				logger.info( "Wrong zipcode range:" + zips[i] );
				errors.add( "Wrong zipcode range:" + zips[i] );
			}
		}
		return errors;
	}
	
	/**
	 * Add zip codes
	 * 
	 * @param con
	 * @param min
	 * @param max
	 * @throws CriticalException
	 */
	public void addZip(Connection con, Long min, Long max) throws CriticalException {

		Zipcode zipcode = Zipcode.findByMinMax(con, min, max);
		if ( zipcode == null ){
			zipcode = new Zipcode();		
			zipcode.setMin( min );
			zipcode.setMax( max );
			zipcode.insert( con );
		}
		
		Location2Zip l2z = Location2Zip.findByLocationAndZip(
			con, this.getId(), zipcode.getId()
		);
		if ( l2z == null ){
			l2z = new Location2Zip();
			l2z.setZipcodeId( zipcode.getId() );
			l2z.setContactId( this.getId() );
			l2z.insert( con );
		}
	}
	
	/**
	 * @return Returns the id.
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @param zipcodeId
	 */
	public boolean removeZipcode(Connection con, Long zipcodeId) throws CriticalException {
		Location2Zip l2z = Location2Zip.findByLocationAndZip( con, this.getId(), zipcodeId);
		if ( l2z != null ){
			l2z.delete(con);
			return true;
		}
		else{
			return false;
		}
	}
}
