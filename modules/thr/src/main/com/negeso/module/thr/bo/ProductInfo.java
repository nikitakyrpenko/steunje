package com.negeso.module.thr.bo;

import static com.negeso.module.thr.bo.ThrJsonConsts.THR_JSON_INFO_ALTERNATIVE_ID;
import static com.negeso.module.thr.bo.ThrJsonConsts.THR_JSON_INFO_AVAILABLE;
import static com.negeso.module.thr.bo.ThrJsonConsts.THR_JSON_INFO_DESCRIPTION;
import static com.negeso.module.thr.bo.ThrJsonConsts.THR_JSON_INFO_IMAGE;
import static com.negeso.module.thr.bo.ThrJsonConsts.THR_JSON_INFO_INCREMENT;
import static com.negeso.module.thr.bo.ThrJsonConsts.THR_JSON_INFO_IS_LOCKED_MIN_COUNT;
import static com.negeso.module.thr.bo.ThrJsonConsts.THR_JSON_INFO_MINCOUNT;
import static com.negeso.module.thr.bo.ThrJsonConsts.THR_JSON_INFO_PRICE;
import static com.negeso.module.thr.bo.ThrJsonConsts.THR_JSON_INFO_PURCHASE_PRICE;
import static com.negeso.module.thr.bo.ThrJsonConsts.THR_JSON_INFO_STOCK;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.google.gson.JsonObject;
import com.negeso.framework.Env;
import com.negeso.module.thr.utils.CurrencyFormatter;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name="info",namespace = Env.NEGESO_NAMESPACE)
public class ProductInfo {
	@XmlAttribute
	private Boolean available = Boolean.FALSE;
	@XmlAttribute
	private String stock;
	private String image;
	@XmlAttribute
	private String description;
	@XmlAttribute
	private Double price;
	@XmlAttribute
	private Double purchasePrice;
	@XmlAttribute
	private Integer mincount;
	private Integer increment;
	private Boolean lockedmincount = Boolean.FALSE;
	private String alternativeid;
	
	public ProductInfo(){}
	
	public ProductInfo(JsonObject jsonObject){
		try {
			if (jsonObject.has(THR_JSON_INFO_AVAILABLE)) {
				this.available = jsonObject.get(THR_JSON_INFO_AVAILABLE).getAsBoolean();
			}
			if (this.available) {
				if (jsonObject.has(THR_JSON_INFO_STOCK)) {
					this.stock = jsonObject.get(THR_JSON_INFO_STOCK).getAsString();
				}
				if (jsonObject.has(THR_JSON_INFO_IMAGE)) {
					this.image = jsonObject.get(THR_JSON_INFO_IMAGE).getAsString();
				}
				if (jsonObject.has(THR_JSON_INFO_DESCRIPTION)) {
					this.description = jsonObject.get(THR_JSON_INFO_DESCRIPTION).getAsString();
				}
				if (jsonObject.has(THR_JSON_INFO_PRICE)) {
					this.price = jsonObject.get(THR_JSON_INFO_PRICE).getAsDouble();
				}
				if (jsonObject.has(THR_JSON_INFO_PURCHASE_PRICE)) {
					this.purchasePrice = jsonObject.get(THR_JSON_INFO_PURCHASE_PRICE).getAsDouble();
				}
				if (jsonObject.has(THR_JSON_INFO_MINCOUNT)) {
					this.mincount = jsonObject.get(THR_JSON_INFO_MINCOUNT).getAsInt();
				}
				if (jsonObject.has(THR_JSON_INFO_INCREMENT)) {
					this.increment = jsonObject.get(THR_JSON_INFO_INCREMENT).getAsInt();
				}
				if (jsonObject.has(THR_JSON_INFO_IS_LOCKED_MIN_COUNT)) {
					this.lockedmincount = jsonObject.get(THR_JSON_INFO_IS_LOCKED_MIN_COUNT).getAsBoolean();
				}
			}
			if (jsonObject.has(THR_JSON_INFO_ALTERNATIVE_ID) && !jsonObject.get(THR_JSON_INFO_ALTERNATIVE_ID).isJsonNull()) {
				this.alternativeid = jsonObject.get(THR_JSON_INFO_ALTERNATIVE_ID).getAsString();			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}
	
	@XmlAttribute
	public String getPriceFormatted() {
		return CurrencyFormatter.format(price == null ? 0d : price);
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getPurchasePrice() {
		return purchasePrice;
	}
	
	@XmlAttribute
	public String getPurchasePriceFormatted() {
		return CurrencyFormatter.format(purchasePrice == null ? 0d : purchasePrice);
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public Integer getMincount() {
		return mincount;
	}

	public void setMincount(Integer mincount) {
		this.mincount = mincount;
	}

	public Integer getIncrement() {
		return increment;
	}

	public void setIncrement(Integer increment) {
		this.increment = increment;
	}

	public Boolean getLockedmincount() {
		return lockedmincount;
	}

	public void setLockedmincount(Boolean lockedmincount) {
		this.lockedmincount = lockedmincount;
	}

	public String getAlternativeid() {
		return alternativeid;
	}

	public void setAlternativeid(String alternativeid) {
		this.alternativeid = alternativeid;
	}
}
