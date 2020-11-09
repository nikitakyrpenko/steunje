/*
 * @(#)GetDictionaryCommand.java
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
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.module.dictionary.generators.DictionaryXmlBuilder;


/**
 * @author Sergiy.Oliynyk
 */
public class GetDictionaryCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(GetDictionaryCommand.class);

    // Input parameters
    public static final String INPUT_DICTIONARY_FILE_ID = "dictionaryFileId";
    public static final String INPUT_LANG_CODE_FROM = "langCodeFrom";
    public static final String INPUT_LANG_CODE_TO = "langCodeTo";

    // Output parameters
    public static final String OUTPUT_DOCUMENT = "xml";
       
    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        User user = request.getSession().getUser();
        Connection conn = null;
        try {
            super.checkPermission(user);
            Long dictionaryFileId = request.getLong(INPUT_DICTIONARY_FILE_ID);
            String langCodeFrom = request.getParameter(INPUT_LANG_CODE_FROM);
            String langCodeTo = request.getParameter(INPUT_LANG_CODE_TO);
            conn = DBHelper.getConnection();
            DictionaryXmlBuilder builder =
                DictionaryXmlBuilder.getInstance();
            Document doc = builder.getDocument(conn, dictionaryFileId,
                langCodeFrom, langCodeTo);
            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, doc);
            response.setResultName(RESULT_SUCCESS);
            logger.debug("-");
        }
        catch (Exception ex) {
            logger.error("- Cannot process request", ex);
            DBHelper.rollback(conn);
        }
        finally {
            DBHelper.close(conn);
        }
        return response;
    }
    
    protected void checkRequest(RequestContext request) 
       throws CriticalException
    {
        if (request.getParameter(INPUT_DICTIONARY_FILE_ID) == null ||
            request.getParameter(INPUT_LANG_CODE_FROM) == null ||
            request.getParameter(INPUT_LANG_CODE_TO) == null)
            throw new CriticalException("Parameter missing");
    }
}
