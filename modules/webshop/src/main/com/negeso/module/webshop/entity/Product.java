package com.negeso.module.webshop.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.negeso.framework.Env;
import com.negeso.framework.exception.ValidationException;
import com.negeso.framework.io.Behavior;
import com.negeso.framework.io.HibernateDependency;
import com.negeso.framework.io.PrimaryKey;
import com.negeso.framework.io.xls.XlsColumnNumber;
import com.negeso.framework.io.xls.XlsObject;
import com.negeso.framework.jaxb.GermanPriceAdapter;
import com.negeso.framework.util.Validator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "product", namespace = Env.NEGESO_NAMESPACE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product implements Serializable, Validator {
	@Expose @XlsColumnNumber private String id;
	@XlsColumnNumber(1) private String productNumber;
	@Expose @XlsColumnNumber(2) private String title;
	@XlsColumnNumber(3) private String ean;
	@XlsColumnNumber(4) private ProductGroup articleGroup;
	@XlsColumnNumber(5) private BigDecimal priceIncludeVat;
	@XlsColumnNumber(6) private BigDecimal priceExcludeVat;
	@XlsColumnNumber(7) private Integer stock;
	@XlsColumnNumber(8) private Integer stockMin;
	@XlsColumnNumber(9) private Boolean visible;
	@XlsColumnNumber(10) private BigDecimal retailPriceExcludeVat;
	@XlsColumnNumber(11) private String content;
	@XlsColumnNumber(12) private String color;

	@XlsColumnNumber @XlsObject(values = {15,14,13})
	@HibernateDependency(behavior = Behavior.MERGE, exclude = {"visible", "visibleTo", "parentCategory", "orderNumber"}, id = "id")
	private ProductCategory category;

	@XlsColumnNumber(16) private String brand;
	@XlsColumnNumber(17) private Boolean keepStock;
	@XlsColumnNumber(18) private Integer multipleOf;
	@XlsColumnNumber(19) private MatrixCategory matrixCategory;
	@XlsColumnNumber(20) private String matrixValue;
	private List<PriceListProduct> priceListProduct;
	private Boolean sale = false;
	@XlsColumnNumber(21) private String description;
	@XlsColumnNumber(22) private Integer orderNumber;
	@XlsColumnNumber(23) private String packingUnit;
	@XlsColumnNumber(24) private String keywords;

	public Product(){}

	public Product(String id) {
		this.id = id;
	}

	@XmlAttribute
	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	@XmlAttribute
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute
	public String getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}

	@XmlAttribute
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@XmlAttribute
	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public ProductGroup getArticleGroup() {
		return articleGroup;
	}

	public void setArticleGroup(ProductGroup articleGroup) {
		this.articleGroup = articleGroup;
	}

	@XmlAttribute
	@XmlJavaTypeAdapter(GermanPriceAdapter.class)
	public BigDecimal getPriceIncludeVat() {
		return priceIncludeVat;
	}

	public void setPriceIncludeVat(BigDecimal priceIncludeVat) {
		this.priceIncludeVat = priceIncludeVat;
	}

	@XmlAttribute
	@XmlJavaTypeAdapter(GermanPriceAdapter.class)
	public BigDecimal getPriceExcludeVat() {
		return priceExcludeVat;
	}

	public void setPriceExcludeVat(BigDecimal priceExcludeVat) {
		this.priceExcludeVat = priceExcludeVat;
	}

	@XmlAttribute
	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	@XmlAttribute
	public Integer getStockMin() {
		return stockMin;
	}

	public void setStockMin(Integer stockMin) {
		this.stockMin = stockMin;
	}

	@XmlAttribute
	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	@XmlAttribute
	@XmlJavaTypeAdapter(GermanPriceAdapter.class)
	public BigDecimal getRetailPriceExcludeVat() {
		return retailPriceExcludeVat;
	}

	public void setRetailPriceExcludeVat(BigDecimal retailPriceExcludeVat) {
		this.retailPriceExcludeVat = retailPriceExcludeVat;
	}

	@XmlAttribute
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@XmlAttribute
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	@XmlAttribute
	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	@XmlAttribute
	public Boolean getKeepStock() {
		return keepStock;
	}

	public void setKeepStock(Boolean keepStock) {
		this.keepStock = keepStock;
	}

	@XmlAttribute
	public Integer getMultipleOf() {
		return multipleOf;
	}

	public void setMultipleOf(Integer multipleOf) {
		this.multipleOf = multipleOf;
	}

	public MatrixCategory getMatrixCategory() {
		return matrixCategory;
	}

	public void setMatrixCategory(MatrixCategory matrixCategory) {
		this.matrixCategory = matrixCategory;
	}

	@XmlAttribute
	public String getMatrixValue() {
		return matrixValue;
	}

	public void setMatrixValue(String matrixValue) {
		this.matrixValue = matrixValue;
	}

	public List<PriceListProduct> getPriceListProduct() {
		return priceListProduct;
	}

	public void setPriceListProduct(List<PriceListProduct> priceListProduct) {
		this.priceListProduct = priceListProduct;
	}

	@XmlAttribute
	@JsonIgnore
	public String getStockColor(){
		return !this.keepStock ? "B" : this.stock == 0 ? "R" : this.stock < this.stockMin ? "O" : "G";
	}

	public Boolean getSale() {
		return sale;
	}

	public void setSale(Boolean sale) {
		this.sale = sale;
	}

	@XmlAttribute
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isProductVisibleFor(Customer customer){
		return this.getVisible() && this.category.isCategoryVisibleFor(customer);
	}

	public boolean isProductVisibleFor(String login){
		return this.getCategory().isCategoryVisibleFor(login);
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	@XmlAttribute
	public String getPackingUnit() {
		return packingUnit;
	}

	public void setPackingUnit(String packingUnit) {
		this.packingUnit = packingUnit;
	}

	public void validate() throws ValidationException {
		if (this.id == null)
			throw new ValidationException("Order code is empty");
		else if (this.productNumber == null)
			throw new ValidationException("Product number is missing in product with code " + this.id);
		else if (this.priceIncludeVat == null || this. priceExcludeVat == null)
			throw new ValidationException("Price is missing in product with code " + this.id);
		else if (this.title == null || articleGroup == null)
			throw new ValidationException("One or more of not nullable fields are null in product with code " + this.id);
		else if (category == null)
			throw new ValidationException("Category not found in product with code " + this.id);
		else if (this.ean != null && (this.ean.length() < 13 || this.ean.length() > 14))
			throw new ValidationException("The EAN size isn't valid in product with code " + this.id);
		else if (stock == null || stockMin == null || this.stock < 0 || this.stockMin < 0)
			throw new ValidationException("Either stock or min stock is below 0 in product with code " + this.id);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Product product = (Product) o;

		if (!id.equals(product.id)) return false;
		if (productNumber != null ? !productNumber.equals(product.productNumber) : product.productNumber != null)
			return false;
		if (title != null ? !title.equals(product.title) : product.title != null) return false;
		if (ean != null ? !ean.equals(product.ean) : product.ean != null) return false;
		if (priceIncludeVat != null ? !priceIncludeVat.equals(product.priceIncludeVat) : product.priceIncludeVat != null)
			return false;
		if (priceExcludeVat != null ? !priceExcludeVat.equals(product.priceExcludeVat) : product.priceExcludeVat != null)
			return false;
		if (stock != null ? !stock.equals(product.stock) : product.stock != null) return false;
		if (stockMin != null ? !stockMin.equals(product.stockMin) : product.stockMin != null) return false;
		if (visible != null ? !visible.equals(product.visible) : product.visible != null) return false;
		if (retailPriceExcludeVat != null ? !retailPriceExcludeVat.equals(product.retailPriceExcludeVat) : product.retailPriceExcludeVat != null)
			return false;
		if (content != null ? !content.equals(product.content) : product.content != null) return false;
		if (color != null ? !color.equals(product.color) : product.color != null) return false;
//		if (category != null ? !category.equals(product.category) : product.category != null) return false;
		if (brand != null ? !brand.equals(product.brand) : product.brand != null) return false;
		if (keepStock != null ? !keepStock.equals(product.keepStock) : product.keepStock != null) return false;
		if (multipleOf != null ? !multipleOf.equals(product.multipleOf) : product.multipleOf != null) return false;
		if (packingUnit != null ? !packingUnit.equals(product.packingUnit) : product.packingUnit != null) return false;
//		if (matrixCategory != null ? !matrixCategory.equals(product.matrixCategory) : product.matrixCategory != null) return false;
		return matrixValue != null ? matrixValue.equals(product.matrixValue) : product.matrixValue == null;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + (productNumber != null ? productNumber.hashCode() : 0);
		result = 31 * result + (title != null ? title.hashCode() : 0);
		result = 31 * result + (ean != null ? ean.hashCode() : 0);
		result = 31 * result + (priceIncludeVat != null ? priceIncludeVat.hashCode() : 0);
		result = 31 * result + (priceExcludeVat != null ? priceExcludeVat.hashCode() : 0);
		result = 31 * result + (stock != null ? stock.hashCode() : 0);
		result = 31 * result + (stockMin != null ? stockMin.hashCode() : 0);
		result = 31 * result + (visible != null ? visible.hashCode() : 0);
		result = 31 * result + (retailPriceExcludeVat != null ? retailPriceExcludeVat.hashCode() : 0);
		result = 31 * result + (content != null ? content.hashCode() : 0);
		result = 31 * result + (color != null ? color.hashCode() : 0);
		result = 31 * result + (category != null ? category.hashCode() : 0);
		result = 31 * result + (brand != null ? brand.hashCode() : 0);
		result = 31 * result + (keepStock != null ? keepStock.hashCode() : 0);
		result = 31 * result + (multipleOf != null ? multipleOf.hashCode() : 0);
		result = 31 * result + (matrixCategory != null ? matrixCategory.hashCode() : 0);
		result = 31 * result + (matrixValue != null ? matrixValue.hashCode() : 0);
		result = 31 * result + (packingUnit != null ? packingUnit.hashCode() : 0);
		result = 31 * result + (keywords != null ? keywords.hashCode() : 0);
		return result;
	}
}
