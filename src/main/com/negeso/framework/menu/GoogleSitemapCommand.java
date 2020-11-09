package com.negeso.framework.menu;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.site.PageAlias;
import com.negeso.framework.site.SiteUrl;
import com.negeso.framework.site.SiteUrlCache;
import com.negeso.framework.site.service.PageAliasService;
import com.negeso.framework.site_map.PageDescriptor;
import com.negeso.framework.site_map.PagesHandler;
import com.negeso.framework.site_map.SiteMapBuilder;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.framework.view.AbstractHttpView;
import com.negeso.framework.view.GetFileView;

public class GoogleSitemapCommand extends AbstractCommand {
	
	private Logger logger = Logger.getLogger(getClass());

    private long curTime = System.currentTimeMillis();
    
    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = this.getRequestContext();
        ResponseContext response = new ResponseContext();
        response.setResultName(RESULT_SUCCESS);
        
        Language language = Language.findByCodeQuietly(request.getParameter(NegesoRequestUtils.LANG_CODE));
        SiteUrl siteUrl = SiteUrlCache.getSiteUrlMap().get(Env.getServerName());
		if (siteUrl != null && siteUrl.getSingleLanguage() && siteUrl.getLangId() != null) {
			language = Language.findByIdQuietly(siteUrl.getLangId());
		}
    	try {
    		if (SiteMapCache.isCacheEnabled()){
    			File cacheFolder = SiteMapCache.getCacheFolder(true);
    			File xmlFile = new File(cacheFolder, SiteMapCache.getLanguageSpecificFileName(language));
    			if (!SiteMapCache.isCachedFileValid(xmlFile)) {
    				SiteMapCache.generateCache(xmlFile, buildSitemap(language));
    			}    		
    			response.getResultMap().put(AbstractHttpView.KEY_FILE, xmlFile);
    			response.getResultMap().put(AbstractHttpView.HEADER_MIME_TYPE, "text/xml; charset=UTF-8");
    			response.getResultMap().put(AbstractHttpView.HEADER_EXPIRES, 0L);
    			new GetFileView().process(request, response);
    		} else {
    			Document doc = buildSitemap(language);
    			response.getResultMap().put(OUTPUT_XML, doc);
    		}		
		} catch (Exception e) {
			logger.error("Error: ", e);
			response.setResultName(RESULT_FAILURE);
		}
        logger.debug("-");
        return response;
    }
    
	private Document buildSitemap(Language language) {
		logger.debug("+");
		String hostName = Env.getHostName().replace(":80/", "/");
		Document doc = XmlHelper.newDocument();
		Element urlset = doc.createElementNS("http://www.google.com/schemas/sitemap/0.84", "urlset");
		urlset.setAttribute("xsi:schemaLocation",
				"http://www.google.com/schemas/sitemap/0.84 http://www.google.com/schemas/sitemap/0.84/sitemap.xsd");
		urlset.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		doc.appendChild(urlset);
		
		Connection cn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			cn = DBHelper.getConnection();
			st = cn.createStatement();
			buildUrlEl(urlset, hostName, null, null, null);
			rs = st.executeQuery(
					" SELECT * FROM page " +
					" WHERE category IN ('page', 'frontpage', 'contentpage') " +
					(language != null ? " AND lang_id = " + language.getId() : org.apache.commons.lang.StringUtils.EMPTY) +
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
				if (isPublic(pubdate, expdate, cid)) {
					buildUrlEl(urlset, hostName, filename, rs.getString("sitemap_freq"), rs.getString("sitemap_prior"));
				}
				for (Entry<String, PagesHandler> entry: SiteMapBuilder.getInstance().getHandlers().entrySet()) {
					if (rs.getString("filename").equals(entry.getValue().getPage(langId))) {
						buildUrlFromDescriptor(urlset, entry.getValue().getPages(langId));
					}
				}
			}
			PageAliasService pageAliasService = (PageAliasService)DispatchersContainer.getInstance().getBean("core", "pageAliasService");
			List<PageAlias> pageAliasList = pageAliasService.readAll(Env.getSiteId());
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
		} finally {
			DBHelper.close(rs, st, cn);
		}
		logger.debug("-");
		return doc;
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
    
    private void buildUrlFromDescriptor(Element parent, PageDescriptor parentPageDescriptor) {
		for (PageDescriptor descriptor : parentPageDescriptor.getContent()) {
			String hostName = Env.getHostName().replace(":80/", "/");
			if( StringUtils.isNotBlank(descriptor.getFriendlyHref()) ) {
				buildUrlEl(parent, hostName, descriptor.getFriendlyHref(), null,null);				
			}else{
				buildUrlEl(parent, hostName, descriptor.getHref(), null, null);
			}
			if( descriptor.getContent() != null ){
				buildUrlFromDescriptor(parent, descriptor);
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
	
}