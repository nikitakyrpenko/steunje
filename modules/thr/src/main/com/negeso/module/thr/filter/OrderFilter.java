package com.negeso.module.thr.filter;


import java.util.Date;

import javax.servlet.http.HttpServletRequest;

public class OrderFilter {

	private static final String HTTP_THR_ORDER_SESSION_KEY = "HTTP_THR_ORDER_SESSION_KEY";

	private Date startDate;
	private Date endDate;
	private String login;
	private String barCode;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public static OrderFilter getFromSession(HttpServletRequest request) {
		OrderFilter filter = (OrderFilter) request.getSession().getAttribute(HTTP_THR_ORDER_SESSION_KEY);
		if (filter == null) {
			filter = new OrderFilter();
			saveToSession(filter, request);
		}
		return filter;
	}

	public static void saveToSession(OrderFilter orderFilter, HttpServletRequest request) {
		request.getSession().setAttribute(HTTP_THR_ORDER_SESSION_KEY, orderFilter);
	}
}
