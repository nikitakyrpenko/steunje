package com.negeso.module.webshop.bo;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CashDeskOrder {

	public CashDeskOrder() {
//		new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
	}

	private String id;
	private Long amount;
	private String created;
	private String currency;
	private String description;
	private String lastTransactionAdded;
	private String merchantId;
	private String merchantOrderId;
	private String modified;
	private String projectId;
	private String returnUrl;
	private String status;
	private List<CashDeskTransaction> transactions;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
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

	public String getLastTransactionAdded() {
		return lastTransactionAdded;
	}

	public void setLastTransactionAdded(String lastTransactionAdded) {
		this.lastTransactionAdded = lastTransactionAdded;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantOrderId() {
		return merchantOrderId;
	}

	public void setMerchantOrderId(String merchantOrderId) {
		this.merchantOrderId = merchantOrderId;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<CashDeskTransaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<CashDeskTransaction> transactions) {
		this.transactions = transactions;
	}

	public void addTransaction(CashDeskTransaction transaction){
		if (this.transactions == null){
			this.transactions = new ArrayList<CashDeskTransaction>();
		}

		this.transactions.add(transaction);
	}
}
