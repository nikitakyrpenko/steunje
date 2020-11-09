/*
 * @(#)ListXmlBuilder.java       @version    09.04.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.list.generators;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.friendly_url.FriendlyUrlService;
import com.negeso.framework.friendly_url.UrlEntityType;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.security.SecurityGuard;

/**
 * @author Sergiy Oliynyk
 *
 */
public abstract class ListXmlBuilder {

    private static Logger logger = Logger.getLogger(ListXmlBuilder.class);
    
    private static Map<Long, String> modules = new HashMap<Long, String>();
    private static Map<Long, Long> listToLang = new HashMap<Long, Long>();
    
    protected ListXmlBuilder() {}

    /**
     * Builds the XML output for the list including its items.
     * Also checks user's rights to the container of each element.
     * @param doc
     * @param list
     * @param user
     * @throws CriticalException
     */
    public Document getDocument(Connection conn, Long listId, Long userId,
        String listPath) throws CriticalException
    {
        logger.debug("+");
        if (conn == null || listId == null) {
            logger.debug("- Invalid argument");
            throw new CriticalException("Invalid argument");
        }
        try {
            Document doc = Env.createDom();
            doc.appendChild(getElement(conn, doc, listId, userId, listPath));
            logger.debug("-");
            return doc;
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
    }

    public Element getElement(Connection conn, Document doc, Long listId, 
        Long userId, String listPath) throws CriticalException
    {
        logger.debug("+");
        if (conn == null || doc == null || listId == null) {
            logger.debug("- Invalid argument");
            throw new CriticalException("Invalid argument");
        }
        try {
            int[] listPathArray = getListPathArray(listPath);
            Element docElement = buildTopLevelListXml(conn, doc, 
                listId.longValue(), userId, listPathArray);
            docElement.setAttribute("xmlns:negeso", Env.NEGESO_NAMESPACE);
            docElement.setAttribute("listPath", getListPath(listPathArray));
            logger.debug("-");
            return docElement;
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
    }

    private static final String listByIdQuery = 
        "select list.id, name, xslt_template.title as template, xslt_template.module_id, container_id, lang_id " +
        "from list left join xslt_template on (list.template_id = " +
        "xslt_template.id) where list.id=";

    private static final String listByParentListItemIdQuery =
        "select list.id, name, xslt_template.title as template , xslt_template.module_id, container_id, lang_id " +
        "from list left join xslt_template on (list.template_id = " +
        "xslt_template.id) ";

    /**
     * Builds xml for a list or tree-list checking of user's right
     * This method builds only the top-level list. For building nested lists
     * calls next buildListXml which searches for list by parent_list_item_id
     * field.
     */
    private Element buildTopLevelListXml(Connection conn, Document doc, 
        long listId, Long userId, int[] listPath) throws CriticalException
    {
        logger.debug("+");
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(listByIdQuery + listId);
            if (!rs.next()) {
                logger.error("- List not found");
                throw new CriticalException("- List with id = " + listId + 
                    " not found");
            }
            Element listElement = Env.createDomElement(doc, "list");
            setListAttributes(listElement, rs);
            XmlHelper.setRightsAttributes(listElement, userId,
                makeLong(rs.getLong("container_id")));
            listElement.setAttribute("level", "0");
            buildItemsXml(conn, listElement, listId, userId, listPath, 0);
            logger.debug("-");
            return listElement;
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
        	DBHelper.close(rs);
            DBHelper.close(stmt);
        }
    }

    /**
     * This method is calling from buildItemsXml method for building the
     * nested list which has the item with specified id as a parent entity.
     * Builds next level list if exists.
     * @param listItemId
     * @param userId
     * @param doc
     * @param conn
     * @param level
     * @return
     * @throws CriticalException
     */
    protected Element buildListXml(Connection conn, Document doc, 
        long listItemId, Long userId, int[] listPath, int level)
        throws CriticalException
    {
        logger.debug("+");
        Statement stmt = null;
        ResultSet rs = null;
        try {                
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                listByParentListItemIdQuery + " where site_id = " + Env.getSiteId() +
                	" and parent_list_item_id = "+ listItemId);
            if (!rs.next()) {
                logger.error("- List not found by parent list item [" +
                    listItemId + ']');
                throw new CriticalException("Invalid listPath value");
            }
            Element listElement = Env.createDomElement(doc, "list");
            setListAttributes(listElement, rs);
            XmlHelper.setRightsAttributes(listElement, userId,
                makeLong(rs.getLong("container_id")));
            listElement.setAttribute("level", String.valueOf(level));
            long listId = rs.getLong("id");
            buildItemsXml(conn, listElement, listId, userId, listPath, level);
            logger.debug("-");
            return listElement;
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
        	DBHelper.close(rs);
            DBHelper.close(stmt);
        }
    }

    /**
     * Builds XML for the items of the list
     * @param listId Identificator of the list
     * @param userId User id
     * @param listElement parent item of the list
     * @param conn
     * @param level
     * @throws CriticalException
     * This method will call buildListXml for the next level
     */
    protected void buildItemsXml(Connection conn, Element listElement,
        long listId, Long userId, int[] listPath, int level) 
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        String moduleName = getModuleNameById(conn, listId);
        String rootUrl = FriendlyUrlService.getModuleFriendlyUrlRoot(listToLang.get(listId), moduleName, moduleName.replace("_module", "").toUpperCase() + "_MODULE_ROOT_FRIENDLY_URL_PART");
		boolean isFriendlyUrlEnabled = Env.isFriendlyUrlEnabled() && rootUrl != null;
        try {
            stmt = conn.prepareStatement(getItemsQuery());
            stmt.setLong(1, listId);
            stmt.setLong(2, Env.getSiteId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long containerId = makeLong(rs.getLong("container_id"));
                // Only if user can view this item - it will be present in XML
                if (SecurityGuard.canView(userId, containerId)) {
                    Element itemElement = Env.createDomElement(
                        listElement.getOwnerDocument(), "listItem");
                    setItemAttributes(itemElement, rs);
                    if (isFriendlyUrlEnabled) {
                    	Xbuilder.setAttr(itemElement, "url", FriendlyUrlService.find(conn, rs.getLong("id"), listToLang.get(listId).intValue(), UrlEntityType.LIST_ITEM, rootUrl));
                    }
                    XmlHelper.setRightsAttributes(itemElement, userId,
                        containerId);
                    listElement.appendChild(itemElement);
                    // next level if tree is present
                    if (listPath != null && level < listPath.length) {
                        int id = rs.getInt("id");
                        if (listPath[level] == 0) {
                            listPath[level] = id;
                        }
                        if (listPath[level] == id) {
                            listElement.setAttribute("selected",
                                String.valueOf(id));
                            itemElement.appendChild(buildListXml(conn,
                                listElement.getOwnerDocument(), id, userId,
                                listPath, level+1));
                        }
                    }
                }
            }
            rs.close();
            logger.debug("-");
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
    }

    /**
     * Appends atributes for the List element 
     * @param listElement
     * @param rs
     * @throws SQLException
     * @throws CriticalException
     */
    protected void setListAttributes(Element listElement, ResultSet rs)
       throws SQLException
    {
        logger.debug("+");
        listElement.setAttribute("id", rs.getString("id"));
        listElement.setAttribute("name", rs.getString("name"));
        listElement.setAttribute("type", rs.getString("template"));
        Xbuilder.setAttr(listElement, "langId", rs.getLong("lang_id"));
        Xbuilder.setAttr(listElement, "moduleId", rs.getLong("module_id"));
        logger.debug("-");
    }

    protected abstract String getItemsQuery();

    protected abstract void setItemAttributes(Element itemElement, ResultSet rs)
        throws SQLException, CriticalException;

    /*
     * Set list path for the tree list
     */
    private int[] getListPathArray(String listPath) {
        logger.debug("+");
        int[] listPathArray = null;
        if (listPath != null) {
            StringTokenizer st = new StringTokenizer(listPath, ";");
            listPathArray = new int[st.countTokens()]; 
            int i = 0;
            while (st.hasMoreTokens()) {
               listPathArray[i++] = Integer.parseInt(st.nextToken());
            }
        }
        logger.debug("-");
        return listPathArray;
    }

    private String getListPath(int[] listPathArray) {
        if (listPathArray == null)
            return null;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < listPathArray.length; i++) {
            sb.append(listPathArray[i]);
            if (i < listPathArray.length-1)
                sb.append(';');
        }
        return sb.toString();
    }

    public static Long makeLong(long value) {
        return value > 0 ? new Long(value) : null;
    }
    
    protected String getModuleNameById(Connection con, Long listId) {
    	PreparedStatement stmt = null;
    	ResultSet rs = null;
    	String moduleName = modules.get(listId);
    	if (moduleName == null) {
    		try {
    			stmt = con.prepareStatement("SELECT module.name, list.lang_id FROM module " +
    					"LEFT JOIN list ON list.module_id = module.id " +
    			"WHERE list.id = ?");
    			stmt.setLong(1, listId);
    			rs = stmt.executeQuery();
    			if (rs.next()) {
    				moduleName = rs.getString("name");
    				modules.put(listId, moduleName);
    				listToLang.put(listId, rs.getLong("lang_id"));				
    			}
    		} catch (SQLException e) {
    			logger.error(e);
    		} finally {
    			DBHelper.close(rs);
    			DBHelper.close(stmt);
    		}
    	}
    	return moduleName;
    }
}
