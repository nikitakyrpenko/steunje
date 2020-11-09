/*
 * @(#)$Id: AbstractWizard.java,v 1.7, 2006-05-04 08:42:50Z, Dmitry Dzifuta$
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;

/**
 *
 * AbstractWizard implementation.
 * Works with request context filled with parameters:
 * - "action=next"
 * - "action=prev"
 * - "action=finish"
 *  
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 8$
 */
abstract public class AbstractWizard implements Wizard, Serializable {
	private static Logger logger = Logger.getLogger( AbstractWizard.class );

	private Collection<String> errors = null;
	private WizardPage currentPage = null;
	private boolean isComplete = false;

	public Element getPageElement( Document document, RequestContext request ) 
		throws CriticalException 
	{
		Element wElement = this.getStubElement(document, request);
		String action = request.getString("wizard_action", "");
		
		try{
			Element pageElement = null; 
			if ( "next".equalsIgnoreCase(action) ){
	    		logger.info("next");
	    		this.getCurrentPage().action( request );
	    		this.setCurrentPage( this.getCurrentPage().getNextPage() );
	    		pageElement = this.getCurrentPage().getElement( document, request );
	    	}
			else if( "prev".equalsIgnoreCase(action) ){
	    		logger.info("prev");
	    		this.currentPage = this.currentPage.getPreviousPage();
	    		pageElement = this.currentPage.getElement( document, request );
			}
			else if( "finish".equalsIgnoreCase(action) ){
	    		logger.info("finish");
	    		this.getCurrentPage().action( request );	    		
	    		this.finish(request);
	    		this.setComplete(true);
	    		pageElement = this.getFinishElement( document, request ); 
			}
			else{
	    		logger.info("no params, rebuild current page");
	    		pageElement = this.currentPage.getElement( document, request);
			}
			wElement.appendChild(this.getBriefPages( document, request ));
			wElement.appendChild(pageElement);
		}
		catch(CriticalException e){
			logger.error("-error", e);
			wElement.setAttribute("error", e.getMessage());
		} 
		
		return wElement;
	}

	public Element getStubElement( Document doc, RequestContext request ){
		return Xbuilder.createEl(doc, "wizard", null);
	}

	public Element getFinishElement( Document doc, RequestContext request ) 
		throws CriticalException
	{
		Element element = Xbuilder.createEl(doc, "wizard-page", null);
		Xbuilder.setAttr(element, "id", "RESULT");
		if ( this.hasErrors() ){
			element.appendChild( XmlHelper.createErrorsElement(doc, this.getErrors()) );
		}
		return element;
	}

	public Element getBriefPages( Document doc, RequestContext request ){
		Element breafPagesEl = Xbuilder.createEl( doc, "brief-wizard-pages", null );
		WizardPage page = null;
		Element briefEl = null;
		for ( Iterator i = this.pageIterator(); i.hasNext(); ){
			page = (WizardPage) i.next();
			
			briefEl = page.getBriefElement( doc );
			if ( !this.isComplete() ){
				if ( page.equals(this.getCurrentPage()) ){
                    Xbuilder.setAttr(briefEl, "is-current", "true");
				}
			}
			breafPagesEl.appendChild( briefEl );
		}
		return breafPagesEl;
	}

	
	/**
	 * Errors collection to support user errors
	 * 
	 * @return  The errors collection
	 */
	public Collection<String> getErrors() {
		if ( this.errors == null ){
			this.errors = new ArrayList<String>();
		}
		return this.errors;
	}

	/**
	 * Tests component for errors
	 * 
	 * @return
	 */
	public boolean hasErrors() {
		if ( this.errors == null ){
			return false;
		}
		if ( this.errors.isEmpty() ){
			return false;
		}
		return true;
	}


	/**
	 * @return Returns the currentPage.
	 */
	public WizardPage getCurrentPage() {
		return currentPage;
	}
	
	/**
	 * @param currentPage The currentPage to set.
	 */
	public void setCurrentPage(WizardPage currentPage) {
		this.currentPage = currentPage;
	}
	/**
	 * @return Returns the isComplete.
	 */
	public boolean isComplete() {
		return isComplete;
	}
	/**
	 * @param isComplete The isComplete to set.
	 */
	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}
}
