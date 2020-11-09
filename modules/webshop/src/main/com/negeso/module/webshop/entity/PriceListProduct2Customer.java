package com.negeso.module.webshop.entity;

import com.negeso.framework.exception.ValidationException;
import com.negeso.framework.io.PrimaryKey;
import com.negeso.framework.io.xls.XlsColumnNumber;
import com.negeso.framework.util.Validator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class PriceListProduct2Customer implements Serializable, PrimaryKey<String>, Validator{

	@XlsColumnNumber    private String productId;
	@XlsColumnNumber(1) private Customer customer;
	@XlsColumnNumber(2) private Timestamp availableFrom;
	/*@XlsColumnNumber(3)*/ private Timestamp availableTo;
	@XlsColumnNumber(3) private Integer sinceCount;
	@XlsColumnNumber(4) private BigDecimal discount;
	@XlsColumnNumber(5) private String explanation;
	@XlsColumnNumber(6) private Boolean actions;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Timestamp getAvailableFrom() {
		return availableFrom;
	}

	public void setAvailableFrom(Timestamp availableFrom) {
		this.availableFrom = availableFrom;
	}

	public Timestamp getAvailableTo() {
		return availableTo;
	}

	public void setAvailableTo(Timestamp availableTo) {
		this.availableTo = availableTo;
	}

	public Integer getSinceCount() {
		return sinceCount;
	}

	public void setSinceCount(Integer sinceCount) {
		this.sinceCount = sinceCount;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public Boolean getActions() {
		return actions;
	}

	public void setActions(Boolean actions) {
		this.actions = actions;
	}

	@Override
	public String[] getJoinedPrimaryKey() {
		return new String[0];
	}

	@Override
	public Object[] getJoined() {
		return new Object[0];
	}

	@Override
	public void setJoinedObject(Object key, int foreign) {

	}

	@Override
	public void validate() throws ValidationException {
		if (productId == null || discount == null || customer==null)
			throw new ValidationException("NULL");
	}
}
