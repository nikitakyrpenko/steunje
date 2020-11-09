package com.negeso.module.webshop.bo;

import com.negeso.framework.Env;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "cart_details", namespace = Env.NEGESO_NAMESPACE)
public class CartDetails {
	private BigDecimal orderPriceExDiscountExVat;
	private BigDecimal orderPriceIncDiscountExVat;
	private BigDecimal orderPriceExDiscountIncVat;
	private BigDecimal orderPriceIncDiscountIncVat;
	private BigDecimal deliveryPrice;
	private Integer items;

	@XmlAttribute
	public BigDecimal getOrderPriceExDiscountExVat() {
		return orderPriceExDiscountExVat;
	}

	public void setOrderPriceExDiscountExVat(BigDecimal orderPriceExDiscountExVat) {
		this.orderPriceExDiscountExVat = orderPriceExDiscountExVat;
	}

	@XmlAttribute
	public BigDecimal getOrderPriceIncDiscountExVat() {
		return orderPriceIncDiscountExVat;
	}

	public void setOrderPriceIncDiscountExVat(BigDecimal orderPriceIncDiscountExVat) {
		this.orderPriceIncDiscountExVat = orderPriceIncDiscountExVat;
	}

	@XmlAttribute
	public BigDecimal getOrderPriceExDiscountIncVat() {
		return orderPriceExDiscountIncVat;
	}

	public void setOrderPriceExDiscountIncVat(BigDecimal orderPriceExDiscountIncVat) {
		this.orderPriceExDiscountIncVat = orderPriceExDiscountIncVat;
	}

	@XmlAttribute
	public BigDecimal getOrderPriceIncDiscountIncVat() {
		return orderPriceIncDiscountIncVat;
	}

	public void setOrderPriceIncDiscountIncVat(BigDecimal orderPriceIncDiscountIncVat) {
		this.orderPriceIncDiscountIncVat = orderPriceIncDiscountIncVat;
	}

	@XmlAttribute
	public BigDecimal getDeliveryPrice() {
		return deliveryPrice;
	}

	public void setDeliveryPrice(BigDecimal deliveryPrice) {
		this.deliveryPrice = deliveryPrice;
	}

	public Integer getItems() {
		return items;
	}

	public void setItems(Integer items) {
		this.items = items;
	}

	public CartDetails delivery(BigDecimal price){
		this.deliveryPrice = price;
		return this;
	}

	public CartDetails exDisExVat(BigDecimal price){
		this.orderPriceExDiscountExVat = price;
		return this;
	}

	public CartDetails incDisExVat(BigDecimal price){
		this.orderPriceIncDiscountExVat = price;
		return this;
	}

	public CartDetails exDisIncVat(BigDecimal price){
		this.orderPriceExDiscountIncVat = price;
		return this;
	}

	public CartDetails incDisIncVat(BigDecimal price){
		this.orderPriceIncDiscountIncVat = price;
		return this;
	}

	public CartDetails items(Integer items){
		this.items = items;
		return this;
	}

}
