/*
 * @(#)CreateListItemCommand.java       @version	18.02.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.list.command;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.command.SetSearchIndexDirty;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.domain.List;
import com.negeso.framework.list.domain.ListItem;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.util.TimeUtil;
import com.negeso.module.DefaultNameNumerator;

/**
 * @author		Sergiy Oliynyk
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CreateListItemCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(CreateListItemCommand.class);

    // Input parameters
    public static final String INPUT_LIST_ID = "listId";
    public static final String INPUT_SUMMARY = "summary";
    public static final String INPUT_DETAILS = "details";
    public static final String INPUT_LIST_PATH = "listPath";
    public static final String INPUT_LIST_LEVEL = "listLevel";
    public static final String DEFAULT_LIST_ITEM_NAME = "New title";

    private Article summaryArticle;
    private Article detailsArticle;

    /*
     * Essential rights: user must be a contributor of the list
     */
    @SetSearchIndexDirty
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
            List list = List.getById(conn, request.getLong(INPUT_LIST_ID));
            checkPermission(user, list.getContainerId());
            createArticles(conn, request, list);
            ListItem listItem = createListItem(conn, list, user);
            listItem.setPerLangId(DBHelper.getNextInsertId(conn, listItem.getTableId() + "_id_seq"));
            listItem.insert(conn);
            String listPath = request.getParameter(INPUT_LIST_PATH);
            String param = request.getParameter(INPUT_LIST_LEVEL);
            if (listPath != null && param != null) {
                int level = Integer.parseInt(param);
                if (level < getNumLevels(listPath)) {
                    // next level list creation
                    List nextList = createList(list, listItem);
                    nextList.insert(conn);
                }
            }
            request.setParameter(GetListItemCommand.INPUT_LIST_ITEM_ID,
                String.valueOf(listItem.getId()));
            conn.commit();
        }
        catch (Exception ex) {
            logger.error("Cannot process request", ex);
            DBHelper.rollback(conn);
        }
        finally {
            DBHelper.close(conn);
        }
        
        GetListItemCommand command = new GetListItemCommand();
        command.setRequestContext(request);
        logger.debug("-"); 
        return command.execute();
    }
    
    protected void checkRequest(RequestContext request)
        throws CriticalException
    {
        if (request.getLong(INPUT_LIST_ID) == null ||
            request.getParameter(INPUT_SUMMARY) == null ||
            request.getParameter(INPUT_DETAILS) == null)
            throw new CriticalException("Parameter missing");
    }

    /*
     * Checks permission to process this operation 
     */
    private void checkPermission(User user, Long listContainerId)
        throws AccessDeniedException
    {
        if (!SecurityGuard.canContribute(user, listContainerId))
            throw new AccessDeniedException("Forbidden");
    }

    private void createArticles(Connection conn, RequestContext request, 
        List list) throws CriticalException
    {
        logger.debug("+");
        if (request.getParameter(INPUT_SUMMARY).equals("true")) {
            summaryArticle = createArticle(list);
            summaryArticle.insert(conn);
        }
        if (request.getParameter(INPUT_DETAILS).equals("true")) {
            detailsArticle = createArticle(list);
            detailsArticle.insert(conn);
        }
        logger.debug("-");
    }

    private String getNumbersSql(Long listId){
    	
    	StringBuffer query = new StringBuffer(700);
    	return query.append("SELECT DISTINCT CASE WHEN substr(title, 11) = '' THEN '0'").
    		append(" ELSE substr(title, 11) ").
    		append(" END ").
    		append(" as number FROM list_item, list").
    		append(" WHERE list_id = " + listId).
    		append(" AND   list_item.list_id = list.id ").
    		append(" AND (title ~ '" + DEFAULT_LIST_ITEM_NAME + " [0-9]+' ").
    		append(" OR title  =  '"+ DEFAULT_LIST_ITEM_NAME + "') ").
    		append(" ORDER by number ").toString();
}
    
    // Create new listItem
    // New item has the same container as a list
    protected ListItem createListItem(Connection conn,
    		                          List list, User user) {
        logger.debug("+");
        ListItem listItem = new ListItem();
        listItem.setListId(list.getId());
        listItem.setTitle(
        		          DefaultNameNumerator.getDefaultName(conn,
        		        		  getNumbersSql(list.getId()), 
        		        		  DEFAULT_LIST_ITEM_NAME
        		        		  )
        		          );
        if (summaryArticle != null)
            listItem.setTeaserId(summaryArticle.getId());
        if (detailsArticle != null)
            listItem.setArticleId(detailsArticle.getId());
        Timestamp currentTime = new Timestamp(TimeUtil.getMidTime());
        listItem.setCreatedDate(currentTime);
        listItem.setLastModifiedDate(currentTime);
        listItem.setPublishDate(currentTime);
        listItem.setExpiredDate(currentTime);
        listItem.setViewDate(currentTime); 
        listItem.setCreatedBy(user.getId());
        listItem.setLastModifiedBy(user.getId());
        listItem.setContainerId(list.getContainerId());
        logger.debug("-");
        return listItem;
    }

    // Creates the next level list
    protected List createList(List list, ListItem parentItem) {
        logger.debug("+");
        List nextList = new List(null,
            parentItem.getTitle(),
            list.getLangId(),
            list.getImageWidth(),
            list.getImageHeight(),
            list.getModuleId(),
            null,
            list.getTemplateId(),
            parentItem.getId(),
            list.getContainerId());
        logger.debug("-");
        return nextList;
    }
    
    /**
     * Creates a new item
     */
    protected Article createArticle(List list) {
        logger.debug("+");
        Article article = new Article();
        article.setLangId(list.getLangId());
        article.setText("Text");
        article.setContainerId(null);
        logger.debug("-");
        return article;
    }

    public static int getNumLevels(String listPath) {
        StringTokenizer st = new StringTokenizer(listPath, ";");
        return st.countTokens();
    }
}
