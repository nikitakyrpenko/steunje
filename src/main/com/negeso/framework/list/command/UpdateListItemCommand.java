/*
 * @(#)UpdateListItemCommand.java       @version	25.10.2004
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import com.negeso.framework.Env;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.command.SetSearchIndexDirty;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.User;
import com.negeso.framework.friendly_url.FriendlyUrlService;
import com.negeso.framework.friendly_url.UrlAnalyzer;
import com.negeso.framework.friendly_url.UrlEntityType;
import com.negeso.framework.list.domain.List;
import com.negeso.framework.list.domain.ListItem;
import com.negeso.framework.list.service.ListItemService;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.site.SiteUrl;
import com.negeso.framework.site.SiteUrlCache;
import com.negeso.framework.util.TimeUtil;
import com.negeso.module.social.SocialNetworkException;
import com.negeso.module.social.bo.SocialNetwork;
import com.negeso.module.social.service.SocialNetworkService;

/**
 * @author		Sergiy Oliynyk
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class UpdateListItemCommand extends AbstractCommand {

	private static Logger logger = Logger.getLogger(UpdateListItemCommand.class);

	// Input parameters
	public static final String INPUT_LIST_ITEM_ID = "listItemId";
	public static final String INPUT_TITLE = "titleField";
	public static final String INPUT_EXPIRED_DATE = "expiredDateField";
	public static final String INPUT_PUBLISH_DATE = "publishDateField";
	public static final String INPUT_VIEWABLE_DATE = "viewableDateField";
	public static final String INPUT_PAGE_LINK = "pageLink";
    public static final String INPUT_IMAGE_LINK = "imageLink";
    public static final String INPUT_THUMBNAIL_LINK = "thumbnailLink";
    public static final String INPUT_DOCUMENT_LINK = "documentLink";
    public static final String INPUT_PARAMETERS = "parameters";
    public static final String INPUT_LIST_ID = "listId";

    // Parameters for tree list support
    public static final String INPUT_LIST_PATH = "listPath";
    public static final String INPUT_ROOT_LIST_ID = "rootListId";

    /*
     * Essential rights: user must be a contributor
     * of the item's container
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
            ListItem listItem = 
            	ListItemService.getInstance().findById(request.getLong(INPUT_LIST_ITEM_ID));
            checkPermission(user, listItem.getContainerId());
            List parentList = List.findById(conn, listItem.getListId());
            setParametersFromRequest(request, listItem, parentList);
            ListItemService.getInstance().createOrUpdate(listItem);
            listItem.update(conn);
            updateLinks(conn, listItem);
            List list = List.findByParentListItemId(conn, listItem.getId());
            if (list != null) {
                // change name of the next level list if title of parent
                // list item is changing
                list.setName(listItem.getTitle());
                list.update(conn);
            }
            String link = UrlAnalyzer.parse(listItem.getTitle());
            FriendlyUrlService.setUrl(conn, listItem.getId(),Language.findById(parentList.getLangId()), link, UrlEntityType.LIST_ITEM);
            conn.commit();
        }
        catch (Exception ex) {
            logger.error("Cannot process request", ex);
            DBHelper.rollback(conn);
        }
        finally {
            DBHelper.close(conn);
        }

        AbstractCommand command = null;
        if (request.getLong(INPUT_LIST_ID) != null)
            command = new GetListCommand();
        else
            command = new GetListItemCommand(); 
        command.setRequestContext(request);
        logger.debug("-"); 
        return command.execute();
	}

    protected void checkRequest(RequestContext request)
        throws CriticalException
    {
        if (request.getLong(INPUT_LIST_ITEM_ID) == null)
            throw new CriticalException("Parameter missing");
    }

    private void checkPermission(User user, Long containerId)
        throws AccessDeniedException
    {
        if (!SecurityGuard.canContribute(user, containerId))
            throw new AccessDeniedException("Forbidden");
    }

    /*
     * Retrieves parameters from request
     */
    private void setParametersFromRequest(RequestContext request,
        ListItem listItem, List parentList) throws CriticalException, ObjectNotFoundException
    {
        logger.debug("+");
        String title = request.getParameter(INPUT_TITLE);
        Timestamp publishDate = request.getSimpleRoundTimestamp(INPUT_PUBLISH_DATE);
        Timestamp expiredDate = request.getSimpleRoundTimestamp(INPUT_EXPIRED_DATE);
        Timestamp viewDate = request.getSimpleRoundTimestamp(INPUT_VIEWABLE_DATE);
        String pageLink = request.getParameter(INPUT_PAGE_LINK);
        if (pageLink != null && pageLink.length() == 0)
            pageLink = null;
        String imageLink = request.getParameter(INPUT_IMAGE_LINK);
        if (imageLink != null && imageLink.length() == 0)
            imageLink = null;
        String thumbnailLink = request.getParameter(INPUT_THUMBNAIL_LINK);
        if (thumbnailLink != null && thumbnailLink.length() == 0)
            thumbnailLink = null;
        String documentLink = request.getParameter(INPUT_DOCUMENT_LINK);
        if (documentLink != null && documentLink.length() == 0)
            documentLink = null;
        String parameters = request.getParameter(INPUT_PARAMETERS);
        if (parameters != null && parameters.length() == 0)
            parameters = null;
        listItem.setTitle(title);
        listItem.setPublishDate(publishDate);
        listItem.setExpiredDate(expiredDate);
        listItem.setViewDate(viewDate);
        listItem.setImageLink(imageLink);
        listItem.setThumbnailLink(thumbnailLink);
        listItem.setDocumentLink(documentLink);
        listItem.setLink(pageLink);
        listItem.setPerLangId(request.getLong("perLangId"));
        listItem.setLastModifiedBy(
            new Long(request.getSessionData().getUserId()));
        listItem.setLastModifiedDate( new Timestamp(TimeUtil.getMidTime()));
        listItem.setParameters(parameters);
        String mainHostName = SiteUrlCache.getMainHostNameForLanguage(parentList.getLangId());
        String itemUrl = getItemUrl(listItem.getPerLangId(), parentList.getLangId(), mainHostName);
        String imageUrl = StringUtils.isNotBlank(listItem.getImageLink()) ? String.format("%s/%s", mainHostName, listItem.getImageLink()) :  StringUtils.EMPTY;
        for (SocialNetwork socialNetwork : SocialNetworkService.getInstance().list()) {
			if (Boolean.valueOf(request.getParameter(socialNetwork.getTitle())) && !listItem.getPublishedTo().contains(socialNetwork)) {
				socialNetwork.setCurrentLangId(parentList.getLangId());
				try {
					SocialNetworkService.getInstance().publish(
							listItem.getTitle(), 
							Article.findById(listItem.getTeaserId()).getText(),
							itemUrl,
							imageUrl,
							socialNetwork);
					listItem.getPublishedTo().add(socialNetwork);
				} catch (SocialNetworkException e) {
					logger.error(e);
				}
			}
		}
        
        logger.debug("-");
    }
    
    private String getItemUrl(Long perLangId, Long langId, String mainHostName) {
    	PageH page = PageService.getInstance().findByClassAndObligatoryComponent("newsline", langId, "news-line-component");
    	String fileName = StringUtils.EMPTY;
    	if (page != null) {
    		fileName = page.getFilename();
    	}
		return String.format("%s/%s?stm_id=%s", mainHostName, fileName, perLangId);
	}

	private final static String selectLinksSql =
        "select * from list_item where list_item_link = ?  and site_id = ? ";
    
    private void updateLinks(Connection conn, ListItem listItem)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(selectLinksSql);
            stmt.setObject(1, listItem.getId(), Types.BIGINT);
            stmt.setObject(2, Env.getSiteId(), Types.BIGINT);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ListItem link = new ListItem();
                link.load(rs);
                link.setTitle(listItem.getTitle());
                link.setLink(listItem.getLink());
                link.setImageLink(listItem.getImageLink());
                link.setThumbnailLink(listItem.getThumbnailLink());
                link.setDocumentLink(listItem.getDocumentLink());
                link.setViewDate(listItem.getViewDate());
                link.setPublishDate(listItem.getPublishDate());
                link.setExpiredDate(listItem.getExpiredDate());
                link.update(conn);
            }
            rs.close();
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
    }
}
