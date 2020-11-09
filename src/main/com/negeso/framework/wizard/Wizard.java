/*
 * @(#)$Id: Wizard.java,v 1.3, 2006-04-03 15:18:04Z, Andrey Morskoy$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.wizard;

import java.io.Serializable;
import java.util.ListIterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;


/**
 *
 * Xml wizard interface
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 4$
 */
public interface Wizard extends Serializable {
	public void start(RequestContext request) throws CriticalException;

	public void finish(RequestContext request) throws CriticalException;

	public Element getPageElement(Document document, RequestContext request) throws CriticalException;
	
	public Element getFinishElement(Document document, RequestContext request) throws CriticalException;

	public ListIterator pageIterator();
	
	public boolean isComplete();
}