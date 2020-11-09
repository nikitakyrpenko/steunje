/*
 * @(#)StandardXmlBulder.java       @version	22.04.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.tree.QNameCache;

/**
 * Standard xml bilder. Used for xml-entity building. Includes:
 * 		- page 
 * 		- error messages
 * 		- user messages 
 *
 * @version 	22.04.2004
 * @author 		OStrashko
 */
public class StandardXmlBulder {
	private QNameCache qNameCache = new QNameCache();
	private Namespace ngNamespace = XmlHelper.getNegesoDom4jNamespace();

	/**
	 * 
	 */
	
	public StandardXmlBulder() {
		super();
	}
	
	/** 
	 * Builds page xml-entity by title.
	 * 
	 * @return
	 */
	public Element getPageDom4j(String title){
		Element page = DocumentHelper.createElement(
			qNameCache.get("page", ngNamespace)
		)
			.addAttribute("title", title)
		;
		return page;
	}


	public Element getMessageDom4j(String message){
		Element errorMsg = DocumentHelper.createElement(
			qNameCache.get("message", ngNamespace)
		);
		errorMsg.addAttribute("text", message);
		return errorMsg;
	}

	public Element getMessagesDom4j(String tag, List messages){
		Element errorMsgs = DocumentHelper.createElement(
			qNameCache.get(tag, ngNamespace)
		);
		for (Iterator i = messages.iterator(); i.hasNext();){
			errorMsgs.add(this.getMessageDom4j((String) i.next()));
		}
		return errorMsgs;
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	public Element getErrorMessageDom4j(String message){
		List messages = new ArrayList();
		messages.add(message); 
		return this.getMessagesDom4j("error-messages", messages);
	}

	/**
	 * 
	 * @param messages
	 * @return
	 */
	public Element getErrorMessageDom4j(List messages){
		return this.getMessagesDom4j("error-messages", messages);
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	public Element getUserMessageDom4j(String message){
		List messages = new ArrayList();
		messages.add(message); 
		return this.getMessagesDom4j("user-messages", messages);
	}
	
	/**
	 * 
	 * @param messages
	 * @return
	 */
	public Element getUserMessageDom4j(List messages){
		return this.getMessagesDom4j("user-messages", messages);
	}

}
