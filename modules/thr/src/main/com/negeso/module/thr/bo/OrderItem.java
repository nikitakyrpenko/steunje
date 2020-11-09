package com.negeso.module.thr.bo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.gson.JsonObject;
import com.negeso.framework.Env;
import com.negeso.framework.dao.Entity;
import com.negeso.module.thr.utils.CurrencyFormatter;

import static com.negeso.module.thr.bo.ThrJsonConsts.*;
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "product",namespace = Env.NEGESO_NAMESPACE)
public class OrderItem implements Entity {

	private static final long serialVersionUID = 5080761948089153851L;

	private Long id;
	@XmlAttribute
	private String barCode;
	@XmlAttribute
	private Integer count;
	@XmlElement(name="info",namespace = Env.NEGESO_NAMESPACE)
	private ProductInfo productInfo;
	private ThrOrder order;
	
	public static OrderItem initWithJson(JsonObject jsonObject) {
		OrderItem product = new OrderItem();
		product.setBarCode(		jsonObject.get(THR_JSON_PRODUCT_BARCODE).getAsString());
		product.setCount(		jsonObject.get(THR_JSON_PRODUCT_COUNT).getAsInt());
		if ( !(jsonObject.get(THR_JSON_PRODUCT_INFO).isJsonNull()) ) {
			product.setProductInfo(	new ProductInfo(jsonObject.get(THR_JSON_PRODUCT_INFO).getAsJsonObject()));
		}
		return product;
	}

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

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public ProductInfo getProductInfo() {
		return productInfo;
	}

	public void setProductInfo(ProductInfo productInfo) {
		this.productInfo = productInfo;
	}

	public ThrOrder getOrder() {
		return order;
	}

	public void setOrder(ThrOrder order) {
		this.order = order;
	}
	
	@XmlAttribute
	public boolean isAvailable() {
		return productInfo != null && productInfo.getAvailable();
	}
	
	public Double getTotalPurchasePrice() {
		if (productInfo != null && productInfo.getPurchasePrice() != null) {
			return productInfo.getPurchasePrice() * count;
		}
		return 0d;
	}
	
	public Double getTotalPrice() {
		if (productInfo != null && productInfo.getPrice() != null) {
			return productInfo.getPrice() * count;
		}
		return 0d;
	}
	
	@XmlAttribute
	public String getTotalPriceFormatted() {
		return CurrencyFormatter.format(getTotalPrice());
	}
	
	@XmlAttribute
	public String getTotalPurchasePriceFormatted() {
		return CurrencyFormatter.format(getTotalPurchasePrice());
	}
}
