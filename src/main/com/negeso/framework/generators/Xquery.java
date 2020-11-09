/*
 * @(#)$Id: Xquery.java,v 1.4, 2006-02-15 19:25:16Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.generators;

import java.util.List;

import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.negeso.framework.Env;

/**
 * Utility class for easy quering W3C DOM trees.
 * All methods are aware of Negeso namespace.
 * For details on how queries work and what are the limitations,
 * see {@linkplain DOMXPath Jaxen documentation}.
 * 
 * @version $Revision: 5$
 * @author  Stanislav Demchenko
 */
public class Xquery {
    
    private static final Logger logger = Logger.getLogger(Xquery.class);
    
    private Xquery() { /** Pervent instantiation; */}
    
    /**
     * Returns an element matching <b>query</b> (<b>node</b> is the starting
     * point of search).
     * 
     * @param   node    node of the tree to search through
     * @param   query   XPath query string, e.g.: "negeso:menu_item[@id=5]"
     * @return          element satisfying the query or null
     */
    public static Element elem(Node node, String query) {
        logger.debug("+ -");
        return (Element) node(node, query);
    }
    
    /**
     * Returns elements matching <b>query</b> (<b>node</b> is the starting
     * point of search).
     * 
     * @param   node    node of the tree to search through
     * @param   query   XPath query string, e.g.: "negeso:menu_item[@id=5]"
     * @return          element satisfying the query; never null
     */
    public static List<Element> elems(Node node, String query) {
        logger.debug("+ -");
        return nodes(node, query);
    }
    
    /**
     * Selects a node satisfying the <b>query</b> from a tree, which parameter
     * <b>node</b> belongs to.
     * 
     * @param   node    node of the tree to search through
     * @param   query   XPath query string, e.g.: "negeso:menu_item[@id=5]"
     * @return          node satisfying the query or null
     */
    public static Node node(Node node, String query) {
        logger.debug("+ -");
        try {
            return (Node) path(query).selectSingleNode(node);
        } catch (JaxenException e) {
            logger.error("- throwing RuntimeException", e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Selects nodes satisfying the <b>query</b> from a tree which parameter
     * <b>node</b> belongs to.
     * 
     * @param   node    node of the tree to search through
     * @param query     XPath query string, e.g.: ".//negeso:menu_item"
     * @return          list of nodes satisfying the query; never null
     */
    public static List nodes(Node node, String query) {
        logger.debug("+ -");
        try {
            return path(query).selectNodes(node);
        } catch (JaxenException e) {
            logger.error("- throwing RuntimeException", e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Builds DOMXPath object
     * 
     * @param query    XPath query string, e.g.: "negeso:menu_item[@id=5]"
     * @return  ready-to-use DOMXPath (aware of Negeso namespase)
     */
    private static DOMXPath path(String query) {
        logger.debug("+");
        try {
            DOMXPath path = new DOMXPath(query);
            path.addNamespace(Env.NEGESO_PREFIX, Env.NEGESO_NAMESPACE);
            logger.debug("-");
            return path;
        } catch (JaxenException e) {
            logger.error("- throwing RuntimeException", e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Finds a <b>node</b> matching the <b>query</b> and returns its value
     * ({@link Node#getNodeValue()}).<br/>
     * Example: <tt>Xquery.val(doc, "//some_item/&#64;name")</tt>
     * 
     * @param   node    node of the tree to search through
     * @param   query   XPath query string, e.g.: "negeso:menu_item[@id=5]"
     * @return      string value of the node satisfying the query;
     *              null otherwise
     */
    public static String val(Node node, String query) {
        Node target = node(node, query);
        return target == null ? null : target.getNodeValue();
    }
    
}
