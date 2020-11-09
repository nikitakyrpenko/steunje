/*
 * @(#)$Id: DefaultNameNumerator.java,v 1.4, 2007-01-10 15:58:59Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;


/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 5$
 *
 */
public class DefaultNameNumerator {
	private static Logger logger = Logger.getLogger( DefaultNameNumerator.class );
	
	private static ArrayList<Integer> getNumberArray(ResultSet rs) throws SQLException {
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		while (rs.next()){
			try{
				numbers.add(rs.getInt(1));
			}catch(SQLException e){
				continue;
			}
		}
		Collections.sort(numbers);
        return numbers;
	}
	
    /** simple query for retrieving the set of default names' suffixes 
     * 
     * @param tableName 
     * @param fieldName field for default names
     * @param defaultName prefix of default name
     */
    private static String getSimpleQuery(String tableName,
    									 String fieldName,
    									 String defaultName){
    	
    	StringBuffer query = new StringBuffer(500);
    	return query.append(" SELECT DISTINCT CASE WHEN substr(" + fieldName + ", 10) = '' THEN '0' ").
    	append(" ELSE substr(" + fieldName + ", 10) ").
    	append(" END ").
    	append(" as number FROM ").
    	append(  	tableName	).
    	append(" WHERE " + fieldName + " ~ '" + defaultName).
    	append(" [0-9]+' ").  
    	append(" OR " + fieldName + " = '" + defaultName + "'").
    	append(" ORDER by number").toString()
    	;
}
	
	/** This method is used for simple cases when only one tableName present and we able
	 *  to use simple query getSimpleQuery(...)  
	 * 
	 * @param connection - pass prepared connection to the method
	 * @param tableName 
	 * @param fieldName  - field for default names
	 * @param defaultName  - prefix for default name
	 * @return
	 */
	public static String getDefaultName(Connection connection,
										String tableName,
										String fieldName,
										String defaultName) {
		logger.debug("+-");
		return getDefaultName(connection,
							  getSimpleQuery(tableName, fieldName, defaultName),
							  defaultName);
		
	}
	
	/** Use this method when it's impossible to apply simplified getDefaultName(4 params) 
	 * 
	 * @param connection pass prepared connection to the method
	 * @param getNumbersQuery query for retrieving the set of default names' suffixes
	 * @param defaultName prefix for default name
	 * @return
	 */
	public static String getDefaultName(Connection connection,
										String getNumbersQuery,	
			                            String defaultName
			                            )
	{
		logger.debug(getNumbersQuery);
		Statement statement = null;
		ResultSet rs = null;
		try {
        	statement = connection.createStatement();
    		rs = statement.executeQuery(getNumbersQuery);
    		try {
    			ArrayList<Integer> numbers = getNumberArray(rs);
    			if(numbers.size() == 0) return defaultName;
    			for (int i = 0; i < numbers.size() - 1; i++) {
    				if (numbers.get(i + 1) - numbers.get(i)> 1) {
    					return defaultName + " " + (numbers.get(i) + 1);
    				}
    			}
    			return defaultName + " " + (numbers.get(numbers.size() - 1) + 1); 
        	}	
        	finally {}
        }
        catch(SQLException e)
        {
            throw new CriticalException(e);
        }finally{
        	DBHelper.close(rs);
        	DBHelper.close(statement);
        }
	}
 
}

