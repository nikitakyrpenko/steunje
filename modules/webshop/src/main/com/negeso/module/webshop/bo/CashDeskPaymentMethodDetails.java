package com.negeso.module.webshop.bo;

public class CashDeskPaymentMethodDetails {
	private String issuerId;

	public CashDeskPaymentMethodDetails(){}

	public CashDeskPaymentMethodDetails(String issuerId) {
		this.issuerId = issuerId;
	}

	public String getIssuerId() {
		return issuerId;
	}

	public void setIssuerId(String issuerId) {
		this.issuerId = issuerId;
	}
}
