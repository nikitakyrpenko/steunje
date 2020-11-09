/*
 * @(#)$ValidateEntryCommand.java$
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
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.module.dictionary.domain.Dictionary;
import com.negeso.module.dictionary.domain.DictionaryFile;

/**
 * @author Sergiy Oliynyk
 *
 */
public class ValidateEntryCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(ValidateEntryCommand.class);

    public static final String INPUT_ENTRY_ID = "entryId";

    // Output parameter
    public static final String OUTPUT_DOCUMENT = "xml";

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
            String result = validateRequest(conn, request);
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

    private String validateRequest(Connection conn, RequestContext request)
        throws Exception
    {
        logger.debug("+");
		String result = "ok";
		String entryId = request.getParameter(INPUT_ENTRY_ID);
		Dictionary dict = Dictionary.findByEntryId(conn, entryId, "en");
		if (dict != null) {
			DictionaryFile file = DictionaryFile.findById(conn,
               dict.getDictionaryFileId());
			result = file.getDescription();
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
        if (request.getParameter(INPUT_ENTRY_ID) == null)
            throw new CriticalException("Parameter missing");
    }
}
