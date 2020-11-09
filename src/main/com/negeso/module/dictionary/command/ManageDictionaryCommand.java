/*
 * @(#)ManageDictionaryCommand.java       @version   04.06.2004
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
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.module.dictionary.DictionaryUtil;
import com.negeso.module.dictionary.domain.DictionaryFile;
import com.negeso.module.dictionary.domain.Dictionary;
import com.negeso.module.dictionary.generators.DictionaryFileBuilder;
import com.negeso.module.dictionary.generators.DictionaryFilesXmlBuilder;
import com.negeso.module.dictionary.generators.DictionaryXmlBuilder;

/**
 * @author Sergiy.Oliynyk
 */
public class ManageDictionaryCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(ManageDictionaryCommand.class);

    // Input parameters
    public static final String INPUT_DICTIONARY_FILE_ID = "dictionaryFileId";
    public static final String INPUT_DICTIONARY_ID = "id";
    public static final String INPUT_ACTION = "action";
    public static final String INPUT_MOVE_TO_ID = "moveToId";
    public static final String INPUT_IS_MOVE = "isMove";
    public static final String INPUT_ENTRY_ID = "entryId";
    public static final String INPUT_ENTRY = "entry";

    // Output parameters
    public static final String OUTPUT_DOCUMENT = "xml";

    public static final String ACTION_GET = "get";
    public static final String ACTION_ENTRIES = "entries";
    public static final String ACTION_FILES = "files";
    public static final String ACTION_ADD = "add";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_MOVE = "move";

    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        Connection conn = null;
        Document doc = null;
        try {
            super.checkPermission(request.getSession().getUser());
            checkRequest(request);
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            String action = request.getParameter("action");
            if (action.equals(ACTION_GET))
                doc = getDictionary(conn, request);
            else if (action.equals(ACTION_FILES)) {
                DictionaryFilesXmlBuilder builder =
                    DictionaryFilesXmlBuilder.getInstance();
                doc = builder.getDocument(conn, "en");
            }
            else if (action.equals(ACTION_ENTRIES))
                doc = getDictionaryEntries(conn, request);
            else if (action.equals(ACTION_ADD)) {
                if (request.getParameter(INPUT_ENTRY_ID) != null)
                    addDictionaryEntry(conn, request);
                else {
                    doc = Env.createDom();
                    doc.appendChild(Env.createDomElement(doc, "dictionary"));
                }
            }
            else if (action.equals(ACTION_DELETE))
                deleteDictionaryEnries(conn, request);
            else if (action.equals(ACTION_MOVE))
                moveDictionaryEntry(conn, request);
            else if (action.equals(ACTION_UPDATE))
                updateDictionaryEntries(conn, request);
            conn.commit();
            if (doc == null)
                doc = getDictionaryEntries(conn, request);
            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, doc);
            response.setResultName(RESULT_SUCCESS);
        }
        catch (Exception ex) {
            DBHelper.rollback(conn);
            response.setResultName(RESULT_FAILURE);
            logger.error("- Cannot process request", ex);
        }
        logger.debug("-");
        return response;
    }

    private Document getDictionary(Connection conn, RequestContext request)
        throws CriticalException
    {
        logger.debug("+");
        Long id = request.getLong(INPUT_DICTIONARY_ID);
        if (id == null) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException("Parameter missing");
        }
        Dictionary dict = Dictionary.findById(conn, id);
        Document doc = null;
        try {
            doc = Env.createDom();
            doc.appendChild(dict.getElement(doc));
        }
        catch (Exception ex) {
            logger.error(ex);
        }
        logger.debug("-");
        return doc;
    }

    private Document getDictionaryEntries(Connection conn, 
        RequestContext request) throws CriticalException
    {
        logger.debug("+");
        Long dictionaryFileId = request.getLong(INPUT_DICTIONARY_FILE_ID);
        if (dictionaryFileId == null) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException("Parameter missing");
        }
        DictionaryXmlBuilder builder = DictionaryXmlBuilder.getInstance();
        Document doc = builder.getDocument(conn, dictionaryFileId);
        logger.debug("-");
        return doc;
    }

    private void addDictionaryEntry(Connection conn, RequestContext request)
        throws CriticalException
    {
        logger.debug("+");
        Long dictionaryFileId = request.getLong(INPUT_DICTIONARY_FILE_ID);
        String entryId = request.getParameter(INPUT_ENTRY_ID);
        String entry = request.getParameter(INPUT_ENTRY);
        if (dictionaryFileId == null || entryId == null || entry == null) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException("Parameter missing");
        }
        Dictionary dict = new Dictionary();
        dict.setDictionaryFileId(dictionaryFileId);
        dict.setEntryId(entryId);
        dict.setlangCode("en");
        dict.setEntry(entry);
        dict.insert(conn);
        String[] languages = DictionaryUtil.getLanguages(conn);
        for (int i = 0; i < languages.length; i++) {
            if (!languages[i].equals("en")) {
                dict.setlangCode(languages[i]);
                dict.setEntry("");
                dict.insert(conn);
            }
        }
        DictionaryFile file = DictionaryFile.findById(conn, dictionaryFileId);
        generateDictionaryFile(conn, file);
        logger.debug("-");
    }

    private void updateDictionaryEntries(Connection conn,
        RequestContext request) throws CriticalException
    {
        logger.debug("+");
        Long id = request.getLong(INPUT_DICTIONARY_ID);
        String entryId = request.getParameter(INPUT_ENTRY_ID);
        String entry = request.getParameter(INPUT_ENTRY);
        if (id == null || entryId == null || entry == null) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException("Parameter missing");
        }
        Dictionary dict = Dictionary.findById(conn, id);
        boolean isChanged = false;
        if (!dict.getEntry().equals(entry)) {
            dict.setEntry(entry);
            dict.update(conn);
            isChanged = true;
        }
        if (!dict.getEntryId().equals(entryId)) {
            Dictionary[] items = DictionaryUtil.findEntries(conn,
                dict.getDictionaryFileId(), dict.getEntryId());
            for (int i = 0; i < items.length; i++) {
                items[i].setEntryId(entryId);
                items[i].update(conn);
            }
            isChanged = true;
        }
        if (isChanged) {
            DictionaryFile file = DictionaryFile.findById(conn,
                dict.getDictionaryFileId());
            generateDictionaryFile(conn, file);
        }
        logger.debug("-");
    }

    private void deleteDictionaryEnries(Connection conn, RequestContext request)
        throws CriticalException
    {
        logger.debug("+");
        Long id = request.getLong(INPUT_DICTIONARY_ID);
        if (id == null) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException("Parameter missing");
        }
        Dictionary dict = Dictionary.findById(conn, id);
        Dictionary[] items = DictionaryUtil.findEntries(conn,
            dict.getDictionaryFileId(), dict.getEntryId());
        for (int i = 0; i < items.length; i++) {
            items[i].delete(conn);
        }
        DictionaryFile file = DictionaryFile.findById(conn,
            dict.getDictionaryFileId());
        generateDictionaryFile(conn, file);
        logger.debug("-");
    }

    private void moveDictionaryEntry(Connection conn, RequestContext request)
        throws CriticalException
    {
        logger.debug("+");
        Long id = request.getLong(INPUT_DICTIONARY_ID);
        Long moveToId = request.getLong(INPUT_MOVE_TO_ID);
        if (id == null || moveToId == null) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException("Parameter missing");
        }
        boolean isMove = "true".equals(request.getParameter(INPUT_IS_MOVE));
        Dictionary dict = Dictionary.findById(conn, id);
        if (!dict.getDictionaryFileId().equals(moveToId)) {
            Dictionary[] items = DictionaryUtil.findEntries(conn,
                dict.getDictionaryFileId(), dict.getEntryId());
            for (int i = 0; i < items.length; i++) {
                items[i].setDictionaryFileId(moveToId);
                if (isMove)
                    items[i].update(conn);
                else
                    items[i].insert(conn);
            }
            DictionaryFile file = DictionaryFile.findById(conn,
                dict.getDictionaryFileId());
            DictionaryFile moveToFile = DictionaryFile.findById(conn, moveToId);
            generateDictionaryFile(conn, file);
            generateDictionaryFile(conn, moveToFile);
        }
        logger.debug("-");
    }

    private void generateDictionaryFile(Connection conn,
        DictionaryFile dictionaryFile) throws CriticalException
    {
        logger.debug("+");
        DictionaryFileBuilder builder = DictionaryFileBuilder.getInstance();
        builder.generate(conn, dictionaryFile);
        logger.debug("-");
    }
    
    protected void checkRequest(RequestContext request)
        throws CriticalException
    {
         if (request.getParameter(INPUT_ACTION) == null)
             throw new CriticalException("Parameter missing");
    }
}
