/*
 * @(#)$ValidateAccessCommand.java$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.photoalbum.command;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.list.command.ModuleConstants;

/**
 * @author Sergiy Oliynyk
 *
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ValidateAccessCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(ValidateAccessCommand.class);

    public static final String INPUT_ALBUM_NAME = "albumName";
    public static final String INPUT_LOGIN = "login";

    private static final String RESULT_OK = "ok";
    private static final String RESULT_USER_EXISTS = "user exists";
    private static final String RESULT_GROUP_EXISTS = "group exists";
    private static final String RESULT_CONTAINER_EXISTS = "container exists";

    // Output parameter
    public static final String OUTPUT_DOCUMENT = "xml";

    @ActiveModuleRequired
    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        User user = request.getSession().getUser();
        Connection conn = null;
        try {
            super.checkPermission(user);
            checkRequest(request);
            conn = DBHelper.getConnection();
            String result = validateAccess(conn, request);
            Document doc = getResultDocument(result);
            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, doc);
            response.setResultName(RESULT_SUCCESS);
            conn.commit();
        }
        catch (Exception ex) {
            logger.error("Cannot process request", ex);
            DBHelper.rollback(conn);
        }
        finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
        return response;
    }

    private String validateAccess(Connection conn, RequestContext request)
        throws Exception
    {
        logger.debug("+");
        String albumName = request.getParameter(INPUT_ALBUM_NAME);
        String login = request.getParameter(INPUT_LOGIN);
        String group = albumName + " visitors";
        String result = RESULT_OK;
        if (userExists(conn, login))
            result = RESULT_USER_EXISTS;
        else if (groupExists(conn, group))
            result = RESULT_GROUP_EXISTS;
        else if (containerExists(conn, albumName))
            result = RESULT_CONTAINER_EXISTS;
        logger.debug("-");
        return result;
    }

    private boolean userExists(Connection conn, String login)
        throws SQLException
    {
        logger.debug("+");
        boolean result = false;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                "select id from user_list where login='" + login + "'");
            result = rs.next();
        }
        finally {
        	DBHelper.close(rs);
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return result;
    }

    private boolean groupExists(Connection conn, String name)
        throws SQLException
    {
        logger.debug("+");
        boolean result = false;
        ResultSet rs = null;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                "select id from groups where name='" + name + "'");
            result = rs.next();
        }
        finally {
        	DBHelper.close(rs);
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return result;
    }

    private boolean containerExists(Connection conn, String name)
        throws SQLException
    {
        logger.debug("+");
        boolean result = false;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                "select id from containers where name='" + name + "'");
            result = rs.next();
        }
        finally {
        	DBHelper.close(rs);
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return result;
    }
    
    private Document getResultDocument(String resultName) throws Exception {
        logger.debug("+");
        Document doc = Env.createDom();
        Element resultNameElement = Env.createDomElement(doc, "resultName");
        resultNameElement.setAttribute("xmlns:negeso",
            Env.NEGESO_NAMESPACE);
        doc.appendChild(resultNameElement);
        resultNameElement.appendChild(doc.createTextNode(resultName));
        logger.debug("-");
        return doc;
    }

    protected void checkRequest(RequestContext request)
       throws CriticalException
    {
        if (request.getParameter(INPUT_ALBUM_NAME) == null ||
            request.getParameter(INPUT_LOGIN) == null)
            throw new CriticalException("Parameter missing");
    }

	@Override
	public String getModuleName() {
		return ModuleConstants.PHOTO_ALBUM;
	}
}
