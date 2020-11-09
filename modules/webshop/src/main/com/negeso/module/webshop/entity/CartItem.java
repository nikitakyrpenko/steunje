package com.negeso.module.webshop.entity;

import com.google.gson.annotations.Expose;
import com.negeso.framework.Env;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "item", namespace = Env.NEGESO_NAMESPACE)
public class CartItem implements ProductItem{

	private Integer id;
	@Expose private Product product;
	@Expose private Integer quantity;
	private String cartOwner;

	public CartItem() {
	}

	public CartItem(Product product, String cartOwner) {
		this(product, 0, cartOwner);
	}

	public CartItem(Product product, Integer quantity, String cartOwner) {
		this.product = product;
		this.quantity = quantity;
		this.cartOwner = cartOwner;
	}

	public String getCartOwner() {
		return cartOwner;
	}

	public void setCartOwner(String cartOwner) {
		this.cartOwner = cartOwner;
	}

	@XmlAttribute
	public Integer getQuantity() {
		return quantity;
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CartItem cartItem = (CartItem) o;

		if (!product.equals(cartItem.product)) return false;
		return cartOwner.equals(cartItem.cartOwner);
	}

	@Override
	public int hashCode() {
		int result = product.hashCode();
		result = 31 * result + cartOwner.hashCode();
		return result;
	}
}
