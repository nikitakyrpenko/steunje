/*
 * @(#)GetSecureFileCommand.java  Created on 27.02.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.command;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;


import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.DBHelper;


/**
 *
 *
 * @version 1.0
 * @author Stanislav Demchenko
 */

public class GetSecureFileCommand extends AbstractCommand {


    private static Logger logger = Logger.getLogger(GetSecureFileCommand.class);


    public static final String ACCESS_DENIED = "access_denied";


    /**
     * Serves protected resources. Makes user validity check beforehand.
     *
     * @return if user is not authorized, GetFileCommand.RESULT_FAILURE;
     *         otherwise, executes GetFileCommand
     */
    public ResponseContext execute()
    {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        response.setResultName(GetFileCommand.RESULT_FAILURE);
        SessionData session = getRequestContext().getSessionData();
        session.startSession();
        if(isUserValid(session.getUserId())){
            GetFileCommand cmdGetFile = new GetFileCommand();
            cmdGetFile.setRequestContext(getRequestContext());
            response = cmdGetFile.execute();
            response.getResultMap()
                .put(GetFileCommand.OUTPUT_EXPIRES, new Long(0));
        }
        logger.debug("-");
        return response;
    }


    /**
     * Returns true, if user is fuond in table 'user', and false otherwise.
     */
    private boolean isUserValid(int uid)
    {
        logger.debug("+");
        Connection conn = null;
        try{
            conn = DBHelper.getConnection();
            ResultSet userRst = conn.createStatement().executeQuery(
                "SELECT id from user_list WHERE id = " + uid);
            if(userRst.next()){
                logger.debug("- user valid");
                return true;
            }
        }catch(SQLException e){
            logger.error("SQLException", e);
        }finally{
            DBHelper.close(conn);
        }
        logger.debug("- unknown user");
        return false;
    }


}
