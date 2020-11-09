/*
 * @(#)$GetAlbumsTreeCommand.java$
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
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.w3c.dom.Document;

import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.module.photoalbum.generators.AdminXmlBuilder;

/**
 * @author Sergiy Oliynyk
 *
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GetAlbumsTreeCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(GetAlbumsTreeCommand.class);

    public static final String INPUT_ID = "id";

    // Output parameters
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
            AdminXmlBuilder builder = AdminXmlBuilder.getInstance();
            Document doc = builder.getDocument(conn, user);
            Long id = request.getLong(INPUT_ID);
            if (id != null)
                doc.getDocumentElement().setAttribute("albumId", id.toString());
            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, doc);
            response.setResultName(RESULT_SUCCESS);
        }
        catch (AccessDeniedException ex) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.warn("- Access denied", ex);
        }
        catch (Exception ex) {
            response.setResultName(RESULT_FAILURE);
            logger.error("-", ex);
        }
        finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
        return response;
    }
    
    protected void checkRequest(RequestContext request)
       throws CriticalException {}

	@Override
	public String getModuleName() {
		return ModuleConstants.PHOTO_ALBUM;
	}
}
