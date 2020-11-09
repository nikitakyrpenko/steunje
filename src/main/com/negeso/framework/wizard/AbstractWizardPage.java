/*
 * Created on 21.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.framework.wizard;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.Xbuilder;

/**
*
* Abtsract Wizard page implementation
* 
* @author        Olexiy Strashko
* @version       $Revision: 4$
*/
abstract public class AbstractWizardPage implements WizardPage, Serializable {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(AbstractWizardPage.class); 
	
	private Collection<String> errors = null;
	private boolean isComplete = false;
	
	public Element getBriefElement( Document doc ) {
		Element briefEl = Xbuilder.createEl(doc, "brief-wizard-page", null);
		Xbuilder.setAttr( briefEl, "id", this.getId() ); 
		Xbuilder.setAttr( briefEl, "title", this.getTitle() ); 
		Xbuilder.setAttr( briefEl, "is-complete", "" + this.isComplete() );
		Xbuilder.setAttr( briefEl, "step", this.getStep());
		return briefEl;
	}

	public Element getElement( Document doc, RequestContext request ) throws CriticalException {
		Element pageEl = Xbuilder.createEl(doc, "wizard-page", null);
		Xbuilder.setAttr( pageEl, "id", this.getId() ); 
		Xbuilder.setAttr( pageEl, "title", this.getTitle() );
		Xbuilder.setAttr( pageEl, "has-next", "" + this.hasNext() );
		Xbuilder.setAttr( pageEl, "has-previous", "" + this.hasPrevious() );
		Xbuilder.setAttr( pageEl, "can-finish", "" + this.canFinish() );
		Xbuilder.setAttr( pageEl, "is-complete", "" + this.isComplete() );
		return pageEl;
	}
	
	/**
	 * Errors collection to support user errors
	 * 
	 * @return  The errors collection
	 */
	public Collection<String> getErrors() {
		if ( this.errors == null ){
			this.errors = new HashSet<String>();
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

	public boolean isComplete(){
		return this.isComplete;
	}
	
	/**
	 * @param isComplete The isComplete to set.
	 */
	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public void action(RequestContext request) throws CriticalException {
		this.getErrors().clear();
		doAction(request);
		if ( !this.hasErrors() ){
			this.setComplete(true);
		}
		else{
			this.setComplete(false);
		}
	}

	abstract protected void doAction(RequestContext request);

}
