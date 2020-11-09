package com.negeso.module.webshop.entity;

import com.negeso.framework.exception.ValidationException;
import com.negeso.framework.io.PrimaryKey;
import com.negeso.framework.io.xls.XlsColumnNumber;
import com.negeso.framework.util.Validator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class PriceListProduct implements Serializable, PrimaryKey<String>, Validator{
	private Integer id;
	@XlsColumnNumber(0) private PriceList listGroup;
	@XlsColumnNumber(1) private Product productId;
	@XlsColumnNumber(2) private String productTitle;
	@XlsColumnNumber(3) private Timestamp availableFrom;
	@XlsColumnNumber(4) private Integer since;
	@XlsColumnNumber(5) private BigDecimal discount;
	@XlsColumnNumber(6) private String explanation;
	@XlsColumnNumber(7) private Boolean actions;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public PriceList getListGroup() {
		return listGroup;
	}

	public void setListGroup(PriceList listGroup) {
		this.listGroup = listGroup;
	}

	public Product getProductId() {
		return productId;
	}

	public void setProductId(Product productId) {
		this.productId = productId;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public Timestamp getAvailableFrom() {
		return availableFrom;
	}

	public void setAvailableFrom(Timestamp availableFrom) {
		this.availableFrom = availableFrom;
	}

	public Integer getSince() {
		return since;
	}

	public void setSince(Integer since) {
		this.since = since;
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

		return new String[]{this.listGroup.getGroup()};
	}

	@Override
	public Object[] getJoined() {
		return new PriceList[]{this.listGroup};
	}

	@Override
	public void setJoinedObject(Object key, int foreign) {
		this.listGroup = (PriceList) key;
	}

	@Override
	public void validate() throws ValidationException {
		if (listGroup == null)
			throw new ValidationException("Product group couldn't be null");
		else if (productId == null || discount == null)
			throw new ValidationException("NULL");
	}
}