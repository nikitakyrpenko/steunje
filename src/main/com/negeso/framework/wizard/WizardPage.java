/*
 * @(#)$Id: WizardPage.java,v 1.1, 2004-12-21 17:25:00Z, Olexiy Strashko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.wizard;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;

/**
*
* Wizard page interface
* 
* @author        Olexiy Strashko
* @version       $Revision: 2$
*/
public interface WizardPage {
	
	public void action(RequestContext request) throws CriticalException;

	public Element getElement(Document document, RequestContext request) throws CriticalException;
	
	public Element getBriefElement( Document doc );
	
	public WizardPage getNextPage();
	
	public WizardPage getPreviousPage();

	public String getTitle(); 
	
	public String getId(); 
	
	public boolean isComplete();
	
	public boolean canFinish();
	
	public boolean hasNext();
	
	public boolean hasPrevious();
	
	public int getStep();
	
}
