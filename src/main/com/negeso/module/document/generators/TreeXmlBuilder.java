package com.negeso.module.document.generators;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.photoalbum.generators.AdminXmlBuilder;

public class TreeXmlBuilder {

    private static Logger logger = Logger.getLogger(AdminXmlBuilder.class);

    private static final TreeXmlBuilder instance = new TreeXmlBuilder();

    private TreeXmlBuilder() {}

    public static TreeXmlBuilder getInstance() {
        return instance;
    }

    static final String categoriesQuery =
        "select dc_category.id, name, container_id from dc_category" +
        " join mc_folder on (dc_category.mc_folder_id = mc_folder.id)" +
        " and dc_category.parent_id";

    /*
     * Builds a document with all albums. Uses to select the album.
     */
    public Document getDocument(Connection conn, User user) 
        throws CriticalException
    {
        logger.debug("+");
        Document doc = null;
        try {
            doc = Env.createDom();
            doc.appendChild(getElement(conn, doc, user));
            doc.getDocumentElement().setAttribute("xmlns:negeso", 
                Env.NEGESO_NAMESPACE);
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        logger.debug("-");
        return doc;
    }

    /*
     * Method returns a subtree with all albums
     */
    public Element getElement(Connection conn, Document doc, User user)
        throws CriticalException
    {
        logger.debug("+");
        Element categoryElement = Env.createDomElement(doc,
            "document_categories");
        appendDescendantCategories(conn, categoryElement, null, user);
        logger.debug("-");
        return categoryElement;
    }

    private void appendDescendantCategories(Connection conn, 
        Element parentElement, Long id, User user) throws CriticalException
    {
        logger.debug("+");
        Document doc = parentElement.getOwnerDocument();
        ArrayList nodes = getDescendantCategories(conn, doc, id, user);
        for (int i = 0; i < nodes.size(); i++)
            parentElement.appendChild((Element)nodes.get(i));
        logger.debug("-");
    }

    /*
     * Returns a list of albums following to the album with specified id
     */
    private ArrayList getDescendantCategories(Connection conn, Document doc, 
        Long parentId, User user) throws CriticalException
    {
        logger.debug("+");
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList nodes = new ArrayList();
        try {
            stmt = conn.createStatement();
            String query = categoriesQuery + 
                (parentId != null ? " =" + parentId : " is null") + 
                " order by name";
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Element element = Env.createDomElement(doc, "category");
                element.setAttribute("id", rs.getString("id"));
                element.setAttribute("name", rs.getString("name"));
                Long containerId = DBHelper.makeLong(rs.getLong("container_id"));
                element.setAttribute("canView", String.valueOf(
                    SecurityGuard.canView(user, containerId)));
                nodes.add(element);
            }
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(rs);
            DBHelper.close(stmt);
        }
        for (int i = 0; i < nodes.size(); i++) {
            appendDescendantCategories(conn, (Element)nodes.get(i), 
                Long.valueOf(((Element)nodes.get(i)).getAttribute("id")), user);
        }
        logger.debug("-");
        return nodes;
    }
}
