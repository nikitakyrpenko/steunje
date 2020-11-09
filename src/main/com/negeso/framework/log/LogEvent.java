/*
 * @(#)$Id: LogEvent.java,v 1.0, 2006-11-27 07:48:33Z, Olexiy Strashko$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.log;

import java.util.Date;

import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.generators.Xbuilder;

/**
 * 
 * @TODO
 * 
 * @author		Olexiy V. Strashko
 * @version		$Revision: 1$
 *
 */
public class LogEvent {
	private String event;
	private String object;
	private String description;
	private String result;
	private String moduleKey;
	private String service;
	private String batchKey;
	private String author;
	private Date date;
	
	public boolean isErrorResult() {
        return SystemLogConstants.RESULT_ERROR.equals(this.getResult());
    }

	public String getBatchKey() {
		return batchKey;
	}
	public void setBatchKey(String batchKey) {
		this.batchKey = batchKey;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getModuleKey() {
		return moduleKey;
	}
	public void setModuleKey(String moduleKey) {
		this.moduleKey = moduleKey;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getDate() {
		return date;
	}

	public String getFormattedDate() {
		return Env.formatRoundDate(date);
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

	public Element getElement(Element eventsEl) {
		Element eventElement = Xbuilder.addEl(eventsEl, "event", null);
		Xbuilder.setAttr(eventElement, "date", getFormattedDate());
		Xbuilder.setAttr(eventElement, "author", getAuthor());
		Xbuilder.setAttr(eventElement, "event", getEvent());
		Xbuilder.setAttr(eventElement, "description", getDescription());
		Xbuilder.setAttr(eventElement, "result", getResult());
		return eventElement;
	}

	
}
