package com.negeso.module.webshop.entity;

import com.google.gson.annotations.Expose;
import com.negeso.framework.domain.Contact;
import com.negeso.framework.domain.User;
import com.negeso.framework.exception.ValidationException;
import com.negeso.framework.io.Behavior;
import com.negeso.framework.io.HibernateDependency;
import com.negeso.framework.io.xls.XlsColumnNumber;
import com.negeso.framework.io.xls.XlsObject;
import com.negeso.framework.util.Validator;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class Customer implements Serializable, Validator{

	@XlsColumnNumber(required = true) @Expose private String userCode;
	@XlsColumnNumber(1) @Expose private String email;
	@XlsColumnNumber(2) @Expose private Boolean postPayAllowed;
	@XlsColumnNumber(3) private PriceList priceGroup;
	private Contact shippingContact;
	@XlsColumnNumber @XlsObject
	@HibernateDependency
	private Contact billingContact;

	@XlsColumnNumber
	@XlsObject
	@HibernateDependency(behavior = Behavior.MERGE)
	private User user;
	private List<Wishlist> wishProducts;
	private String displayPrice;
	private Set<CartItem> items;

	public Set<CartItem> getItems() {
		return items;
	}

	public void setItems(Set<CartItem> items) {
		this.items = items;
	}

	public String getDisplayPrice() {
		return displayPrice;
	}

	public void setDisplayPrice(String displayPrice) {
		this.displayPrice = displayPrice;
	}

	public List<Wishlist> getWishProducts() {
		return wishProducts;
	}

	public void setWishProducts(List<Wishlist> wishProducts) {
		this.wishProducts = wishProducts;
	}

	public Customer(){}

	public Customer(String userCode) {
		this.userCode = userCode;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public PriceList getPriceGroup() {
		return priceGroup;
	}

	public void setPriceGroup(PriceList priceGroup) {
		this.priceGroup = priceGroup;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Contact getShippingContact() {
		return shippingContact;
	}

	public void setShippingContact(Contact shippingContact) {
		this.shippingContact = shippingContact;
	}

	public Contact getBillingContact() {
		return billingContact;
	}

	public void setBillingContact(Contact billingContact) {
		this.billingContact = billingContact;
	}

	public Boolean getPostPayAllowed() {
		return postPayAllowed;
	}

	public void setPostPayAllowed(Boolean postPayAllowed) {
		this.postPayAllowed = postPayAllowed;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Customer customer = (Customer) o;

		if (userCode != null ? !userCode.equals(customer.userCode) : customer.userCode != null) return false;
		if (priceGroup != null ? !priceGroup.equals(customer.priceGroup) : customer.priceGroup != null) return false;
		if (email != null ? !email.equals(customer.email) : customer.email != null) return false;
		if (postPayAllowed != null ? !postPayAllowed.equals(customer.postPayAllowed) : customer.postPayAllowed != null)
			return false;
		if (user != null ? !user.equals(customer.user) : customer.user != null) return false;
		if (wishProducts != null ? !wishProducts.equals(customer.wishProducts) : customer.wishProducts != null)
			return false;
		return displayPrice != null ? displayPrice.equals(customer.displayPrice) : customer.displayPrice == null;
	}

	@Override
	public int hashCode() {
		int result = userCode != null ? userCode.hashCode() : 0;
		result = 31 * result + (priceGroup != null ? priceGroup.hashCode() : 0);
		result = 31 * result + (email != null ? email.hashCode() : 0);
		result = 31 * result + (postPayAllowed != null ? postPayAllowed.hashCode() : 0);
		result = 31 * result + (user != null ? user.hashCode() : 0);
		result = 31 * result + (wishProducts != null ? wishProducts.hashCode() : 0);
		result = 31 * result + (displayPrice != null ? displayPrice.hashCode() : 0);
		return result;
	}

	@Override
	public void validate() throws ValidationException {
		if (this.userCode == null)
			throw new ValidationException("User code is empty");
		else if (this.priceGroup == null)
			throw new ValidationException("Price group is missing. " + this.userCode);
		else if (this.user == null)
			throw new ValidationException("Unable to create user");
		else if (this.user.getPassword() == null)
			throw new ValidationException("Password is missing. " + this.userCode);
	}
}
