/*
 * @(#)$Id: GetDictionaryFileCommand.java
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
 */
public class GetDictionaryFileCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(GetDictionaryFileCommand.class);

    // Input parameters
    public static final String INPUT_FILE_ID = "dictionaryFileId";

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
            Long id = request.getLong(INPUT_FILE_ID);
            Document doc = Env.createDom();
            if (id != null) {
                conn = DBHelper.getConnection();
                DictionaryFile dictionaryFile = DictionaryFile.findById(conn, id);
                doc.appendChild(dictionaryFile.getElement(doc));
                doc.getDocumentElement().setAttribute("xmlns:negeso",
                    Env.NEGESO_NAMESPACE);
            }
            else {
                doc.appendChild(Env.createDomElement(doc, "dictionaryFile"));
            }
            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, doc);
            response.setResultName(RESULT_SUCCESS);
            logger.debug("-");
        }
        catch (Exception ex) {
            response.setResultName(RESULT_FAILURE);
            logger.error("- Cannot process request", ex);
        }
        finally {
            DBHelper.close(conn);
        }
        return response;
    }

    protected void checkRequest(RequestContext request) 
        throws CriticalException {}
}
