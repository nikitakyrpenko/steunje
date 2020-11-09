
package com.negeso.framework;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class helps to keep absolute name of files and folders separately
 * from Java code. [File identifier - Location] map is configured in
 * <code>cms-resources.xconf</code>
 */
public abstract class ResourceMap {
    
    static Logger logger = Logger.getLogger(ResourceMap.class);
    
    /** Configuration file CONF_FILE_NAME in this web-application */
    private final static File CONF_FILE =
        new File(Env.getRealPath("WEB-INF/cms-resources.xconf"));
    
    /** Used to detect configuration changes */
    private static long confFileLastMod = 0;
    
    /** Keys are <b>resourceId</b>s, values are absolute file names */
    private static Map files = new HashMap();
    
    /** Factory with custom URIResolver (to access language resources etc) */
    private static TransformerFactory factory = TransformerFactory.newInstance();
    
    private static final String SYS_ID = Env.getRealPath("/").replace('\\', '/');
    
    static {
        factory.setURIResolver(new URIResolver() {
            public Source resolve(String href, String base) {
                logger.debug("+ -");
                StreamSource source = null;
                String path;
                if ( href.startsWith("/site/")){
                    /* XSLs specific to the thread-local site */
                    path = Env.getSite().getTemplateRealPath(href);
                } else{
                    /* XSLs common for all sites */
                    path = Env.getRealPath(href);
                }
                source = new StreamSource(new File(path));
                return source;
            }
        });
    }
    
    /**
     * Returns absolute filename or path identified by <b>resourceId</b>.
     * <br>
     * File.separator separates folders in path string. <br>
     * Folder names end with a separator ({@link java.io.File#separator}).
     * 
     * @param resourceId
     *            symbolic identifier
     * @return absolute path to file, including filename; <br>
     *         returns null if resourceId was not found in configuration file
     */
    public static synchronized String getRealPath(String resourceId) {
        logger.debug("+ -");
        ensureMapIsUpToDate();
        return (String) files.get(resourceId);
    }
    
    /** Returns W3C DOM2 Document */
    public static synchronized Document getDom(String resourceId) {
        logger.debug("+");
        if(resourceId == null) {
            logger.debug("- resourceId is null");
            return null;
        }
        ensureMapIsUpToDate();
        try {  
            File file = null;
            if ( "SITE_XSL".equals(resourceId) ){
                file = new File( 
                    Env.getSite().getTemplateRealPath("/site/xsl/site.xsl") 
                );
                if ( (file == null) || (!file.exists()) ){
                    logger.error(
                        "SITE_XSL not found for site:" + Env.getSite().getName() + 
                        " template:" + Env.getSite().getTemplateName() +
                        " path:" + Env.getSite().getTemplateRealPath("/site/xsl/site.xsl")
                    );
                }
            } else {
                file = new File((String) files.get(resourceId));
            }
            SAXReader reader = new SAXReader();
            reader.setEntityResolver(new WcmsDefaultHandler());
            logger.debug("-");
            return new DOMWriter(Document.class).write(reader.read(file));
        } catch (Exception e) {
            logger.error("- failed to parse resource " + resourceId, e);
            return null;
        }
    }
    
    /**
     * Returns Templates for XSL file specified by <b>location</b>
     * (location is relative to webapp root).
     * 
     * @throws RuntimeException if <b>location</b>  is not specified.
     */
    public static synchronized Templates getTemplatesByLocation(String location) {
        logger.debug("+");
        Validate.notEmpty(location, "Location is not specified");
        try {
            File file = new File(Env.getRealPath(location));
            SAXReader reader = new SAXReader();
            reader.setEntityResolver(new WcmsDefaultHandler());
            Document doc = new DOMWriter(Document.class).write(reader.read(file));
        	DOMSource source = new DOMSource(doc);
        	source.setSystemId(SYS_ID);
        	logger.debug("-");
        	return factory.newTemplates(source);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

	/**
     * Returns Templates based on Document with specified resourceId. If
     * stylesheet is not cached yet, it is compiled and cached. If revalidation
     * is forced by configuration, the stylesheet is re-compiled.<br>
     * 
     * @param resourceId    symbolic identifier of XSL file
     * @return      Templates (stylesheet) or null (if resourceId refers
     *              to invalid document
     */
    public static synchronized Templates getTemplates(String resourceId) {
        logger.debug("+");
        if(resourceId == null) {
            logger.debug("- resourceId is null; returning null");
            return null;
        }
        ensureMapIsUpToDate();
        Templates xsl = null;
        try {
            if("SITE_XSL".equals(resourceId)) {
                if(Env.getSite().getCachedSiteXsl() != null) {
                    Env.getSite().setCachedSiteXslLastUsed( System.currentTimeMillis() );
                    logger.info("- return site.xsl from cache");
                    return Env.getSite().getCachedSiteXsl();
                }
                DOMSource source = new DOMSource(getDom("SITE_XSL"));
                source.setSystemId(SYS_ID);
                xsl = factory.newTemplates(source);
                logger.info("- cache.xsl:" + Env.getGlobalProperty("cache.xsl"));
                if ("true".equals(Env.getGlobalProperty("cache.xsl")) ) {
                    Env.getSite().setCachedSiteXslLastUsed( System.currentTimeMillis() );
                    Env.getSite().setCachedSiteXsl( xsl );
                    logger.info("- install site.xml cache to site");
                }
            } else {
                DOMSource source = new DOMSource(getDom(resourceId));
                source.setSystemId(SYS_ID);
                xsl = factory.newTemplates(source);
            }
        } catch (Exception e) {
            logger.error("Exception in resource " + resourceId, e);
        }
        logger.debug("-");
        return xsl;
    }
    
    /**
     * Checks if any changes were made to configuration file, and reloads the
     * configuration if so.
     */
    private static void ensureMapIsUpToDate() {
        logger.debug("+");
        if(CONF_FILE.lastModified() == confFileLastMod) {
            logger.debug("- map is up-to-date");
            return;
        }
        confFileLastMod = CONF_FILE.lastModified();
        reloadCache();
    }
    
    public static void reloadCache() {
        Env.getSite().setCachedSiteXsl(null);
        try {
            files.clear();
            Iterator iter = new SAXReader().read(CONF_FILE)
                    .selectNodes("/resources/*").iterator();
            while (iter.hasNext()) {
                org.dom4j.Element info = (org.dom4j.Element) iter.next();
                String location =
                    Env.getRealPath(info.attributeValue("location", ""));
                if (new File(location).isDirectory()) {
                    location += File.separator;
                }
                files.put(info.attributeValue("id", ""), location);
            }
            logger.debug("- map has been updated");
            return;
        } catch (Exception e) {
            logger.error("- Exception", e);
            return;
        }
    }
    
    private static class WcmsDefaultHandler extends DefaultHandler {
    	
    	@Override
    	public InputSource resolveEntity(String publicId, String systemId) {
    		logger.debug("+");
    		String sid = systemId.replaceFirst("file:/*", "");
    		sid = Env.getRealPath(sid);
    		sid = "file:///" + sid.replaceAll("\\\\", "/");
    		InputSource source = new InputSource(sid);
    		logger.debug("-");
    		return source;
    	}
    	
    }
    
}