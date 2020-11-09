package com.negeso.module.webshop.bo;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class CashDeskTransaction {
	private Long amount;
	private String balance;
	private String created;
	private String creditDebit;
	private String currency;
	private String description;
	private String expirationPeriod;
	private String id;
	private String merchantId;
	private String modified;
	private String orderId;
	private String paymentMethod;
	@SerializedName("payment_method_details")
	private CashDeskPaymentMethodDetails paymentMethodDetails;
	private String paymentUrl;
	private String productType;
	private String projectId;
	private String status;

	public CashDeskTransaction(){}

	public CashDeskTransaction(String paymentMethod, String issuerId) {
		this.paymentMethod = paymentMethod;
		this.paymentMethodDetails = new CashDeskPaymentMethodDetails(issuerId);
	}

	public CashDeskTransaction(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getCreditDebit() {
		return creditDebit;
	}

	public void setCreditDebit(String creditDebit) {
		this.creditDebit = creditDebit;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExpirationPeriod() {
		return expirationPeriod;
	}

	public void setExpirationPeriod(String expirationPeriod) {
		this.expirationPeriod = expirationPeriod;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPaymentUrl() {
		return paymentUrl;
	}

	public void setPaymentUrl(String paymentUrl) {
		this.paymentUrl = paymentUrl;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public CashDeskPaymentMethodDetails getPaymentMethodDetails() {
		return paymentMethodDetails;
	}

	public void setPaymentMethodDetails(CashDeskPaymentMethodDetails paymentMethodDetails) {
		this.paymentMethodDetails = paymentMethodDetails;
	}
}
