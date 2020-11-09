package com.negeso.module.google_shop.bo;

import com.negeso.framework.dao.Entity;

public class GoogleMerchant implements Entity {

	private static final long serialVersionUID = 2519225390957235448L;

	private Long id;
	private String hostName;
	private String langCode;
	private String countryCode;
	private boolean enabled;
	private String currencyCode;
	private String merchantId;
	private String adwordsId;
	private String notes;
	private String defaultBrand;
	private String defaultCondition;
	private String defaultAvailability;
	private String defaultGoogleProductCategory;
	private String defaultGoogleProductType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getAdwordsId() {
		return adwordsId;
	}

	public void setAdwordsId(String adwordsId) {
		this.adwordsId = adwordsId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getDefaultBrand() {
		return defaultBrand;
	}

	public void setDefaultBrand(String defaultBrand) {
		this.defaultBrand = defaultBrand;
	}

	public String getDefaultCondition() {
		return defaultCondition;
	}

	public void setDefaultCondition(String defaultCondition) {
		this.defaultCondition = defaultCondition;
	}

	public String getDefaultAvailability() {
		return defaultAvailability;
	}

	public void setDefaultAvailability(String defaultAvailability) {
		this.defaultAvailability = defaultAvailability;
	}

	public String getDefaultGoogleProductCategory() {
		return defaultGoogleProductCategory;
	}

	public void setDefaultGoogleProductCategory(
			String defaultGoogleProductCategory) {
		this.defaultGoogleProductCategory = defaultGoogleProductCategory;
	}

	public String getDefaultGoogleProductType() {
		return defaultGoogleProductType;
	}

	public void setDefaultGoogleProductType(String defaultGoogleProductType) {
		this.defaultGoogleProductType = defaultGoogleProductType;
	}
}
