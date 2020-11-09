/*
 * @(#)$Id: PageComponentRecord.java,v 1.7, 2006-06-15 19:45:55Z, Stanislav Demchenko$
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;


/**
 * @version 	$Revision: 8$
 * @author 		Oleg Lyebyedyev
 * @author 		Olexiy Strashko
 */

public class PageComponentRecord {

	protected Long id;
    private Long pageId;
    private String name;

    private static final Logger logger = Logger.getLogger( PageComponentRecord.class );

	private static final String GET_COMPONENT_PARAMETERS_SQL =
		" SELECT name, value FROM page_component_params " +
		" WHERE element_id = ? ORDER BY name";

	private static final String getPageNameByComponentNameAndLangIdSql =
		" SELECT filename FROM page LEFT JOIN page_component " +
		" ON (page.id = page_component.page_id) AND (page.lang_id=?) " +
		" WHERE class_name=?";

	private static final String getPageNameByComponentNameAndLangIdAndCalssSql =
		getPageNameByComponentNameAndLangIdSql + 
		" AND class=?";
    
    public String getTableId() { return "page_component"; }
    
    // cachable parameters
    private Map parameters = null;

    private static PageComponentRecord load(ResultSet rs, PageComponentRecord pe) throws SQLException {
        logger.debug( "+" );
        pe.id = new Long(rs.getLong("id"));
        pe.pageId = new Long(rs.getLong("page_id"));
        pe.name = rs.getString("class_name");
        logger.debug( "-" );
        return pe;
    }

    /**
     *
     * @param con
     * @param page_id
     * @return
     * @throws CriticalException
     */
    public static PageComponentRecord[] getPageComponents( Connection con, long page_id ) throws CriticalException {
    	Statement stmt = null;
    	ResultSet rs = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(
                "SELECT * FROM page_component WHERE page_id = " + page_id + " ORDER BY id");
            ArrayList<PageComponentRecord> res = new ArrayList<PageComponentRecord>(3);
            while ( rs.next() ) {
                res.add( PageComponentRecord.load( rs, new PageComponentRecord() ) );
            }

            return res.toArray( new PageComponentRecord[ res.size() ] );
        }
        catch ( SQLException ex ) {
            logger.error( " - ", ex );
            throw new CriticalException( ex );
        }finally{
        	DBHelper.close(rs);
        	DBHelper.close(stmt);
        }
    }

	/**
	 * Get component parameters. Every parameter can have one or more values.
	 * Return map, where every value is [] array.
	 * Case of parameter names are ingnored.
	 *    
	 * @param con		The JDBC Connection to DB.
	 * @return			The result map with parameters.
	 * @throws 			CriticalException in case of DB error.
	 */
	public Map getParameters( Connection con ) throws CriticalException{
		
		if (this.parameters == null) {
			// load parameters from database
			this.parameters = new HashMap();
			PreparedStatement stmt = null;
			try{
				stmt = con.prepareStatement(GET_COMPONENT_PARAMETERS_SQL);
				stmt.setLong(1, this.getId().longValue());
				ResultSet rs = stmt.executeQuery();
				
				// values container
				List<String> tmpValueList = new ArrayList<String>();
				// temp name for param name tracking
				String tmpName = "";			
				while (rs.next()){
					if ( !tmpName.equalsIgnoreCase(rs.getString("name")) ) {
						// new name, add values to map
						if (!"".equals(tmpName)){
							this.parameters.put(tmpName, tmpValueList.toArray());					
						}
						// reset temp name and list for values
						tmpName = rs.getString("name");
						tmpValueList.clear();
					}
					tmpValueList.add(rs.getString("value"));
					//logger.info("Parameter " + tmpName + " = " + rs.getString("value"));
				}
				if (!"".equals(tmpName)){
					this.parameters.put(tmpName, tmpValueList.toArray());					
				}
			}
			catch (Exception e){
				logger.error("- error", e);
				throw new CriticalException(e); 		
			}
		}
	
		return this.parameters;
	}

	/**
	 * Get bind page name by page component name. 
	 * If page not found, null result returned
	 * 
	 * @param con
	 * @param componentName
	 * @param languageId
	 * @return
	 * @throws CriticalException
	 */

    public static String getBindPageName(
	        Connection con, String componentName, int languageId)
    {
        String res = null;
		PreparedStatement stmt = null;
		try{
			stmt = con.prepareStatement(getPageNameByComponentNameAndLangIdSql);
			stmt.setLong(1, languageId);
			stmt.setString(2, componentName);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()){
			    res = rs.getString("filename");
			}
			rs.close();
		} catch (SQLException e){
			logger.error("- error", e);
			throw new CriticalException(e);
		} finally{
		    DBHelper.close(stmt);
		}
		return res;
	}

    /**
         * Get bind page name by page component name.
         * If page not found, null result returned
         *
         * @param con
         * @param componentName
         * @param pageClassName   
         * @param languageId
         * @return
         * @throws CriticalException
         */

    public static String getBindPageName(
            Connection con, String componentName, String pageClassName, int languageId)
    {
        String res = null;
        PreparedStatement stmt = null;
        try{
            stmt = con.prepareStatement(getPageNameByComponentNameAndLangIdAndCalssSql);
            stmt.setLong(1, languageId);
            stmt.setString(2, componentName);
            stmt.setString(3, pageClassName );
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                res = rs.getString("filename");
            }
            rs.close();
        } catch (SQLException e){
            logger.error("- error", e);
            throw new CriticalException(e);
        } finally{
            DBHelper.close(stmt);
        }
        return res;
    }

    public Long getId()  { return id; }
    public void setId( Long arg ) { id = arg; }

    public Long getPageId() { return pageId; }
    public void setPageId( Long arg ) { pageId = arg; }

    public String getName() { return name; }
    public void setName( String arg ) { name = arg; }

}