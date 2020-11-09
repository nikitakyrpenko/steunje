/*
 * @(#)$Id: Xbuilder.java,v 1.8, 2007-03-20 08:29:20Z, Olexiy Strashko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.generators;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.negeso.framework.Env;
import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;


/**
 * Utility class for ease of use of W3C DOM. All methods are aware of
 * Negeso namespace.
 * 
 * @version $Revision: 9$
 * @author  Stanislav Demchenko
 * @author  Olexiy Strashko
 */

public class Xbuilder {
	private static final Map<Class<?>, JAXBContext> cachedJAXBContexts = new LinkedHashMap<Class<?>, JAXBContext>();
    
    private static final Logger logger = Logger.getLogger(Xbuilder.class);


    private Xbuilder() { /** Pervent instantiation; */}
    
	
	/**
	 * Creates a new Document and an element in it.
	 * 
	 * @param name 		The name of the element.
	 * @return			The element created.
	 */
    public static Element createTopEl(String name)
    {
        logger.debug("+");
    	Element element = addEl(XmlHelper.newDocument(), name, null);
        element.setAttribute("xmlns:negeso", Env.NEGESO_NAMESPACE);
        logger.debug("-");
    	return element;
    }
    
    
	/**
	 * Creates an element, but unlike <b>addEl(...)</b> does not add it to
     * <b>parent</b>.
	 * 
	 * @param parent 	The parent node (used for getOwnerDocument())
	 * @param name 		The name for the element
	 * @param text 		The text value for the element (ignored if null or
     *                  empty string)
	 * @return			The element created.
	 */
	public static Element createEl(Node parent, String name, Object text)
    {
		logger.debug("+");
        Document doc;
        if(parent instanceof Document){
        	doc = (Document) parent;
        }else{
        	doc = parent.getOwnerDocument();
        }
        Element child = doc.createElementNS(Env.NEGESO_NAMESPACE,name);
        child.setPrefix(Env.NEGESO_PREFIX);
        
        if( text != null && !"".equals(text.toString()) ){
            child.appendChild( doc.createTextNode(text.toString()) );
        }
        logger.debug("-");
        return child;
	}
    
    
    /**
     * Creates an element and adds it to <b>parent</b>.
     * Also <b>text</b> is appended (if any).
     * 
     * @param parent    The element (or document) to append the new element to.
     * @param name      The name of the element to be created.
     * @param text      The value of the newly created element (ignored if
     *                  null or empty string).
     * @return          The element created.
     */
    public static Element addEl(Node parent, String name, Object text)
    {
        logger.debug("+ -");
        return (Element) parent.appendChild(createEl(parent, name, text));
    }
    
    
    /**
     * If <code>text</code> is not null and not empty, an additional child
     * Text node is created and added to the <code>element</code>.
     * 
     * @param element   The element to append some text to.
     * @param text      The text to be added to the element.
     */
    public static void addText(Element element, Object text)
    {
        logger.debug("+ -");
        if(text != null && !"".equals(text)){
            element.appendChild(
                element.getOwnerDocument().createTextNode(text.toString()));
        }
    }
    
    
    /**
     * If <b>text</b> is not empty, sets an attribute of the element;
     * if <b>text</b> is null or empty string, the attribute is removed
     * 
     * @param element   The element whose attribute is being set.
     * @param name      The name of the attribute to be added/updated/removed.
     * @param text      The value of the attribute. If null or empty string,
     *                  the attribute is removed.
     */
    public static void setAttr(Element element, String name, Object text)
    {
        logger.debug("+ -");
        if(text == null || "".equals(text.toString())) {
            Attr attr = element.getAttributeNode(name);
            if(attr != null){
                element.removeAttributeNode(attr);
            }
        } else {
            element.setAttribute(name, text.toString());
        }
    }
    
    
    /**
     * Sets an attribute of the element. If <b>text</b> is null or empty,
     * the new value of the attribute is empty string.
     * 
     * @param element   The element whose attribute is being set.
     * @param name      The name of the attribute to be added or set.
     * @param text      The value of the attribute. If null or empty string,
     *                  the value is empty string.
     */
    public static void setAttrForce(Element element, String name, Object text)
    {
        logger.debug("+ -");
        element.setAttribute(name, text == null ? "" : text.toString());
    }


	public static Element createTopEl(String name, String msgBody) {
        logger.debug("+");
    	Element element = addEl(XmlHelper.newDocument(), name, msgBody);
        //element.setAttribute("xmlns:negeso", Env.NEGESO_NAMESPACE);
        logger.debug("-");
    	return element;
	}
    
	/**
	 *  Adds date element with "year", "month", "day, "dayOfWeek" attributes
	 */
	public static Element addFormattedDateEl(Element parentEl, Date date, String dateElementName, String langCode) {
		logger.debug("+");
		Element eventEl = Xbuilder.addEl(parentEl, dateElementName == null ? "Date" : dateElementName, null);
		if (date == null) {
			logger.debug("- null date");
			return eventEl;   			
		}		
		SimpleDateFormat dateFormater = new SimpleDateFormat(
				"yyyy MM dd EEEE", 
				new Locale(langCode == null ? Env.getDefaultLanguageCode() : langCode));
		StringTokenizer st = new StringTokenizer(dateFormater.format(date), " ");
		Xbuilder.setAttr(eventEl, "year", st.nextElement());
		Xbuilder.setAttr(eventEl, "month", st.nextElement());
		Xbuilder.setAttr(eventEl, "day", st.nextElement());
		Xbuilder.setAttr(eventEl, "dayOfWeek", st.nextElement());		
		logger.debug("-");
		return eventEl;   
	}

	/**
	 *  Set attributes: year, month, day
	 */
	public static Element setDateAttrs(Element parentEl, Date date) {
		logger.debug("+");
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		
		Xbuilder.setAttr(parentEl, "year", calendar.get(Calendar.YEAR));
		Xbuilder.setAttr(parentEl, "month", String.format("%02d", (calendar.get(Calendar.MONTH) + 1)));
		Xbuilder.setAttr(parentEl, "day", String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
		return parentEl;
	}
	
	public static void addRedirection(Element el, String redirectionUrl) {
		Element redirection = Xbuilder.addEl(el, "redirection", redirectionUrl);
		el.appendChild(redirection);
	}
	
	
	/**
	 * Creates Element from object
	 */
	public static Element objectToElement(Document doc, Class clazz,
			Object object, String alias) {
		Element resultEl = null;
		QNameMap qnameMap = new QNameMap();
		qnameMap.setDefaultPrefix(Env.NEGESO_PREFIX);
		qnameMap.registerMapping(new QName(Env.NEGESO_NAMESPACE, alias,
				Env.NEGESO_PREFIX), clazz);
		XStream xstream = new XStream(new StaxDriver(qnameMap));
		String xml = xstream.toXML(object);
		try {
			Document document = XmlHelper.newBuilder().parse(
							new ByteArrayInputStream(xml.getBytes()));
			resultEl = (Element) doc.adoptNode(document.getDocumentElement());
		} catch (SAXException e) {
			resultEl = createEl(doc, alias, null);
			e.printStackTrace();
		} catch (IOException e) {
			resultEl = createEl(doc, alias, null);
			e.printStackTrace();
		}
		return resultEl;
	}

	public static Element addBeanJAXB(Element parent, Object bean){
        return addBeanJAXB(parent, bean, bean.getClass());    
    }

    public static Element addBeanJAXB(Element parent, Object bean, Class<?> marshallingClass){
		try {
            JAXBContext context = cachedJAXBContexts.get(marshallingClass);

            
            
            if (context == null){
                context = JAXBContext.newInstance(marshallingClass);

                synchronized (cachedJAXBContexts){
                    logger.info("Caching JAXBContext for class " + marshallingClass.toString());
                    cachedJAXBContexts.put( marshallingClass, context);
                }
            }

            Marshaller m = context.createMarshaller();
            m.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapper() {
				@Override
				public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
					if(namespaceUri.equals(Env.NEGESO_NAMESPACE)){
						return Env.NEGESO_PREFIX;
					}
					return suggestion;
				}
			} );
            m.marshal(bean, parent);
		
		}catch(JAXBException e){
			logger.error("-error", e);
		}
        return (Element) parent.getLastChild();
	}


}
