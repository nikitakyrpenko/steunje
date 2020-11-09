/*
 * @(#)$Id: PreparedPageComponent.java,v 1.10, 2007-03-19 17:04:09Z, Alexander Serbin$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.page;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;

/**
 * 
 * Prepared PageComponent. Subclass having initialized:
 *  - model element;
 *  - connection;
 *  - request;
 *  - langId;
 *  - session;
 *  Overriden methods:
 *  - getNaeme - xml name of model element;
 *  - buildXml - general method for content building    
 * 	
 * @author Olexiy Strashko
 *
 */
abstract public class PreparedPageComponent extends AbstractPageComponent {
	private static Logger logger = Logger.getLogger(PreparedPageComponent.class);

	public static final String PARAMETER_VIEW = "view";

    private Element model;

    private RequestContext request;
    
    private SessionData session;

    private Session hiberSession;

    private int langId;

    private Map parameters;
    
    private String view;
    
    abstract public String getName();
    
    /**
     * build model xml, using internal model element
     *
     */
    abstract public void buildXml() throws Exception;
	
    /**
     * 
     */
	public Element getElement(
		Document doc, 
		RequestContext request, 
		Map parameters)
	{
        logger.debug("+");
        this.request = request;
        this.session = request.getSession();
        
        checkParameters();
        
        this.langId = session.getLanguage().getId().intValue();
        this.parameters = parameters; 
        this.model = Xbuilder.createEl(doc, this.getName(), null);
        this.view = this.request.getNonblankParameter(PARAMETER_VIEW);
        
        try {
        	this.setHiberSession(DBHelper.openSession());
        	this.buildXml();
        } 
        catch (Throwable e) {
            logger.error("Throwable", e);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(bos));
            this.setResponse("error", bos.toString());
        }
        finally{
        	DBHelper.close(this.getHiberSession()); 
        }
        
        Xbuilder.setAttr(this.model, "view", this.view);
        logger.debug("-");
        return model;
	}

	public void setResponse(String response) {
        logger.debug("+ -");
        Xbuilder.setAttr(model, "response", response);
    }

	public void setResponse(String response, String modelText) {
        logger.debug("+ -");
        Xbuilder.setAttr(model, "response", response);
        Xbuilder.addText(model, modelText);
    }

	public void setStatus(String status) {
        logger.debug("+ -");
        Xbuilder.setAttr(model, "status", status);
    }

	public int getLangId() {
		return langId;
	}


	public void setLangId(int langId) {
		this.langId = langId;
	}


	public Element getModel() {
		return model;
	}


	public void setModel(Element model) {
		this.model = model;
	}


	public RequestContext getRequestContext() {
		return request;
	}


	public void setRequest(RequestContext request) {
		this.request = request;
	}


	public SessionData getSession() {
		return session;
	}


	public void setSession(SessionData session) {
		this.session = session;
	}

	public Map getParameters() {
		return parameters;
	}

	public void setParameters(Map parameters) {
		this.parameters = parameters;
	}

	public Session getHiberSession() {
		return hiberSession;
	}

	public void setHiberSession(Session hiberSession) {
		this.hiberSession = hiberSession;
	}

	public Connection getConnection() {
		return this.getHiberSession().connection();
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}
	
	
	public void checkParameters() {
		// stub for back compatibility
		// should be overrided to use Condition
	}

}
