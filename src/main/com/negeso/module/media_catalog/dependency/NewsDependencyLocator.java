/*
 * @(#)$Id: NewsDependencyLocator.java,v 1.11, 2007-04-02 14:58:23Z, Anatoliy Pererva$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.media_catalog.dependency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;


import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.module.media_catalog.CatalogXmlBuilder;

/**
 *
 * News module dependency locator 
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 12$
 */

public class NewsDependencyLocator implements DependencyLocator {
	private static Logger logger = Logger.getLogger(NewsDependencyLocator.class);

    //private static int NEWS_MODULE_ID = 1;
    public static String NEWS_MODULE = ModuleConstants.NEWS;

	private static String getListItemDependenciesSql = 
		" SELECT list_item.id, list_item.title, list_item.list_id, " +
		" list.name, language.code AS lang_code " +
		" FROM list_item " +
		" LEFT JOIN list " +
		" 	ON list.id=list_item.list_id " + 
		" LEFT JOIN article AS teaser " +
		" 	ON teaser.id=list_item.teaser_id " + 
		" LEFT JOIN article AS fulltext " +
		" 	ON fulltext.id=list_item.teaser_id " +
		" LEFT JOIN language " +
		"   ON language.id = list.lang_id" +
		" WHERE " +
		" (" +
		" 	(image_link LIKE ?) " +
		"   OR (document_link LIKE ?) " +
		" 	OR (teaser.text LIKE ?) " +
		"	OR (fulltext.text LIKE ?) " +
		" )" +
		" AND list.module_id = ?" +
		" ORDER BY list_id"
	;

	private static String countDependentListItemsSql = 
		" SELECT COUNT(*) FROM list_item " +
		" LEFT JOIN article AS teaser " +
		" 	ON teaser.id=list_item.teaser_id " +
		" LEFT JOIN list ON  list.module_id = ? "+
		" LEFT JOIN article AS fulltext " +
		" 	ON fulltext.id=list_item.teaser_id " +
		" WHERE " +
		" (" +
		" 	(image_link LIKE ?) " +
		"   OR (document_link LIKE ?) " +
		" 	OR (teaser.text LIKE ?) " +
		"	OR (fulltext.text LIKE ?) " +
		" )" 
	;

    /**
     * 
     */
    public NewsDependencyLocator() {
        super();
    }

    /* (non-Javadoc)
     * @see com.negeso.module.media_catalog.dependency.DependencyLocator#buildDom4jXml(java.sql.Connection, org.dom4j.Element, java.lang.String)
     */
    public void buildDom4jXml(Connection con, Element parent, String catalogPath)
    	throws CriticalException 
	{
		logger.info("locating:" + catalogPath );
		try{
			
			PreparedStatement stmt = con.prepareStatement(
			        NewsDependencyLocator.getListItemDependenciesSql
			);
			String searchStr = "%" + catalogPath + "%";
			stmt.setString(1, searchStr);
			stmt.setString(2, searchStr);
			stmt.setString(3, searchStr);
			stmt.setString(4, searchStr);
			stmt.setLong  (5, Env.getSite().getModuleId(ModuleConstants.NEWS));
			ResultSet rs = stmt.executeQuery();
			
			String href = null;
			while ( rs.next() ){
                
			    href = MessageFormat.format(
                    "?command=get-list-item-command&listId={0}&listItemId={1}",
                    rs.getLong("list_id"), 
                    rs.getLong("id")
                );                  

				Element el = CatalogXmlBuilder.get().buildDependencyDom4j(
				    rs.getString("name"),
					new Long(rs.getLong("id")),
					href,					
					rs.getString("title")			
				);
				el.addAttribute("lang-code", rs.getString("lang_code"));
				el.addAttribute("module", NEWS_MODULE);
				parent.add(el);
			}
			rs.close();
			stmt.close();
		}
		catch(SQLException e){
			logger.error("-error", e);
			throw new CriticalException(e);
		}
    }

    /* (non-Javadoc)
     * @see com.negeso.module.media_catalog.dependency.DependencyLocator#hasDependencies(java.sql.Connection, java.lang.String)
     */
    public boolean hasDependencies(Connection con, String catalogPath)
        throws CriticalException 
	{
		try{
            logger.info("locating:" + catalogPath );
			PreparedStatement stmt = con.prepareStatement(
			        NewsDependencyLocator.countDependentListItemsSql
			);
			String searchStr = "%" + catalogPath + "%";
			stmt.setLong  (1, Env.getSite().getModuleId(ModuleConstants.NEWS));
			stmt.setString(2, searchStr);
			stmt.setString(3, searchStr);	
			stmt.setString(4, searchStr);
			stmt.setString(5, searchStr);
			

			ResultSet rs = stmt.executeQuery();
			if ( rs.next() ){
				if ( rs.getLong("count") > 0 ){
					return true;
				}
			}
			rs.close();
			stmt.close();
		}
		catch(SQLException e){
			logger.error("-error", e);
			throw new CriticalException(e);
		}
		return false;
    }

}
