package com.negeso.module.webshop.entity;

import com.google.gson.annotations.Expose;
import com.negeso.framework.Env;
import com.negeso.framework.jaxb.GermanPriceAdapter;
import com.negeso.framework.jaxb.PercentAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "item", namespace = Env.NEGESO_NAMESPACE)
public class OrderItem implements ProductItem{
	private Integer id;
	@Expose private Integer quantity;
	@Expose private Product product;
	private BigDecimal discount;
	private BigDecimal price;
	private Order order;

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public OrderItem() {
	}

	public OrderItem(Product product) {
		this.product = product;
	}

	public OrderItem(Integer quantity, Product product) {
		this.quantity = quantity;
		this.product = product;
	}

	@XmlAttribute
	public Integer getQuantity() {
		return this.quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@XmlElement
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	@XmlAttribute
	@XmlJavaTypeAdapter(PercentAdapter.class)
	public BigDecimal getDiscount() {
		return discount;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@XmlAttribute
	@XmlJavaTypeAdapter(GermanPriceAdapter.class)
	public BigDecimal getPrice() {
		return price;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OrderItem orderItem = (OrderItem) o;

		if (product != null ? !product.equals(orderItem.product) : orderItem.product != null) return false;
		if (discount != null ? !discount.equals(orderItem.discount) : orderItem.discount != null) return false;
		return price != null ? price.equals(orderItem.price) : orderItem.price == null;
	}

	@Override
	public int hashCode() {
		int result = product != null ? product.hashCode() : 0;
		result = 31 * result + (discount != null ? discount.hashCode() : 0);
		result = 31 * result + (price != null ? price.hashCode() : 0);
		return result;
	}
}
