package com.negeso.module.webshop.entity;

import com.google.gson.annotations.Expose;
import com.negeso.framework.Env;
import com.negeso.framework.io.PrimaryKey;
import com.negeso.framework.jaxb.TimestampAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "category", namespace = Env.NEGESO_NAMESPACE)
public class ProductCategory implements Serializable, PrimaryKey<String>{
	@Expose
	private String id;
	@Expose private String title;
	@Expose private String description;
	@Expose private String image;
	@Expose private Integer orderNumber;
	@Expose private Boolean visible;
	private ProductCategory parentCategory;
	private Set<ProductCategory> childCategories;
	@Expose private Timestamp creationDate;
	private List<Product> products;
	@Expose private Set<String> visibleTo;

	public ProductCategory(){}
	public ProductCategory(String id) {
		this.id = id;
	}
	public ProductCategory(List<String> parents){
		String webLevel;
		if (parents.size() == 1){
			webLevel = parents.get(0);
		}else {
			webLevel = parents.remove(0);
			this.parentCategory = new ProductCategory(parents);
		}
		this.title = webLevel;
		this.id = webLevel.replaceAll("[!@#$%^&* .]", "_").toLowerCase();
		this.visible = true;
	}

	public ProductCategory(Map<Integer, String> parents){
		Iterator<Map.Entry<Integer, String>> iterator = parents.entrySet().iterator();
		Map.Entry<Integer, String> next = iterator.next();

		if (parents.size() != 1){
			iterator.remove();
			this.parentCategory = new ProductCategory(parents);
		}
		this.title = next.getValue();
		this.id = next.getValue().replaceAll("[!@#$%^&* .]", "_").toLowerCase() + "-" + next.getKey();
		this.visible = true;
	}



	@XmlAttribute
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@XmlAttribute
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlAttribute
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@XmlAttribute
	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	@XmlAttribute
	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public ProductCategory getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(ProductCategory parentCategory) {
		this.parentCategory = parentCategory;
	}

	@XmlElementWrapper(name="subcategories",namespace = Env.NEGESO_NAMESPACE)
	@XmlElement(name="category",namespace = Env.NEGESO_NAMESPACE)
	public Set<ProductCategory> getChildCategories() {

		return childCategories;
	}

	public void setChildCategories(Set<ProductCategory> childCategories) {
		this.childCategories = childCategories;
	}

	@XmlAttribute
	@XmlJavaTypeAdapter(TimestampAdapter.class)
	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	@XmlAttribute
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<String> getVisibleTo() {
		return visibleTo;
	}

	public void setVisibleTo(Set<String> visibleTo) {
		this.visibleTo = visibleTo;
	}

	public boolean isCategoryVisibleFor(String login){
		if (this.parentCategory == null){
			return this.getVisible() || this.getVisibleTo().contains(login);
		}else {
			return parentCategory.isCategoryVisibleFor(login) && (this.getVisible() || this.getVisibleTo().contains(login));
		}
	}

	public boolean isCategoryVisibleFor(Customer customer){
		return customer == null ? this.parentCategory != null ? this.getVisible() && this.parentCategory.isCategoryVisibleFor((Customer) null): this.getVisible() : this.isCategoryVisibleFor(customer.getUserCode());
	}

	@Override
	public String[] getJoinedPrimaryKey() {
		if (parentCategory != null)
			return new String[]{parentCategory.getId()};
		return new String[]{null};
	}

	@Override
	public Object[] getJoined() {
		return new Object[]{parentCategory};
	}

	@Override
	public void setJoinedObject(Object key, int foreign) {
		this.parentCategory = (ProductCategory) key;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ProductCategory category = (ProductCategory) o;

		if (!id.equals(category.id)) return false;
		if (!title.equals(category.title)) return false;
		if (description != null ? !description.equals(category.description) : category.description != null)
			return false;
		if (image != null ? !image.equals(category.image) : category.image != null) return false;
		if (orderNumber != null ? !orderNumber.equals(category.orderNumber) : category.orderNumber != null)
			return false;
		if (visible != null ? !visible.equals(category.visible) : category.visible != null) return false;
		if (parentCategory != null ? !parentCategory.equals(category.parentCategory) : category.parentCategory != null)
			return false;
		return creationDate != null ? creationDate.equals(category.creationDate) : category.creationDate == null;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + title.hashCode();
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + (image != null ? image.hashCode() : 0);
		result = 31 * result + (orderNumber != null ? orderNumber.hashCode() : 0);
		result = 31 * result + (visible != null ? visible.hashCode() : 0);
		result = 31 * result + (parentCategory != null ? parentCategory.hashCode() : 0);
		result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
		return result;
	}
}
