/*
 * @(#)$Id: $
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.imp.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.log.LogEvent;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class EventLogger {

	private String batchKey;
	private String userName;
	private List<LogEvent> events = new LinkedList<LogEvent>();
	private boolean errors = false;

	public EventLogger(String batchKey, String userName) {
		this.batchKey = batchKey;
		this.userName = userName;
	}

	public void addEvent(String event, String object, String description,
			String result, String moduleKey, String service) {
		LogEvent logEvent = new LogEvent();
		logEvent.setBatchKey(this.batchKey);
		logEvent.setAuthor(this.userName);
		logEvent.setDescription(description);
		logEvent.setEvent(event);
		logEvent.setModuleKey(moduleKey);
		logEvent.setObject(object);
		logEvent.setResult(result);
		logEvent.setService(service);
		logEvent.setDate(new Date());
		if (logEvent.isErrorResult()) {
			this.setErrors(true);
		}
		events.add(logEvent);
	}

	public List<LogEvent> getEvents() {
		if (events == null) {
			events = new ArrayList<LogEvent>();
		}
		return events;
	}

	public boolean hasErrors() {
		return errors;
	}

	public void setErrors(boolean errors) {
		this.errors = errors;
	}

	public String getBatchKey() {
		return batchKey;
	}
	
	public Element getElement(Node parent) {
		Element eventsEl = Xbuilder.addEl(parent, "events", null);
		for (LogEvent event: getEvents()) {
			Element eventEl = event.getElement(eventsEl);
			eventsEl.appendChild(eventEl);
		}
		return eventsEl;
	}


}
