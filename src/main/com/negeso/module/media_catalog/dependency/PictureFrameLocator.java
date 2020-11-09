/*
 * @(#)$Id: PictureFrameLocator.java,v 1.0.1.0, 2007-02-19 09:20:05Z, Olexiy Strashko$
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


/**
 *
 * Photo album media locator
 * 
 * @version		$Revision: 2$
 * @author		Olexiy Strashko
 * 
 */
public class PictureFrameLocator implements DependencyLocator {
    private static Logger logger = Logger.getLogger(PictureFrameLocator.class);

    private static final String HAS_DEPENDENT_IMAGES =
        " SELECT COUNT(*) AS cnt " +
        " FROM image " +
        " WHERE src LIKE ? " 
    ;

    /**
     * 
     */
    public PictureFrameLocator() {
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
        // UNSUPPORTED
        // UNSUPPORTED
        // UNSUPPORTED
        // UNSUPPORTED
    }
    

    /* (non-Javadoc)
     * @see com.negeso.module.media_catalog.dependency.DependencyLocator#hasDependencies(java.sql.Connection, java.lang.String)
     */
    public boolean hasDependencies(Connection con, String catalogPath)
        throws CriticalException 
    {
    	if (logger.isInfoEnabled()) {
    		logger.info("locator invoked" + catalogPath);
    	}
        PreparedStatement stmt = null;
        try{
            stmt = con.prepareStatement(HAS_DEPENDENT_IMAGES);
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
