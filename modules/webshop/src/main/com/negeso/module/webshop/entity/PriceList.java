package com.negeso.module.webshop.entity;

import java.io.Serializable;

public class PriceList implements Serializable {
	private String group;

	public PriceList(){}
	public PriceList(String group) {
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

		PriceList priceList = (PriceList) o;

		return group.equals(priceList.group);
	}

	@Override
	public int hashCode() {
		return group.hashCode();
	}
}
