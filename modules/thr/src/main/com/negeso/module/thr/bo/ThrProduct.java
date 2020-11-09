package com.negeso.module.thr.bo;

import java.math.BigDecimal;

import com.negeso.framework.dao.Entity;

public class ThrProduct implements Entity {

	private static final long serialVersionUID = -4570024034165684295L;
	
	enum Show {y,n,t};

	private Long id;
	private String barCode;
	private String title;
	private String description;
	private BigDecimal price = BigDecimal.ZERO;
	private String image;
	private String show;
	private Integer orderNumber;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getShow() {
		return show;
	}

	public void setShow(String show) {
		this.show = show;
	}
	
	public boolean isVisible() {
		return Show.y.toString().equals(show);
	}
	
	public boolean isTest() {
		return Show.t.toString().equals(show);
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
}
