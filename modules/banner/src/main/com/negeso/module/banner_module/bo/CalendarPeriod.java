package com.negeso.module.banner_module.bo;

import java.util.Date;

/*
 * @(#)Id: CalendarPeriod.java, 26.01.2008 11:56:28, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class CalendarPeriod {
	
	public static final int WEEK_PERIOD = 1; 
	public static final int MONTH_PERIOD = 2; 
	
	private int number;
	
	private Date startDate;
	
	private Date finishDate;
	
	private int period = WEEK_PERIOD;
	
	private Long clicks;
	
	private Long views;
	
	public CalendarPeriod(int number, Date d1, Date d2, int period, Long clicks, Long views) {
		this.number = number;
		this.startDate = d1;
		this.finishDate = d2;
		this.period = period;
		this.clicks = clicks;
		this.views = views;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public Long getClicks() {
		return clicks;
	}

	public void setClicks(Long clicks) {
		this.clicks = clicks;
	}

	public Long getViews() {
		return views;
	}

	public void setViews(Long views) {
		this.views = views;
	}
}
