/*
 * @(#)$Id: PageDependencyLocator.java,v 1.5, 2006-04-25 14:55:21Z, Anatoliy Pererva$
 *
 * Copyright (c) 2004 Negeso Ukraine
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

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.negeso.framework.domain.CriticalException;
import com.negeso.module.media_catalog.CatalogXmlBuilder;

/**
 *
 * Page dependency locator
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 6$
 */
public class PageDependencyLocator implements DependencyLocator {
	private static Logger logger = Logger.getLogger(PageDependencyLocator.class);

	private static String ARTICLE_COMPONENT_ID = "article-component";
    private static String PAGE_MODULE = "Page";

	 
	private static String getDependentPageSql = 
		" SELECT page.id, page.title, page.filename, language.code AS lang_code FROM page " + 
		" LEFT JOIN page_component ON page.id=page_component.page_id" +
		" LEFT JOIN page_component_params ON page_component.id = page_component_params.element_id" +
		" LEFT JOIN article ON article.id = page_component_params.value" +
		" LEFT JOIN language ON language.id = page.lang_id" +
		" WHERE (page_component.class_name=?) AND (article.text LIKE ?)";
	;

	private static String countDependentPageSql = 
		" SELECT COUNT(*) FROM page" + 
		" LEFT JOIN page_component ON page.id=page_component.page_id" +
		" LEFT JOIN page_component_params ON page_component.id = page_component_params.element_id" +
		" LEFT JOIN article ON article.id = page_component_params.value" +
		" WHERE (page_component.class_name=?) AND (article.text LIKE ?)";
	;

	public void buildDom4jXml(Connection con, Element parent, String catalogPath) 
		throws CriticalException 
	{
		try{
            logger.info("locator invoked" + catalogPath);
			PreparedStatement stmt = con.prepareStatement(
			        PageDependencyLocator.getDependentPageSql
			);
			stmt.setString(1, ARTICLE_COMPONENT_ID);
			stmt.setString(2, "%" + catalogPath + "%");
			ResultSet rs = stmt.executeQuery();
			while ( rs.next() ){
				Element el = CatalogXmlBuilder.get().buildDependencyDom4j(
					rs.getString("filename"),
					new Long(rs.getLong("id")),
					rs.getString("filename"),					
					rs.getString("title")			
				);
				el.addAttribute("lang-code", rs.getString("lang_code"));
				el.addAttribute("module", PAGE_MODULE);
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
	public boolean hasDependencies(Connection con, String catalogPath) throws CriticalException {
        logger.info("locator invoked" + catalogPath);
		try{
			PreparedStatement stmt = con.prepareStatement(
				PageDependencyLocator.countDependentPageSql
			);
			stmt.setString(1, ARTICLE_COMPONENT_ID);
			stmt.setString(2, "%" + catalogPath + "%");
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
