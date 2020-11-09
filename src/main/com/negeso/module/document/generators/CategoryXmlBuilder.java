/*
 * @(#)$Id: CategoryXmlBuilder.java,v 1.12, 2006-05-30 09:31:36Z, Dmitry Dzifuta$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.document.generators;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.document.DocumentModule;
import com.negeso.module.document.domain.Category;
import com.negeso.module.document.domain.SortOrder;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;

/**
 *
 * Category xml builder
 * 
 * @version		$Revision: 13$
 * @author		Olexiy Strashko
 * 
 */      
public class CategoryXmlBuilder {
	private static Logger logger = Logger.getLogger( CategoryXmlBuilder.class );
	
    public static Element buildVisitorCategoryXml(Long categoryId, Connection con, Element parent, Long userId, Long langId, SortOrder sortOrder)
			throws Exception{
		Element elt = buildPathXml(categoryId, con, parent, userId, langId, true);
		buildFolderContentXml(categoryId, con, elt, userId, langId, true, sortOrder);
		return elt;
		//return buildAdminCategoryXml(categoryId, con, parent, userId, langId);
    }
	
    /**
     * 
     * @param categoryId
     * @param conn
     * @param parent
     * @param userId
     * @param langId
     * @param isVisitor
     * @return
     * @throws Exception
     */
	private static Element buildFolderContentXml(
        Long categoryId, 
        Connection conn, 
        Element parent, 
        Long userId, 
        Long langId,
        boolean isVisitor, 
		SortOrder sortOrder)

		throws Exception
    {
		Element elt = null;
		
		String categorySortOrder = "order_number";
		String documentSortOrder = "order_number";
		
		PreparedStatement stat = conn.prepareStatement(
				" SELECT dc_category.id, dc_category.name, dc_category.parent_id, "
						+ " mc_folder_id, container_id, containers.name AS container_name "
						+ " FROM dc_category"
						+ " LEFT JOIN mc_folder ON mc_folder.id=mc_folder_id "
						+ " LEFT JOIN containers ON mc_folder.container_id=containers.id "
						+ " WHERE dc_category.parent_id=? and dc_category.site_id=? ORDER BY dc_category."
						+ categorySortOrder + " " + sortOrder.getOrder() + ",dc_category.name");
		stat.setLong(1, categoryId.longValue());
		logger.debug("site id = " + Env.getSiteId());
		stat.setLong(2, Env.getSiteId());
		ResultSet res = stat.executeQuery();
		while (res.next()){
			Long contId = DBHelper.makeLong(res.getLong("container_id"));
			if (res.getObject(5)!=null){
				new Long((Integer)res.getObject(5));
			}
			if (SecurityGuard.canView(userId, contId)){
				elt = Xbuilder.addEl(parent, "category", null);
				elt.setAttribute("id", res.getString("id"));
				elt.setAttribute("parent_id", res.getString("parent_id"));
				elt.setAttribute("name", res.getString("name"));
                if ( !isVisitor ){
                    Folder folder = Repository.get().getFolder(res.getLong("mc_folder_id"));
                    logger.info("Path:" + folder.getCatalogPath());
                    elt.setAttribute("can_view", "true");
                    elt.setAttribute("can_edit", "" + SecurityGuard.canEdit(userId, contId));
                    elt.setAttribute("can_manage", "" + SecurityGuard.canManage(userId, contId));
                    elt.setAttribute("container_id", "" + contId);
                    elt.setAttribute("container_name", res.getString("container_name"));
                    elt.setAttribute("folder_id", "" + res.getLong("mc_folder_id"));
                }
			}
		}
		stat = conn.prepareStatement(
				" SELECT id, name, document_link, description, owner, last_modified, thumbnail_link " +
				" FROM dc_document " +
				" WHERE category_id=? ORDER BY dc_document." + documentSortOrder + " " + sortOrder.getOrder());
		stat.setLong(1, categoryId.longValue());
		res = stat.executeQuery();
		while (res.next()){
			elt = Xbuilder.addEl(parent, "document", null);
			elt.setAttribute("id", res.getString("id"));
			elt.setAttribute("name", res.getString("name"));
			elt.setAttribute("document_link", res.getString("document_link"));
			elt.setAttribute("description", res.getString("description"));
			elt.setAttribute("owner", res.getString("owner"));
			elt.setAttribute("last_modified", Env.formatRoundDate(res.getTimestamp("last_modified")));
			Xbuilder.setAttr(elt, "thumbnail_link", res.getString("thumbnail_link"));
			if (res.getString("thumbnail_link") != null) {
				Xbuilder.setAttr(elt, "type", "image");
			} else {
				Xbuilder.setAttr(elt, "type", "file");
			}
		}
		return parent;
	}
	
    /**
     * 
     * @param categoryId
     * @param conn
     * @param parent
     * @param userId
     * @param langId
     * @param isVisitor
     * @return
     * @throws Exception
     */
    public static Element buildPathXml(
        Long categoryId, 
        Connection conn, 
        Element parent,
        Long userId, 
        Long langId,
        boolean isVisitor
    ) 
		throws Exception
    {
		Element elt = null;
        logger.info("catId:" + categoryId);
		Category cat = Category.findById(conn, categoryId);
        if ( cat == null ){
            logger.info("error: category not found by id: " + categoryId);
            cat = DocumentModule.get().getRootCategory(conn); 
        }
		Folder folder = Repository.get().getFolder( cat.getMcFolderId() ); 
		if (SecurityGuard.canView(userId, folder.getContainerId())){
			elt = parent;
			ArrayList<Category> categoryList = new ArrayList<Category>();
			categoryList.add(cat);
			while (cat.getParentId() != null){
				cat = Category.findById(conn, cat.getParentId());
				categoryList.add(cat);
			}
			for (int i = categoryList.size()-1; i>=0; i--){
				elt = Xbuilder.addEl(elt, "category", null);
				if (i==categoryList.size()-1){
					elt.setAttribute("is_root", "true");
				}
				if (i==0){
					elt.setAttribute("is_current", "true");
				}
				elt.setAttribute("id", categoryList.get(i).getId().toString());
				elt.setAttribute("name", categoryList.get(i).getName());
				if (categoryList.get(i).getParentId()!= null){
					elt.setAttribute("parent_id", categoryList.get(i).getParentId().toString());
                }

                if ( !isVisitor ){
                    Long containerId = null; 
                    folder = Repository.get().getFolder(categoryList.get(i).getMcFolderId());
                    if ( folder != null ){
                        containerId = folder.getContainerId();
                    }
                    elt.setAttribute("can_view", "true");
                    elt.setAttribute("can_edit", "" + SecurityGuard.canEdit(userId, containerId));
                    elt.setAttribute("can_manage", "" + SecurityGuard.canManage(userId, containerId));
                    elt.setAttribute("container_id", "" + containerId);
                    elt.setAttribute("container_name", folder.getContainerName());
                    elt.setAttribute("folder_id", folder.getId().toString());
                }
			}
		}
		else{
			logger.error("CAN'T VIEW");
		}
		return elt;
	}

    public static Element buildAdminCategoryXml(
            Long categoryId, Connection con, Element parent, Long userId, Long langId, SortOrder sortOrder)
            throws CriticalException
        {
            Element elt = null;
            try{
                elt = buildPathXml(categoryId, con, parent, userId, langId, false);
                buildFolderContentXml(categoryId, con, elt, userId, langId, false, sortOrder);
            }
            catch(Exception e){
                logger.error("-errror", e);
                throw new CriticalException(e);
            }
            
            return elt;
        }

    public static Element buildCategoryDetailsXml(
        Long categoryId, Connection con, Element parent, Long userId, Long langId) 
        throws CriticalException
    {
        Element catEl = null;
        try{
            catEl = buildPathXml(categoryId, con, parent, userId, langId, false);
            logger.info("catId:" + categoryId);
            Category cat = Category.findById(con, categoryId);
            if ( cat == null ){
                logger.info("error: category not found by id: " + categoryId);
                return null; 
            }
            Xbuilder.setAttr(catEl, "is_current", "true");
            Xbuilder.setAttr(catEl, "name", cat.getName());
        }
        catch(Exception e){
            logger.error("-errror", e);
            throw new CriticalException(e);
        }
        return catEl;
    }
	
    /**
     * Build new category xml
     * 
     * @param parentCatId
     * @param con
     * @param parent
     * @param userId
     * @param langId
     * @return
     * @throws CriticalException
     */
    public static Element buildNewCategoryDetailsXml(
        Long parentCatId, Connection con, Element parent, Long userId, Long langId) 
        throws CriticalException
    {
        Element catEl = null;
        try{
            catEl = buildPathXml(parentCatId, con, parent, userId, langId, false);
            logger.info("catId:" + parentCatId);
            Category cat = Category.findById(con, parentCatId);
            if ( cat == null ){
                logger.info("error: category not found by id: " + parentCatId);
                return null; 
            }
            Xbuilder.setAttr(catEl, "is_current", null);
            Element newCat = Xbuilder.addEl(catEl, "category", null);
            Xbuilder.setAttr(newCat, "is_current", "true");
            Xbuilder.setAttr(newCat, "parent_id", parentCatId);
        }
        catch(Exception e){
            logger.error("-errror", e);
            throw new CriticalException(e);
        }
        return catEl;
    }

    public static Element buildSearchResultsXml(Connection conn, Element parent, String searchWord, Long userId, SortOrder sortOrder) 
			throws Exception{
		String sql = " SELECT dc_document.name, dc_document.document_link, dc_document.description, dc_document.owner, dc_document.last_modified, dc_document.category_id, mc_folder.container_id, dc_category.name AS cat_name  " +
		" FROM dc_document, mc_folder, dc_category " +
		" WHERE (dc_document.name iLike '%"+searchWord+"%' OR dc_document.description iLike '%"+searchWord+"%' OR dc_document.document_link iLike '%"+searchWord+"%') AND "+
		" dc_document.category_id=dc_category.id AND dc_category.mc_folder_id=mc_folder.id  " +
		" ORDER BY dc_document." + sortOrder.getColumn() + " " + sortOrder.getOrder();
		//logger.error(sql);
		PreparedStatement stat = conn.prepareStatement(sql);
		ResultSet res = stat.executeQuery();
		Long contId = null;
		Element elt = null;
		while (res.next()){
			contId = null;
			if (res.getObject("container_id")!=null){
				contId= res.getLong("container_id");
			}
			if (SecurityGuard.canView(userId, contId)){
				elt = Xbuilder.addEl(parent, "document", null);
				elt.setAttribute("name", res.getString("name"));
				elt.setAttribute("document_link", res.getString("document_link"));
				elt.setAttribute("description", res.getString("description"));
				elt.setAttribute("owner", res.getString("owner"));
				elt.setAttribute("last_modified", Env.formatRoundDate(res.getTimestamp("last_modified")));
				elt.setAttribute("category_id", res.getString("category_id"));
				elt.setAttribute("cat_name", res.getString("cat_name"));
				Xbuilder.setAttr(elt, "thumbnail_link", res.getString("thumbnail_link"));
				if (res.getString("thumbnail_link") != null) {
					Xbuilder.setAttr(elt, "type", "image");
				} else {
					Xbuilder.setAttr(elt, "type", "file");
				}
				
			}
		}
		return parent;
	}
}
