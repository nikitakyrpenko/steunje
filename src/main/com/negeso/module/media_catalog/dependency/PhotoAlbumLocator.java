/*
 * @(#)$Id: PhotoAlbumLocator.java,v 1.4, 2005-06-06 13:04:56Z, Stanislav Demchenko$
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
import com.negeso.module.media_catalog.CatalogXmlBuilder;


/**
 *
 * Photo album media locator
 * 
 * @version		$Revision: 5$
 * @author		Olexiy Strashko
 * 
 */
public class PhotoAlbumLocator implements DependencyLocator {
    private static Logger logger = Logger.getLogger(PhotoAlbumLocator.class);

    
    private static final String MODULE = "Photo album";
    
    
    private static final String GET_DEPENDENT_PHOTOS =
        " SELECT target_album.id, target_album.name, target_album.lang_id, " +
        "   parent_album.name AS parent_name, language.code AS lang_code " +
        " FROM photo_album AS target_album " +
        " LEFT JOIN photo_album AS parent_album " +
        "   ON target_album.parent_id = parent_album.id " +
        " LEFT JOIN image AS image_big " +
        "   ON target_album.image_id = image_big.image_id " +
        " LEFT JOIN image AS image_th " +
        "   ON target_album.thumbnail_id = image_th.image_id " +
        " LEFT JOIN language " +
        "   ON language.id = target_album.lang_id" +
        " WHERE (image_big.src LIKE ?) OR (image_th.src LIKE ?)" 
    ;

    private static final String HAS_DEPENDENT_PHOTOS =
        " SELECT COUNT(*) AS cnt " +
        " FROM photo_album AS target_album " +
        " LEFT JOIN image AS image_big " +
        "   ON target_album.image_id = image_big.image_id " +
        " LEFT JOIN image AS image_th " +
        "   ON target_album.thumbnail_id = image_th.image_id " +
        " WHERE (image_big.src LIKE ?) OR (image_th.src LIKE ?)" 
    ;

    /**
     * 
     */
    public PhotoAlbumLocator() {
        super();
    }

    /* (non-Javadoc)
     * @see com.negeso.module.media_catalog.dependency.DependencyLocator#buildDom4jXml(java.sql.Connection, org.dom4j.Element, java.lang.String)
     */
    public void buildDom4jXml(Connection con, Element parent, String catalogPath)
        throws CriticalException 
    {
        logger.info("locator invoked" + catalogPath);
        
        PreparedStatement stmt = null;
        try{
            stmt = con.prepareStatement(GET_DEPENDENT_PHOTOS);
            stmt.setString(1, "%" + catalogPath + "%");
            stmt.setString(2, "%" + catalogPath + "%");
            ResultSet rs = stmt.executeQuery();

            while ( rs.next() ){
                String parentName = rs.getString("parent_name");
                if ( parentName == null ){
                    parentName = "";
                }
                    
                Element el = CatalogXmlBuilder.get().buildDependencyDom4j(
                    parentName,
                    new Long(rs.getLong("id")),
                    "?command=get-photo-command&id=" + rs.getLong("id"),
                    rs.getString("name")           
                );
                el.addAttribute("lang-code", rs.getString("lang_code"));
                el.addAttribute("module", MODULE);
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
        logger.info("locator invoked" + catalogPath);
        PreparedStatement stmt = null;
        try{
            stmt = con.prepareStatement(HAS_DEPENDENT_PHOTOS);
            stmt.setString(1, "%" + catalogPath + "%");
            stmt.setString(2, "%" + catalogPath + "%");
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
