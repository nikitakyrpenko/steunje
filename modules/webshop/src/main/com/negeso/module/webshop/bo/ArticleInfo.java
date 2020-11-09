package com.negeso.module.webshop.bo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.negeso.framework.Env;
import com.negeso.module.webshop.entity.Product;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleInfo {
	private String id;
	private Integer increment;
	private Boolean valid;
	private String stock;
	private String price;
	private Boolean lockedminount;
	private String description;
	private String purchaseprice;
	private String image;
	private Integer mincount;
	private Boolean	available;

	public ArticleInfo() {
	}

	public ArticleInfo(Product product) {
		int i = product.getMultipleOf() == null || product.getMultipleOf() == 0 ? 1 : product.getMultipleOf();
		this.id = product.getEan();
		this.increment = i;
		this.valid = true;
		this.stock = product.getStockColor();
		this.price = product.getPriceExcludeVat().setScale(2, BigDecimal.ROUND_UP).toString();
		this.lockedminount = false;
		this.description = product.getTitle();
		this.purchaseprice = product.getRetailPriceExcludeVat() == null ? BigDecimal.ZERO.toString() : product.getRetailPriceExcludeVat().setScale(2, BigDecimal.ROUND_UP).toString();
		this.image = Env.getCommonHostName() + "media/productsImages/" + product.getId() + ".jpg";
		this.mincount = i;
		this.available = product.getVisible();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getIncrement() {
		return increment;
	}

	public void setIncrement(Integer increment) {
		this.increment = increment;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public Boolean getLockedminount() {
		return lockedminount;
	}

	public void setLockedminount(Boolean lockedminount) {
		this.lockedminount = lockedminount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPurchaseprice() {
		return purchaseprice;
	}

	public void setPurchaseprice(String purchaseprice) {
		this.purchaseprice = purchaseprice;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Integer getMincount() {
		return mincount;
	}

	public void setMincount(Integer mincount) {
		this.mincount = mincount;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}
}
