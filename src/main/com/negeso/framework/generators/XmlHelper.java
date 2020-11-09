/*
 * @(#)XmlHelper.java       @version 22.12.2003
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */


package com.negeso.framework.generators;



import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.dom4j.Namespace;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.ResourceMap;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;


/**
 * This class contains static helper methods for basic XML-related operations.
 *
 * @author Stanislav Demchenko
 * @author Olexiy.Strashko
 **/
public class XmlHelper {
	
	private static Namespace ngNamespace = new Namespace(Env.NEGESO_PREFIX, Env.NEGESO_NAMESPACE);
	
	private static Logger logger = Logger.getLogger(XmlHelper.class);
	
    private static final DocumentBuilderFactory documentBuilderFactory =
        DocumentBuilderFactory.newInstance();
    
    /** We need not create many DocumentBuilder's to instantiate Document's */
    private static final DocumentBuilder documentBuilder;
    
    private static final TransformerFactory transformerFactory =
        TransformerFactory.newInstance();
	
    
    static
    {
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder builder = null;
        try {
            builder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error("ParserConfigurationException", e);
        }
        documentBuilder = builder;
        
    }
    
    	
    private XmlHelper()
    {
        // Utlity class, no need to instantiate it
    }
    
    
	/**
	 * Retrieves Negeso namespace  
	 * 
	 * @return negeso Namespace Dom4j object. 
	 */
	public static Namespace getNegesoDom4jNamespace(){
		logger.debug("+ -");
		return XmlHelper.ngNamespace;
	}
	
    
	/**
	 * Gets a new instance of namespace aware DocumentBuilder. The method is
     * thread safe.
	 * 
     * @throws RuntimeException if the parser is not configured properly
	 */ 
	public static DocumentBuilder newBuilder()
    {
		logger.debug("+");
		try {
            synchronized(documentBuilderFactory){
                return documentBuilderFactory.newDocumentBuilder();
            }
		} catch (ParserConfigurationException e) {
			logger.error("- Cannot create a document builder", e);
			throw new RuntimeException("Cannot create a document builder");
		}
	}
    
    
    /**
     * Gets a new instance of Transformer. The method is thread safe.
     * 
     * @throws RuntimeException if the parser is not configured properly
     */ 
    public static Transformer newTransformer()
    {
        logger.debug("+ -");
        try {
            synchronized(transformerFactory){
                return transformerFactory.newTransformer();
            }
        } catch (TransformerConfigurationException e) {
            logger.error("- Cannot create XSL Transformer", e);
            throw new RuntimeException("Cannot create XSL Transformer");
        }
    }
    
    
    /**
     * Gets a new instance of Document. The method is thread safe.
     * 
     * @throws RuntimeException if the parser is not configured properly
     */ 
    public static Document newDocument()
    {
        logger.debug("+ -");
        synchronized(documentBuilder){
            return documentBuilder.newDocument();
        }
    }
    
    
    //======================================================================
	// Standard Negeso Elements creation helpers (w3c dom model)   
	//======================================================================
    /**
	 * Create path-element. Encapsulate element of path in page. Indentified by
	 * title and link(required parameters).      
	 * 
	 * @param doc		The Document container 
	 * @param title		The path-element title
	 * @param link		The path-element link
	 * @param type		The path-element type
	 * @param id		The path-element id
	 * @return			The path-element <code>Element</code>
	 */
	public static Element createPathElement(
		Document doc, String title, String link, String type, String id 
	)
	{
        Element currEl = Xbuilder.createEl(doc, "path-element", null);
		// required
		Xbuilder.setAttr(	currEl, "title", title );
		Xbuilder.setAttr(	currEl, "link", link );

		// optional
		if (type != null){
		    Xbuilder.setAttr( currEl, "type", type );
		}
		if (id != null){
		    Xbuilder.setAttr( currEl, "id", id );
		}
		return currEl;
	}
    
    
	/**
	 * Create standard page element for admin interface. Unlike
     * {@link XmlHelper#createPageElement(RequestContext)}, it does not set
     * attribute "interface-language".
	 * 
	 * @param langCode
	 * @param langId
	 * @return
	 */
	public static Element createPageElement(String langCode, int langId)
    {
        Element page = Xbuilder.createTopEl("page");
	    Xbuilder.setAttr(page, "lang", langCode);
	    Xbuilder.setAttr(page, "lang-id", "" + langId);
	    return page;
	}
    
    
    /**
     * Create standard page element for admin interface. Unlike
     * {@link XmlHelper#createPageElement(String,int)}, also sets attribute
     * "interface-language".
     * 
     * @param request
     * @return
     * @throws CriticalException
     */
    public static Element createPageElement(RequestContext request) 
    	throws CriticalException 
    {
        Language lang = request.getSession().getLanguage();
        if (lang == null) {
            lang = Language.getDefaultLanguage();
        }
        Element page = Xbuilder.createTopEl("page");
        Xbuilder.setAttr(page, "lang", lang.getCode());
        Xbuilder.setAttr(page, "lang-id", lang.getId());
        Xbuilder.setAttr(page, "interface-language",
            request.getSession().getInterfaceLanguageCode() );
        Xbuilder.setAttr(page, "total-site-languages", Language.getItems().size());
        addRequestParameters(page, request);
        return page;
    }
    
    
    /**
     * Adds request parameters to the root element, e.g.:
     * [negeso:page [negeso:request [negeso:parameter @name [value], ...] ] ]
     */
    private static void addRequestParameters(Element elPage, RequestContext request) {
    	Element elRequest = Xbuilder.addEl(elPage, "request", null);
    	for (Object name : request.getParameterMap().keySet()) {
    		Element elParameter = Xbuilder.addEl(elRequest, "parameter", null);
    		Xbuilder.setAttr(elParameter, "name", name);
    		
    		if (request.getParameters((String) name) != null) {
	    		for (String value : request.getParameters((String) name)){
	    			Xbuilder.addEl(elParameter, "value", value);
	    		}
    		}
		}
    }
    
    
    /**
     * Create errors element by Collection of error strings:
     * <negeso:errors>
     * 		<negeso:error>error text1</negeso:error>
     * 		<negeso:error>error text2</negeso:error>
     * 		<negeso:error>error textN</negeso:error>
     * </negeso:errors>
     * 
     * @param request
     * @return
     * @throws CriticalException
     */
    public static Element createErrorsElement( Document doc, Collection errors ) 
	{
	    Element errorsEl = Xbuilder.createEl(doc, "errors", null);
	    if ( errors != null ){
		    for (Iterator i = errors.iterator(); i.hasNext();){
		    	Xbuilder.addEl( errorsEl, "error", i.next());
		    }
	    }
	    return errorsEl;
	}

    /**
     * Create errors element by Collection of error strings:
     * <negeso:errors>
     *      <negeso:error>error text1</negeso:error>
     *      <negeso:error>error text2</negeso:error>
     *      <negeso:error>error textN</negeso:error>
     * </negeso:errors>
     * 
     * @param request
     * @return
     * @throws CriticalException
     */
    public static Element buildErrorsElement( Element parent, Collection errors ){
        Element errorsEl = Xbuilder.addEl(parent, "errors", null);
        if ( errors != null ){
            for (Object error: errors){
                if (error == null){
                    logger.error("ERROR: null error found in errors collection");
                }
                else{
                    Xbuilder.addEl( errorsEl, "error", error.toString());
                }
            }
        }
        return errorsEl;
    }

    
	/**
	 * Perform XSLT transformation of XML document. XSL is setuped
	 * by RESOURCE_ID in resource conf file.  
	 * 
	 * @param document
	 * @param pm_activation_mail_xsl
	 * @return
	 * @throws CriticalException
	 */
	public static String transformToString(
			Document doc, String xslId, String method) throws CriticalException
	{
		Templates templates = ResourceMap.getTemplates(xslId);
        return XmlHelper.transformToString(doc, templates, method);
	}
    
    
    
    /**
     * Perform XSLT transformation of XML document. XSL is setuped
     * by RESOURCE_ID in resource conf file.  
     * 
     * @param document
     * @param pm_activation_mail_xsl
     * @return
     * @throws CriticalException
     */
    public static String transformToString(
            Document doc, Templates templates, String method) throws CriticalException
    {
        
        if ( templates == null ){
            logger.error("-error: template is null");
            throw new CriticalException("-error: template is null");
        }
        
        StringWriter writer = new StringWriter();
        Transformer transformer = null;
        try {
            transformer = templates.newTransformer();
            if ( method != null ){
                transformer.setOutputProperty("method", method);
            }
            transformer.setOutputProperty("indent", "no");
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            writer.flush();
            return writer.getBuffer().toString();
        } 
        catch (TransformerException e) {
            logger.error("-error: ", e);
            throw new CriticalException(e);
        }
        finally {
            if (writer != null){
                try{
                    writer.close();
                }
                catch(IOException e){
                    logger.error("-error", e);
                }
            }
        }
    }
    
    
    /**
     * Build hidden parameters from request
     * 
     * @param parent
     * @param request
     * @param unusedParams 
     * @return
     */
    public static Element buildHiddenParameters(Element parent, 
    		RequestContext request, List unusedParams) {
    	Element hParams = null; 
    	Map params = request.getParameterMap();
    	for (Object key: params.keySet()) {
    		if (hParams == null) {
    			hParams = Xbuilder.addEl(parent, "hiddenParameters", null);
    		}
    		if ( RequestContext.REQUEST_LOCALES.equals(key) || RequestContext.REQUEST_URI.equals(key) 
    				|| unusedParams.contains(key)) {
    			continue;
    		}
    		if ( params.get(key) != null ) {
	    		Element param = Xbuilder.addEl(hParams, "parameter", null);
	    		if ("act".equals(key)) {
	    			Xbuilder.setAttr(param, "name", "action");
	    		} else {
		    		Xbuilder.setAttr(param, "name", key);
	    		}
	    		Xbuilder.setAttr(param, "value", request.getParameter((String) key));
    		}
    	}
    	return hParams;
    }
    
    
    /**
     * Get xsl templates, customized by:
     * - path - webapp relative path of .xsl file
     * - resolver - URIResolver  
     * 
     * @param path
     * @param resolver
     * @return
     */
    public static Templates getTemplates(File file, URIResolver resolver){
        logger.debug("+");

    	if (!file.exists()){
    		logger.error("File: " + file.getAbsolutePath() + " not exists");
    		throw new CriticalException("File: " + file.getAbsolutePath() + " not exists");
    	}
    	SAXReader reader = new SAXReader();
    	try{
    		org.dom4j.Document doc = reader.read(file);
	    	DOMWriter writer = new DOMWriter();
	    	org.w3c.dom.Document xslDoc = writer.write(doc);
	        //logger.error("Got sample xsl file");
	
	        TransformerFactory factory = TransformerFactory.newInstance();
	        factory.setURIResolver(resolver);
		    
		    DOMSource source = new DOMSource(xslDoc);
		    //logger.error("real path:" + Mentrix.get().getRealPath("/"));
		    source.setSystemId(Env.getRealPath("/").replace('\\', '/'));
		    Templates templates = factory.newTemplates(source);
            logger.debug("-");
		    return templates;
    	}
	    catch(Exception e){
	    	logger.error("-ERROR:", e);
	    	throw new CriticalException(e);
	    }
    }
}
