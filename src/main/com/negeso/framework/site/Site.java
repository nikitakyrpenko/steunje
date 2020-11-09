/*
 * @(#)$Id: Site.java,v 1.4, 2007-04-11 06:41:54Z, Alexander Serbin$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.site;

import java.io.File;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.AbstractDbObject;
import com.negeso.framework.domain.Cacheble;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.module.Module;
import com.negeso.module.core.service.ParameterService;

/**
 *
 * Site domain classs
 * 
 * @version		$Revision: 5$
 * @author		Olexiy Strashko
 * 
 */
public class Site extends AbstractDbObject implements Cacheble{
    private static Logger logger = Logger.getLogger( Site.class );
    private static String tableId = "site";

    private static final String findByIdSql = "SELECT * FROM site WHERE id = ?";

    private static int fieldCount = 4;
	private static ParameterService parameterService;
    
    private String name = null;
    private String templateName = null;
    private Long langId = null;
    private String confXml = null;
    private String status;
    private Date createdDate;
    
    
    /** The only cacheable XSL-file */
    private Templates cachedSiteXsl = null;
    
    
    /** If caching SITE_XSL is on, keeps millis when it was last used
     * (since Unix epoche) */
    private long siteXslLastUsed;
    private Properties properties = null;
    private Map<String, Module> modules = null;
	private long cacheTime = System.currentTimeMillis();

    public String getName() { return name; }
    
    public void setName(String name) { this.name = name; }
    
    public Long getLangId() { return langId; }

    public void setLangId(Long langId) { this.langId = langId; }
    
    public String getTemplateName() { return templateName; }
    
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
    
    public String getConfXml() {
    	return buildMentrixConfigXml(
    			SiteEngine.get().getMentrixConfig(Env.getSiteId()));    	
    }
    
    public String buildMentrixConfigXml(MentrixConfig mentrixConfig) {    	
    	if(mentrixConfig == null)
    		return null;
    	
    	Element page = com.negeso.framework.generators.Xbuilder.createTopEl("page");		
		Element siteConf = Xbuilder.addEl(page, "site-conf", null);
		
		Xbuilder.setAttr(siteConf, "categoryName", mentrixConfig.getCategoryName());
		Xbuilder.setAttr(siteConf, "contactInfo", mentrixConfig.getContactInfo());
		Xbuilder.setAttr(siteConf, "contentLinkColor", mentrixConfig.getContentLinkColor());
		Xbuilder.setAttr(siteConf, "contentTextColor", mentrixConfig.getContentTextColor());
		Xbuilder.setAttr(siteConf, "copyright", mentrixConfig.getCopyright());
		Xbuilder.setAttr(siteConf, "currentBgImage", mentrixConfig.getCurrentBgImage());
		Xbuilder.setAttr(siteConf, "currentTextColor", mentrixConfig.getCurrentTextColor());
		Xbuilder.setAttr(siteConf, "defaultBgImage", mentrixConfig.getDefaultBgImage());
		Xbuilder.setAttr(siteConf, "defaultTextAlign", mentrixConfig.getDefaultTextAlign());
		Xbuilder.setAttr(siteConf, "defaultTextColor", mentrixConfig.getDefaultTextColor());		
		Xbuilder.setAttr(siteConf, "footerleftBgColor", mentrixConfig.getFooterleftBgColor());
		Xbuilder.setAttr(siteConf, "footerleftName", mentrixConfig.getFooterleftName());
		Xbuilder.setAttr(siteConf, "footerleftScreenshot", mentrixConfig.getFooterleftScreenshot());
		Xbuilder.setAttr(siteConf, "footerleftTextColor", mentrixConfig.getFooterleftTextColor());
		Xbuilder.setAttr(siteConf, "header", mentrixConfig.getHeader());
		Xbuilder.setAttr(siteConf, "headerLayout", mentrixConfig.getHeaderLayout());
		Xbuilder.setAttr(siteConf, "headerName", mentrixConfig.getHeaderName());
		Xbuilder.setAttr(siteConf, "languageCode", mentrixConfig.getLanguageCode());
		Xbuilder.setAttr(siteConf, "layoutName", mentrixConfig.getLayoutName());
		Xbuilder.setAttr(siteConf, "level2Border", mentrixConfig.getLevel2Border());		
		Xbuilder.setAttr(siteConf, "logo", mentrixConfig.getLogo());
		Xbuilder.setAttr(siteConf, "menuBgColor", mentrixConfig.getMenuBgColor());
		Xbuilder.setAttr(siteConf, "menuCurrentBgColor", mentrixConfig.getMenuCurrentBgColor());
		Xbuilder.setAttr(siteConf, "menuLiHeight", mentrixConfig.getMenuLiHeight());
		Xbuilder.setAttr(siteConf, "menuLiMargin", mentrixConfig.getMenuLiMargin());
		Xbuilder.setAttr(siteConf, "menuLiPadding", mentrixConfig.getMenuLiPadding());
		Xbuilder.setAttr(siteConf, "menuLiWidth", mentrixConfig.getMenuLiWidth());
		Xbuilder.setAttr(siteConf, "menuName", mentrixConfig.getMenuName());
		Xbuilder.setAttr(siteConf, "menuPosition", mentrixConfig.getMenuPosition());
		Xbuilder.setAttr(siteConf, "menuScreenshot", mentrixConfig.getMenuScreenshot());		
		Xbuilder.setAttr(siteConf, "menuUlMargin", mentrixConfig.getMenuUlMargin());
		Xbuilder.setAttr(siteConf, "menuUlWidth", mentrixConfig.getMenuUlWidth());
		Xbuilder.setAttr(siteConf, "pageBgColor", mentrixConfig.getPageBgColor());
		Xbuilder.setAttr(siteConf, "pageBgImage", mentrixConfig.getPageBgImage());
		Xbuilder.setAttr(siteConf, "pageBgName", mentrixConfig.getPageBgName());
		Xbuilder.setAttr(siteConf, "pageBgScreenshot", mentrixConfig.getPageBgScreenshot());
		Xbuilder.setAttr(siteConf, "pageFontBackground", mentrixConfig.getPageFontBackground());
		Xbuilder.setAttr(siteConf, "pageFontFace", mentrixConfig.getPageFontFace());
		Xbuilder.setAttr(siteConf, "pageFontSizeBig", mentrixConfig.getPageFontSizeBig());
		Xbuilder.setAttr(siteConf, "pathPrefix", mentrixConfig.getPathPrefix());		
		Xbuilder.setAttr(siteConf, "sloganAlignment", mentrixConfig.getSloganAlignment());
		Xbuilder.setAttr(siteConf, "sloganColor", mentrixConfig.getSloganColor());
		Xbuilder.setAttr(siteConf, "sloganFontSize", mentrixConfig.getSloganFontSize());
		Xbuilder.setAttr(siteConf, "sloganHeight", mentrixConfig.getSloganHeight());
		Xbuilder.setAttr(siteConf, "sloganMargins", mentrixConfig.getSloganMargins());
		Xbuilder.setAttr(siteConf, "sloganText", mentrixConfig.getSloganText());
		Xbuilder.setAttr(siteConf, "sloganWidth", mentrixConfig.getSloganWidth());
		
		return XmlUtils.documentToString(page.getOwnerDocument());    	
    }

    
    public void setConfXml(String confXml) { this.confXml = confXml; }
    
    public String getStatus() { return status; }
    
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedDate() { return createdDate; }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    /**
     * Find Site By id
     * 
     * @param siteId
     * @return
     * @throws CriticalException 
     */
    public static Site findById(Connection con, Long siteId) {
        return (Site) DBHelper.findDbObjectById(con, new Site(), siteId);
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#load(java.sql.ResultSet)
     */
    public Long load(ResultSet rs) {
        logger.debug( "+" );
        try{
            setId(DBHelper.makeLong(rs.getLong("id")));
            this.name = rs.getString("name");
            this.templateName = rs.getString("template_name");
            this.langId = DBHelper.makeLong(rs.getLong("lang_id"));
            //this.confXml = rs.getString("conf_xml");
            this.status = rs.getString("status");
            this.createdDate = rs.getDate("created_date");
            logger.debug( "-" );
            return getId();
        }
        catch(SQLException e){
            throw new CriticalException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#saveIntoStatement(java.sql.PreparedStatement)
     */
    public PreparedStatement saveIntoStatement(PreparedStatement stmt)
        throws SQLException 
    {
        logger.debug( "+" );
        try{
            stmt.setLong(1, getId());
            stmt.setString(2, this.name);
            stmt.setString(3, this.templateName);
            stmt.setLong(4, this.langId);
            stmt.setString(5, this.confXml);
            stmt.setString(6, this.status);
            
        }
        catch (SQLException e)
        {
            logger.error(e.getMessage(), e);
            throw new SQLException("Error while saving into statement");
        }
        logger.debug( "-" );
        return stmt;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getTableId()
     */
    public String getTableId() {
        return Site.tableId;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getFindByIdSql()
     */
    public String getFindByIdSql() {
        return Site.findByIdSql;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getUpdateSql()
     */
    public String getUpdateSql() {
        throw new NotImplementedException("getUpdateSql() not implemented");
//        return Site.updateSql;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getInsertSql()
     */
    public String getInsertSql() {
        throw new NotImplementedException("getInsertSql() not implemented");
//        return Site.insertSql;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getFieldCount()
     */
    public int getFieldCount() {
        return Site.fieldCount;
    }

    /**
     * Convert abstract site resource path into real path on disk. 

     * input path format    : /* 
     * output path format   : /customers/{$site_name}/* 
     * 
     * @param path
     * @return
     */
    public String getRealPath(String path) {
        return MessageFormat.format(
            "{0}customers/{1}{2}", Env.getRealPath("/"), this.getName(), path
        );
    }
    
    /**
     * Convert abstract site resource path into template dependent real path on 
     * disk.
     * input path format    : /site/* 
     * output path format   : /customers/{$site_name}/{$template_name}/* 
     * 
     * @param path
     * @return
     */
    public String getTemplateRealPath(String path){
        //path = path.replaceFirst("site", this.getTemplateName());
        path = path.replaceFirst("/site", "");
        return this.getRealPath(path);
    }

    public Templates getCachedSiteXsl() { return cachedSiteXsl; }

    public void setCachedSiteXslLastUsed(long time) {
        this.siteXslLastUsed = time;
    }

    public long getCachedSiteXslLastUsed() { return siteXslLastUsed; }

    public void setCachedSiteXsl(Templates xsl) {
        this.cachedSiteXsl = xsl;
    }

    public void setExpired(boolean newExpired) {
    	throw new NotImplementedException("");
    }
   
    public Properties getProperties() {
        if ( this.properties  == null ){
            this.properties = parameterService.getSiteParameters(this.getId());
        } 
        return properties;
    }
    
    /**
     * Get modules cache map
     * 
     * @return
     */
    private Map<String, Module> getModules(){
        if (this.modules == null){
            this.modules = new HashMap<String, Module>();
        }
        return this.modules;
    }
        
    /**
     * 
     * @param name
     * @return
     */
    public Module getModule(String name){
        Module module = this.getModules().get(name);
		if (module == null) {
			module = this.loadModule(name);
		}
		if (module.isExpired()) {
			module = this.loadModule(name);
		}
		return module;			
    }

    /**
     * 
     * @param name
     * @return moduleId if module exists, else - new Long(0)
     */
    public Long getModuleId(String name){
        try {
        	return getModule(name).getId();
        } catch (CriticalException e) {
			return new Long(0);
		}
    }
    

    /**
     * Load module into cache
     * 
     * @param name
     * @return
     */
    private Module loadModule(String name){
        Connection con = null;
        Module module = null;
        try{
            con = DBHelper.getConnection();
            module = Module.findByName(con, name, getId().toString());
            if (module == null){
                String msg = "module not found by: name = " + name + " and siteId = " + getId();
                logger.error(msg);
                throw new CriticalException(msg);
            }
            this.getModules().put(name, module);
            module.setExpired(false);

            if( logger.isInfoEnabled() ){
                logger.info(
                    "Module " + name + " is loaded into site:" + Env.getSite().getName()
                );
            }
        }
        catch(SQLException e){
            logger.error("-error", e);
            throw new CriticalException(e);
        }
        finally{
            DBHelper.close(con);
        }
        return module;
    }
    
    /**
     * oohhooooo
     * 
     * @return
     */
    public Element getConfElement(){
    	if (this.getConfXml() == null){
    		return null;
    	}
    	
		SAXReader reader = new SAXReader();
		org.w3c.dom.Document confDoc = null;
		try{
			Document doc = reader.read(new StringReader(getConfXml()));
        	DOMWriter writer = new DOMWriter();
        	confDoc = writer.write(doc);
		}
		catch(DocumentException e){
    		logger.error("-ERROR:", e);
    		throw new CriticalException(e);
		}
		
		Element el = (Element)confDoc.getFirstChild();
		logger.info("nodeName:" + el.getNodeName());
		el = (Element) el.getFirstChild();
		logger.info("nodeName:" + el.getNodeName());
		return el;
    }

//    public static List<Site> getAllSites() {
//        List<Site> sites = new LinkedList<Site>();
//        JdbcTemplate tp = DBHelper.getJdbcTemplate();
//        List list = tp.queryForList("select * from site");
//        for (Map row : (List<Map>) list) {
//            Site site = new Site();
//            site.id = new Long(((Integer) row.get("id")).intValue());
//            site.name = (String) row.get("name");
//            site.templateName = (String) row.get("template_name");
//            site.langId = new Long(((Integer) row.get("lang_id")).intValue());
//            site.confXml = (String) row.get("conf_xml");
//            site.status = (String) row.get("status");
//            site.createdDate = (Date) row.get("created_date");
//            sites.add(site);
//        }
//        return sites;
//    }
    
    public boolean isConstructed() { return "construction".equals(status); }
    
    public boolean isSuspended() { return "suspended".equals(status); }
    
    public boolean isLive() { return "production".equals(status); }

	public void setCacheTime(long l) {
		this.cacheTime  = l;
	}

	public long getCacheTime() {
		return cacheTime;
	}

	public boolean isExpired() { throw new NotImplementedException(""); }
    
    public Templates getXslTemplates(String relativePath){
    	File file = new File(this.getRealPath(relativePath));
    	if (!file.exists()) {
  			logger.error("file not found: " + relativePath);
			throw new CriticalException("file not found: " + relativePath);
    	}
       	return XmlHelper.getTemplates(
       		file,
   			new URIResolver() {
                    public Source resolve(String href, String base)
                            throws TransformerException {
                       logger.debug("+");
                       String path = MessageFormat.format(
                               "{0}{1}",
                               Env.getRealPath("/"),
                               href
                       );
                       File file = new File(path);
                       if (!file.exists()) {
                           String msg = "file not found: " + path;
                           logger.error(msg);
                           throw new CriticalException(msg);
                       }
                       logger.info(path);

                       StreamSource source = new StreamSource(file);
                       logger.debug("-");
                       return source;
                   }
               }
       	);  
    }
	
	public static void setParameterService(ParameterService parameterService) {
		Site.parameterService = parameterService;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
    
}
