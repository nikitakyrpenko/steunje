/*
 * @(#)DeleteDictionaryFileCommand.java
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.dictionary.command;

import java.io.File;
import java.sql.Connection;

import org.apache.log4j.Logger;


import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.module.dictionary.domain.DictionaryFile;

/**
 * @author Sergiy Oliynyk
 *
 */
public class DeleteDictionaryFileCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(DeleteDictionaryFileCommand.class);

    // Input parameters
    public static final String INPUT_DICTIONARY_FILE_ID = "dictionaryFileId";
    
    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        User user = request.getSession().getUser();
        Connection conn = null;
        try {
            super.checkPermission(user);
            checkRequest(request);
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            Long dictionaryFileId = request.getLong(INPUT_DICTIONARY_FILE_ID);
            DictionaryFile dictionaryFile = DictionaryFile.findById(conn,
                dictionaryFileId);
            dictionaryFile.delete(conn);
            new File(Env.getRealPath(dictionaryFile.getFileName())).delete();
            conn.commit();
        }
        catch (Exception ex) {
            logger.error("Cannot process request", ex);
            DBHelper.rollback(conn);
        }
        finally {
            DBHelper.close(conn);
        }
        AbstractCommand command = new GetDictionaryFilesCommand();
        request.setParameter("interface_type", "developer");
        command.setRequestContext(request);
        logger.debug("-");
        return command.execute();
    }
    
    protected void checkRequest(RequestContext request)
       throws CriticalException
    {
        if (request.getLong(INPUT_DICTIONARY_FILE_ID) == null)
            throw new CriticalException("Parameter missing");
    }
}
