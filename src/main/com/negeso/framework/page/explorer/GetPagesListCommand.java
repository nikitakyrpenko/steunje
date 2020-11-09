/*
 * @(#)GetPagesListCommand.java       @version	11.03.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.page.explorer;

import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.security.SecurityGuard;

/**
 * @author Sergiy Oliynyk
 */

public class GetPagesListCommand extends AbstractCommand {

    private static Logger logger = 
        Logger.getLogger(GetPagesListCommand.class);

    // Output parameters
    public static final String OUTPUT_DOCUMENT = "xml";

    public ResponseContext execute() {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        RequestContext request = getRequestContext();
        User user = request.getSession().getUser();
        Connection conn = null;
        
        try {
            if (!SecurityGuard.canContribute(user, null)) {
                response.setResultName(RESULT_ACCESS_DENIED);
                logger.warn("- Access denied");
                return response;
            }
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            response.setResultName(RESULT_SUCCESS);
            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, 
                PageExplorerXmlBuilder.getInstance().getDocument(
                    conn, user.getId()));
        }
        catch (Exception ex) {
            response.setResultName(RESULT_FAILURE);
            logger.error("- Cannot process request", ex);
        }
        finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
        return response;
    }    
}
