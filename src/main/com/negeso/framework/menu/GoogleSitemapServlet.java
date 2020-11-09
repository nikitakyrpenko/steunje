/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.site.PageAlias;
import com.negeso.framework.site.service.PageAliasService;
import com.negeso.framework.site_map.PageDescriptor;
import com.negeso.framework.site_map.PagesHandler;
import com.negeso.framework.site_map.SiteMapBuilder;

/**
 * 
 * @TODO
 * 
 * @author		Pochapskiy Olexandr
 * @version		$Revision: $
 *
 */
public class GoogleSitemapServlet extends HttpServlet {
	
	private static final long serialVersionUID = 2461351865972606657L;

	private long curTime = System.currentTimeMillis();
	
	private Logger logger = Logger.getLogger(GoogleSitemapServlet.class);
	private static Pattern pattern = Pattern.compile("/sitemap_?([a-zA-Z\\-_]*)\\.xml");
	
	public static void main(String[] args) {
		Matcher m = pattern.matcher("/sitemap.xml");
		System.out.println(m.find());
		System.out.println(m.groupCount());
			
		System.out.println(m.group(1));
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		logger.debug("+");
		PrintWriter writer = resp.getWriter();;
		if (Env.getProperty("GENERATE_SITEMAP").equals("true")){
			try {
				String sitemap = getStringFromDocument(buildSitemap(req));
				if (sitemap != null) {
					resp.setContentType("text/xml;charset=UTF-8");
					writer.print(sitemap);
					writer.flush();
				} else {
					resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				}
			} finally {
				if (writer != null) writer.close();
			}
		}else{
			ServletContext context = getServletContext();
			
			InputStream is = context.getResourceAsStream("/sitemap.xml");
			if (is != null) {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader reader = new BufferedReader(isr);
				String text = "";
				while ((text = reader.readLine()) != null) {
					writer.println(text);
				}
			}
			writer.close();
		}
		logger.debug("-");

	}

	private Document buildSitemap(HttpServletRequest req) {
		logger.debug("+");
		Document doc = XmlHelper.newDocument();
		Element urlset = doc.createElementNS("http://www.google.com/schemas/sitemap/0.84", "urlset");
		urlset.setAttribute("xsi:schemaLocation",
				"http://www.google.com/schemas/sitemap/0.84 http://www.google.com/schemas/sitemap/0.84/sitemap.xsd");
		urlset.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		doc.appendChild(urlset);
		Matcher m = pattern.matcher(req.getRequestURI());
		Language language = null;
		if (m.find()) {
			String langCode = m.group(1);
			if (StringUtils.isNotBlank(langCode)) {
				try {
					language = Language.findByCode(langCode);
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
		Connection cn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			cn = DBHelper.getConnection();
			st = cn.createStatement();
			String hostName = Env.getHostName().replace(":80/", "/");
			buildUrlEl(urlset, hostName, null, null,null);
			rs = st.executeQuery(
					" SELECT * FROM page " +
					" WHERE category IN ('page', 'frontpage', 'contentpage') " +
					(language != null ? " AND lang_id = " + language.getId() : StringUtils.EMPTY) +
					" AND is_sitemap IS TRUE " +
					" AND site_id = " + Env.getSiteId().toString() +
					" ORDER BY lang_id ");
			while (rs.next()) {
				logger.debug("# filename = " + rs.getString("filename"));
				Timestamp pubdate = rs.getTimestamp("publish_date");
				Timestamp expdate = rs.getTimestamp("expired_date");
				long cid = rs.getLong("container_id");
				String filename = rs.getString("filename");
				Long langId = rs.getLong("lang_id");
				//Language lang = Language.findById(langId);
				//String startLangCodeAlias = lang.getCode() + "/" + filename.replace("_" + lang.getCode() + ".html", StringUtils.EMPTY);
				/*if (Env.isUseLangCodeAsUrlStart()) {
					Language lang = Language.findById(langId);
					startLangCodeAlias = lang.getCode() + "/" + filename.replace("_" + lang.getCode() + ".", ".");
				}*/
				if (isPublic(pubdate, expdate, cid)) {
					buildUrlEl(urlset, hostName, filename, rs.getString("sitemap_freq"), rs.getString("sitemap_prior"));
					//buildUrlEl(urlset, Env.getHostName(), startLangCodeAlias, rs.getString("sitemap_freq"), rs.getString("sitemap_prior"));
				}
				for (Entry<String, PagesHandler> entry: SiteMapBuilder.getInstance().getHandlers().entrySet()) {
					if (rs.getString("filename").equals(entry.getValue().getPage(langId))) {
						buildUrlFromDescriptor(urlset, entry.getValue().getPages(langId, 1L, true), hostName);
					}
				}
			}
			PageAliasService pageAliasService = (PageAliasService)DispatchersContainer.getInstance().getBean("core", "pageAliasService");
			List<PageAlias> pageAliasList = (language != null) ? pageAliasService.readByLangId(Env.getSiteId(), language.getId()) : pageAliasService.readAll(Env.getSiteId());
			for (PageAlias pageAlias : pageAliasList) {
				if (pageAlias.isInSiteMap() != null && pageAlias.isInSiteMap()) {
					buildUrlEl(urlset, hostName, pageAlias.getFileName(), null, null);
				}
			}
		} catch (SQLException e) {
			logger.error("- Unsupported structure of table 'page'", e);
			return null;
		} catch (CriticalException e) {
			logger.error("CriticalException - ", e);
			return null;
//		} catch (ObjectNotFoundException e) {
//			logger.error("ObjectNotFoundException - ", e);
//			return null;
		} finally {
			DBHelper.close(rs, st, cn);
		}
		logger.debug("-");
		return doc;
	}
	private void buildUrlFromDescriptor(Element parent, PageDescriptor parentPageDescriptor, String host) {
		for (PageDescriptor descriptor : parentPageDescriptor.getContent()) {
			if (StringUtils.isNotBlank((descriptor.getFriendlyHref()))) {
				buildUrlEl(parent, host, descriptor.getFriendlyHref(), null,null);				
			}
			if (!descriptor.isLeaf())  {
				buildUrlFromDescriptor(parent, descriptor, host);
			}
		}
	}
	
	private Element buildUrlEl(Element parent, String host, String filename, String changefreq, String priority) {
		logger.debug("+");
		Element urlEl = parent.getOwnerDocument().createElement("url");
		
		Element locEl = urlEl.getOwnerDocument().createElement("loc");
		locEl.appendChild(urlEl.getOwnerDocument().createTextNode(filename == null?host:host+filename));
		urlEl.appendChild(locEl);
		
		Element changefreqEl = urlEl.getOwnerDocument().createElement("changefreq");
		changefreqEl.appendChild(urlEl.getOwnerDocument().createTextNode(changefreq != null ? changefreq.toLowerCase():"daily"));
		urlEl.appendChild(changefreqEl);
		
		Element priorityEl = urlEl.getOwnerDocument().createElement("priority");
		priorityEl.appendChild(urlEl.getOwnerDocument().createTextNode(priority != null ? priority : "0.5" ));
		urlEl.appendChild(priorityEl);

		parent.appendChild(urlEl);
		
		logger.debug("-");
		return urlEl;
	}
	
    private boolean isPublic(Timestamp publish_date, Timestamp expired_date, long cid) {
        if (publish_date != null && curTime < publish_date.getTime()) {
            return false;
        }
        if (expired_date != null && curTime >= expired_date.getTime()) {
            return false;
        }
        return cid == 0 || SecurityGuard.canView((Long)null, new Long(cid));
    } 
	
	public String getStringFromDocument(Document doc)
	{
	    try
	    {
	       DOMSource domSource = new DOMSource(doc);
	       StringWriter writer = new StringWriter();
	       StreamResult result = new StreamResult(writer);
	       TransformerFactory tf = TransformerFactory.newInstance();
	       Transformer transformer = tf.newTransformer();
	       transformer.transform(domSource, result);
	       return writer.toString();
	    }
	    catch(TransformerException ex)
	    {
	       ex.printStackTrace();
	       return null;
	    }
	}


}

