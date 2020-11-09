/*
 * @(#)$Id: DocumentModuleFolderLocator.java,v 1.0, 2006-11-28 09:48:50Z, Olexiy Strashko$
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

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.negeso.framework.domain.CriticalException;
import com.negeso.module.document.DocumentModule;
import com.negeso.module.media_catalog.CatalogXmlBuilder;

/**
 *
 * DocumentModule dependency locator 
 * 
 * @version		$Revision: 1$
 * @author		Olexiy Strashko
 * 
 */
public class DocumentModuleFolderLocator implements DependencyLocator {
    private static Logger logger = Logger.getLogger(DocumentModuleFolderLocator.class);

    
    private static final String DOCUMENT_MODULE = "Document module";
    
    
    private static final String GET_DEPENDENT_CATEGORIES =
        " SELECT dc_category.id, dc_category.name " +
        " FROM dc_category " +
        " LEFT JOIN mc_folder ON dc_category.mc_folder_id = mc_folder.id " +
        " WHERE mc_folder.path LIKE ?" 
    ;

    private static final String HAS_DEPENDENT_CATEGORIES =
        " SELECT COUNT(*) AS cnt FROM dc_category " +
        " LEFT JOIN mc_folder ON dc_category.mc_folder_id = mc_folder.id " + 
        " WHERE mc_folder.path LIKE ? "
    ;

    /**
     * 
     */
    public DocumentModuleFolderLocator() {
        super();
    }

    /* (non-Javadoc)
     * @see com.negeso.module.media_catalog.dependency.DependencyLocator#buildDom4jXml(java.sql.Connection, org.dom4j.Element, java.lang.String)
     */
    public void buildDom4jXml(Connection con, Element parent, String catalogPath)
        throws CriticalException 
    {
    	if (logger.isInfoEnabled()) {
    		logger.info("locator invoked" + catalogPath);
    	}
        
        PreparedStatement stmt = null;
        try{
            stmt = con.prepareStatement(GET_DEPENDENT_CATEGORIES);
            stmt.setString(1, "%" + catalogPath + "%");
            ResultSet rs = stmt.executeQuery();

            while ( rs.next() ){
                Element el = CatalogXmlBuilder.get().buildDependencyDom4j(
                    rs.getString("name"),
                    new Long(rs.getLong("id")),
                    DocumentModule.get().getAminCategoryLink(rs.getLong("id")),
                    rs.getString("name")           
                );
                el.addAttribute("module", DOCUMENT_MODULE);
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
        PreparedStatement stmt = null;
        try{
            stmt = con.prepareStatement(HAS_DEPENDENT_CATEGORIES);
            stmt.setString(1, "%" + catalogPath + "%");
            ResultSet rs = stmt.executeQuery();

            if ( rs.next() ){
                if (rs.getLong("cnt") > 0){
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
