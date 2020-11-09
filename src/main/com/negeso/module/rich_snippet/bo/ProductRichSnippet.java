package com.negeso.module.rich_snippet.bo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.negeso.framework.Env;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "rs-product", namespace = Env.NEGESO_NAMESPACE)
public class ProductRichSnippet extends RichSnippet {

	private static final long serialVersionUID = 3834398732529240940L;

	@XmlAttribute
	private String brand;
	@XmlAttribute
	private String manufacturer;
	@XmlAttribute
	private String model;
	@XmlAttribute
	private Integer averageRating;
	@XmlAttribute
	private Integer reviews;
	@XmlAttribute
	private String productId;
	@XmlAttribute
	private String price;
	@XmlAttribute
	private String condition;
	@XmlAttribute
	private String availability;

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Integer getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(Integer averageRating) {
		this.averageRating = averageRating;
	}

	public Integer getReviews() {
		return reviews;
	}

	public void setReviews(Integer reviews) {
		this.reviews = reviews;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}
}
