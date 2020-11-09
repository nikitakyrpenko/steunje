/*
* @(#)$Id: Subscription.java,v 1.0, 2007-01-31 12:01:42Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.module.lite_event.domain;

import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 *
 * TODO
 * 
 * @version                $Revision: 1$
 * @author                 Svetlana Bondar
 * 
 */
public class Subscription {
	
	Long 	id;
	Date 	date;
	Long 	eventId;
	Long 	peopleAmount;
	Long 	userId;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Long getEventId() {
		return eventId;
	}
	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getPeopleAmount() {
		return peopleAmount;
	}
	public void setPeopleAmount(Long peopleAmount) {
		this.peopleAmount = peopleAmount;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String toString () {
		return ReflectionToStringBuilder.reflectionToString(this);
	}

}
