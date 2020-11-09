package com.negeso.module.baten.entity;

import com.negeso.framework.dao.Entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class ArticleInfo implements Entity{
	private boolean available;
	private Long id;
	private String stock;
	private String image;
	private String description;
	private BigDecimal price;
	private BigDecimal purchaseprice;
	private Integer mincount;
	private Integer increment;
	private boolean lockedminount;

	public ArticleInfo() {
	}

	public ArticleInfo(boolean available, Long id, String stock, String image, String description, BigDecimal price, BigDecimal purchaseprice, Integer mincount, Integer increment, boolean lockedminount) {
		this.available = available;
		this.id = id;
		this.stock = stock;
		this.image = image;
		this.description = description;
		this.price = price;
		this.purchaseprice = purchaseprice;
		this.mincount = mincount;
		this.increment = increment;
		this.lockedminount = lockedminount;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
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

	public BigDecimal getPurchaseprice() {
		return purchaseprice;
	}

	public void setPurchaseprice(BigDecimal purchaseprice) {
		this.purchaseprice = purchaseprice;
	}

	public Integer getMincount() {
		return mincount;
	}

	public void setMincount(Integer mincount) {
		this.mincount = mincount;
	}

	public Integer getIncrement() {
		return increment;
	}

	public void setIncrement(Integer increment) {
		this.increment = increment;
	}

	public boolean isLockedminount() {
		return lockedminount;
	}

	public void setLockedminount(boolean lockedminount) {
		this.lockedminount = lockedminount;
	}

	public boolean isValid(){
		return id != null && price != null;
	}
}
