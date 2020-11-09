/*
 * @(#) UpdateDictionaryFileCommand.java
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

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.module.dictionary.DictionaryUtil;
import com.negeso.module.dictionary.domain.DictionaryFile;
import com.negeso.module.dictionary.generators.DictionaryFileBuilder;

/**
 * @author Sergiy Oliynyk
 */
public class UpdateDictionaryFileCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(UpdateDictionaryFileCommand.class);

    // Input parameters
    public static final String INPUT_FILE_ID = "dictionaryFileId";
    public static final String INPUT_FILE_NAME = "fileName";
    public static final String INPUT_DESCRIPTION = "description";
   
    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        User user = request.getSession().getUser();
        Connection conn = null;
        try {
            super.checkPermission(user);
            checkRequest(request);
            conn = DBHelper.getConnection();
            Long id = request.getLong(INPUT_FILE_ID);
            String fileName = request.getParameter(INPUT_FILE_NAME);
            String description = request.getParameter(INPUT_DESCRIPTION);
            DictionaryFile file = null;
            if (id != null) {
                file = DictionaryFile.findById(conn, id);
                new File(DictionaryUtil.getPath(file.getFileName())).delete();
                file.setFileName(fileName);
                file.setDescription(description);
                file.update(conn);
            }
            else {
                file = new DictionaryFile();
                file.setFileName(fileName);
                file.setDescription(description);
                file.insert(conn);
            }
            conn.commit();
            DictionaryFileBuilder builder = DictionaryFileBuilder.getInstance();
            builder.generate(conn, file);
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
        if (request.getParameter(INPUT_FILE_NAME) == null ||
            request.getParameter(INPUT_DESCRIPTION) == null)
            throw new CriticalException("Parameter missing");
    }
}
