/*
 * @(#)UpdateDictionaryCommand.java
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.dictionary.command;

import java.sql.Connection;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.module.dictionary.domain.Dictionary;
import com.negeso.module.dictionary.domain.DictionaryFile;
import com.negeso.module.dictionary.generators.DictionaryFileBuilder;

/**
 * @author Sergiy.Oliynyk
 */
public class UpdateDictionaryCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(UpdateDictionaryCommand.class);

    // Input parameters is the set of fields with the following prexif
    private static final String INPUT_ID = "id";

    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        User user = request.getSession().getUser();
        Connection conn = null;
        try {
            super.checkPermission(user);
            checkRequest(request);
            conn = DBHelper.getConnection();
            Dictionary dictionaryEntry = null;
            Iterator names = request.getParameterNames();
            while (names.hasNext()) {
                String paramName = (String)names.next();
                if (paramName.startsWith(INPUT_ID)) {
                    Long id = Long.valueOf(paramName.substring(
                        INPUT_ID.length()));
                    String entry = request.getParameter(paramName);
                    entry = entry.replaceAll("\"", "'");
                    dictionaryEntry = Dictionary.findById(conn, id);
                    dictionaryEntry.setEntry(entry);
                    dictionaryEntry.update(conn);
                }
            }
            conn.commit();
            if (dictionaryEntry != null) {
                DictionaryFile file = DictionaryFile.findById(conn, 
                    dictionaryEntry.getDictionaryFileId());
                DictionaryFileBuilder builder = DictionaryFileBuilder.getInstance();
                builder.generate(conn, file);
            }
        }
        catch (Exception ex) {
            logger.error("Cannot process request", ex);
            DBHelper.rollback(conn);
        }
        finally {
            DBHelper.close(conn);
        }
        AbstractCommand command = new GetDictionaryCommand();
        command.setRequestContext(request);
        logger.debug("-");
        return command.execute();
    }
    
    protected void checkRequest(RequestContext request) 
       throws CriticalException {}
}
