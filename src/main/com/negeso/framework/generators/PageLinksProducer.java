package com.negeso.framework.generators;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DomainObject;
import com.negeso.framework.security.SecurityGuard;

/**
 * Builds XML list of links to pages:
 * <code>
 *   <pages>
 *     <page url="yandex.ru" title="YANDEX" />
 *     ...
 *   </pages>
 * </code>
 */
public class PageLinksProducer {

        private String pageType;
        private String langCode = null;
        private String fullContextPath = null;
        private DocumentFactory documentFactory = DocumentFactory.getInstance();

        private static Logger logger = Logger.getLogger(PageLinksProducer.class);


        public PageLinksProducer(
                        String fullContextPath,
                        String langCode,
                        String pageType){
                logger.debug("+");
                this.fullContextPath = fullContextPath;
                this.langCode = langCode;
                if("popup".equals(pageType)){
                        this.pageType = pageType;
                }else{
                        this.pageType = "page";
                }
                logger.debug("-");
        }

        /**
         * Creates list of links to pages of the site. The required data is fetched
         * from database.
         *
         * @return org.dom4j.Element root element of the list of links to pages
         */
        public Element getElement() {
                logger.debug("+");
                Element root = documentFactory.createElement("pages");
                Connection conn = null;
                Statement statement = null;
                ResultSet rstPages = null;
                try {
                        conn = DBHelper.getConnection();
                        statement = conn.createStatement();
                        rstPages = statement.executeQuery(
                                " SELECT filename, title, container_id AS cid" +
                                " FROM page, language " +
                                " WHERE page.lang_id = language.id " +
                                " AND page.category = '" + pageType + "' " +
                                " AND language.code = '" + langCode + "' " +
                                " AND site_id = " + Env.getSiteId());
                        while(rstPages.next()){
                                String link = fullContextPath + rstPages.getString("filename");
                                String title = rstPages.getString("title");
                                Long cid = DomainObject.makeLong(
                                                rstPages.getLong("cid"));
                                if (!SecurityGuard.canView((Long) null, cid))
                                    title = "[protected] " + title;

                                root.addElement("page")
                                        .addAttribute("link", link)
                                        .addAttribute("title", title);
                        }
                        logger.debug("-");
                        return root;
                } catch (SQLException e) {
                        logger.error("- SQLException", e);
                        return null;
                }finally{
                    DBHelper.close(rstPages, statement, conn);
                }
        }

        /**
         * A wrapper for {@link PageLinksProducer#getElement()}
         */
        public Document getDocument() {
                logger.debug("+ -");
                return documentFactory.createDocument(getElement());
        }

}
