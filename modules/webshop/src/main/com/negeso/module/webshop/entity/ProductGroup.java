package com.negeso.module.webshop.entity;

import java.io.Serializable;

public class ProductGroup implements Serializable {
	private String group;

	public ProductGroup() {}
	public ProductGroup(String group) {
		this.group = group;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ProductGroup that = (ProductGroup) o;

		return group.equals(that.group);
	}

	@Override
	public int hashCode() {
		return group.hashCode();
	}
}
