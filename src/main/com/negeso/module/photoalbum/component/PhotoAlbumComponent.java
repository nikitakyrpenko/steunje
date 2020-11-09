/*
 * @(#)$PhotoAlbumComponent.java$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.photoalbum.component;

import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.page.PageComponent;
import com.negeso.module.photoalbum.generators.VisitorXmlBuilder;

/**
 * @author Sergiy Oliynyk
 *
 */
public class PhotoAlbumComponent implements PageComponent {

    private static Logger logger = Logger.getLogger(PhotoAlbumComponent.class);

    // Input parameters
    public static final String INPUT_ID = "albumId";

    public Element getElement(Document document, RequestContext request,
        Map parameters)
    {
        logger.debug("+");
        Connection conn = null;
        Element element = null;
        User user = request.getSession().getUser();
        try {
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            Long id = request.getLong(INPUT_ID);
            VisitorXmlBuilder builder = VisitorXmlBuilder.getInstance();
            element = builder.getElement(conn, document, id, user);
        }
        catch (Exception ex) {
            logger.error("- Cannot process request", ex);
            element = getStubElement(document);
        }
        finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
        return element;
    }

    public Element getStubElement(Document doc) {
        logger.debug("+");
        return Env.createDomElement(doc, "photo_album");
    }
}
