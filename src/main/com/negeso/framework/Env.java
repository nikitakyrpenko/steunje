/*
 * @(#)$Id: Env.java,v 1.100, 2007-04-11 06:33:38Z, Alexander Serbin$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */


package com.negeso.framework;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.maxmind.geoip.LookupService;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.StringResource;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.mailer.SMPTChecker;
import com.negeso.framework.site.Site;
import com.negeso.framework.site.SiteEngine;
import com.negeso.framework.site.SiteUrl;
import com.negeso.framework.site.SiteUrlCache;
import com.negeso.framework.util.PropertiesHelper;
import com.negeso.module.core.domain.ConfigurationParameter;
import com.negeso.module.core.service.ParameterService;


/**
* Negeso W/CMS environment. Pattern Registry; some methods are
* ThreadLocalRegistry.
* 
* @version		$Revision: 101$
* @author 		Stanislav Demchenko
* @author		Olexiy Strashko
*/
public class Env {
    private static final String GEO_DB_PATH = "/var/www/share-new/config/geoip/GeoIP.dat";

	private static final Logger logger = Logger.getLogger(Env.class);

    public static final int COOKIE_MAX_AGE = 60*60*24*365;
    
    public static final String XMLMODE_ADMIN = "xadmin";
    
    public static final String XMLMODE_VISITOR = "xvisitor";
    
    public static final String BROWSER_LANG_DETERMINATION = "browser";
    public static final String IP4_LANG_DETERMINATION = "ip4";
    public static final String NO_LANG_DETERMINATION = "defaultLang";
    
    public static final String INTERFACE_LANGUAGE_ATTR_NAME = "interface-language";
    
    public static final String NEGESO_NAMESPACE = "http://negeso.com/2003/Framework";
    
    public static final String NEGESO_PREFIX = "negeso";
    
    private static Properties properties;
    
    private static ServletContext context;
    
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
    
    private static final SimpleDateFormat friendlyFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    private static final SimpleDateFormat roundFormatter = new SimpleDateFormat("yyyy-MM-dd");

    private static final SimpleDateFormat roundSimpleFormatter = new SimpleDateFormat("dd-MM-yyyy");
    
    private static final SimpleDateFormat roundSimpleFullFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    
    public static final String SIMPLE_DATE_FORMAT = "dd-MM-yyyy";
    
    public static final String WCMS_X_MAILER = "Negeso WCMS Mailer";
    
    private static ThreadLocal<Long> siteId = new ThreadLocal<Long>();
    
    /** Complete host name (format: scheme, host, port and triling slash) */
    private static ThreadLocal<String> hostName = new ThreadLocal<String>();
    
    private static ThreadLocal<String> serverName = new ThreadLocal<String>();

    private static String commonHostName;

	private static ParameterService parameterService;

    private Env() { /* Prevent instantiation */ }
    
    /** Retrieves default language code */
    public static String getDefaultLanguageCode() {
        return getProperty("SS.DEFAULT_LANGUAGE");
    }
    
    /* */
    public static boolean isContentFreeze(){
    	logger.debug("+");
    	String value = getProperty("CONTENT_FREEZE");
    	if (value == null){
    		logger.debug("- cannot find CONTENT_FREEZE variable");
    		return false;
    	}
    	logger.debug("-");
		return value.toLowerCase().equalsIgnoreCase("true");
    }
    
    /**
     * @param request
     * @return language code, never null
     */
    public static String getPreferredLangCode(RequestContext request) {
        logger.debug("+");
        
        String langDeterminationStrategy = getProperty("SS.LANG_DETERMINATION_STRATEGY", BROWSER_LANG_DETERMINATION);
        String langCode = null;
        if(BROWSER_LANG_DETERMINATION.equals(langDeterminationStrategy)){
        	langCode = getPreferredLangCodeByBrowser(request.getParameter(RequestContext.REQUEST_LOCALES));
        }else if(IP4_LANG_DETERMINATION.equals(langDeterminationStrategy)){
        	langCode = getPreferredLangCodeByIp4(request.getRemoteAddr());
        }else if(NO_LANG_DETERMINATION.equals(langDeterminationStrategy)){
        	langCode = Env.getDefaultLanguageCode();
        }
        	
       return langCode != null ? langCode : Env.getDefaultLanguageCode();
    }
    
    public static String getPreferredLangCode(HttpServletRequest request) {
        logger.debug("+");
        SiteUrl siteUrl = SiteUrlCache.getSiteUrlMap().get(request.getServerName());
        String langCode = null;
        if (siteUrl != null && siteUrl.getLangId() != null) {
        	try {
        		Language lang = Language.findById(siteUrl.getLangId());
        		langCode = lang.getCode();
			} catch (Exception e) {}
        }
        
        String langDeterminationStrategy = getProperty("SS.LANG_DETERMINATION_STRATEGY", BROWSER_LANG_DETERMINATION);
        if(BROWSER_LANG_DETERMINATION.equals(langDeterminationStrategy)){
        	langCode = getPreferredLangCodeByBrowser(getLocales(request));
        }else if(IP4_LANG_DETERMINATION.equals(langDeterminationStrategy)){
        	langCode = getPreferredLangCodeByIp4(request.getRemoteAddr());
        }else if(NO_LANG_DETERMINATION.equals(langDeterminationStrategy)){
        	langCode = Env.getDefaultLanguageCode();
        }
        return langCode != null ? langCode : Env.getDefaultLanguageCode();
    }
    
    
	public static String getPreferredLangCodeByIp4(String ip4) {
		String code = getCountryByIp4(ip4);
		if (code != null) {
			code = code.toUpperCase();
			try {
				return Language.findLangCodeByCountryCode(code);
			} catch (Exception e) {
				logger.debug("Cannot find language by country code " + code);
				return null;
			}
		}
		return null;
	}
    
    public static String getCountryByIp4(String ip4){
    	String dbfile = GEO_DB_PATH; 
    	LookupService cl = null; 
    	try {
			
    		cl = new LookupService(dbfile,LookupService.GEOIP_STANDARD);
			String code = cl.getCountry(ip4).getCode();
			cl.close();
			return code;
			
    	} catch (IOException e) {
			logger.error(e);
		}finally{
			if(cl != null){
				cl.close();
			}
		}
		return null;
    }
    
    
    public static String getPreferredLangCodeByBrowser(String locales){
        if(StringUtils.isEmpty(locales)){
            logger.debug("- preferred language is not specified");
            return null;
        }
        String[] langs = locales.split(";");
    	for (int i = 0; i < langs.length; i++) {
            String lang = langs[i];
            try{
                Language.findByCode(lang);
                logger.debug("- preferred language is found");
                return lang;
            }catch(Exception e){
                logger.debug("Cannot find language");
            }
        }
    	return null;
    	
    }
	
	public static boolean isMultisite(){
		return Boolean.valueOf(getGlobalProperty("core.isMultisite"));
	}
    
    /**
     * Retrieves property value by name. If property by this name
     * not exists - null is returned.
     *
     * @param name         The parameter name.
     * @return                  The property value (null if not found).
     */
	
    public static String getProperty(String name) {
        return getProperty(name, null); 
    }
    
    /**
     * Returns property value by name. If property does not exist,
     * the <b>defaultValue</b> is returned.
     */
    public static String getProperty(String name, String defaultValue) {
        logger.debug("+");
        // TODO realization
        PropertiesHelper helper = new PropertiesHelper();
        helper.setProperties(Env.getSite().getProperties());
        if ( helper.hasProperty(name) ){
            return helper.getString(name, defaultValue);
        }
        helper.setProperties( Env.getGlobalProperties() );
        logger.debug("-");
        return helper.getString(name, defaultValue);
    }
    
    public static String getNotCachedSiteProperty(String name, String defaultValue) {
    	ConfigurationParameter param = parameterService.findParameterByNameAndSiteId(name, getSiteId(), defaultValue);
    	if (param != null) {
    		return param.getValue();
    	}
    	return defaultValue;
    }
    
    public static void updateNotCachedSiteProperty(String name, String newValue) {
    	ConfigurationParameter param = parameterService.findParameterByNameAndSiteId(name, getSiteId(), newValue);
    	if (param != null) {
    		param.setValue(newValue);
    		parameterService.update(param, false);
    	}
    }
    
    public static String getFrontPageName(String lang){
    	String pages = getProperty("SS.FRONT_PAGES", null);
    	if(pages == null)
    		return "index.html";
    	
    	Map<String,String> langFrontPages = parseLangParam(pages);
    	String page = langFrontPages.get(lang);
    	if (page == null)
    		return "index.html";
    	return page;
    }
    
    public static List<String> getPropertyList(String name, String[] defaultValue){
        String property = getProperty(name, null);
        List<String> result = new ArrayList<String>();
        if(property != null) getSubsting(result, ";", property);
        else for(int i = 0; i<defaultValue.length; i++) result.add(defaultValue[i]);
        return result;
    }

    private static void getSubsting(List<String> result, String label, String input){
        int index = input.indexOf(label);
        if(index != -1) {
            result.add(input.substring(0,index));
            getSubsting(result, label, input.substring(index+1).trim());
        }
    }
    
    /**
     * Return property value by name. If it does not exist in application props,
     * lookup property in default JNDI context (java:comp/env). If property not found in context,
     * the <b>defaultValue</b> is returned.
     */
    public static String getJndiProperty(String name, String defaultValue) {
    	logger.debug("+");
    	String value = getProperty(name);
    	if (value == null) {
	    	logger.info("property " + name + " not found in application props");
			try {			
				InitialContext inCtx = new InitialContext();
				Context envCtx = (Context) inCtx.lookup("java:comp/env");
				value = envCtx.lookup(name).toString();
		    	logger.info("get property " + name + " from java:comp/env Context");
				
			} catch (NamingException e) {
				logger.error("property " + name + " not found in JNDI context " + e.getMessage());
				value = defaultValue;
				
			}
    	}
		logger.debug("-");
    	return value;
    }
    
    /**
     * Retrieves <code>int</code> property value by name.
     * If property does not exist or properties source is not set,
     * default value is returned.
     *
     * @param name         The parameter name.
     * @param defaultValue      The default value to get.
     * @return                  The property value.
     */
    public static int getIntProperty(String name, int defaultValue) {
        logger.debug("+");
        PropertiesHelper helper = new PropertiesHelper();
        helper.setProperties(Env.getSite().getProperties());
        if ( helper.hasProperty(name) ){
            return helper.getInt(name, defaultValue);
        }
        helper.setProperties( Env.getGlobalProperties() );
        logger.debug("-");
        return helper.getInt(name, defaultValue);
    }

    public static int getGlobalIntProperty(String name, int defaultValue) {
        logger.debug("+");
        PropertiesHelper helper = new PropertiesHelper();
        helper.setProperties( Env.getGlobalProperties() );
        logger.debug("-");
        return helper.getInt(name, defaultValue);
    }
    
    public static String getGlobalProperty(String name) {
        logger.debug("+");
        PropertiesHelper helper = new PropertiesHelper();
        helper.setProperties( Env.getGlobalProperties() );
        logger.debug("-");
        return helper.getString(name);
    }
    
    public static String getGlobalProperty(String name, String defaultValue) {
        logger.debug("+");
        PropertiesHelper helper = new PropertiesHelper();
        helper.setProperties( Env.getGlobalProperties() );
        logger.debug("-");
        return helper.getString(name, defaultValue);
    }
    
    /**
     * Returns a String containing the real path for a given virtual path.
     * For example, the path "/index.html" returns the absolute file path
     * on the server's filesystem.
     *
     * @param path a String specifying a virtual path
     * @return a String specifying the real path, or null if the translation
     *         cannot be performed
     */
    public static String getRealPath(String path) {
       
    	if(path.startsWith("/help/")){ //help intercepter... delete if need use local help directory (www/help)
    		path = path.substring("/help".length());
    		String dirPath = getProperty("CORE.HELP_PATH","/var/www/Sites/help") ;
        	return dirPath + path;
        	
        }
    	return context != null ? context.getRealPath(path) : path;
    }
    
    
    
    
    /**
     * Returns the MIME type of the specified file, or null if the MIME type
     * is not known. Common MIME types are "text/html" and "image/gif".
     *
     * @param file a String specifying the name of a file
     * @return a String specifying the file's MIME type
     */
    public static String getMimeType(String file) {
        return context.getMimeType(file);
    }
    
    /** Sets servlet context */
    static synchronized void setServletContext(ServletContext ctx) {
        logger.debug("+");
        if(context != null){
            logger.warn("- (context is already set)");
            return;
        }
        context = ctx;
        logger.debug("-");
    }
    
    /********************************************
     * Formats date as "yyyy-MM-dd'T'hh:mm:ss'Z'"
     * 
     * @param date must be not null
     ********************************************/
    public static String formatDate(Date date) {
        synchronized(formatter){
            return formatter.format(date);
        }
    }
    
    public static String friendlyFormatDate(Date date) {
        synchronized(friendlyFormatter){
            return friendlyFormatter.format(date);
        }
    }

    /**
     * Parses a date expected in format "yyyy-MM-dd'T'hh:mm:ss'Z'"
     * 
     * @param date must be not null
     * @throws java.text.ParseException
     */
    public static Date parseDate(String date) throws ParseException {
        synchronized(formatter){
            return formatter.parse(date);
        }
    }
    
    /********************************************
     * Formats date as "yyyy-MM-dd"
     * 
     * @param date must be not null
     ********************************************/
    public static String formatSimpleRoundDate(Date date) {
        synchronized(roundSimpleFormatter){
            return (date==null)? "" : roundSimpleFormatter.format(date);
        }
    }  
    
    /**
     * Parses a date expected in format "dd-MM-yyyy"
     * 
     * @param date must be not null
     * @throws java.text.ParseException
     */
    public static Date parseSimpleRoundDate(String date) throws ParseException {
        synchronized(roundSimpleFormatter){
            return roundSimpleFormatter.parse(date);
        }
    }
    
    /********************************************
     * Formats date as "dd-MM-yyyy HH:mm:ss"
     * 
     * @param date must be not null
     ********************************************/
    
    public static String formatSimpleRoundFullFormatter(Date date) {
        synchronized(roundSimpleFullFormatter){
            return (date==null)? "" : roundSimpleFullFormatter.format(date);
        }
    }
    
    /**
     * Parses a date expected in format "dd-MM-yyyy"
     * 
     * @param date must be not null
     * @throws java.text.ParseException
     */
    public static Date parseSimpleRoundFullFormatter(String date) throws ParseException {
        synchronized(roundSimpleFullFormatter){
            return roundSimpleFullFormatter.parse(date);
        }
    }
    
    
    /**
     * Parses a date expected in format "yyyy-MM-dd"
     * 
     * @param date must be not null
     * @throws java.text.ParseException
     */
    public static Date parseRoundDate(String date) throws ParseException {
        synchronized(roundFormatter){
            return roundFormatter.parse(date);
        }
    }

    public static String formatRoundDate(java.sql.Date date) {
        if (date == null){
            return null;
        }
        synchronized(roundFormatter){
            return roundFormatter.format(date);
        }
    }
    
    public static String formatRoundDate(Date date) {
        if (date == null){
            return null;
        }
        synchronized(roundFormatter){
            return roundFormatter.format(date);
        }
    }
	
	public static String formatRoundDate(java.sql.Timestamp date) {
        if (date == null){
            return null;
        }
		synchronized(roundFormatter){
            return roundFormatter.format(date);
        }
    }

    /** Creates W3C DOM Document */
    public static Document createDom() { return XmlHelper.newDocument(); }

    /** Create W3C DOM Element for specified document and tag name */
    public static Element createDomElement(Document doc, String tag) {
        return Xbuilder.createEl(doc, tag, null);
    }

    /**
     * Get localized text value by resourceId and language code. Text is stored
     * in database resource table. If text not found in database - returned 
     * dafault value=resourceId.    
     * 
     * @param resourceId        The <code>String</code> text resource id
     * @param languageId        The <code>int</code> language code
     * @return                  The localized language text or default 
     */
    public static String getDbText(String resourceId, int languageId) {
        try{
            return StringResource.get().getText(resourceId, languageId);
        } catch(CriticalException e) {
            logger.error("- error", e);
            return resourceId;
        }
    }

    /**
     * Update or insert localized value for text(resourceId).
     * 
     * @param resourceId        Resource id to be localized
     * @param langId            Language id (locale)
     * @param newText           Localized value for text
     * @return                  The oldValue if text was updated, 
     *                          null - if text was inserted  
     */
    public static String setDbText(
            String resourceId, int langId, String newText) {
        try{
            return StringResource.get().setText(resourceId, langId, newText);
        } catch(CriticalException e){
            logger.error("- error", e);
            return resourceId;
        }
    }
    
    /**
     * Get default interface language code.
     * <b>Since 2.5, returns string "en"</b>.
     * Before 2.5, returned code from table <b>interface_language</b>
     * for the language with minimum <b>id</b>. 
     */
    public static String getDefaultInterfaceLanguageCode() { return "en"; }
    
    public static String getInterfaceLanguageCode(HttpServletRequest request){
    	String interfaceLanguage = 
            (String) request.getSession().getAttribute(INTERFACE_LANGUAGE_ATTR_NAME);         
        if (interfaceLanguage == null){
            interfaceLanguage = getDefaultInterfaceLanguageCode(); 
            request.getSession().setAttribute(
                    INTERFACE_LANGUAGE_ATTR_NAME,
                    interfaceLanguage
            );
        }
        return interfaceLanguage;
    }
    
    /** Gets SMTP-Server host (name or IP) */
    public static String getSmtpHost() {
    	return Env.getProperty("SMTP_SERVER", "127.0.0.1");
    }
    /**check and gets valid SMTP-Server host (name or IP) WARNING: this function spends resource, be careful   */
    public static String getValidSmtpHost() {
    	
    	String smtpDefaultHost = Env.getProperty("SMTP_SERVER", "127.0.0.1");
    	String smtpReserveHost = Env.getProperty("SMTP_SERVER2", "127.0.0.1");
    	if (new SMPTChecker().checkSmtpAvailability(smtpDefaultHost))
    		return smtpDefaultHost;
    	else
    		return smtpReserveHost;
    }
    
    /**
     * Returns thread local host name
     * (format: scheme, host, port and traling slash).
     */
    public static String getHostName() { return hostName.get(); }
    
    /** Sets thread local host name. */
    public static void setHostName(String name) { hostName.set(name); }
    
    public static String getServerName() { return serverName.get(); }
    
    public static void setServerName(String name) { serverName.set(name); }

    public static String getCommonHostName() { return commonHostName; }
    
    public static void setCommonHostName(String name) { commonHostName = name; }
    
    /** Gets default email */
    public static String getDefaultEmail() {
    	return getProperty("DEFAULT_EMAIL", "support.negeso.com");
    }
    
    /** Returns thread local site id */
    public static Long getSiteId() {
    	if (siteId.get() == null) {
    		siteId.set(1L);
    	}
    	return siteId.get(); 
    }
    
    /** Sets thread local site id */
    public static void setSiteId(Long id){ siteId.set(id); }
    
    /** Returns site thread local site object */
    public static Site getSite() {
        return SiteEngine.get().getSite(Env.getSiteId());
    }

	public static int getSecurityMode() {
		int mode = getIntProperty("security.mode", -1);
		if (mode != 0 && mode != 1) {
			throw new CriticalException("Site can not function properly. Please set security mode (0 or 1)");
		}
		return mode;
	}
	
	public static void setParameterService(ParameterService parameterService) {
		Env.parameterService = parameterService;
	}

	private static Properties getGlobalProperties() {
		if (properties == null) {
			properties = parameterService.getGlobalParameters();
		}
		return properties;
	}

	public static void setProperties(Properties properties) {
		Env.properties = properties;
	}
	
	private static Map<String, String> parseLangParam(String paramValue) {
		Map<String, String> langFrontPages = new HashMap<String, String>();
		if (paramValue == null)
			return langFrontPages;
		String[] langValues = paramValue.split(";");
		for(String langValue: langValues){
			if (langValue == null || "".equals(langValue.trim()))
				continue;
			String[]keyValue = langValue.split("=");
			if (keyValue.length == 2){
				langFrontPages.put(keyValue[0], keyValue[1]);
			}
			
		}
		return langFrontPages;
	}

	@SuppressWarnings("unchecked")
	public static String getLocales(HttpServletRequest request) {
        StringBuffer locales = new StringBuffer();
        if (request.getHeader("accept-language") == null)  {
        	return null;
        } 
        Enumeration<Locale> e = request.getLocales();
        while (e.hasMoreElements()) {
            locales.append(e.nextElement().getLanguage()).append(';');
        }
        return locales.toString();
    }
	
	
	public static boolean isFriendlyUrlEnabled() {
		return "Y".equals(Env.getProperty("pm_isFriendlyUrlEnabled"));
	}
	
	public static boolean isUseLangCodeAsUrlStart() {
		return "Y".equals(Env.getProperty("useLangCodeAsUrlStart"));
	}
	
	public static SimpleDateFormat getRoundsimplefullformatter() {
		return roundSimpleFullFormatter;
	}

	public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		transformer.transform(new DOMSource(doc),
				new StreamResult(new OutputStreamWriter(out, "UTF-8")));
	}
}
