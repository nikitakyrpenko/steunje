package com.negeso.module.webshop.bo;

import java.util.List;

public class JsonOrder {
	private String model;
	private List<ArticleInfoRequest> products;
	private String orderNumber;
	private String password;
	private String login;
	private String debtorNumber;
	private String appVersion;
	private String osVersion;
	private String width;
	private String heigth;
	private String device;

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public List<ArticleInfoRequest> getProducts() {
		return products;
	}

	public void setProducts(List<ArticleInfoRequest> products) {
		this.products = products;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getDebtorNumber() {
		return debtorNumber;
	}

	public void setDebtorNumber(String debtorNumber) {
		this.debtorNumber = debtorNumber;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeigth() {
		return heigth;
	}

	public void setHeigth(String heigth) {
		this.heigth = heigth;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}
}
