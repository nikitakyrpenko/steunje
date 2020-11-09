package com.negeso.module.webshop.entity;

import com.negeso.framework.io.xls.XlsColumnNumber;

import java.io.Serializable;
import java.util.List;

public class MatrixCategory implements Serializable{
	@XlsColumnNumber(19) private String title;
	private List<Product> products;

	public MatrixCategory(){}

	public MatrixCategory(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MatrixCategory that = (MatrixCategory) o;

		return title.equals(that.title);
	}

	@Override
	public int hashCode() {
		return title.hashCode();
	}
}
