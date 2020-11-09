package com.negeso.module.webshop.entity;

import com.negeso.framework.exception.ValidationException;
import com.negeso.framework.io.PrimaryKey;
import com.negeso.framework.io.xls.XlsColumnNumber;
import com.negeso.framework.util.Validator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class PriceListProductGroup2Customer implements Serializable, PrimaryKey<String>, Validator {

	@XlsColumnNumber    private ProductGroup productGroup;
	@XlsColumnNumber(1) private Customer customer;
	@XlsColumnNumber(2) private Timestamp availableFrom;
	@XlsColumnNumber(3) private Integer sinceCount;
	@XlsColumnNumber(4) private BigDecimal discount;
	@XlsColumnNumber(5) private String explanation;
	@XlsColumnNumber(6) private Boolean actions;

	@Override
	public String[] getJoinedPrimaryKey() {
		return new String[]{productGroup.getGroup()};
	}

	@Override
	public Object[] getJoined() {
		return new Object[]{productGroup};
	}

	@Override
	public void setJoinedObject(Object key, int foreign) {
		if (foreign == 0){
			setProductGroup((ProductGroup) key);
		}
	}

	public ProductGroup getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;
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
	public void validate() throws ValidationException {
		if (productGroup == null || customer==null || discount == null){
			throw new ValidationException("NULL");
		}
	}
}
