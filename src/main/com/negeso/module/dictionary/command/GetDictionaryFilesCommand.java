/*
 * @(#)GetDictionaryFilesCommand.java
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

import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.module.dictionary.generators.DictionaryFilesXmlBuilder;
import com.negeso.module.dictionary.generators.LanguagesXmlBuilder;

/**
 * @author Sergiy.Oliynyk
 */
public class GetDictionaryFilesCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(GetDictionaryFilesCommand.class);

    // Input parameters
    public static final String INPUT_LANG_CODE_FROM = "langCodeFrom";
    public static final String INPUT_LANG_CODE_TO = "langCodeTo";
    public static final String INPUT_SELECTED = "selected";
    public static final String INPUT_INTERFACE_TYPE = "interface_type";

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
            conn = DBHelper.getConnection();
            DictionaryFilesXmlBuilder builder = 
                DictionaryFilesXmlBuilder.getInstance();
            String langCodeTo = request.getParameter(INPUT_LANG_CODE_TO);
            if (langCodeTo == null)
                langCodeTo = "nl";
            Document doc = builder.getDocument(conn, langCodeTo);
            setAdditionalAttributes(doc.getDocumentElement(), request);
            doc.getDocumentElement().appendChild(
                LanguagesXmlBuilder.getInstance().getElement(conn, doc));
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
    
    private void setAdditionalAttributes(Element element, RequestContext request)
    {
        logger.debug("+");
        String langCodeFrom = request.getParameter(INPUT_LANG_CODE_FROM);
        String langCodeTo = request.getParameter(INPUT_LANG_CODE_TO);
        if (langCodeFrom == null)
            langCodeFrom = "en";
        if (langCodeTo == null)
            langCodeTo = "nl";
        element.setAttribute("lang_code_from", langCodeFrom);
        element.setAttribute("lang_code_to", langCodeTo);
        String selected = request.getParameter(INPUT_SELECTED);
        if (selected != null)
            element.setAttribute("selected", selected);
        String interfaceType = request.getParameter(INPUT_INTERFACE_TYPE);
        if (interfaceType != null)
            element.setAttribute("interface_type", interfaceType);
        logger.debug("-");
    }

    protected void checkRequest(RequestContext request)
        throws CriticalException {}
}
