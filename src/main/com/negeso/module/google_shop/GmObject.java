package com.negeso.module.google_shop;

import java.math.BigDecimal;

public interface GmObject {

	public void setGoogleMerchantEnabled(boolean googleMerchantEnabled);

	public void setGoogleMerchantBrand(String googleMerchantBrand);

	public void setGoogleMerchantCondition(String googleMerchantCondition);

	public void setGoogleMerchantAvailability(String googleMerchantAvailability);

	public void setGoogleMerchantProductCategory(
			String googleMerchantProductCategory);

	public void setGoogleMerchantProductType(String googleMerchantProductType);

	public void setGoogleMerchantGtin(String googleMerchantGtin);

	public void setGoogleMerchantTitle(String googleMerchantTitle);

	public void setGoogleMerchantDescription(String googleMerchantDescription);

	public void setGoogleMerchantPrice(BigDecimal googleMerchantPrice);

	public boolean isGoogleMerchantEnabled();

	public String getGoogleMerchantBrand();

	public String getGoogleMerchantCondition();

	public String getGoogleMerchantAvailability();

	public String getGoogleMerchantProductCategory();

	public String getGoogleMerchantProductType();

	public String getGoogleMerchantGtin();

	public String getGoogleMerchantTitle();

	public String getGoogleMerchantDescription();

	public BigDecimal getGoogleMerchantPrice();
}
