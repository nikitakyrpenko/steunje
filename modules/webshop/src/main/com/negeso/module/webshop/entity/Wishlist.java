package com.negeso.module.webshop.entity;

import java.io.Serializable;
import java.util.List;

public class Wishlist implements Serializable {

	private Integer id;
	private Customer customer;
	private Product product;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}
