package com.negeso.module.webshop.entity;

import com.negeso.framework.domain.Contact;

public class DeliveryContact {

	private Integer id;
	private String firstName;
	private String secondName;
	private String companyName;
	private String addressLine;
	private String zipCode;
	private String city;
	private String country;
	private String phone;
	private String fax;
	private Order order;

	public DeliveryContact() {
	}

	public DeliveryContact(Order order) {
		this.order = order;
		Contact shippingContact = order.getCustomer().getShippingContact();
		if (shippingContact == null)
			return;
		this.setFirstName(shippingContact.getFirstName());
		this.setSecondName(shippingContact.getSecondName());
		this.setCompanyName(shippingContact.getCompanyName());
		this.setAddressLine(shippingContact.getAddressLine());
		this.setZipCode(shippingContact.getZipCode());
		this.setCity(shippingContact.getCity());
		this.setCountry(shippingContact.getCountry());
		this.setPhone(shippingContact.getPhone());
		this.setFax(shippingContact.getFax());
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}

	public String getAddressLine() {
		return addressLine;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return city;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountry() {
		return country;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone() {
		return phone;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getFax() {
		return fax;
	}
}
