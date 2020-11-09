/*
 * @(#)$Id: DbObjectIterator.java,v 1.2, 2005-06-06 13:04:19Z, Stanislav Demchenko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.log4j.Logger;



/**
 *
 * DbObject iterator 
 * 
 * @version		$Revision: 3$
 * @author		Olexiy Strashko
 * 
 */
public class DbObjectIterator implements Iterator {

	private static Logger logger = Logger.getLogger( DbObjectIterator.class );
	
	ResultSet resultSet = null;
	DbObject container = null;
	
	public DbObjectIterator(ResultSet resultSet, DbObject container){
		this.resultSet = resultSet;
		this.container = container;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		try{
			return this.resultSet.next();
		}
		catch(SQLException e){
			logger.error("error", e);
			return false;		
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		try{
			this.container.load(this.resultSet);
			return this.container;
		}
		catch(CriticalException e){
			logger.error("-error", e);
		}
		return null;
	}

}
