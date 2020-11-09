package com.negeso.module.webshop.entity;

import com.negeso.framework.exception.ValidationException;
import com.negeso.framework.io.PrimaryKey;
import com.negeso.framework.io.xls.XlsColumnNumber;
import com.negeso.framework.util.Validator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class PriceListStandard implements Serializable, PrimaryKey<String>, Validator {

	@XlsColumnNumber(0) private PriceList priceGroup;
	@XlsColumnNumber(1) private ProductGroup productGroup;
	@XlsColumnNumber(2) private String definition;
	@XlsColumnNumber(3) private Timestamp from;
	@XlsColumnNumber(4) private Integer sinceCount;
	@XlsColumnNumber(5) private BigDecimal discount;
	@XlsColumnNumber(6) private String explanation;
	@XlsColumnNumber(7) private Boolean actions;

	@Override
	public String[] getJoinedPrimaryKey() {
		return new String[]{priceGroup.getGroup(), productGroup.getGroup()};
	}

	@Override
	public Object[] getJoined() {
		return new Object[]{priceGroup, productGroup};
	}

	@Override
	public void setJoinedObject(Object key, int foreign) {
		if (foreign == 0)
			setPriceGroup((PriceList) key);
		else if (foreign == 1)
			setProductGroup((ProductGroup) key);
	}

	public PriceList getPriceGroup() {
		return priceGroup;
	}

	public void setPriceGroup(PriceList priceGroup) {
		this.priceGroup = priceGroup;
	}

	public ProductGroup getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public Timestamp getFrom() {
		return from;
	}

	public void setFrom(Timestamp from) {
		this.from = from;
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
		if (priceGroup == null || productGroup == null || discount == null)
			throw new ValidationException("NULL");
	}
}
