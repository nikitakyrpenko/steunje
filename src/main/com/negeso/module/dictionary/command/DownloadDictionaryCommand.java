/*
 * @(#)$Id: DownloadDictionaryCommand.java
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
import java.util.Map;

import org.apache.log4j.Logger;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.view.AbstractHttpView;
import com.negeso.module.dictionary.DictionaryUtil;
import com.negeso.module.dictionary.generators.DictionaryScriptBuilder;

/**
 * @author Sergiy Oliynyk
 */
public class DownloadDictionaryCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(DownloadDictionaryCommand.class);

    // Input parameters
    public static final String INPUT_LANGUAGES = "languages";

    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        User user = request.getSession().getUser();
        Connection conn = null;
        try {
            super.checkPermission(user);
            checkRequest(request);
            String[] languages = 
				request.getParameter(INPUT_LANGUAGES).split(";");
			logger.error("languages.lenght=" + languages.length);
            String fileName = DictionaryUtil.getPath("dictionary.sql");
            conn = DBHelper.getConnection();
            DictionaryScriptBuilder builder = 
                DictionaryScriptBuilder.getInstance();
			logger.error("fileName=" + fileName);
            builder.generate(conn, fileName, languages);
            Map resultMap = response.getResultMap();
            resultMap.put(AbstractHttpView.HEADER_MIME_TYPE, "text/javascript");
            resultMap.put("file", new File(fileName));
            resultMap.put("Expires", new Long(0));
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
        throws CriticalException
    {
        logger.debug("+");
        if (request.getParameter(INPUT_LANGUAGES) == null)
            throw new CriticalException("The parameter 'Languages' missing");
        logger.debug("-");
    }
}
